import {
  BadGatewayException,
  HttpException,
  Logger,
} from '@nestjs/common';
import type { BancoError } from './banquito.types.js';

/** Message the retailer returns when the core cannot be reached. */
export const BANQUITO_UNAVAILABLE =
  'El servicio del Banco BanQuito no está disponible';

/**
 * The core did not answer: connection refused, timeout, a 5xx or a body that
 * is not JSON. From the retailer's point of view the whole bank is down, so
 * every one of those cases becomes the same 502.
 */
export class BanquitoUnavailableError extends Error {
  constructor(
    /** `METHOD /path` of the call, so the log names the failing service. */
    readonly call: string,
    readonly cause?: unknown,
  ) {
    super(`No se pudo consumir ${call} del Banco BanQuito.`);
    this.name = 'BanquitoUnavailableError';
  }
}

/**
 * The core rejected the request with a 4xx and its own error body. The
 * invoicing service relays the status and the message to the seller (S11),
 * instead of hiding the bank's reason behind a generic 500.
 */
export class BanquitoApiError extends Error {
  constructor(
    readonly status: number,
    readonly body: BancoError | null,
  ) {
    super(body?.mensaje ?? `El Banco BanQuito respondió HTTP ${status}.`);
    this.name = 'BanquitoApiError';
  }
}

const logger = new Logger('BanquitoClient');

/**
 * Turns whatever a `BanquitoClient` call threw into the exception the API
 * answers, and rethrows anything that is not a BanQuito failure untouched
 * (a bug must stay a 500, not become a "bank is down").
 *
 * - core unreachable / 5xx -> 502 `{ statusCode, message }`
 * - core 4xx -> the same status with the bank's `codigo`/`mensaje`/`motivos`
 */
export function rethrowAsHttp(error: unknown): never {
  if (error instanceof HttpException) {
    throw error;
  }

  if (error instanceof BanquitoUnavailableError) {
    logger.warn(error.message, error.cause);
    throw new BadGatewayException(BANQUITO_UNAVAILABLE);
  }

  if (error instanceof BanquitoApiError) {
    throw new HttpException(
      {
        statusCode: error.status,
        message: error.message,
        codigo: error.body?.codigo ?? null,
        motivos: error.body?.motivos ?? null,
        montoMaximo: error.body?.montoMaximo ?? null,
      },
      error.status,
    );
  }

  throw error;
}
