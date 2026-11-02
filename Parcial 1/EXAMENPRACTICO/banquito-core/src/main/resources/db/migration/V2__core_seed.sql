-- Seed data. Every date is relative to the day the migration runs, so the
-- "last month" / "last 3 months" rules hold on demo day after a fresh reset
-- (docker compose down -v && docker compose up -d, then start banquito-core).
--
--  #  Client          Expected result
--  1  Ana Torres      subject of credit, max 1706.25
--  2  Carlos Mena     subject of credit, max  840.00
--  3  Maria Lopez     subject of credit, max  525.00 (female under 25: rule 3 only applies to men)
--  4  Jorge Paredes   subject of credit, max 2520.00
--  5  Luis Ortiz      NOT a subject: male under 25            (rule 3)
--  6  Sofia Vega      NOT a subject: no deposit in last month (rule 2)
--  7  Pedro Salazar   NOT a subject: already has an active credit (rule 4)
--  Rule 1 (not a client): any cedula not listed here, e.g. 1799999999.

INSERT INTO cliente (cod_cliente, cedula, nombre, genero, fecha_nacimiento) VALUES
    (1, '1712345678', 'Ana Torres',    'F', CURRENT_DATE - INTERVAL '30 years 3 months'),
    (2, '1723456789', 'Carlos Mena',   'M', CURRENT_DATE - INTERVAL '40 years 1 month'),
    (3, '1734567890', 'Maria Lopez',   'F', CURRENT_DATE - INTERVAL '22 years 5 months'),
    (4, '1745678901', 'Jorge Paredes', 'M', CURRENT_DATE - INTERVAL '28 years 7 months'),
    (5, '1756789012', 'Luis Ortiz',    'M', CURRENT_DATE - INTERVAL '23 years 2 months'),
    (6, '1767890123', 'Sofia Vega',    'F', CURRENT_DATE - INTERVAL '35 years 4 months'),
    (7, '1778901234', 'Pedro Salazar', 'M', CURRENT_DATE - INTERVAL '45 years 6 months');

INSERT INTO cuenta (num_cuenta, cod_cliente, saldo) VALUES
    ('10000001', 1, 4850.00),
    ('10000002', 2, 2100.00),
    ('10000003', 3, 1250.00),
    ('10000004', 4, 6300.00),
    ('10000005', 5, 1100.00),
    ('10000006', 6, 7400.00),
    ('10000007', 7, 2100.00);

