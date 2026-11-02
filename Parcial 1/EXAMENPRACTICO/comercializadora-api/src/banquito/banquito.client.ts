import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { readEnv } from '../config/env.js';
import {
  BanquitoApiError,
  BanquitoUnavailableError,
} from './banquito.errors.js';
import type {
  BancoError,
  Credito,
  MontoMaximo,
  OtorgarCreditoRequest,
  SujetoCredito,
} from './banquito.types.js';

/** The core is a local service; a call that takes longer is a call that failed. */
const TIMEOUT_MS = 5000;

/**
 * HTTP client of the BanQuito Core credit module (S5-S9, S11, S12).
 *
 * The retailer never talks to the bank's database: every credit decision is a
 * REST call to the Spring Boot core (`BANQUITO_URL`, `http://localhost:8080`
 * by default). Using the global `fetch` of Node keeps the dependency list
 * untouched.
 *
 * Errors are translated once, here, and the invoicing service only has to turn
 * them into HTTP answers (`rethrowAsHttp`):
 * - no answer, timeout, 5xx or a body that is not JSON -> `BanquitoUnavailableError`
 * - any other 4xx -> `BanquitoApiError`, carrying the bank's error body
 */
@Injectable()
export class BanquitoClient {
  private readonly logger = new Logger(BanquitoClient.name);
  private readonly baseUrl: string;

  constructor(config: ConfigService) {
    // Trailing slashes would produce `//api/creditos` in every path.
    this.baseUrl = readEnv(config, 'BANQUITO_URL').replace(/\/+$/, '');
  }

  /** Rubric 5: is this cédula a credit subject? */
  sujetoCredito(cedula: string): Promise<SujetoCredito> {
    const query = new URLSearchParams({ cedula });

    return this.request<SujetoCredito>(
      'GET',
      `/api/creditos/sujeto-credito?${query.toString()}`,
    );
  }

  /** Rubric 6: maximum amount the bank approves for a client. */
  montoMaximo(codCliente: number): Promise<MontoMaximo> {
    return this.request<MontoMaximo>(
      'GET',
      `/api/creditos/monto-maximo/${codCliente}`,
    );
  }

  /** Rubric 7: grants the credit and returns it with its amortization table. */
  otorgarCredito(body: OtorgarCreditoRequest): Promise<Credito> {
    return this.request<Credito>('POST', '/api/creditos', body);
  }

  /** Rubric 10: amortization table of an already granted credit. */
  amortizacion(codCredito: number): Promise<Credito> {
    return this.request<Credito>(
      'GET',
      `/api/creditos/${codCredito}/amortizacion`,
    );
  }

  private async request<T>(
    method: string,
    path: string,
    body?: unknown,
  ): Promise<T> {
    const call = `${method} ${path}`;
    let response: Response;
    let text: string;

    try {
      response = await fetch(`${this.baseUrl}${path}`, {
        method,
        headers:
          body === undefined
            ? { Accept: 'application/json' }
            : { Accept: 'application/json', 'Content-Type': 'application/json' },
        body: body === undefined ? undefined : JSON.stringify(body),
        // Fails the call (and the request) after TIMEOUT_MS instead of
        // leaving the seller waiting on a dead core.
        signal: AbortSignal.timeout(TIMEOUT_MS),
      });
      text = await response.text();
    } catch (error) {
      this.logger.warn(`No se pudo contactar al Banco (${call}).`);
      throw new BanquitoUnavailableError(call, error);
    }

    const payload = parseJson(text);

    if (response.status >= 500) {
      this.logger.warn(`El Banco respondió ${response.status} en ${call}.`);
      throw new BanquitoUnavailableError(call, payload);
    }

    if (!response.ok) {
      throw new BanquitoApiError(response.status, payload as BancoError | null);
    }

    return payload as T;
  }
}

/** JSON body of a response, or `null` when it is empty or not JSON. */
function parseJson(text: string): unknown {
  if (text === '') {
    return null;
  }

  try {
    return JSON.parse(text) as unknown;
  } catch {
    return null;
  }
}
