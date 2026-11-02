import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AuthFeatureModule } from './auth/auth.module.js';
import { readEnv } from './config/env.js';
import { migrations } from './database/migrations/index.js';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    // TypeORM owns the `comercializadora` schema: entities are registered per
    // feature module (`autoLoadEntities`) and the migrations run on startup.
    TypeOrmModule.forRootAsync({
      inject: [ConfigService],
      useFactory: (config: ConfigService) => ({
        type: 'postgres',
        url: readEnv(config, 'DATABASE_URL'),
        autoLoadEntities: true,
        synchronize: false,
        migrationsRun: true,
        migrations,
      }),
    }),
    AuthFeatureModule,
  ],
})
export class AppModule {}
