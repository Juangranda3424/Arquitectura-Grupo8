import type { NextConfig } from "next";

/**
 * Port of the NestJS API (`comercializadora-api`). Override it with `API_URL`
 * when the API does not run on localhost:3001.
 */
const API_URL = process.env.API_URL ?? "http://localhost:3001";

const nextConfig: NextConfig = {
  /* config options here */
  cacheComponents: true,
  partialPrefetching: true,
  turbopack: {
    rules: {
      "*.css": {
        loaders: ["@tailwindcss/turbopack"],
        as: "*.css",
      },
    },
  },
  async rewrites() {
    // The browser only ever talks to this app: `/api/*` (Better Auth session
    // plus the retailer endpoints) is proxied to the NestJS API, so the auth
    // cookies belong to the web origin (localhost:3000) and no CORS is needed.
    return [
      {
        source: "/api/:path*",
        destination: `${API_URL}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;
