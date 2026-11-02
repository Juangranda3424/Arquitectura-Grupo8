import { betterAuth, type BetterAuthOptions } from 'better-auth';
import type { Pool } from 'pg';

export interface CreateAuthOptions {
  /** Shared `pg` pool; the schema itself is owned by TypeORM migrations. */
  pool: Pool;
  /** Public URL the browser uses (the Next.js app, not the API port). */
  baseURL: string;
  secret: string;
  trustedOrigins: string[];
}

/**
 * Builds the Better Auth instance.
 *
 * It is created by a Nest factory (not at module load time) so that the
 * values loaded from `.env` by ConfigModule are already available.
 *
 * Better Auth keeps everything in the database (user, session, account,
 * verification) through the `pg` pool. Its default `basePath` is `/api/auth`.
 */
export function createAuth(options: CreateAuthOptions) {
  return betterAuth({
    // Better Auth accepts a `pg` Pool (typed structurally through kysely).
    // `pg` has no type declarations in this project (see src/types/pg.d.ts),
    // so the locally declared pool is widened to the expected union here.
    database: options.pool as unknown as BetterAuthOptions['database'],
    baseURL: options.baseURL,
    secret: options.secret,
    trustedOrigins: options.trustedOrigins,
    emailAndPassword: {
      enabled: true,
    },
  });
}

export type Auth = ReturnType<typeof createAuth>;
