import { ConfigService } from '@nestjs/config';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module.js';
import { readEnv } from './config/env.js';
import { createValidationPipe } from './config/validation-pipe.js';

async function bootstrap() {
  // Better Auth has to read the raw request body, so Nest's body parser is
  // disabled; the auth module re-registers the JSON/urlencoded parsers for
  // every route outside `/api/auth`.
  const app = await NestFactory.create(AppModule, { bodyParser: false });

  // DTO validation for the catalog/invoicing endpoints. The e2e specs register
  // the same pipe through `createValidationPipe()`.
  app.useGlobalPipes(createValidationPipe());

  const config = app.get(ConfigService);
  await app.listen(Number(readEnv(config, 'PORT')));
}

await bootstrap();
