/* ===========================================================
   0) CREAR BASE DE DATOS
   =========================================================== */
IF DB_ID('eurekabank_ti24') IS NOT NULL
BEGIN
  ALTER DATABASE eurekabank_ti24 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
  DROP DATABASE eurekabank_ti24;
END;
GO
CREATE DATABASE eurekabank_ti24;
GO
ALTER DATABASE eurekabank_ti24 SET RECOVERY SIMPLE;
GO
USE eurekabank_ti24;
GO

/* ===========================================================
   1) TABLAS (mismos nombres/columnas que tu código)
   =========================================================== */

-- MONEDA
CREATE TABLE dbo.moneda(
  chr_monecodigo  CHAR(2)   NOT NULL PRIMARY KEY,
  vch_monedescripcion VARCHAR(30) NOT NULL
);

-- SUCURSAL
CREATE TABLE dbo.sucursal(
  chr_sucucodigo  CHAR(3)   NOT NULL PRIMARY KEY,
  vch_sucunombre  VARCHAR(50) NOT NULL,
  vch_sucuciudad  VARCHAR(30) NOT NULL,
  vch_sucudireccion VARCHAR(50) NULL,
  int_sucucontcuenta INT NOT NULL DEFAULT(0)
);

-- EMPLEADO
CREATE TABLE dbo.empleado(
  chr_emplcodigo  CHAR(4)   NOT NULL PRIMARY KEY,
  vch_emplpaterno VARCHAR(25) NOT NULL,
  vch_emplmaterno VARCHAR(25) NOT NULL,
  vch_emplnombre  VARCHAR(30) NOT NULL,
  vch_emplciudad  VARCHAR(30) NOT NULL,
  vch_empldireccion VARCHAR(50) NULL
);

-- CLIENTE
CREATE TABLE dbo.cliente(
  chr_cliecodigo  CHAR(5)   NOT NULL PRIMARY KEY,
  vch_cliepaterno VARCHAR(25) NOT NULL,
  vch_cliematerno VARCHAR(25) NOT NULL,
  vch_clienombre  VARCHAR(30) NOT NULL,
  vch_cliedni     CHAR(8)     NOT NULL,
  vch_clieciudad  VARCHAR(30) NOT NULL,
  vch_cliedireccion VARCHAR(50) NULL
);

-- CUENTA
CREATE TABLE dbo.cuenta(
  chr_cuencodigo        CHAR(8)  NOT NULL PRIMARY KEY,
  chr_monecodigo        CHAR(2)  NOT NULL,
  chr_sucucodigo        CHAR(3)  NOT NULL,
  chr_emplcreacuenta    CHAR(4)  NOT NULL,
  chr_cliecodigo        CHAR(5)  NOT NULL,
  dec_cuensaldo         DECIMAL(12,2) NOT NULL CONSTRAINT DF_cuenta_saldo DEFAULT(0),
  dtt_cuenfechacreacion DATE NOT NULL CONSTRAINT DF_cuenta_fecha DEFAULT (CONVERT(date, SYSDATETIME())),
  vch_cuenestado        VARCHAR(15) NOT NULL CONSTRAINT DF_cuenta_estado DEFAULT('ACTIVO'),
  int_cuencontmov       INT NOT NULL CONSTRAINT DF_cuenta_cont DEFAULT(0),
  chr_cuenclave         CHAR(6) NOT NULL,
  CONSTRAINT FK_cuenta_moneda   FOREIGN KEY(chr_monecodigo)     REFERENCES dbo.moneda(chr_monecodigo),
  CONSTRAINT FK_cuenta_sucursal FOREIGN KEY(chr_sucucodigo)     REFERENCES dbo.sucursal(chr_sucucodigo),
  CONSTRAINT FK_cuenta_empleado FOREIGN KEY(chr_emplcreacuenta) REFERENCES dbo.empleado(chr_emplcodigo),
  CONSTRAINT FK_cuenta_cliente  FOREIGN KEY(chr_cliecodigo)     REFERENCES dbo.cliente(chr_cliecodigo)
);

