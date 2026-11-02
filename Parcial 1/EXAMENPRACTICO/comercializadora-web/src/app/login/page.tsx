"use client";

import Link from "next/link";
import { useState, type FormEvent } from "react";
import { authClient } from "@/lib/auth-client";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    const { error: signInError } = await authClient.signIn.email({
      email,
      password,
    });

    if (signInError) {
      setError("Correo o contraseña incorrectos. Verifique sus credenciales.");
      setIsSubmitting(false);
      return;
    }

    // Full navigation: the client router may hold a cached redirect to /login
    // taken by src/proxy.ts before the session cookie existed.
    // eslint-disable-next-line @next/next/no-location-assign-relative-destination -- full reload drops the cached proxy redirect
    window.location.assign("/");
  };

  return (
    <main className="mx-auto flex w-full max-w-md flex-1 flex-col justify-center px-6 py-12">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
        Iniciar sesión
      </h1>
      <p className="mt-2 text-sm text-zinc-600">
        Ingrese con su cuenta de la comercializadora.
      </p>

      <form onSubmit={handleSubmit} className="mt-8 space-y-4">
        <div>
          <label
            htmlFor="email"
            className="block text-sm font-medium text-zinc-700"
          >
            Correo electrónico
          </label>
          <input
            id="email"
            type="email"
            name="email"
            autoComplete="email"
            required
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
          />
        </div>

        <div>
          <label
            htmlFor="password"
            className="block text-sm font-medium text-zinc-700"
          >
            Contraseña
          </label>
          <input
            id="password"
            type="password"
            name="password"
            autoComplete="current-password"
            required
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
          />
        </div>

        {error ? (
          <p
            role="alert"
            className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700"
          >
            {error}
          </p>
        ) : null}

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-md bg-zinc-900 px-3 py-2 text-sm font-medium text-white transition-colors hover:bg-zinc-700 disabled:opacity-60"
        >
          {isSubmitting ? "Ingresando..." : "Ingresar"}
        </button>
      </form>

      <p className="mt-6 text-sm text-zinc-600">
        ¿No tiene cuenta?{" "}
        <Link href="/registro" className="font-medium text-zinc-900 underline">
          Regístrese
        </Link>
      </p>
    </main>
  );
}
