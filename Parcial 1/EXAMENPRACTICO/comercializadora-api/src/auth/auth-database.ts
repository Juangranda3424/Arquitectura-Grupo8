import { Injectable, Module, type OnModuleDestroy } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { InjectDataSource } from '@nestjs/typeorm';
import { Pool } from 'pg';
import { DataSource } from 'typeorm';
import { readEnv } from '../config/env.js';

/**
 * Owns the `pg` pool that Better Auth uses, so the connection is closed when
 * the Nest application shuts down (this also keeps `npm test` from hanging).
 *
 * It injects the TypeORM `DataSource` on purpose: the DataSource provider only
 * resolves once `migrationsRun` has applied the pending migrations, which
 * guarantees the auth tables exist before Better Auth is instantiated (Better
 * Auth validates the schema against its own model on first use).
 */
@Injectable()
export class AuthDatabase implements OnModuleDestroy {
  readonly pool: Pool;

  constructor(
    config: ConfigService,
    // Dependency only, to wait for the migrations that create the auth tables.
    @InjectDataSource() _dataSource: DataSource,
  ) {
    this.pool = new Pool({ connectionString: readEnv(config, 'DATABASE_URL') });
  }

  async onModuleDestroy(): Promise<void> {
    await this.pool.end();
  }
}

@Module({
  imports: [ConfigModule],
  providers: [AuthDatabase],
  exports: [AuthDatabase],
})
export class AuthDatabaseModule {}
