# Examen práctico — BanQuito Módulo de Crédito + Comercializadora de Celulares

Enunciado: [`docs/ExamenComplexivoPractivo_CELULARES.pdf`](docs/ExamenComplexivoPractivo_CELULARES.pdf).
Diagramas (casos de uso, ER, arquitectura): [`docs/diagramas/`](docs/diagramas/).

| Aplicación | Stack | Puerto | Rol |
|---|---|---|---|
| `banquito-core/` | Spring Boot 4, JPA, Flyway | 8080 | Core bancario: solo expone los servicios REST del módulo de crédito |
| `comercializadora-api/` | NestJS, TypeORM, Better Auth | 3001 | Catálogo, facturación y consumo de los servicios del banco |
| `comercializadora-web/` | Next.js | 3000 | Interfaz de la comercializadora (redirige `/api/*` a la API) |
| `docker-compose.yml` | PostgreSQL 17 | 5432 | Bases `banquito` y `comercializadora` (usuario `gr8` / `gr8pass`) |

## Cómo ejecutar

Requisitos: Docker, JDK 21, Node.js 20+.

```bash
# 1. Base de datos
docker compose up -d

# 2. Core bancario (crea y llena la base banquito con Flyway)
cd banquito-core && ./mvnw spring-boot:run

# 3. API de la comercializadora (corre las migraciones de TypeORM y crea el usuario demo)
cd comercializadora-api && npm install && npm run start

# 4. Web de la comercializadora
cd comercializadora-web && npm install && npm run dev
```

Abrir <http://localhost:3000> e ingresar con `vendedor@comercializadora.ec` / `Vendedor2026!`.

## Datos de prueba del Core

Las fechas de los movimientos se calculan respecto al día en que corre la migración, así las reglas
de "último mes" y "últimos 3 meses" se cumplen el día de la demo. Para volver al estado inicial:
`docker compose down -v && docker compose up -d` y arrancar de nuevo el core y la API.

| Cédula | Cliente | Resultado esperado |
|---|---|---|
| 1712345678 | Ana Torres | Sujeto de crédito, monto máximo 1706.25 |
| 1723456789 | Carlos Mena | Sujeto de crédito, monto máximo 840.00 |
| 1734567890 | Maria Lopez | Sujeto de crédito (mujer menor de 25), monto máximo 525.00 |
| 1745678901 | Jorge Paredes | Sujeto de crédito, monto máximo 2520.00 |
| 1756789012 | Luis Ortiz | No: masculino menor de 25 años |
| 1767890123 | Sofia Vega | No: sin depósitos en el último mes |
| 1778901234 | Pedro Salazar | No: ya tiene un crédito activo (la tabla de ejemplo del enunciado) |
| 1799999999 | — | No: no es cliente del banco |

## Reglas implementadas

- Monto máximo: `((promedio depósitos − promedio retiros de los últimos 3 meses) × 35%) × 6`, nunca menor a 0.
- Crédito: cuota fija mensual, 3 a 18 cuotas, 16.5% anual (`TasaPeriodo = 16.5% / 12`).
  La cuota se redondea a centavos y con ella se calculan interés, capital y saldo; así la tabla
  coincide al centavo con el ejemplo del enunciado (450.00, 6 cuotas → 78.65).
- Pago en efectivo: 42% de descuento sobre el precio de venta.
- Pago con crédito directo: la comercializadora verifica sujeto de crédito y monto máximo, pide el
  crédito al banco y solo entonces guarda la factura con el código del crédito. Si un cliente ya
  recibió su crédito, el banco lo rechaza después ("crédito activo"): en la demo, mostrar primero el
  caso "precio mayor al monto máximo" y luego la venta aprobada.
