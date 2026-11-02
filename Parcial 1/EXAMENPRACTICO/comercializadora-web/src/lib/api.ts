/**
 * Minimal JSON client for the NestJS API of the retailer.
 *
 * The browser never calls the API directly: `next.config.ts` rewrites
 * `/api/*` to `http://localhost:3001`, so these relative URLs stay same-origin
 * and the Better Auth session cookie travels with them.
 */

/** Body of the errors Nest returns: `{ statusCode, message }`. */
interface NestErrorBody {
  statusCode?: number;
  message?: string | string[];
}

/** Turns the `message` of a NestJS error into one line for the UI. */
function readMessage(body: unknown, status: number): string {
  const message = (body as NestErrorBody | null)?.message;

  if (Array.isArray(message)) {
    return message.join(". ");
  }

  return message ?? `La operación falló (HTTP ${status}).`;
}

/**
 * `fetch` + JSON, throwing an `Error` whose message is the one the API sent,
 * so the screens can show validation and conflict messages as they are.
 */
export async function apiRequest<T>(
  path: string,
  init?: RequestInit,
): Promise<T> {
  const response = await fetch(path, {
    ...init,
    headers: init?.body
      ? { "Content-Type": "application/json", ...init?.headers }
      : init?.headers,
    cache: "no-store",
  });

  const text = await response.text();
  const body = parseJson(text);

  if (!response.ok) {
    throw new Error(readMessage(body, response.status));
  }

  return body as T;
}

/** JSON body of a response, or `null` when it is empty or not JSON. */
function parseJson(text: string): unknown {
  if (text === "") {
    return null;
  }

  try {
    return JSON.parse(text) as unknown;
  } catch {
    return null;
  }
}

/** Message of an unknown thrown value, for the error banners. */
export function errorMessage(error: unknown): string {
  return error instanceof Error
    ? error.message
    : "Ocurrió un error inesperado. Intente nuevamente.";
}
