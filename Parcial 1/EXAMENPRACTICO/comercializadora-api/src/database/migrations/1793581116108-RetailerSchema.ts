import type { MigrationInterface, QueryRunner } from 'typeorm';

/**
 * Retailer sales schema (S10, prepared for S11 of T7): the phone catalog plus
 * the invoicing model (`cliente`, `factura`, `detalle_factura`).
 *
 * The DDL is the one the exam statement asks for, so the columns keep their
 * snake_case names and the check/unique constraints are the text of the
 * statement. Three decisions worth knowing:
 *
 * - `celular.precio` is NUMERIC(10,2) with a positive check; the entity maps
 *   it to a JS number through a transformer (see
 *   `src/database/numeric.transformer.ts`).
 * - `uq_celular_marca_modelo` is what the API turns into a 409 when a second
 *   phone repeats marca + modelo.
 * - `ck_factura_credito` forces a BanQuito credit code (and 3..18 quotas) only
 *   for `forma_pago = 'CRE'`; a cash invoice cannot carry one.
 *
 * There is no inventory: the catalog is the list of phones on sale.
 */
export class RetailerSchema1793581116108 implements MigrationInterface {
  name = 'RetailerSchema1793581116108';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `create table celular (
        cod_celular  serial        primary key,
        marca        varchar(50)   not null,
        modelo       varchar(100)  not null,
        descripcion  varchar(255),
        precio       numeric(10,2) not null check (precio > 0),
        activo       boolean       not null default true,
        constraint uq_celular_marca_modelo unique (marca, modelo)
      )`,
    );
    await queryRunner.query(
      `create table cliente (
        cod_cliente  serial        primary key,
        cedula       varchar(10)   not null unique,
        nombre       varchar(100)  not null
      )`,
    );
    await queryRunner.query(
      `create table factura (
        num_factura  serial        primary key,
        fecha        timestamp     not null default current_timestamp,
        cod_cliente  int           not null references cliente (cod_cliente),
        forma_pago   varchar(3)    not null check (forma_pago in ('EFE', 'CRE')),
        subtotal     numeric(10,2) not null,
        descuento    numeric(10,2) not null default 0,
        total        numeric(10,2) not null,
        cod_credito  int,
        num_cuotas   int,
        valor_cuota  numeric(10,2),
        constraint ck_factura_credito check (
          (forma_pago = 'EFE' and cod_credito is null)
          or (
            forma_pago = 'CRE'
            and cod_credito is not null
            and num_cuotas between 3 and 18
          )
        )
      )`,
    );
    await queryRunner.query(
      `create table detalle_factura (
        cod_detalle      serial        primary key,
        num_factura      int           not null references factura (num_factura),
        cod_celular      int           not null references celular (cod_celular),
        cantidad         int           not null check (cantidad > 0),
        precio_unitario  numeric(10,2) not null,
        subtotal         numeric(10,2) not null
      )`,
    );
    // Seed of the phones on sale, exactly the six of the exam statement.
    await queryRunner.query(
      `insert into celular (marca, modelo, descripcion, precio) values
        ('Samsung',  'Galaxy A16',    '6.7", 128 GB, 4 GB RAM',  229.00),
        ('Samsung',  'Galaxy S25',    '6.2", 256 GB, 12 GB RAM', 999.00),
        ('Apple',    'iPhone 16',     '6.1", 128 GB',           1099.00),
        ('Xiaomi',   'Redmi Note 14', '6.67", 256 GB, 8 GB RAM', 289.00),
        ('Motorola', 'Moto G85',      '6.67", 256 GB, 8 GB RAM', 319.00),
        ('Honor',    'X8c',           '6.7", 256 GB, 8 GB RAM',  259.00)`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    // Children first: both tables reference the rows above them.
    await queryRunner.query(`drop table if exists detalle_factura`);
    await queryRunner.query(`drop table if exists factura`);
    await queryRunner.query(`drop table if exists cliente`);
    await queryRunner.query(`drop table if exists celular`);
  }
}
