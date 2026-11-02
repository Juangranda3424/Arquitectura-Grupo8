# 02 — Descripción del Caso de Uso «Verificar sujeto de crédito»

**Rúbrica 2 — Descripción del Caso de Uso que verifica si el cliente es sujeto de crédito — 1.0 punto.**

## 1. Identificación

| Campo | Valor |
|---|---|
| **ID** | UC-01 |
| **Nombre** | Verificar sujeto de crédito |
| **Actor** | Sistema de la Comercializadora (sistema externo que invoca el servicio web) |
| **Módulo** | Módulo de Crédito del Banco BanQuito (`banquito-core`) |
| **Servicio** | `GET /api/creditos/sujeto-credito?cedula={cedula}` — REST/JSON |
| **Rúbrica** | 5 — 1.5 puntos |
| **Tipo** | Consulta (solo lectura) |

## 2. Descripción

El sistema de la comercializadora envía al Core del banco la **cédula** del comprador y recibe si esa
persona es **sujeto de crédito** del Banco BanQuito. La respuesta incluye el **código de cliente** y el
**nombre** cuando la cédula corresponde a un cliente del banco, y la lista de **motivos** (código y
mensaje) con cada regla de negocio que no se cumplió. La comercializadora usa esta respuesta como
condición para facturar con la forma de pago **Crédito Directo**.

## 3. Precondiciones

1. El Core del banco está en ejecución y expone el servicio en `http://localhost:8080`.
2. La base `banquito` tiene el esquema del Core y del módulo de crédito aplicado por Flyway
   (`V1__core_schema.sql`: `cliente`, `cuenta`, `movimiento`, `credito`, `tabla_amortizacion`) y sus
   datos de prueba (`V2__core_seed.sql`).
3. La comercializadora conoce la cédula del comprador (es el dato que ingresa el vendedor en la
   facturación).

## 4. Disparador

El vendedor de la comercializadora selecciona la forma de pago **Crédito Directo** e inicia la
facturación de un teléfono celular; antes de emitir la factura, el sistema de la comercializadora invoca
este servicio con la cédula del comprador.

## 5. Flujo básico (camino de éxito)

1. El Sistema de la Comercializadora invoca `GET /api/creditos/sujeto-credito?cedula={cedula}`.
2. El Core normaliza el parámetro (`trim`) y busca la cédula en la tabla `cliente`.
3. **Regla 1 — es cliente del banco:** la cédula existe, por lo que el Core recupera `codCliente` y
   `nombre` del cliente.
4. **Regla 2 — depósito en el último mes:** el Core verifica que el cliente tenga al menos un movimiento
   de tipo `DEP` en cualquiera de sus cuentas con fecha mayor o igual a `hoy − 1 mes`. Se cumple.
5. **Regla 3 — edad según género:** el cliente no es de género masculino, o lo es y tiene 25 años o más.
   Se cumple.
6. **Regla 4 — sin crédito activo:** el cliente no tiene ningún registro en `credito` con
   `estado = 'ACT'`. Se cumple.
7. El Core arma la respuesta con `sujetoCredito = true`, `motivos = []`, y responde **HTTP 200** con
   `cedula`, `codCliente` y `nombre`.
8. El Sistema de la Comercializadora continúa con el caso de uso **UC-02 Consultar monto máximo de
   crédito** para el `codCliente` recibido.

El Core evalúa **todas** las reglas 2, 3 y 4 antes de responder: no se detiene en la primera que falla,
así que un cliente puede recibir varios motivos en una sola respuesta. La regla 1 sí es excluyente,
porque sin registro de cliente no hay cuentas, movimientos ni créditos que evaluar.

## 6. Flujos alternativos

