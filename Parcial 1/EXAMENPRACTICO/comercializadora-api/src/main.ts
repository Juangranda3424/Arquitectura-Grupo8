import { ValidationPipe } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module.js';
import { readEnv } from './config/env.js';

async function bootstrap() {
  // Better Auth has to read the raw request body, so Nest's body parser is
  // disabled; the auth module re-registers the JSON/urlencoded parsers for
  // every route outside `/api/auth`.
  const app = await NestFactory.create(AppModule, { bodyParser: false });

  // DTO validation for the catalog/invoicing endpoints added later on.
  app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));

  const config = app.get(ConfigService);
  await app.listen(Number(readEnv(config, 'PORT')));
}

await bootstrap();
