/**
 * Minimal ambient types for `pg` (node-postgres).
 *
 * `pg` 8.x ships no type declarations and this project does not depend on
 * `@types/pg`, so only the small surface the API actually calls is declared
 * here. If `@types/pg` is added as a dev dependency later, delete this file
 * and remove the cast in `src/auth/auth.ts`.
 */
declare module 'pg' {
  export interface PoolConfig {
    connectionString?: string;
    max?: number;
    idleTimeoutMillis?: number;
    allowExitOnIdle?: boolean;
  }

  export interface QueryResult<R = Record<string, unknown>> {
    rows: R[];
    rowCount: number | null;
  }

  export class Pool {
    constructor(config?: PoolConfig);
    query<R = Record<string, unknown>>(
      sql: string,
      parameters?: readonly unknown[],
    ): Promise<QueryResult<R>>;
    end(): Promise<void>;
  }
}
