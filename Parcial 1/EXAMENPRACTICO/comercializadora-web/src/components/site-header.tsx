"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useTransition } from "react";
import { authClient } from "@/lib/auth-client";

/**
 * Application header. It is a client component because the session is read in
 * the browser through the Better Auth client (no server side cookie read, so
 * pages stay static and nothing needs a Suspense boundary).
 */
export function SiteHeader() {
  const router = useRouter();
  const { data: session, isPending } = authClient.useSession();
  const [isSigningOut, startSignOut] = useTransition();

  const handleSignOut = () => {
    startSignOut(async () => {
      await authClient.signOut();
      router.replace("/login");
      router.refresh();
    });
  };

  return (
    <header className="border-b border-zinc-200 bg-white">
      <div className="mx-auto flex w-full max-w-5xl flex-wrap items-center justify-between gap-4 px-6 py-4">
        <Link
          href="/"
          className="text-base font-semibold tracking-tight text-zinc-900"
        >
          Comercializadora de Celulares
        </Link>

        <div className="flex items-center gap-3 text-sm">
          {session ? (
            <>
              <span className="text-zinc-600">{session.user.name}</span>
              <button
                type="button"
                onClick={handleSignOut}
                disabled={isSigningOut}
                className="rounded-md border border-zinc-300 px-3 py-1.5 font-medium text-zinc-700 transition-colors hover:bg-zinc-100 disabled:opacity-60"
              >
                {isSigningOut ? "Cerrando sesión..." : "Cerrar sesión"}
              </button>
            </>
          ) : isPending ? null : (
            <>
              <Link
                href="/login"
                className="rounded-md px-3 py-1.5 font-medium text-zinc-600 transition-colors hover:bg-zinc-100"
              >
                Iniciar sesión
              </Link>
              <Link
                href="/registro"
                className="rounded-md bg-zinc-900 px-3 py-1.5 font-medium text-white transition-colors hover:bg-zinc-700"
              >
                Crear cuenta
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
