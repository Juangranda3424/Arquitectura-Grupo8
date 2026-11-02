import { getSessionCookie } from "better-auth/cookies";
import { NextResponse, type NextRequest } from "next/server";

/** Routes that must stay reachable without a session. */
const PUBLIC_PATHS = new Set(["/login", "/registro"]);

/**
 * Optimistic gate only: it checks that the Better Auth session cookie exists.
 * The real authorization happens in the API, whose global Better Auth guard
 * rejects unauthenticated requests with 401.
 */
export function proxy(request: NextRequest) {
  if (PUBLIC_PATHS.has(request.nextUrl.pathname)) {
    return NextResponse.next();
  }

  if (getSessionCookie(request)) {
    return NextResponse.next();
  }

  return NextResponse.redirect(new URL("/login", request.url));
}

export const config = {
  // Everything except `/api/*` (proxied to the NestJS API), Next.js internals
  // and static assets.
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)"],
};
