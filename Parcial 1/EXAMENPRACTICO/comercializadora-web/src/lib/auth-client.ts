import { createAuthClient } from "better-auth/react";

/**
 * Better Auth client used by the browser.
 *
 * No `baseURL` on purpose: every call goes to the same origin (`/api/auth/*`)
 * and `next.config.ts` rewrites it to the NestJS API, so the session cookie is
 * stored for this app and the requests stay same-origin.
 */
export const authClient = createAuthClient();
