import { ValidationPipe } from '@nestjs/common';

/**
 * Global DTO validation used by the API.
 *
 * Returned by a function instead of being instantiated inline so the CLI
 * bootstrap (`main.ts`) and the e2e specs register exactly the same pipe:
 * without it the specs would test an application the real server never runs.
 *
 * - `whitelist` drops properties the DTO does not declare.
 * - `transform` turns the JSON payload into DTO instances, so `number` fields
 *   reach the service as numbers (and classes serialize the same way in the
 *   response).
 */
export function createValidationPipe(): ValidationPipe {
  return new ValidationPipe({ whitelist: true, transform: true });
}
