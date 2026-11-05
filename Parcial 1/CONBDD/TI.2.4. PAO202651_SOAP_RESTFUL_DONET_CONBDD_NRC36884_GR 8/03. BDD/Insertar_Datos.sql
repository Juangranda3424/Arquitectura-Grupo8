USE eurekabank_ti24;
GO

-- (Quitar) SET NAMES utf8;   -- <-- MySQL, no va en SQL Server

-- MONEDA
INSERT INTO dbo.moneda (chr_monecodigo, vch_monedescripcion) VALUES ('01', 'Soles');
INSERT INTO dbo.moneda (chr_monecodigo, vch_monedescripcion) VALUES ('02', 'Dolares');

-- CARGO MANTENIMIENTO
INSERT INTO dbo.cargomantenimiento (chr_monecodigo, dec_cargMontoMaximo, dec_cargImporte) VALUES ('01', 3500.00, 7.00);
INSERT INTO dbo.cargomantenimiento (chr_monecodigo, dec_cargMontoMaximo, dec_cargImporte) VALUES ('02', 1200.00, 2.50);

-- COSTO MOVIMIENTO
INSERT INTO dbo.costomovimiento (chr_monecodigo, dec_costimporte) VALUES ('01', 2.00);
INSERT INTO dbo.costomovimiento (chr_monecodigo, dec_costimporte) VALUES ('02', 0.60);

-- INTERES MENSUAL
INSERT INTO dbo.interesmensual (chr_monecodigo, dec_inteimporte) VALUES ('01', 0.70);
INSERT INTO dbo.interesmensual (chr_monecodigo, dec_inteimporte) VALUES ('02', 0.60);

