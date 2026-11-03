
/*==============================================================*/
/* Table: CLIENTE                                               */
/*==============================================================*/
create table CLIENTE (
   ID_CLIENTE           INT4                 not null,
   NOMBRE               VARCHAR(50)          null,
   CEDULA               VARCHAR(10)          null,
   EMAIL                VARCHAR(50)          null,
   constraint PK_CLIENTE primary key (ID_CLIENTE)
);

/*==============================================================*/
/* Index: CLIENTE_PK                                            */
/*==============================================================*/
create unique index CLIENTE_PK on CLIENTE (
ID_CLIENTE
);

/*==============================================================*/
/* Table: DETALLE_DE_FACTURA                                    */
/*==============================================================*/
create table DETALLE_DE_FACTURA (
   ID_DETALLE           INT4                 not null,
   ID_FACTURA           INT4                 not null,
   ID_PRODUCTO          INT4                 not null,
   CANTIDAD             INT4                 null,
   SUBTOTAL             DECIMAL(10,2)        null,
   constraint PK_DETALLE_DE_FACTURA primary key (ID_DETALLE)
);

/*==============================================================*/
/* Index: DETALLE_DE_FACTURA_PK                                 */
/*==============================================================*/
create unique index DETALLE_DE_FACTURA_PK on DETALLE_DE_FACTURA (
ID_DETALLE
);

/*==============================================================*/
/* Index: CONTIENE_FK                                           */
/*==============================================================*/
create  index CONTIENE_FK on DETALLE_DE_FACTURA (
ID_FACTURA
);

/*==============================================================*/
/* Index: ES_PARTE_DE_FK                                        */
/*==============================================================*/
create  index ES_PARTE_DE_FK on DETALLE_DE_FACTURA (
ID_PRODUCTO
);

/*==============================================================*/
/* Table: FACTURA                                               */
/*==============================================================*/
create table FACTURA (
   ID_FACTURA           INT4                 not null,
   ID_CLIENTE           INT4                 not null,
   FECHA                DATE                 null,
   TOTAL                DECIMAL(10,2)        null,
   FORMAPAGO            VARCHAR(10)          null,
   DESCUENTO            DECIMAL(10,2)        null,
   constraint PK_FACTURA primary key (ID_FACTURA)
);

/*==============================================================*/
/* Index: FACTURA_PK                                            */
/*==============================================================*/
create unique index FACTURA_PK on FACTURA (
ID_FACTURA
);

/*==============================================================*/
/* Index: GENERA_FK                                             */
/*==============================================================*/
create  index GENERA_FK on FACTURA (
ID_CLIENTE
);

/*==============================================================*/
/* Table: PRODUCTO                                              */
/*==============================================================*/
create table PRODUCTO (
   ID_PRODUCTO          INT4                 not null,
   NOMBRE               VARCHAR(50)          null,
   PRECIO               DECIMAL(10,2)        null,
   constraint PK_PRODUCTO primary key (ID_PRODUCTO)
);

/*==============================================================*/
/* Index: PRODUCTO_PK                                           */
/*==============================================================*/
create unique index PRODUCTO_PK on PRODUCTO (
ID_PRODUCTO
);

alter table DETALLE_DE_FACTURA
   add constraint FK_DETALLE__CONTIENE_FACTURA foreign key (ID_FACTURA)
      references FACTURA (ID_FACTURA)
      on delete restrict on update restrict;

alter table DETALLE_DE_FACTURA
   add constraint FK_DETALLE__ES_PARTE__PRODUCTO foreign key (ID_PRODUCTO)
      references PRODUCTO (ID_PRODUCTO)
      on delete restrict on update restrict;

alter table FACTURA
   add constraint FK_FACTURA_GENERA_CLIENTE foreign key (ID_CLIENTE)
      references CLIENTE (ID_CLIENTE)
      on delete restrict on update restrict;

