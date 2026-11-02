<p align="center">
  <a href="http://nestjs.com/" target="blank"><img src="https://nestjs.com/img/logo-small.svg" width="120" alt="Nest Logo" /></a>
</p>

[circleci-image]: https://img.shields.io/circleci/build/github/nestjs/nest/master?token=abc123def456
[circleci-url]: https://circleci.com/gh/nestjs/nest

  <p align="center">A progressive <a href="http://nodejs.org" target="_blank">Node.js</a> framework for building efficient and scalable server-side applications.</p>
    <p align="center">
<a href="https://www.npmjs.com/~nestjscore" target="_blank"><img src="https://img.shields.io/npm/v/@nestjs/core.svg" alt="NPM Version" /></a>
<a href="https://www.npmjs.com/~nestjscore" target="_blank"><img src="https://img.shields.io/npm/l/@nestjs/core.svg" alt="Package License" /></a>
<a href="https://www.npmjs.com/~nestjscore" target="_blank"><img src="https://img.shields.io/npm/dm/@nestjs/common.svg" alt="NPM Downloads" /></a>
<a href="https://circleci.com/gh/nestjs/nest" target="_blank"><img src="https://img.shields.io/circleci/build/github/nestjs/nest/master" alt="CircleCI" /></a>
<a href="https://discord.gg/G7Qnnhy" target="_blank"><img src="https://img.shields.io/badge/discord-online-brightgreen.svg" alt="Discord"/></a>
<a href="https://opencollective.com/nest#backer" target="_blank"><img src="https://opencollective.com/nest/backers/badge.svg" alt="Backers on Open Collective" /></a>
<a href="https://opencollective.com/nest#sponsor" target="_blank"><img src="https://opencollective.com/nest/sponsors/badge.svg" alt="Sponsors on Open Collective" /></a>
  <a href="https://paypal.me/kamilmysliwiec" target="_blank"><img src="https://img.shields.io/badge/Donate-PayPal-ff3f59.svg" alt="Donate us"/></a>
    <a href="https://opencollective.com/nest#sponsor"  target="_blank"><img src="https://img.shields.io/badge/Support%20us-Open%20Collective-41B883.svg" alt="Support us"></a>
  <a href="https://twitter.com/nestframework" target="_blank"><img src="https://img.shields.io/twitter/follow/nestframework.svg?style=social&label=Follow" alt="Follow us on Twitter"></a>