-- TIPO MOVIMIENTO
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('001', 'Apertura de Cuenta', 'INGRESO', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('002', 'Cancelar Cuenta', 'SALIDA', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('003', 'Deposito', 'INGRESO', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('004', 'Retiro', 'SALIDA', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('005', 'Interes', 'INGRESO', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('006', 'Mantenimiento', 'SALIDA', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('007', 'ITF', 'SALIDA', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('008', 'Transferencia', 'INGRESO', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('009', 'Transferencia', 'SALIDA', 'ACTIVO');
INSERT INTO dbo.tipomovimiento (chr_tipocodigo, vch_tipodescripcion, vch_tipoaccion, vch_tipoestado) VALUES ('010', 'Cargo por Movimiento', 'SALIDA', 'ACTIVO');

-- SUCURSAL
INSERT INTO dbo.sucursal (chr_sucucodigo, vch_sucunombre, vch_sucuciudad, vch_sucudireccion, int_sucucontcuenta) VALUES
('001', 'Sipan', 'Chiclayo', 'Av. Balta 1456', 2),
('002', 'Chan Chan', 'Trujillo', 'Jr. Independencia 456', 3),
('003', 'Los Olivos', 'Lima', 'Av. Central 1234', 0),
('004', 'Pardo', 'Lima', 'Av. Pardo 345 - Miraflores', 0),
('005', 'Misti', 'Arequipa', 'Bolivar 546', 0),
('006', 'Machupicchu', 'Cusco', 'Calle El Sol 534', 0),
('007', 'Grau', 'Piura', 'Av. Grau 1528', 0);

-- EMPLEADO
INSERT INTO dbo.empleado (chr_emplcodigo, vch_emplpaterno, vch_emplmaterno, vch_emplnombre, vch_emplciudad, vch_empldireccion) VALUES
('9999', 'Internet', 'Internet', 'internet', 'Internet', 'internet'),
('0001', 'Romero', 'Castillo', 'Carlos Alberto', 'Trujillo', 'Call1 1 Nro. 456'),
('0002', 'Castro', 'Vargas', 'Lidia', 'Lima', 'Federico Villarreal 456 - SMP'),
('0003', 'Reyes', 'Ortiz', 'Claudia', 'Lima', 'Av. Aviaci�n 3456 - San Borja'),
('0004', 'Ramos', 'Garibay', 'Angelica', 'Chiclayo', 'Calle Barcelona 345'),
('0005', 'Ruiz', 'Zabaleta', 'Claudia', 'Cusco', 'Calle Cruz Verde 364'),
('0006', 'Cruz', 'Tarazona', 'Ricardo', 'Arequipa', 'Calle La Gruta 304'),
('0007', 'Diaz', 'Flores', 'Edith', 'Lima', 'Av. Pardo 546'),
('0008', 'Sarmiento', 'Bellido', 'Claudia Rocio', 'Arequipa', 'Calle Alfonso Ugarte 1567'),
('0009', 'Pachas', 'Sifuentes', 'Luis Alberto', 'Trujillo', 'Francisco Pizarro 1263'),
('0010', 'Tello', 'Alarcon', 'Hugo Valentin', 'Cusco', 'Los Angeles 865'),
('0011', 'Carrasco', 'Vargas', 'Pedro Hugo', 'Chiclayo', 'Av. Balta 1265'),
('0012', 'Mendoza', 'Jara', 'Monica Valeria', 'Lima', 'Calle Las Toronjas 450'),
('0013', 'Espinoza', 'Melgar', 'Victor Eduardo', 'Huancayo', 'Av. San Martin 6734 Dpto. 508'),
('0014', 'Hidalgo', 'Sandoval', 'Milagros Leonor', 'Chiclayo', 'Av. Luis Gonzales 1230');

-- USUARIO (reemplaza SHA() por HASHBYTES + CONVERT a HEX)
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado) VALUES
('9999', 'internet',  CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','internet'), 2), 'ACTIVO'),
('0001', 'cromero',   CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','chicho'),   2), 'ACTIVO'),
('0002', 'lcastro',   CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','flaca'),    2), 'ACTIVO'),
('0003', 'creyes',    CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','linda'),    2), 'ANULADO'),
('0004', 'aramos',    CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','china'),    2), 'ACTIVO'),
('0005', 'cvalencia', CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','angel'),    2), 'ACTIVO'),
('0006', 'rcruz',     CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','cerebro'),  2), 'ACTIVO'),
('0007', 'ediaz',     CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','princesa'), 2), 'ANULADO'),
('0008', 'csarmiento',CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','chinita'),  2), 'ANULADO'),
('0009', 'lpachas',   CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','gato'),     2), 'ACTIVO'),
('0010', 'htello',    CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','machupichu'),2),'ACTIVO'),
('0011', 'pcarrasco', CONVERT(VARCHAR(64), HASHBYTES('SHA2_256','tinajones'),2),'ACTIVO');

-- MODULO
INSERT INTO dbo.modulo (int_moducodigo, vch_modunombre, vch_moduestado) VALUES
(1, 'Procesos', 'ACTIVO'), (2, 'Tablas', 'ACTIVO'), (3, 'Consultas', 'ACTIVO'),
(4, 'Reportes', 'ACTIVO'), (5, 'Util', 'ACTIVO'), (6, 'Seguridad', 'ACTIVO');

-- PERMISO
INSERT INTO dbo.permiso (chr_emplcodigo, int_moducodigo, vch_permestado) VALUES
('0001',1,'ACTIVO'),('0001',2,'ACTIVO'),('0001',3,'ACTIVO'),('0001',4,'ACTIVO'),('0001',5,'ACTIVO'),('0001',6,'ACTIVO'),
('0002',1,'ACTIVO'),('0002',2,'ACTIVO'),('0002',3,'ACTIVO'),('0002',4,'ACTIVO'),('0002',5,'CANCELADO'),('0002',6,'CANCELADO'),
('0003',1,'ACTIVO'),('0003',2,'CANCELADO'),('0003',3,'ACTIVO'),('0003',4,'ACTIVO'),('0003',5,'ACTIVO'),('0003',6,'CANCELADO'),
('0004',1,'CANCELADO'),('0004',2,'ACTIVO'),('0004',3,'ACTIVO'),('0004',4,'CANCELADO'),('0004',5,'ACTIVO'),('0004',6,'CANCELADO'),
('0005',1,'ACTIVO'),('0005',2,'CANCELADO'),('0005',3,'ACTIVO'),('0005',4,'ACTIVO'),('0005',5,'ACTIVO'),('0005',6,'CANCELADO'),
('0006',1,'ACTIVO'),('0006',2,'ACTIVO'),('0006',3,'ACTIVO'),('0006',4,'ACTIVO'),('0006',5,'ACTIVO'),('0006',6,'ACTIVO'),
('0007',1,'CANCELADO'),('0007',2,'ACTIVO'),('0007',3,'ACTIVO'),('0007',4,'CANCELADO'),('0007',5,'ACTIVO'),('0007',6,'CANCELADO');

-- ASIGNADO
INSERT INTO dbo.asignado (chr_asigcodigo, chr_sucucodigo, chr_emplcodigo, dtt_asigfechaalta, dtt_asigfechabaja) VALUES
('000001','001','0004','2007-11-15',NULL),
('000002','002','0001','2007-11-20',NULL),
('000003','003','0002','2007-11-28',NULL),
('000004','004','0003','2007-12-12','2008-03-25'),
('000005','005','0006','2007-12-20',NULL),
('000006','006','0005','2008-01-05',NULL),
('000007','004','0007','2008-01-07',NULL),
('000008','005','0008','2008-01-07',NULL),
('000009','001','0011','2008-01-08',NULL),
('000010','002','0009','2008-01-08',NULL),
('000011','006','0010','2008-01-08',NULL);

-- PARAMETRO
INSERT INTO dbo.parametro (chr_paracodigo, vch_paradescripcion, vch_paravalor, vch_paraestado) VALUES
('001','ITF - Impuesto a la Transacciones Financieras','0.08','ACTIVO'),
('002','N�mero de Operaciones Sin Costo','15','ACTIVO');

-- CLIENTE
-- CLIENTES
INSERT INTO dbo.cliente
  (chr_cliecodigo, vch_cliepaterno, vch_cliematerno, vch_clienombre,
   vch_cliedni, vch_clieciudad, vch_cliedireccion)
VALUES
('00001','CORONEL','CASTILLO','ERIC GUSTAVO','06914897','LIMA','LOS OLIVOS'),
('00002','VALENCIA','MORALES','PEDRO HUGO','01576173','LIMA','MAGDALENA'),
('00003','MARCELO','VILLALOBOS','RICARDO','10762367','LIMA','LINCE'),
('00004','ROMERO','CASTILLO','CARLOS ALBERTO','06531983','LIMA','LOS OLIVOS'),
('00005','ARANDA','LUNA','ALAN ALBERTO','10875611','LIMA','SAN ISIDRO'),
('00006','AYALA','PAZ','JORGE LUIS','10679245','LIMA','SAN BORJA'),
('00007','CHAVEZ','CANALES','EDGAR RAFAEL','10145693','LIMA','MIRAFLORES'),
('00008','FLORES','CHAFLOQUE','ROSA LIZET','10773456','LIMA','LA MOLINA'),
('00009','FLORES','CASTILLO','CRISTIAN RAFAEL','10346723','LIMA','LOS OLIVOS'),
('00010','GONZALES','GARCIA','GABRIEL ALEJANDRO','10192376','LIMA','SAN MIGUEL'),
('00011','LAY','VALLEJOS','JUAN CARLOS','10942287','LIMA','LINCE'),
('00012','MONTALVO','SOTO','DEYSI LIDIA','10612376','LIMA','SURCO'),
('00013','RICALDE','RAMIREZ','ROSARIO ESMERALDA','10761324','LIMA','MIRAFLORES'),
('00014','RODRIGUEZ','FLORES','ENRIQUE MANUEL','10773345','LIMA','LINCE'),
('00015','ROJAS','OSCANOA','FELIX NINO','10238943','LIMA','LIMA'),
('00016','TEJADA','DEL AGUILA','TANIA LORENA','10446791','LIMA','PUEBLO LIBRE'),
('00017','VALDEVIESO','LEYVA','LIDIA ROXANA','10452682','LIMA','SURCO'),
('00018','VALENTIN','COTRINA','JUAN DIEGO','10398247','LIMA','LA MOLINA'),
('00019','YAURICASA','BAUTISTA','YESABETH','10934584','LIMA','MAGDALENA'),
('00020','ZEGARRA','GARCIA','FERNANDO MOISES','10772365','LIMA','SAN ISIDRO');


-- CUENTA
INSERT INTO dbo.cuenta (chr_cuencodigo, chr_monecodigo, chr_sucucodigo, chr_emplcreacuenta, chr_cliecodigo, dec_cuensaldo, dtt_cuenfechacreacion, vch_cuenestado, int_cuencontmov, chr_cuenclave) VALUES
('00200001','01','002','0001','00008',7000,'2008-01-05','ACTIVO',15,'123456'),
('00200002','01','002','0001','00001',6800,'2008-01-09','ACTIVO',3,'123456'),
('00200003','02','002','0001','00007',6000,'2008-01-11','ACTIVO',6,'123456'),
('00100001','01','001','0004','00005',6900,'2008-01-06','ACTIVO',7,'123456'),
('00100002','02','001','0004','00005',4500,'2008-01-08','ACTIVO',4,'123456'),
('00300001','01','003','0002','00010',0,'2008-01-07','CANCELADO',3,'123456');

-- MOVIMIENTO
INSERT INTO dbo.movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES
('00100002',1,'2008-01-08','0004','001',1800,NULL),
('00100002',2,'2008-01-25','0004','004',1000,NULL),
('00100002',3,'2008-02-13','0004','003',2200,NULL),
('00100002',4,'2008-03-08','0004','003',1500,NULL),

('00100001',1,'2008-01-06','0004','001',2800,NULL),
('00100001',2,'2008-01-15','0004','003',3200,NULL),
('00100001',3,'2008-01-20','0004','004',800,NULL),
('00100001',4,'2008-02-14','0004','003',2000,NULL),
('00100001',5,'2008-02-25','0004','004',500,NULL),
('00100001',6,'2008-03-03','0004','004',800,NULL),
('00100001',7,'2008-03-15','0004','003',1000,NULL),

('00200003',1,'2008-01-11','0001','001',2500,NULL),
('00200003',2,'2008-01-17','0001','003',1500,NULL),
('00200003',3,'2008-01-20','0001','004',500,NULL),
('00200003',4,'2008-02-09','0001','004',500,NULL),
('00200003',5,'2008-02-25','0001','003',3500,NULL),
('00200003',6,'2008-03-11','0001','004',500,NULL),

('00200002',1,'2008-01-09','0001','001',3800,NULL),
('00200002',2,'2008-01-20','0001','003',4200,NULL),
('00200002',3,'2008-03-06','0001','004',1200,NULL),

('00200001',1,'2008-01-05','0001','001',5000,NULL),
('00200001',2,'2008-01-07','0001','003',4000,NULL),
('00200001',3,'2008-01-09','0001','004',2000,NULL),
('00200001',4,'2008-01-11','0001','003',1000,NULL),
('00200001',5,'2008-01-13','0001','003',2000,NULL),
('00200001',6,'2008-01-15','0001','004',4000,NULL),
('00200001',7,'2008-01-19','0001','003',2000,NULL),
('00200001',8,'2008-01-21','0001','004',3000,NULL),
('00200001',9,'2008-01-23','0001','003',7000,NULL),
('00200001',10,'2008-01-27','0001','004',1000,NULL),
('00200001',11,'2008-01-30','0001','004',3000,NULL),
('00200001',12,'2008-02-04','0001','003',2000,NULL),
('00200001',13,'2008-02-08','0001','004',4000,NULL),
('00200001',14,'2008-02-13','0001','003',2000,NULL),
('00200001',15,'2008-02-19','0001','004',1000,NULL);

-- CONTADOR
INSERT INTO dbo.contador (vch_conttabla, int_contitem, int_contlongitud) VALUES
('Moneda', 2, 2),
('TipoMovimiento', 10, 3),
('Sucursal', 7, 3),
('Empleado', 14, 4),
('Asignado', 11, 6),
('Parametro', 2, 3),
('Cliente', 20, 5);

-- CREDENCIALES (contrasena = MD5('MONSTER9'), same as the MySQL databases)
INSERT INTO dbo.credenciales (usuario, contrasena) VALUES
('MONSTER', '6c3f6757e773775fd059e2f025bd14ba');
