-- Runs once on the first "docker compose up": creates the retailer database next to "banquito".
-- Its tables are created by the TypeORM migrations of comercializadora-api.
CREATE DATABASE comercializadora OWNER gr8;