</p>
  <!--[![Backers on Open Collective](https://opencollective.com/nest/backers/badge.svg)](https://opencollective.com/nest#backer)
  [![Sponsors on Open Collective](https://opencollective.com/nest/sponsors/badge.svg)](https://opencollective.com/nest#sponsor)-->

## Description

[Nest](https://github.com/nestjs/nest) framework TypeScript starter repository.

## Retailer API (examen practice)

Backend of the phone retailer: TypeORM over its own PostgreSQL database
(`comercializadora`) and Better Auth for the seller sessions. It listens on
port **3001**; the browser only talks to the Next.js app on port 3000, which
rewrites `/api/*` here (see `comercializadora-web/next.config.ts`).

```bash
docker compose up -d   # PostgreSQL, from the parent folder
npm run start          # or: npm run build && node dist/main
```

### Environment

Every variable is optional and has a documented default in
`src/config/env.ts`; copy `.env.example` to `.env` to override them.

| Variable | Default | Purpose |
|---|---|---|
| `PORT` | `3001` | Port of this API |
| `DATABASE_URL` | `postgres://gr8:gr8pass@localhost:5432/comercializadora` | Retailer database |
| `BETTER_AUTH_URL` | `http://localhost:3000` | Public URL of the Next.js app (used to build the auth URLs) |
| `BETTER_AUTH_SECRET` | `dev-only-insecure-secret-change-me-32chars` (**dev only**) | Signs the session cookies |
| `WEB_ORIGIN` | `http://localhost:3000` | Origin trusted by Better Auth |

### Database migrations

TypeORM owns the schema: `synchronize: false`, `migrationsRun: true`, so the
pending migrations in `src/database/migrations/` are applied when the
application boots. The CLI scripts build first and then use the compiled
data source:

```bash
npm run migration:show     # list migrations and whether they ran
npm run migration:run      # apply pending migrations
npm run migration:revert   # revert the last migration
```

Add a new migration to `src/database/migrations/` and register it in
`src/database/migrations/index.ts` (the list is imported explicitly instead of
a glob so the same list works for `dist/`, `nest start` and vitest).

### Authentication

Better Auth with email and password (`/api/auth/*`, default base path). The
TypeORM migration creates the `user`, `session`, `account` and `verification`
tables. On bootstrap the API seeds one demo user, idempotently:

```
vendedor@comercializadora.ec / Vendedor2026!   (Vendedor Demo)
```

`GET /api/me` returns `{ id, name, email }` of the session user; without a
session cookie it answers `401` (the global Better Auth guard protects every
route, add `@AllowAnonymous()` to open one).

`npm test` runs the auth integration spec against the docker database: it only
adds the rows for the session it signs in.

> `pg` ships no type declarations and this project does not depend on
> `@types/pg`; `src/types/pg.d.ts` declares the small subset the API calls. If
> `@types/pg` is added later, delete that file and the cast in
> `src/auth/auth.ts`.

### Phone catalog

`RetailerSchema` creates the retailer model — `celular`, `cliente`, `factura`
and `detalle_factura` — and inserts the six phones of the exam statement.
`src/catalogo/` exposes their maintenance (every route needs a session and
answers `401` without one):

| Route | Effect |
|---|---|
| `GET /api/celulares` | phones on sale; `?todos=true` also lists the inactive ones |
| `GET /api/celulares/:id` | one phone |
| `POST /api/celulares` | creates a phone (`201`) |
| `PUT /api/celulares/:id` | replaces marca, modelo, descripcion and precio |
| `DELETE /api/celulares/:id` | soft delete: `activo = false` (invoices reference the phone) |
| `PATCH /api/celulares/:id/activar` | brings a soft deleted phone back |

Errors are the Nest defaults, `{ statusCode, message }`: `400` for an invalid
body (marca 1-50, modelo 1-100, descripcion up to 255, precio > 0 with 2
decimals), `404` for an unknown id and `409` when marca + modelo already exist
(`uq_celular_marca_modelo`).

Money columns are NUMERIC(10,2); `src/database/numeric.transformer.ts` returns
them as numbers with 2 decimals instead of the strings `pg` produces.
`src/facturacion/` holds the invoicing entities that T7 uses (no endpoints yet).

## Project setup

```bash
$ npm install
```

## Compile and run the project

```bash
# development
$ npm run start

# watch mode
$ npm run start:dev

# production mode
$ npm run start:prod
```

## Run tests

```bash
# unit tests
$ npm run test

# e2e tests
$ npm run test:e2e

# test coverage
$ npm run test:cov
```

## Deployment

When you're ready to deploy your NestJS application to production, there are some key steps you can take to ensure it runs as efficiently as possible. Check out the [deployment documentation](https://docs.nestjs.com/deployment) for more information.

If you are looking for a cloud-based platform to deploy your NestJS application, check out [Mau](https://mau.nestjs.com), our official platform for deploying NestJS applications on AWS. Mau makes deployment straightforward and fast, requiring just a few simple steps:

```bash
$ npm install -g @nestjs/mau
$ mau deploy
```

With Mau, you can deploy your application in just a few clicks, allowing you to focus on building features rather than managing infrastructure.

## Observability

In production applications, observability is essential for understanding how your system behaves, detecting issues early, and maintaining reliable performance.

[NestJS Observe](https://observe.nestjs.com) automatically instruments your NestJS application, giving you deep visibility into your system with minimal setup:

- **Distributed tracing:** Follow requests across services and understand how they flow through your system.
- **Waterfall analysis:** Visualize request execution and identify slow operations, bottlenecks, and unexpected delays.
- **Performance analysis:** Analyze application performance in real time and quickly pinpoint areas that need optimization.
- **Metrics:** Track key application and infrastructure metrics to understand system health and performance trends.
- **Logging:** Centralize and correlate logs with traces and other telemetry to make debugging easier.
- **Error tracking:** Detect errors quickly and investigate their root causes with the surrounding context.
- **SLA monitoring:** Track service-level objectives and identify when your application is approaching or exceeding defined thresholds.
- **Alarms and alerts:** Set up alerts for critical errors, performance degradation, SLA violations, and other anomalies so your team can react quickly.

To add it to this project, run this and approve the code it prints at [observe.nestjs.com/link](https://observe.nestjs.com/link) - signing up for free if you have no account:

```bash
$ npx @nestjs/observe link
```

It installs `@nestjs/observe`, wires it into `AppModule` and `main.ts`, and writes the app key and secret into `.env`, which every `nest start` script (`start`, `start:dev`, `start:debug`) then loads. In production, set `OBSERVE_APP_KEY` and `OBSERVE_APP_SECRET` in the environment instead.

Or do it by hand:

```bash
$ npm install @nestjs/observe
```

Then follow the [setup guide](https://docs.nestjs.com/observability/sdk) - it takes a single import and an app key.

The free plan needs no payment details and covers 300,000 events a month. You can also browse the [live demo](https://www.observe-demo.nestjs.com/dashboard) first - the whole dashboard over a busy service's data, with nothing to install.

## Resources

Check out a few resources that may come in handy when working with NestJS:

- Visit the [NestJS Documentation](https://docs.nestjs.com) to learn more about the framework.
- For questions and support, please visit our [Discord channel](https://discord.gg/G7Qnnhy).
- To dive deeper and get more hands-on experience, check out our official video [courses](https://courses.nestjs.com/).
- Deploy your application to AWS with the help of [NestJS Mau](https://mau.nestjs.com) in just a few clicks.
- Auto-instrument your application with [NestJS Observe](https://observe.nestjs.com). Distributed tracing, metrics, and logging made easy. Error tracking and performance monitoring for your NestJS applications.
- Visualize your application graph and interact with the NestJS application in real-time using [NestJS Devtools](https://devtools.nestjs.com).
- Need help with your project (part-time to full-time)? Check out our official [enterprise support](https://enterprise.nestjs.com).
- To stay in the loop and get updates, follow us on [X](https://x.com/nestframework) and [LinkedIn](https://linkedin.com/company/nestjs).
- Looking for a job, or have a job to offer? Check out our official [Jobs board](https://jobs.nestjs.com).

## Support

Nest is an MIT-licensed open source project. It can grow thanks to the sponsors and support by the amazing backers. If you'd like to join them, please [read more here](https://docs.nestjs.com/support).

## Stay in touch

- Author - [Kamil Myśliwiec](https://twitter.com/kammysliwiec)
- Website - [https://nestjs.com](https://nestjs.com/)
- Twitter - [@nestframework](https://twitter.com/nestframework)

## License

Nest is [MIT licensed](https://github.com/nestjs/nest/blob/master/LICENSE).
