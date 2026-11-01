-- Lets the application user (created by MYSQL_USER as 'MONSTER'@'%') reach every
-- project database from the host; the seed scripts only grant 'MONSTER'@'localhost'.
GRANT ALL PRIVILEGES ON `eurekabank_ti21`.* TO 'MONSTER'@'%';
GRANT ALL PRIVILEGES ON `eurekabank_ti22`.* TO 'MONSTER'@'%';
GRANT ALL PRIVILEGES ON `eurekabank_ti23`.* TO 'MONSTER'@'%';
FLUSH PRIVILEGES;
