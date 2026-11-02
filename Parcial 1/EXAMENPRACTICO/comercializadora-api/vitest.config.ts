import { defineConfig } from 'vitest/config';

export default defineConfig({
  // Resolves the path aliases declared in tsconfig.json, including the ones
  // added by `nest g library`. Requires Vite 8, which ships this option.
  resolve: { tsconfigPaths: true },
  test: {
    globals: true,
    root: './',
    // The auth spec is an integration test against the docker-compose
    // PostgreSQL, so `npm test` and `npm run test:e2e` run the same files here.
    include: ['**/*.spec.ts', '**/*.e2e-spec.ts'],
  },
});
