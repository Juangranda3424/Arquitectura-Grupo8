# Diagramas — Examen práctico «Sistema BanQuito Módulo de Crédito»

Índice de los diagramas que se defienden ante el tribunal, con la rúbrica y el puntaje que cubre cada
uno. Enunciado: [`../ExamenComplexivoPractivo_CELULARES.pdf`](../ExamenComplexivoPractivo_CELULARES.pdf).

| Archivo | Rúbrica | Ítem de la rúbrica | Puntos |
|---|---|---|---|
| [`01-casos-de-uso.md`](01-casos-de-uso.md) | 1 | Diagrama de Casos de Uso del Módulo de Crédito del Banco BanQuito | 2.0 |
| [`02-descripcion-caso-de-uso.md`](02-descripcion-caso-de-uso.md) | 2 | Descripción del Caso de Uso que verifica si el cliente es sujeto de crédito | 1.0 |
| [`03-er-comercializadora.md`](03-er-comercializadora.md) | 4 | Diagrama Entidad-Relación del Sistema de la Comercializadora | 1.0 |
| [`03-er-comercializadora.md`](03-er-comercializadora.md) | 3 | Implementación de la Base de Datos del sistema CORE (modelo del enunciado + crédito) | 2.0 |
| [`04-arquitectura.md`](04-arquitectura.md) | 12 | Diagrama de Arquitectura de la solución completa | 1.0 |
| **Total diagramas** | | | **7.0** |

## Qué muestra cada documento

**01 — Casos de uso.** Actor único: el *Sistema de la Comercializadora* (sistema externo, porque el Core
solo expone servicios web). Casos de uso UC-01 verificar sujeto de crédito, UC-02 consultar monto máximo,
UC-03 otorgar crédito, UC-04 generar tabla de amortización (`<<include>>` de UC-03) y UC-05 consultar
tabla de amortización. Incluye el diagrama en Mermaid (flowchart con notación UML) y el mismo diagrama en
PlantUML.

**02 — Descripción del caso de uso.** Ficha completa de UC-01: actor, precondiciones, disparador, flujo
básico, un flujo alternativo por cada regla incumplida, postcondiciones, las cuatro reglas del enunciado
citadas literalmente y la entrada/salida con el JSON real de
`GET /api/creditos/sujeto-credito?cedula=`.

**03 — Entidad-Relación.** Tres bloques: el ER de la base `comercializadora` (`celular`, `cliente`,
`factura`, `detalle_factura`) con columnas, tipos, PK y FK exactos de la migración de TypeORM; las tablas
de autenticación de Better Auth (`user`, `session`, `account`, `verification`); y el ER de la base
`banquito` (`cliente`, `cuenta`, `movimiento`, `credito`, `tabla_amortizacion`) con su diccionario de
datos y el detalle de los datos sembrados.

**04 — Arquitectura.** Diagrama de componentes de la solución completa (navegador → Next.js :3000 →
NestJS :3001 → PostgreSQL `comercializadora`; NestJS → REST/JSON → Spring Boot :8080 → PostgreSQL
`banquito`; ambas bases en un contenedor `postgres:17` en :5432), el diagrama de secuencia de la venta con
Crédito Directo, el de la venta en efectivo con el descuento del 42% y la tabla de servicios REST del
Core.

## Cómo renderizarlos

- **GitHub / VS Code / IntelliJ**: los bloques ` ```mermaid ` se renderizan directamente en la vista
  previa del Markdown.
- **PlantUML** (solo el diagrama de casos de uso): pegar el bloque `@startuml ... @enduml` en
  <https://www.plantuml.com/plantuml> o renderizarlo con la extensión de PlantUML.
- **Línea de comandos** (valida y exporta las imágenes de todos los bloques):

  ```bash
  npx -y @mermaid-js/mermaid-cli -i 01-casos-de-uso.md -o 01-casos-de-uso.md
  ```

  El comando deja un archivo `.svg` por diagrama junto al Markdown; se pueden borrar después.
