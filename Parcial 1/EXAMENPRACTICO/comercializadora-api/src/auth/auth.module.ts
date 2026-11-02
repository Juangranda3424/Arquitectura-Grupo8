import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { AuthModule } from '@thallesp/nestjs-better-auth';
import { readEnv } from '../config/env.js';
import { createAuth } from './auth.js';
import { AuthDatabase, AuthDatabaseModule } from './auth-database.js';
import { DemoUserSeed } from './demo-user.seed.js';
import { MeController } from './me.controller.js';

/**
 * Better Auth for the retailer (S15): email/password sessions stored in the
 * `comercializadora` database, plus a global guard so every route requires a
 * session unless it says `@AllowAnonymous()`.
 */
@Module({
  imports: [
    AuthModule.forRootAsync({
      imports: [ConfigModule, AuthDatabaseModule],
      inject: [ConfigService, AuthDatabase],
      useFactory: (config: ConfigService, database: AuthDatabase) => ({
        auth: createAuth({
          pool: database.pool,
          baseURL: readEnv(config, 'BETTER_AUTH_URL'),
          secret: readEnv(config, 'BETTER_AUTH_SECRET'),
          trustedOrigins: [readEnv(config, 'WEB_ORIGIN')],
        }),
      }),
    }),
  ],
  controllers: [MeController],
  providers: [DemoUserSeed],
})
export class AuthFeatureModule {}
