"use client";

import { useEffect, useState, type FormEvent } from "react";
import { apiRequest, errorMessage } from "@/lib/api";

/** Phone as the NestJS API returns it (`GET /api/celulares`). */
interface Celular {
  codCelular: number;
  marca: string;
  modelo: string;
  descripcion: string | null;
  precio: number;
  activo: boolean;
}

/** Text inputs of the form; `precio` stays a string until it is submitted. */
interface FormState {
  marca: string;
  modelo: string;
  descripcion: string;
  precio: string;
}

const EMPTY_FORM: FormState = {
  marca: "",
  modelo: "",
  descripcion: "",
  precio: "",
};

const usd = new Intl.NumberFormat("es-EC", {
  style: "currency",
  currency: "USD",
});

export default function CatalogoPage() {
  const [celulares, setCelulares] = useState<Celular[]>([]);
  const [mostrarInactivos, setMostrarInactivos] = useState(false);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  /** `codCelular` being edited; `null` means the form creates a new phone. */
  const [editando, setEditando] = useState<number | null>(null);
  const [guardando, setGuardando] = useState(false);
  /** `codCelular` with a pending activar/desactivar request. */
  const [ocupado, setOcupado] = useState<number | null>(null);
  /** Bumped after every write to load the catalog again. */
  const [recarga, setRecarga] = useState(0);

  // The list is loaded in the browser (the session cookie lives there), so a
  // reload is just a new request; `recarga` is what triggers it after a write.
  useEffect(() => {
    let cancelado = false;
    const query = mostrarInactivos ? "?todos=true" : "";

    apiRequest<Celular[]>(`/api/celulares${query}`)
      .then((data) => {
        if (cancelado) return;
        setCelulares(data);
        setCargando(false);
      })
      .catch((loadError: unknown) => {
        if (cancelado) return;
        setError(errorMessage(loadError));
        setCargando(false);
      });

    return () => {
      cancelado = true;
    };
  }, [mostrarInactivos, recarga]);

  const recargar = () => setRecarga((actual) => actual + 1);

  const actualizarCampo = (campo: keyof FormState, valor: string) => {
    setForm((actual) => ({ ...actual, [campo]: valor }));
  };

  const cancelarEdicion = () => {
    setEditando(null);
    setForm(EMPTY_FORM);
  };

  const enviar = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setGuardando(true);
    setError(null);

    const descripcion = form.descripcion.trim();
    const body = JSON.stringify({
      marca: form.marca.trim(),
      modelo: form.modelo.trim(),
      descripcion: descripcion === "" ? undefined : descripcion,
      precio: Number(form.precio),
    });

    try {
      await apiRequest<Celular>(
        editando === null ? "/api/celulares" : `/api/celulares/${editando}`,
        { method: editando === null ? "POST" : "PUT", body },
      );
      cancelarEdicion();
      recargar();
    } catch (saveError) {
      setError(errorMessage(saveError));
    } finally {
      setGuardando(false);
    }
  };

  const cambiarEstado = async (celular: Celular) => {
    setOcupado(celular.codCelular);
    setError(null);

    try {
      await apiRequest<Celular>(
        celular.activo
          ? `/api/celulares/${celular.codCelular}`
          : `/api/celulares/${celular.codCelular}/activar`,
        { method: celular.activo ? "DELETE" : "PATCH" },
      );
      recargar();
    } catch (stateError) {
      setError(errorMessage(stateError));
    } finally {
      setOcupado(null);
    }
  };

  const editar = (celular: Celular) => {
    setEditando(celular.codCelular);
    setError(null);
    setForm({
      marca: celular.marca,
      modelo: celular.modelo,
      descripcion: celular.descripcion ?? "",
      precio: celular.precio.toFixed(2),
    });
  };

  const tituloFormulario =
    editando === null ? "Nuevo celular" : "Editar celular";

  return (
    <main className="mx-auto w-full max-w-5xl flex-1 px-6 py-12">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
        Catálogo de celulares
      </h1>
      <p className="mt-2 max-w-2xl text-sm text-zinc-600">
        Teléfonos que la comercializadora tiene a la venta. Un celular con
        facturas no se elimina: se desactiva y deja de aparecer en el catálogo.
      </p>

      {error ? (
        <p
          role="alert"
          className="mt-6 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700"
        >
          {error}
        </p>
      ) : null}

      <form
        onSubmit={enviar}
        className="mt-8 rounded-xl border border-zinc-200 bg-white p-5"
      >
        <h2 className="text-base font-medium text-zinc-900">
          {tituloFormulario}
        </h2>

        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <div>
            <label
              htmlFor="marca"
              className="block text-sm font-medium text-zinc-700"
            >
              Marca
            </label>
            <input
              id="marca"
              name="marca"
              type="text"
              required
              maxLength={50}
              value={form.marca}
              onChange={(event) => actualizarCampo("marca", event.target.value)}
              className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
            />
          </div>

          <div>
            <label
              htmlFor="modelo"
              className="block text-sm font-medium text-zinc-700"
            >
              Modelo
            </label>
            <input
              id="modelo"
              name="modelo"
              type="text"
              required
              maxLength={100}
              value={form.modelo}
              onChange={(event) => actualizarCampo("modelo", event.target.value)}
              className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
            />
          </div>

          <div>
            <label
              htmlFor="descripcion"
              className="block text-sm font-medium text-zinc-700"
            >
              Descripción{" "}
              <span className="font-normal text-zinc-500">(opcional)</span>
            </label>
            <input
              id="descripcion"
              name="descripcion"
              type="text"
              maxLength={255}
              value={form.descripcion}
              onChange={(event) =>
                actualizarCampo("descripcion", event.target.value)
              }
              className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
            />
          </div>

          <div>
            <label
              htmlFor="precio"
              className="block text-sm font-medium text-zinc-700"
            >
              Precio (USD)
            </label>
            <input
              id="precio"
              name="precio"
              type="number"
              required
              min="0.01"
              step="0.01"
              value={form.precio}
              onChange={(event) => actualizarCampo("precio", event.target.value)}
              className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
            />
          </div>
        </div>

        <div className="mt-4 flex flex-wrap items-center gap-3">
          <button
            type="submit"
            disabled={guardando}
            className="rounded-md bg-zinc-900 px-3 py-2 text-sm font-medium text-white transition-colors hover:bg-zinc-700 disabled:opacity-60"
          >
            {guardando
              ? "Guardando..."
              : editando === null
                ? "Agregar al catálogo"
                : "Guardar cambios"}
          </button>

          {editando === null ? null : (
            <button
              type="button"
              onClick={cancelarEdicion}
              className="rounded-md border border-zinc-300 px-3 py-2 text-sm font-medium text-zinc-700 transition-colors hover:bg-zinc-100"
            >
              Cancelar
            </button>
          )}
        </div>
      </form>

      <div className="mt-8 flex flex-wrap items-center justify-between gap-3">
        <h2 className="text-base font-medium text-zinc-900">Celulares</h2>

        <label className="flex items-center gap-2 text-sm text-zinc-700">
          <input
            type="checkbox"
            checked={mostrarInactivos}
            onChange={(event) => setMostrarInactivos(event.target.checked)}
            className="h-4 w-4 rounded border-zinc-300"
          />
          Mostrar desactivados
        </label>
      </div>

      {cargando ? (
        <p className="mt-4 text-sm text-zinc-600">Cargando catálogo...</p>
      ) : celulares.length === 0 ? (
        <p className="mt-4 text-sm text-zinc-600">
          No hay celulares que mostrar.
        </p>
      ) : (
        <div className="mt-4 overflow-x-auto rounded-xl border border-zinc-200 bg-white">
          <table className="w-full min-w-[40rem] border-collapse text-left text-sm">
            <thead className="bg-zinc-50 text-xs uppercase tracking-wide text-zinc-500">
              <tr>
                <th scope="col" className="px-4 py-3 font-medium">
                  Marca
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Modelo
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Descripción
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Precio
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Estado
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-200">
              {celulares.map((celular) => (
                <tr key={celular.codCelular}>
                  <td className="px-4 py-3 text-zinc-900">{celular.marca}</td>
                  <td className="px-4 py-3 text-zinc-900">{celular.modelo}</td>
                  <td className="px-4 py-3 text-zinc-600">
                    {celular.descripcion ?? "—"}
                  </td>
                  <td className="px-4 py-3 tabular-nums text-zinc-900">
                    {usd.format(celular.precio)}
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className={
                        celular.activo
                          ? "rounded-full bg-emerald-50 px-2 py-0.5 text-xs font-medium text-emerald-700"
                          : "rounded-full bg-zinc-100 px-2 py-0.5 text-xs font-medium text-zinc-600"
                      }
                    >
                      {celular.activo ? "Activo" : "Desactivado"}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex flex-wrap gap-2">
                      <button
                        type="button"
                        onClick={() => editar(celular)}
                        className="rounded-md border border-zinc-300 px-2.5 py-1 text-xs font-medium text-zinc-700 transition-colors hover:bg-zinc-100"
                      >
                        Editar
                      </button>
                      <button
                        type="button"
                        onClick={() => void cambiarEstado(celular)}
                        disabled={ocupado === celular.codCelular}
                        className="rounded-md border border-zinc-300 px-2.5 py-1 text-xs font-medium text-zinc-700 transition-colors hover:bg-zinc-100 disabled:opacity-60"
                      >
                        {celular.activo ? "Desactivar" : "Activar"}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}
