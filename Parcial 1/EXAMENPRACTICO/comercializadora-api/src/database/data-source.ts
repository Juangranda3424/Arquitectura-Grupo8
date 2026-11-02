import { DataSource } from 'typeorm';
import { ENV_DEFAULTS } from '../config/env.js';
import { migrations } from './migrations/index.js';

/**
 * DataSource used by the TypeORM CLI (`npm run migration:run` /
 * `npm run migration:revert`), which loads the compiled `dist` output:
 *
 *   npm run migration:run         # build + migrate
 *   npm run migration:revert      # build + revert the last migration
 *
 * The Nest application builds its own connection in `AppModule` with the same
 * database URL, `synchronize: false` and `migrationsRun: true`.
 */
export default new DataSource({
  type: 'postgres',
  url: process.env.DATABASE_URL ?? ENV_DEFAULTS.DATABASE_URL,
  entities: [],
  migrations,
  synchronize: false,
  migrationsRun: false,
});
