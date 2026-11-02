import {
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { QueryFailedError, Repository } from 'typeorm';
import { Celular } from './celular.entity.js';
import type { CreateCelularDto } from './dto/create-celular.dto.js';
import type { UpdateCelularDto } from './dto/update-celular.dto.js';

/** Message and status of the errors the controller returns. */
const NOT_FOUND = 'No existe un celular con ese código.';
const DUPLICATE = 'Ya existe un celular con esa marca y modelo.';

/** `23505` is the PostgreSQL code of a unique constraint violation. */
function isUniqueViolation(error: unknown): boolean {
  return (
    error instanceof QueryFailedError &&
    (error.driverError as { code?: string }).code === '23505'
  );
}

/**
 * Maintenance of the phone catalog (S10).
 *
 * Phones are never removed: invoices reference them, so `remove` only sets
 * `activo = false` and `activate` brings the row back. Reads return entities
 * as they are (camelCase columns, price as a number with 2 decimals) and the
 * list is ordered by marca + modelo so the web table is stable.
 */
@Injectable()
export class CelularesService {
  constructor(
    @InjectRepository(Celular)
    private readonly celulares: Repository<Celular>,
  ) {}

  /** Phones on sale, or every phone (inactive ones included) with `todos`. */
  findAll(includeInactive: boolean): Promise<Celular[]> {
    return this.celulares.find({
      where: includeInactive ? undefined : { activo: true },
      order: { marca: 'ASC', modelo: 'ASC' },
    });
  }

  async findOne(codCelular: number): Promise<Celular> {
    const celular = await this.celulares.findOneBy({ codCelular });

    if (!celular) {
      throw new NotFoundException(NOT_FOUND);
    }

    return celular;
  }

  async create(dto: CreateCelularDto): Promise<Celular> {
    await this.assertUnique(dto.marca, dto.modelo);

    const created = await this.save(
      this.celulares.create({
        marca: dto.marca,
        modelo: dto.modelo,
        descripcion: dto.descripcion ?? null,
        precio: dto.precio,
        activo: true,
      }),
    );

    return this.findOne(created.codCelular);
  }

  async update(
    codCelular: number,
    dto: UpdateCelularDto,
  ): Promise<Celular> {
    const celular = await this.findOne(codCelular);
    await this.assertUnique(dto.marca, dto.modelo, codCelular);

    celular.marca = dto.marca;
    celular.modelo = dto.modelo;
    celular.descripcion = dto.descripcion ?? null;
    celular.precio = dto.precio;

    await this.save(celular);

    return this.findOne(codCelular);
  }

  /** Soft delete: the row stays, so old invoices keep their phone. */
  async deactivate(codCelular: number): Promise<Celular> {
    return this.setActive(codCelular, false);
  }

  /** Brings a soft deleted phone back to the catalog. */
  async activate(codCelular: number): Promise<Celular> {
    return this.setActive(codCelular, true);
  }

  private async setActive(
    codCelular: number,
    activo: boolean,
  ): Promise<Celular> {
    const celular = await this.findOne(codCelular);

    if (celular.activo !== activo) {
      celular.activo = activo;
      await this.save(celular);
    }

    return this.findOne(codCelular);
  }

  /**
   * Rejects a repeated marca + modelo before touching the database, so the
   * caller gets a 409 instead of a driver error; `save` keeps the same
   * conversion for the race the unique constraint catches.
   */
  private async assertUnique(
    marca: string,
    modelo: string,
    ownId?: number,
  ): Promise<void> {
    const existing = await this.celulares.findOneBy({ marca, modelo });

    if (existing && existing.codCelular !== ownId) {
      throw new ConflictException(DUPLICATE);
    }
  }

  private async save(celular: Celular): Promise<Celular> {
    try {
      return await this.celulares.save(celular);
    } catch (error) {
      if (isUniqueViolation(error)) {
        throw new ConflictException(DUPLICATE);
      }

      throw error;
    }
  }
}