-- TIPOMOVIMIENTO (con la columna que usas para el acumulado)
CREATE TABLE dbo.tipomovimiento(
  chr_tipocodigo      CHAR(3)    NOT NULL PRIMARY KEY,
  vch_tipodescripcion VARCHAR(40) NOT NULL,
  vch_tipoaccion      VARCHAR(10) NOT NULL,   -- 'INGRESO' | 'SALIDA'
  vch_tipoestado      VARCHAR(15) NOT NULL CONSTRAINT DF_tipomov_estado DEFAULT('ACTIVO'),
  CONSTRAINT CK_tipomov_accion  CHECK (vch_tipoaccion IN ('INGRESO','SALIDA')),
  CONSTRAINT CK_tipomov_estado  CHECK (vch_tipoestado IN ('ACTIVO','ANULADO','CANCELADO'))
);

-- MOVIMIENTO (PK compuesta cuenta+numero)
CREATE TABLE dbo.movimiento(
  chr_cuencodigo    CHAR(8)  NOT NULL,
  int_movinumero    INT      NOT NULL,
  dtt_movifecha     DATETIME2(0) NOT NULL,
  chr_emplcodigo    CHAR(4)  NOT NULL,
  chr_tipocodigo    CHAR(3)  NOT NULL,
  dec_moviimporte   DECIMAL(12,2) NOT NULL,
  chr_cuenreferencia CHAR(8) NULL,
  CONSTRAINT PK_movimiento PRIMARY KEY (chr_cuencodigo, int_movinumero),
  CONSTRAINT FK_mov_cuenta  FOREIGN KEY(chr_cuencodigo) REFERENCES dbo.cuenta(chr_cuencodigo),
  CONSTRAINT FK_mov_empleado FOREIGN KEY(chr_emplcodigo) REFERENCES dbo.empleado(chr_emplcodigo),
  CONSTRAINT FK_mov_tipomov FOREIGN KEY(chr_tipocodigo) REFERENCES dbo.tipomovimiento(chr_tipocodigo),
  CONSTRAINT CK_mov_importe CHECK (dec_moviimporte >= 0)
);

-- USUARIO (para login app, vinculado a empleado)
CREATE TABLE dbo.usuario(
  chr_emplcodigo   CHAR(4)    NOT NULL PRIMARY KEY,
  vch_emplusuario  VARCHAR(20) NOT NULL UNIQUE,
  vch_emplclave    VARCHAR(64) NOT NULL,   -- guardaremos SHA2_256 hex
  vch_emplestado   VARCHAR(15) NOT NULL CONSTRAINT DF_usuario_estado DEFAULT('ACTIVO'),
  CONSTRAINT FK_usuario_empleado FOREIGN KEY(chr_emplcodigo) REFERENCES dbo.empleado(chr_emplcodigo),
  CONSTRAINT CK_usuario_estado CHECK (vch_emplestado IN ('ACTIVO','ANULADO','CANCELADO'))
);

-- (Opcional) credenciales para otros tests
CREATE TABLE dbo.credenciales(
  id INT IDENTITY(1,1) PRIMARY KEY,
  usuario    VARCHAR(255) NOT NULL,
  contrasena VARCHAR(255) NOT NULL
);
GO




USE eurekabank_ti24;
GO

-- MODULO
IF OBJECT_ID('dbo.modulo','U') IS NULL
CREATE TABLE dbo.modulo(
  int_moducodigo   INT NOT NULL PRIMARY KEY,
  vch_modunombre   VARCHAR(50) NULL,
  vch_moduestado   VARCHAR(15) NOT NULL CONSTRAINT DF_modulo_estado DEFAULT('ACTIVO'),
  CONSTRAINT CK_modulo_estado CHECK (vch_moduestado IN ('ACTIVO','ANULADO','CANCELADO'))
);

-- PERMISO
IF OBJECT_ID('dbo.permiso','U') IS NULL
CREATE TABLE dbo.permiso(
  chr_emplcodigo   CHAR(4) NOT NULL,
  int_moducodigo   INT NOT NULL,
  vch_permestado   VARCHAR(15) NOT NULL CONSTRAINT DF_permiso_estado DEFAULT('ACTIVO'),
  CONSTRAINT PK_permiso PRIMARY KEY(chr_emplcodigo,int_moducodigo),
  CONSTRAINT FK_permiso_modulo  FOREIGN KEY(int_moducodigo) REFERENCES dbo.modulo(int_moducodigo),
  CONSTRAINT FK_permiso_usuario FOREIGN KEY(chr_emplcodigo)  REFERENCES dbo.usuario(chr_emplcodigo),
  CONSTRAINT CK_permiso_estado CHECK (vch_permestado IN ('ACTIVO','ANULADO','CANCELADO'))
);

