"use client";

import Link from "next/link";
import { useEffect, useState, type FormEvent } from "react";
import { apiRequest, errorMessage } from "@/lib/api";
import { fechaHora, formaPagoLegible, usd } from "@/lib/format";
import type { Factura } from "@/lib/types";

/**
 * Invoice list (S11): newest first, with an optional cédula filter.
 *
 * Each row links to `/facturas/[num]`, where a credit invoice also shows its
 * amortization table (S12).
 */
export default function FacturasPage() {
  const [facturas, setFacturas] = useState<Factura[]>([]);
  const [filtro, setFiltro] = useState("");
  /** Cédula of the list currently shown; `""` means every invoice. */
  const [cedula, setCedula] = useState("");
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelado = false;
    const query = cedula === "" ? "" : `?cedula=${cedula}`;

    apiRequest<Factura[]>(`/api/facturas${query}`)
      .then((data) => {
        if (cancelado) return;
        setFacturas(data);
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
  }, [cedula]);

  /**
   * Applies a cédula filter. The loading flag is raised here and not inside the
   * effect: an equal cédula does not reload (same list), and setting state
   * synchronously in an effect would only trigger an extra render.
   */
  const aplicarFiltro = (nueva: string) => {
    setError(null);

    if (nueva === cedula) {
      return;
    }

    setCargando(true);
    setCedula(nueva);
  };

  const filtrar = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    aplicarFiltro(filtro.trim());
  };

  return (
    <main className="mx-auto w-full max-w-5xl flex-1 px-6 py-12">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
        Facturas
      </h1>
      <p className="mt-2 max-w-2xl text-sm text-zinc-600">
        Ventas emitidas, de la más reciente a la más antigua. Abra una factura
        para ver su detalle y, si fue a crédito directo, la tabla de
        amortización del Banco.
      </p>

      <form onSubmit={filtrar} className="mt-6 flex flex-wrap items-end gap-3">
        <div>
          <label
            htmlFor="cedula"
            className="block text-sm font-medium text-zinc-700"
          >
            Filtrar por cédula
          </label>
          <input
            id="cedula"
            name="cedula"
            type="text"
            inputMode="numeric"
            maxLength={10}
            pattern="\d{10}"
            placeholder="1712345678"
            value={filtro}
            onChange={(event) => setFiltro(event.target.value.trim())}
            className="mt-1 w-56 rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
          />
        </div>

        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-2 text-sm font-medium text-white transition-colors hover:bg-zinc-700"
        >
          Filtrar
        </button>

        {cedula === "" ? null : (
          <button
            type="button"
            onClick={() => {
              setFiltro("");
              aplicarFiltro("");
            }}
            className="rounded-md border border-zinc-300 px-3 py-2 text-sm font-medium text-zinc-700 transition-colors hover:bg-zinc-100"
          >
            Ver todas
          </button>
        )}
      </form>

      {error ? (
        <p
          role="alert"
          className="mt-6 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700"
        >
          {error}
        </p>
      ) : null}

      {cargando ? (
        <p className="mt-6 text-sm text-zinc-600">Cargando facturas...</p>
      ) : facturas.length === 0 ? (
        <p className="mt-6 text-sm text-zinc-600">
          {cedula === ""
            ? "Todavía no hay facturas emitidas."
            : `No hay facturas para la cédula ${cedula}.`}
        </p>
      ) : (
        <div className="mt-6 overflow-x-auto rounded-xl border border-zinc-200 bg-white">
          <table className="w-full min-w-[46rem] border-collapse text-left text-sm">
            <thead className="bg-zinc-50 text-xs uppercase tracking-wide text-zinc-500">
              <tr>
                <th scope="col" className="px-4 py-3 font-medium">
                  N°
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Fecha
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Cliente
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Forma de pago
                </th>
                <th scope="col" className="px-4 py-3 text-right font-medium">
                  Total
                </th>
                <th scope="col" className="px-4 py-3 font-medium">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-200">
              {facturas.map((factura) => (
                <tr key={factura.numFactura}>
                  <td className="px-4 py-3 tabular-nums text-zinc-900">
                    {factura.numFactura}
                  </td>
                  <td className="px-4 py-3 text-zinc-600">
                    {fechaHora(factura.fecha)}
                  </td>
                  <td className="px-4 py-3 text-zinc-900">
                    {factura.cliente.nombre}
                    <span className="block text-xs text-zinc-500">
                      {factura.cliente.cedula}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <span
                      className={
                        factura.formaPago === "EFE"
                          ? "rounded-full bg-emerald-50 px-2 py-0.5 text-xs font-medium text-emerald-700"
                          : "rounded-full bg-sky-50 px-2 py-0.5 text-xs font-medium text-sky-700"
                      }
                    >
                      {formaPagoLegible(factura.formaPago)}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-right tabular-nums text-zinc-900">
                    {usd.format(factura.total)}
                  </td>
                  <td className="px-4 py-3">
                    <Link
                      href={`/facturas/${factura.numFactura}`}
                      className="rounded-md border border-zinc-300 px-2.5 py-1 text-xs font-medium text-zinc-700 transition-colors hover:bg-zinc-100"
                    >
                      Ver factura
                    </Link>
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
