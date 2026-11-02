import { AuthTables1793580336123 } from './1793580336123-AuthTables.js';

/**
 * Migrations are imported explicitly instead of discovered with a glob.
 *
 * A glob would depend on where the file runs from (`dist/` after `nest build`
 * or `nest start`, the `src/` TypeScript sources under vitest), so the same
 * list is used by the application, the TypeORM CLI and the tests. Add every
 * new migration here, in timestamp order.
 */
export const migrations = [AuthTables1793580336123];