| ID | Condición | Pasos | Resultado |
|---|---|---|---|
| **A1** | La cédula no pertenece a ningún cliente del banco (regla 1) | 1. El Core no encuentra la cédula en `cliente`. 2. No evalúa las reglas 2 a 4. 3. Responde `200` con `sujetoCredito = false` y el motivo `NO_ES_CLIENTE`. | El cliente no es sujeto de crédito; la comercializadora no puede facturar con Crédito Directo. |
| **A2** | El cliente no tiene ningún depósito en el último mes (regla 2) | 1. El Core busca movimientos `DEP` del cliente desde `hoy − 1 mes` y no encuentra ninguno. 2. Agrega el motivo `SIN_DEPOSITO_ULTIMO_MES`. 3. Sigue evaluando las reglas 3 y 4. 4. Responde `sujetoCredito = false` con los motivos acumulados. | No es sujeto de crédito. |
| **A3** | El cliente es de género masculino y menor de 25 años (regla 3) | 1. El Core calcula la edad con `fecha_nacimiento` y detecta `genero = 'M'` con edad menor a 25. 2. Agrega el motivo `MENOR_25_MASCULINO`. 3. Sigue con la regla 4. 4. Responde `sujetoCredito = false`. | No es sujeto de crédito. La regla **no aplica a las mujeres**: una cliente menor de 25 años sí puede ser sujeto de crédito. |
| **A4** | El cliente ya tiene un crédito activo (regla 4) | 1. El Core encuentra un registro en `credito` del cliente con `estado = 'ACT'`. 2. Agrega el motivo `CREDITO_ACTIVO`. 3. Responde `sujetoCredito = false`. | No es sujeto de crédito mientras el crédito anterior siga activo. |
| **A5** | Varias reglas fallan a la vez | El Core acumula un motivo por cada regla incumplida (por ejemplo `SIN_DEPOSITO_ULTIMO_MES` **y** `CREDITO_ACTIVO`) y los devuelve en el mismo arreglo `motivos`. | No es sujeto de crédito; la comercializadora muestra todos los motivos. |
| **A6** | La petición llega sin el parámetro `cedula` (o el servicio está mal formado) | El Core responde **HTTP 400** con `codigo = SOLICITUD_INVALIDA`. | No se evalúa ninguna regla. |

Los flujos alternativos A1–A5 **no son errores HTTP**: el servicio siempre responde `200` con
`sujetoCredito = false`, porque la consulta se ejecutó correctamente y lo que se informa es el resultado
de la evaluación. Solo A6 es un error de la petición (`400`).

## 7. Postcondiciones

**Éxito (sujeto de crédito):**

1. La respuesta contiene `codCliente`, `nombre`, `sujetoCredito = true` y `motivos = []`.
2. La comercializadora queda habilitada para consultar el monto máximo (UC-02) y, si el precio del
   teléfono no lo supera, para otorgar el crédito (UC-03).
3. **No se modifica ningún dato**: el servicio corre en una transacción de solo lectura
   (`@Transactional(readOnly = true)`); no crea clientes, ni movimientos, ni créditos.

**Fracaso (no es sujeto de crédito):**

1. La respuesta contiene `sujetoCredito = false` y al menos un motivo con su código y mensaje.
2. La comercializadora no puede facturar con Crédito Directo: no se otorga crédito ni se emite factura
   con esa forma de pago.
3. Tampoco se modifica ningún dato de la base `banquito`.

## 8. Reglas de negocio

Texto del enunciado del examen, transcrito literalmente:

> Para verificar si una persona es sujeto de crédito se deberá seguir las siguientes reglas.
>
> 1. Verificar si el solicitante es cliente del banco.
> 2. Verificar que el cliente al menos posea una transacción de depósito en el último mes.
> 3. Verificar que el cliente no sea menor de 25 años si género es masculino
> 4. Verificar que el cliente actualmente no tenga un crédito activo en el banco.

Interpretación implementada:

| Regla | Implementación | Código de motivo si falla |
|---|---|---|
| 1. Es cliente del banco | `cliente.cedula` único; sin coincidencia no se evalúa el resto | `NO_ES_CLIENTE` |
| 2. Depósito en el último mes | existe `movimiento` con `tipo = 'DEP'` en las cuentas del cliente y `fecha >= hoy − 1 mes` | `SIN_DEPOSITO_ULTIMO_MES` |
| 3. No menor de 25 años si es masculino | solo si `genero = 'M'`: años cumplidos (`Period.between(fecha_nacimiento, hoy)`) `< 25` | `MENOR_25_MASCULINO` |
| 4. Sin crédito activo | no existe `credito` del cliente con `estado = 'ACT'` | `CREDITO_ACTIVO` |

Todas las fechas se calculan con el reloj del servidor (`hoy`), no con fechas escritas en el código,
por eso los datos de prueba usan fechas relativas y las reglas "último mes" / "últimos 3 meses" se
siguen cumpliendo el día de la defensa.

