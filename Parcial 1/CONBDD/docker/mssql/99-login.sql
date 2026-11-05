-- Login de la aplicacion para eurekabank_ti24 (TI.2.4): MONSTER / MONSTER9, igual que las bases MySQL.
-- CHECK_POLICY = OFF porque MONSTER9 no cumple la politica de complejidad de SQL Server.
-- Es idempotente: se ejecuta en cada "docker compose up" (tambien sobre volumenes ya existentes).
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'MONSTER')
    CREATE LOGIN [MONSTER] WITH PASSWORD = N'MONSTER9', CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF, DEFAULT_DATABASE = [eurekabank_ti24];
GO
USE [eurekabank_ti24];
GO
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'MONSTER')
    CREATE USER [MONSTER] FOR LOGIN [MONSTER];
GO
ALTER ROLE db_owner ADD MEMBER [MONSTER];
GO
