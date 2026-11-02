import { CreateCelularDto } from './create-celular.dto.js';

/**
 * Body of `PUT /api/celulares/:id`.
 *
 * It is a full replacement of the editable fields, so `marca`, `modelo` and
 * `precio` are required again and an omitted `descripcion` clears it. The
 * fields (and their rules) are inherited from `CreateCelularDto`.
 */
export class UpdateCelularDto extends CreateCelularDto {
  // Inherited: marca, modelo, descripcion, precio.
}
