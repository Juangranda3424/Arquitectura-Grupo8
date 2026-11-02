import type { ConfigService } from '@nestjs/config';

/**
 * Development defaults for every environment variable the API reads.
 *
 * The values are only used when the variable is absent from the environment
 * (or from the `.env` file loaded by ConfigModule), so a real deployment only
 * has to export its own values. See `.env.example`.
 */
export const ENV_DEFAULTS = {
  /** Port the NestJS API listens on. */
  PORT: '3001',
  /** PostgreSQL database owned by the retailer (`comercializadora`). */
  DATABASE_URL: 'postgres://gr8:gr8pass@localhost:5432/comercializadora',
  /**
   * Public base URL of the Next.js app. The browser only ever talks to the
   * web app, which rewrites `/api/*` to this API, so the auth cookies are
   * created for the web origin.
   */
  BETTER_AUTH_URL: 'http://localhost:3000',
  /** Secret used to sign the Better Auth cookies. Replace it outside dev. */
  BETTER_AUTH_SECRET: 'dev-only-insecure-secret-change-me-32chars',
  /** Origin allowed to call the auth endpoints from a browser. */
  WEB_ORIGIN: 'http://localhost:3000',
  /**
   * Base URL of the BanQuito Core (Spring Boot) REST API. The invoicing module
   * calls it for every credit decision and for the amortization table.
   */
  BANQUITO_URL: 'http://localhost:8080',
} as const;

export type EnvKey = keyof typeof ENV_DEFAULTS;

/**
 * Reads an environment variable, falling back to the documented dev default.
 */
export function readEnv(config: ConfigService, key: EnvKey): string {
  return config.get<string>(key) ?? ENV_DEFAULTS[key];
}