INSERT INTO movimiento (num_cuenta, tipo, valor, fecha) VALUES
    -- Ana: avg DEP 1212.50, avg RET 400.00 (the 100-day-old deposit is outside the 3-month window)
    ('10000001', 'DEP', 1200.00, CURRENT_DATE - 5),
    ('10000001', 'RET',  400.00, CURRENT_DATE - 10),
    ('10000001', 'DEP', 1250.00, CURRENT_DATE - 20),
    ('10000001', 'DEP', 1100.00, CURRENT_DATE - 35),
    ('10000001', 'RET',  350.00, CURRENT_DATE - 40),
    ('10000001', 'RET',  400.00, CURRENT_DATE - 50),
    ('10000001', 'DEP', 1300.00, CURRENT_DATE - 65),
    ('10000001', 'RET',  450.00, CURRENT_DATE - 70),
    ('10000001', 'DEP', 9999.00, CURRENT_DATE - 100),
    -- Carlos: avg DEP 900.00, avg RET 500.00
    ('10000002', 'DEP',  900.00, CURRENT_DATE - 3),
    ('10000002', 'RET',  500.00, CURRENT_DATE - 8),
    ('10000002', 'DEP',  900.00, CURRENT_DATE - 15),
    ('10000002', 'DEP',  950.00, CURRENT_DATE - 33),
    ('10000002', 'RET',  520.00, CURRENT_DATE - 38),
    ('10000002', 'RET',  500.00, CURRENT_DATE - 45),
    ('10000002', 'DEP',  850.00, CURRENT_DATE - 63),
    ('10000002', 'RET',  480.00, CURRENT_DATE - 68),
    -- Maria: avg DEP 600.00, avg RET 350.00
    ('10000003', 'DEP',  600.00, CURRENT_DATE - 7),
    ('10000003', 'RET',  350.00, CURRENT_DATE - 12),
    ('10000003', 'DEP',  600.00, CURRENT_DATE - 25),
    ('10000003', 'DEP',  620.00, CURRENT_DATE - 37),
    ('10000003', 'RET',  340.00, CURRENT_DATE - 42),
    ('10000003', 'RET',  350.00, CURRENT_DATE - 55),
    ('10000003', 'DEP',  580.00, CURRENT_DATE - 67),
    ('10000003', 'RET',  360.00, CURRENT_DATE - 72),
    -- Jorge: avg DEP 2000.00, avg RET 800.00
    ('10000004', 'DEP', 2000.00, CURRENT_DATE - 2),
    ('10000004', 'RET',  800.00, CURRENT_DATE - 9),
    ('10000004', 'DEP', 2000.00, CURRENT_DATE - 18),
    ('10000004', 'DEP', 2100.00, CURRENT_DATE - 32),
    ('10000004', 'RET',  750.00, CURRENT_DATE - 39),
    ('10000004', 'RET',  800.00, CURRENT_DATE - 48),
    ('10000004', 'DEP', 1900.00, CURRENT_DATE - 62),
    ('10000004', 'RET',  850.00, CURRENT_DATE - 69),
    -- Luis: recent deposits, but male under 25
    ('10000005', 'DEP',  700.00, CURRENT_DATE - 4),
    ('10000005', 'RET',  300.00, CURRENT_DATE - 14),
    ('10000005', 'DEP',  700.00, CURRENT_DATE - 34),
    ('10000005', 'RET',  300.00, CURRENT_DATE - 44),
    ('10000005', 'DEP',  650.00, CURRENT_DATE - 64),
    ('10000005', 'RET',  250.00, CURRENT_DATE - 74),
    -- Sofia: only withdrawals in the last month
    ('10000006', 'RET',  200.00, CURRENT_DATE - 5),
    ('10000006', 'RET',  250.00, CURRENT_DATE - 20),
    ('10000006', 'DEP', 1500.00, CURRENT_DATE - 40),
    ('10000006', 'RET',  300.00, CURRENT_DATE - 50),
    ('10000006', 'DEP', 1400.00, CURRENT_DATE - 70),
    ('10000006', 'DEP', 1450.00, CURRENT_DATE - 85),
    ('10000006', 'DEP', 5000.00, CURRENT_DATE - 120),
    -- Pedro: recent deposits, but an active credit (below)
    ('10000007', 'DEP', 1000.00, CURRENT_DATE - 6),
    ('10000007', 'RET',  300.00, CURRENT_DATE - 16),
    ('10000007', 'DEP', 1000.00, CURRENT_DATE - 36),
    ('10000007', 'RET',  300.00, CURRENT_DATE - 46),
    ('10000007', 'DEP',  900.00, CURRENT_DATE - 66),
    ('10000007', 'RET',  200.00, CURRENT_DATE - 76);

-- Pedro's active credit is the exam's sample amortization table (450.00, 6 months, 16.5%).
INSERT INTO credito (cod_cliente, monto, num_cuotas, tasa_anual, valor_cuota, fecha, estado)
VALUES (7, 450.00, 6, 16.50, 78.65, CURRENT_DATE - 20, 'ACT');

INSERT INTO tabla_amortizacion (cod_credito, num_cuota, valor_cuota, interes_pagado, capital_pagado, saldo)
SELECT c.cod_credito, t.num_cuota, t.valor_cuota, t.interes, t.capital, t.saldo
FROM credito c
CROSS JOIN (VALUES
    (1, 78.65, 6.19, 72.46, 377.54),
    (2, 78.65, 5.19, 73.46, 304.08),
    (3, 78.65, 4.18, 74.47, 229.61),
    (4, 78.65, 3.16, 75.49, 154.12),
    (5, 78.65, 2.12, 76.53,  77.59),
    (6, 78.65, 1.07, 77.58,   0.00)
) AS t (num_cuota, valor_cuota, interes, capital, saldo)
WHERE c.cod_cliente = 7;
