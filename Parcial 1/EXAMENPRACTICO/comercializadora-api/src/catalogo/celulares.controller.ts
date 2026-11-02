import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  ParseIntPipe,
  Patch,
  Post,
  Put,
  Query,
} from '@nestjs/common';
import { CelularesService } from './celulares.service.js';
import type { Celular } from './celular.entity.js';
import { CreateCelularDto } from './dto/create-celular.dto.js';
import { UpdateCelularDto } from './dto/update-celular.dto.js';

/**
 * Maintenance of the phone catalog (S10).
 *
 * No `@AllowAnonymous()` anywhere: the global Better Auth guard of the auth
 * module rejects every one of these routes with 401 when there is no session.
 *
 * | Route | Effect |
 * |---|---|
 * | `GET /api/celulares` | phones on sale (`?todos=true` adds the inactive ones) |
 * | `GET /api/celulares/:id` | one phone, 404 when it does not exist |
 * | `POST /api/celulares` | creates a phone, 201; 400 invalid body, 409 repeated marca+modelo |
 * | `PUT /api/celulares/:id` | replaces the editable fields of a phone |
 * | `DELETE /api/celulares/:id` | soft delete (`activo = false`) |
 * | `PATCH /api/celulares/:id/activar` | brings a soft deleted phone back |
 */
@Controller('api/celulares')
export class CelularesController {
  constructor(private readonly celulares: CelularesService) {}

  @Get()
  findAll(@Query('todos') todos?: string): Promise<Celular[]> {
    return this.celulares.findAll(todos === 'true');
  }

  @Get(':id')
  findOne(@Param('id', ParseIntPipe) id: number): Promise<Celular> {
    return this.celulares.findOne(id);
  }

  @Post()
  create(@Body() dto: CreateCelularDto): Promise<Celular> {
    return this.celulares.create(dto);
  }

  @Put(':id')
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: UpdateCelularDto,
  ): Promise<Celular> {
    return this.celulares.update(id, dto);
  }

  @Delete(':id')
  remove(@Param('id', ParseIntPipe) id: number): Promise<Celular> {
    return this.celulares.deactivate(id);
  }

  @Patch(':id/activar')
  activate(@Param('id', ParseIntPipe) id: number): Promise<Celular> {
    return this.celulares.activate(id);
  }
}
