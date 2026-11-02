import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Celular } from './celular.entity.js';
import { CelularesController } from './celulares.controller.js';
import { CelularesService } from './celulares.service.js';

/**
 * Phone catalog (S10): the CRUD behind `/api/celulares` and the Next.js
 * `/catalogo` screen.
 *
 * Registering the entity here is what lets TypeORM see it, because
 * `AppModule` uses `autoLoadEntities`.
 */
@Module({
  imports: [TypeOrmModule.forFeature([Celular])],
  controllers: [CelularesController],
  providers: [CelularesService],
})
export class CatalogoModule {}
