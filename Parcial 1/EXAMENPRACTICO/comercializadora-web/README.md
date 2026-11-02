# comercializadora-web

Next.js UI of the phone retailer: login, catalog, invoicing and invoice detail with the BanQuito
amortization table. It has no backend logic of its own: every `/api/*` request is proxied to
`comercializadora-api` (NestJS).

## Run

```bash
npm install
npm run dev            # http://localhost:3000 (needs comercializadora-api on :3001)
# or
npm run build && npm start
```

Demo user: `vendedor@comercializadora.ec` / `Vendedor2026!` (seeded by the API).

## Environment variables

| Variable | Default | Purpose |
|---|---|---|
| `API_URL` | `http://localhost:3001` | Where `next.config.ts` proxies `/api/*` (the NestJS API) |

Copy [`.env.example`](.env.example) to `.env.local` only to change it. It is read when Next.js
starts, so restart `npm run dev` or rebuild before `npm start`.

## How it talks to the API

The browser only calls this app (`localhost:3000`). The `rewrites()` in `next.config.ts` forwards
`/api/*` to the API, so the Better Auth session cookie stays on the web origin and no CORS is
involved. This app holds **no secret**: the API validates each user's session cookie, signed with
`BETTER_AUTH_SECRET`, which lives only in `comercializadora-api`. `src/proxy.ts` only checks that
the cookie exists to redirect to `/login`; the real enforcement is the API's auth guard (401).