-- LOGSESSION
IF OBJECT_ID('dbo.logsession','U') IS NULL
CREATE TABLE dbo.logsession(
  ID             INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  chr_emplcodigo CHAR(4) NOT NULL,
  fec_ingreso    DATETIME2(0) NOT NULL,
  fec_salida     DATETIME2(0) NULL,
  ip             VARCHAR(20)  NOT NULL CONSTRAINT DF_logsession_ip DEFAULT('NONE'),
  hostname       VARCHAR(100) NOT NULL CONSTRAINT DF_logsession_host DEFAULT('NONE'),
  CONSTRAINT FK_logsession_empleado FOREIGN KEY(chr_emplcodigo) REFERENCES dbo.empleado(chr_emplcodigo)
);

-- ASIGNADO
IF OBJECT_ID('dbo.asignado','U') IS NULL
CREATE TABLE dbo.asignado(
  chr_asigcodigo     CHAR(6) NOT NULL PRIMARY KEY,
  chr_sucucodigo     CHAR(3) NOT NULL,
  chr_emplcodigo     CHAR(4) NOT NULL,
  dtt_asigfechaalta  DATE NOT NULL,
  dtt_asigfechabaja  DATE NULL,
  CONSTRAINT FK_asignado_empleado FOREIGN KEY(chr_emplcodigo) REFERENCES dbo.empleado(chr_emplcodigo),
  CONSTRAINT FK_asignado_sucursal FOREIGN KEY(chr_sucucodigo) REFERENCES dbo.sucursal(chr_sucucodigo)
);

-- PARAMETRO
IF OBJECT_ID('dbo.parametro','U') IS NULL
CREATE TABLE dbo.parametro(
  chr_paracodigo      CHAR(3) NOT NULL PRIMARY KEY,
  vch_paradescripcion VARCHAR(50) NOT NULL,
  vch_paravalor       VARCHAR(70) NOT NULL,
  vch_paraestado      VARCHAR(15) NOT NULL CONSTRAINT DF_parametro_estado DEFAULT('ACTIVO'),
  CONSTRAINT CK_parametro_estado CHECK (vch_paraestado IN ('ACTIVO','ANULADO','CANCELADO'))
);

-- INTERESMENSUAL
IF OBJECT_ID('dbo.interesmensual','U') IS NULL
CREATE TABLE dbo.interesmensual(
  chr_monecodigo  CHAR(2) NOT NULL PRIMARY KEY,
  dec_inteimporte DECIMAL(12,2) NOT NULL,
  CONSTRAINT FK_interes_moneda FOREIGN KEY(chr_monecodigo) REFERENCES dbo.moneda(chr_monecodigo)
);

-- COSTOMOVIMIENTO
IF OBJECT_ID('dbo.costomovimiento','U') IS NULL
CREATE TABLE dbo.costomovimiento(
  chr_monecodigo  CHAR(2) NOT NULL PRIMARY KEY,
  dec_costimporte DECIMAL(12,2) NOT NULL,
  CONSTRAINT FK_costomov_moneda FOREIGN KEY(chr_monecodigo) REFERENCES dbo.moneda(chr_monecodigo)
);

-- CARGOMANTENIMIENTO
IF OBJECT_ID('dbo.cargomantenimiento','U') IS NULL
CREATE TABLE dbo.cargomantenimiento(
  chr_monecodigo     CHAR(2) NOT NULL PRIMARY KEY,
  dec_cargMontoMaximo DECIMAL(12,2) NOT NULL,
  dec_cargImporte     DECIMAL(12,2) NOT NULL,
  CONSTRAINT FK_cargomant_moneda FOREIGN KEY(chr_monecodigo) REFERENCES dbo.moneda(chr_monecodigo)
);

-- CONTADOR
IF OBJECT_ID('dbo.contador','U') IS NULL
CREATE TABLE dbo.contador(
  vch_conttabla    VARCHAR(30) NOT NULL PRIMARY KEY,
  int_contitem     INT NOT NULL,
  int_contlongitud INT NOT NULL
);