## 9. Entrada

Petición real del servicio:

```http
GET /api/creditos/sujeto-credito?cedula=1712345678 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `cedula` | texto (`varchar(100)` en la base) | Sí | Cédula del comprador; el Core la normaliza con `trim` antes de buscar. |

## 10. Salida

Cuerpo JSON (`application/json`). Los campos nulos se omiten de la respuesta
(`spring.jackson.default-property-inclusion=non_null`), por eso un no cliente no trae `codCliente` ni
`nombre`.

| Campo | Tipo | Descripción |
|---|---|---|
| `cedula` | texto | Cédula consultada, tal como se normalizó. |
| `codCliente` | entero | Código del cliente en el banco; ausente si no es cliente. |
| `nombre` | texto | Nombre del cliente; ausente si no es cliente. |
| `sujetoCredito` | booleano | `true` solo si `motivos` está vacío. |
| `motivos` | arreglo | Reglas incumplidas; cada una con `codigo` y `mensaje`. Vacío cuando es sujeto de crédito. |

### 10.1 Es sujeto de crédito — HTTP 200

Cédula `1712345678` (Ana Torres, cliente 1). Las cuatro reglas se cumplen:

```json
{
  "cedula": "1712345678",
  "codCliente": 1,
  "nombre": "Ana Torres",
  "sujetoCredito": true,
  "motivos": []
}
```

### 10.2 No es cliente del banco — HTTP 200

Cédula `1799999999`, que no está en la tabla `cliente`:

```json
{
  "cedula": "1799999999",
  "sujetoCredito": false,
  "motivos": [
    {
      "codigo": "NO_ES_CLIENTE",
      "mensaje": "El solicitante no es cliente del banco"
    }
  ]
}
```

### 10.3 Es cliente pero no tiene depósito en el último mes — HTTP 200

Cédula `1767890123` (Sofia Vega, cliente 6):

```json
{
  "cedula": "1767890123",
  "codCliente": 6,
  "nombre": "Sofia Vega",
  "sujetoCredito": false,
  "motivos": [
    {
      "codigo": "SIN_DEPOSITO_ULTIMO_MES",
      "mensaje": "El cliente no tiene depósitos en el último mes"
    }
  ]
}
```

### 10.4 Es cliente, masculino y menor de 25 años — HTTP 200

Cédula `1756789012` (Luis Ortiz, cliente 5):

```json
{
  "cedula": "1756789012",
  "codCliente": 5,
  "nombre": "Luis Ortiz",
  "sujetoCredito": false,
  "motivos": [
    {
      "codigo": "MENOR_25_MASCULINO",
      "mensaje": "El cliente es de género masculino y menor de 25 años"
    }
  ]
}
```

### 10.5 Es cliente pero ya tiene un crédito activo — HTTP 200

Cédula `1778901234` (Pedro Salazar, cliente 7; su crédito activo es la tabla de ejemplo del enunciado,
450.00 a 6 cuotas):

```json
{
  "cedula": "1778901234",
  "codCliente": 7,
  "nombre": "Pedro Salazar",
  "sujetoCredito": false,
  "motivos": [
    {
      "codigo": "CREDITO_ACTIVO",
      "mensaje": "El cliente ya tiene un crédito activo"
    }
  ]
}
```

### 10.6 Petición sin el parámetro `cedula` — HTTP 400

```json
{
  "codigo": "SOLICITUD_INVALIDA",
  "mensaje": "Solicitud mal formada"
}
```

## 11. Casos de prueba usados en la demostración

| Cédula | Cliente | Resultado |
|---|---|---|
| 1712345678 | Ana Torres | Sujeto de crédito (`true`, sin motivos) |
| 1734567890 | Maria Lopez | Sujeto de crédito (mujer menor de 25: la regla 3 solo aplica a hombres) |
| 1756789012 | Luis Ortiz | No: `MENOR_25_MASCULINO` |
| 1767890123 | Sofia Vega | No: `SIN_DEPOSITO_ULTIMO_MES` |
| 1778901234 | Pedro Salazar | No: `CREDITO_ACTIVO` |
| 1799999999 | — | No: `NO_ES_CLIENTE` |
