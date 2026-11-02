"use client";

import { useEffect, useState, type FormEvent } from "react";
import { FacturaResumen } from "@/components/factura-resumen";
import { ApiError, apiRequest, errorMessage } from "@/lib/api";
import { descuentoEfectivo, totalEfectivo, usd } from "@/lib/format";
import type {
  Celular,
  EvaluacionCredito,
  Factura,
  Motivo,
} from "@/lib/types";

type FormaPago = "EFE" | "CRE";

/** Quotas the bank accepts for a credit: 3 to 18 (S7). */
const CUOTAS = Array.from({ length: 16 }, (_, index) => index + 3);

/** Body of a rejected credit, as `POST /api/facturas` returns it. */
interface RechazoCredito {
  motivos?: Motivo[];
  montoMaximo?: number;
}

/**
 * Invoicing (S11): one phone, one buyer, cash with the 42% discount or direct
 * credit through the BanQuito core.
 *
 * The credit step is explicit ("Verificar crédito"): it shows the bank's
 * verdict, its reasons and the maximum amount before the seller issues the
 * invoice, and the final sale repeats the same checks in the API. The 42%
 * preview is computed here with the same rounding as the API so the seller
 * sees the amount that will be stored.
 */
export default function FacturacionPage() {
  const [celulares, setCelulares] = useState<Celular[]>([]);
  const [cargando, setCargando] = useState(true);

  const [codCelular, setCodCelular] = useState("");
  const [cedula, setCedula] = useState("");
  const [nombre, setNombre] = useState("");
  const [formaPago, setFormaPago] = useState<FormaPago>("EFE");
  const [cuotas, setCuotas] = useState("6");

  const [evaluacion, setEvaluacion] = useState<EvaluacionCredito | null>(null);
  const [verificando, setVerificando] = useState(false);
  const [facturando, setFacturando] = useState(false);
  const [factura, setFactura] = useState<Factura | null>(null);

  const [error, setError] = useState<string | null>(null);
  /** Failed credit rules of the bank, when the sale was rejected. */
  const [motivos, setMotivos] = useState<Motivo[] | null>(null);

  useEffect(() => {
    let cancelado = false;

    apiRequest<Celular[]>("/api/celulares")
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
  }, []);

  const celular = celulares.find(
    (row) => row.codCelular === Number(codCelular),
  );
  const precio = celular?.precio ?? 0;
  const cedulaValida = /^\d{10}$/.test(cedula);

  /** Clears the bank verdict: it belongs to the previous cédula/phone pair. */
  const limpiarEvaluacion = () => {
    setEvaluacion(null);
    setMotivos(null);
  };

  const verificarCredito = async () => {
    if (!cedulaValida || !celular) {
      setError("Ingrese una cédula de 10 dígitos y seleccione un celular.");
      return;
    }

    setVerificando(true);
    setError(null);
    setMotivos(null);

    try {
      setEvaluacion(
        await apiRequest<EvaluacionCredito>(
          `/api/credito/evaluacion?cedula=${cedula}&codCelular=${celular.codCelular}`,
        ),
      );
    } catch (evaluationError) {
      setEvaluacion(null);
      setError(errorMessage(evaluationError));
    } finally {
      setVerificando(false);
    }
  };

  const enviar = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFacturando(true);
    setError(null);
    setMotivos(null);

    try {
      setFactura(
        await apiRequest<Factura>("/api/facturas", {
          method: "POST",
          body: JSON.stringify({
            cedula,
            nombre: nombre.trim(),
            codCelular: Number(codCelular),
            formaPago,
            numeroCuotas: formaPago === "CRE" ? Number(cuotas) : undefined,
          }),
        }),
      );
    } catch (saleError) {
      const body =
        saleError instanceof ApiError
          ? (saleError.body as RechazoCredito | null)
          : null;
      setMotivos(body?.motivos ?? null);
      setError(errorMessage(saleError));
    } finally {
      setFacturando(false);
    }
  };

  const puedeFacturar =
    !facturando &&
    !cargando &&
    cedulaValida &&
    nombre.trim() !== "" &&
    celular !== undefined;

  return (
    <main className="mx-auto w-full max-w-5xl flex-1 px-6 py-12">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
        Facturación
      </h1>
      <p className="mt-2 max-w-2xl text-sm text-zinc-600">
        Una factura por celular. En efectivo se aplica el 42% de descuento sobre
        el precio de venta; a crédito directo el Banco BanQuito decide si el
        cliente es sujeto de crédito, cuánto puede comprar y otorga el crédito
        antes de emitir la factura.
      </p>

      {error ? (
        <div
          role="alert"
          className="mt-6 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700"
        >
          <p>{error}</p>
          {motivos && motivos.length > 0 ? (
            <ul className="mt-2 list-disc space-y-1 pl-5">
              {motivos.map((motivo) => (
                <li key={motivo.codigo}>
                  <span className="font-medium">{motivo.codigo}:</span>{" "}
                  {motivo.mensaje}
                </li>
              ))}
            </ul>
          ) : null}
        </div>
      ) : null}

      <form
        onSubmit={enviar}
        className="mt-8 rounded-xl border border-zinc-200 bg-white p-5"
      >
        <h2 className="text-base font-medium text-zinc-900">Nueva factura</h2>

        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <div>
            <label
              htmlFor="celular"
              className="block text-sm font-medium text-zinc-700"
            >
              Celular
            </label>
            <select
              id="celular"
              name="celular"
              required
              value={codCelular}
              onChange={(event) => {
                setCodCelular(event.target.value);
                limpiarEvaluacion();
              }}
              className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
            >
              <option value="">
                {cargando ? "Cargando catálogo..." : "Seleccione un celular"}
              </option>
              {celulares.map((row) => (
                <option key={row.codCelular} value={row.codCelular}>
                  {row.marca} {row.modelo} — {usd.format(row.precio)}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label
              htmlFor="cedula"
              className="block text-sm font-medium text-zinc-700"
            >
              Cédula del cliente
            </label>
            <input
              id="cedula"
              name="cedula"
              type="text"
              required
              inputMode="numeric"
              maxLength={10}
              pattern="\d{10}"
              placeholder="1712345678"
              value={cedula}
              onChange={(event) => {
                setCedula(event.target.value.trim());
                limpiarEvaluacion();
              }}
              className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
            />
          </div>

          <div>
            <label
              htmlFor="nombre"
              className="block text-sm font-medium text-zinc-700"
            >
              Nombre del cliente
            </label>
            <input
              id="nombre"
              name="nombre"
              type="text"
              required
              maxLength={100}
              value={nombre}
              onChange={(event) => setNombre(event.target.value)}
              className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
            />
          </div>

          <fieldset>
            <legend className="block text-sm font-medium text-zinc-700">
              Forma de pago
            </legend>
            <div className="mt-2 flex gap-4 text-sm text-zinc-800">
              <label className="flex items-center gap-2">
                <input
                  type="radio"
                  name="formaPago"
                  value="EFE"
                  checked={formaPago === "EFE"}
                  onChange={() => {
                    setFormaPago("EFE");
                    limpiarEvaluacion();
                  }}
                  className="h-4 w-4"
                />
                Efectivo
              </label>
              <label className="flex items-center gap-2">
                <input
                  type="radio"
                  name="formaPago"
                  value="CRE"
                  checked={formaPago === "CRE"}
                  onChange={() => {
                    setFormaPago("CRE");
                    limpiarEvaluacion();
                  }}
                  className="h-4 w-4"
                />
                Crédito directo
              </label>
            </div>
          </fieldset>
        </div>

        {formaPago === "EFE" ? (
          <div className="mt-6 rounded-lg border border-zinc-200 bg-zinc-50 p-4 text-sm">
            <h3 className="font-medium text-zinc-900">
              Descuento por pago en efectivo
            </h3>
            <dl className="mt-2 grid max-w-sm gap-1">
              <div className="flex justify-between gap-6">
                <dt className="text-zinc-600">Subtotal</dt>
                <dd className="tabular-nums text-zinc-900">
                  {usd.format(precio)}
                </dd>
              </div>
              <div className="flex justify-between gap-6">
                <dt className="text-zinc-600">Descuento (42%)</dt>
                <dd className="tabular-nums text-zinc-900">
                  −{usd.format(descuentoEfectivo(precio))}
                </dd>
              </div>
              <div className="flex justify-between gap-6 border-t border-zinc-300 pt-1 font-medium">
                <dt className="text-zinc-900">Total a pagar</dt>
                <dd className="tabular-nums text-zinc-900">
                  {usd.format(totalEfectivo(precio))}
                </dd>
              </div>
            </dl>
          </div>
        ) : (
          <div className="mt-6 rounded-lg border border-zinc-200 bg-zinc-50 p-4 text-sm">
            <div className="flex flex-wrap items-end gap-4">
              <div>
                <label
                  htmlFor="cuotas"
                  className="block text-sm font-medium text-zinc-700"
                >
                  Número de cuotas
                </label>
                <select
                  id="cuotas"
                  name="cuotas"
                  value={cuotas}
                  onChange={(event) => setCuotas(event.target.value)}
                  className="mt-1 w-40 rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none focus:border-zinc-500"
                >
                  {CUOTAS.map((numero) => (
                    <option key={numero} value={numero}>
                      {numero} cuotas
                    </option>
                  ))}
                </select>
              </div>

              <button
                type="button"
                onClick={() => void verificarCredito()}
                disabled={verificando || !cedulaValida || !celular}
                className="rounded-md border border-zinc-300 bg-white px-3 py-2 font-medium text-zinc-700 transition-colors hover:bg-zinc-100 disabled:opacity-60"
              >
                {verificando ? "Verificando..." : "Verificar crédito"}
              </button>

              <p className="text-zinc-600">
                Monto del crédito:{" "}
                <span className="tabular-nums text-zinc-900">
                  {usd.format(precio)}
                </span>
              </p>
            </div>

            {evaluacion ? (
              <div className="mt-4 border-t border-zinc-300 pt-4">
                <p className="font-medium text-zinc-900">
                  {evaluacion.sujetoCredito
                    ? `${evaluacion.nombre ?? "El cliente"} es sujeto de crédito`
                    : "El cliente no es sujeto de crédito"}
                </p>

                {evaluacion.motivos.length > 0 ? (
                  <ul className="mt-2 list-disc space-y-1 pl-5 text-red-700">
                    {evaluacion.motivos.map((motivo) => (
                      <li key={motivo.codigo}>
                        <span className="font-medium">{motivo.codigo}:</span>{" "}
                        {motivo.mensaje}
                      </li>
                    ))}
                  </ul>
                ) : null}

                <p className="mt-2 text-zinc-700">
                  Monto máximo aprobado:{" "}
                  <span className="tabular-nums">
                    {evaluacion.montoMaximo === null
                      ? "no aplica"
                      : usd.format(evaluacion.montoMaximo)}
                  </span>
                </p>

                <p
                  className={
                    evaluacion.aprobable
                      ? "mt-1 font-medium text-emerald-700"
                      : "mt-1 font-medium text-red-700"
                  }
                >
                  {evaluacion.aprobable
                    ? "La compra se puede facturar a crédito."
                    : "La compra no se puede facturar a crédito."}
                </p>
              </div>
            ) : null}
          </div>
        )}

        <div className="mt-6">
          <button
            type="submit"
            disabled={!puedeFacturar}
            className="rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-zinc-700 disabled:opacity-60"
          >
            {facturando ? "Facturando..." : "Facturar"}
          </button>
        </div>
      </form>

      {factura ? (
        <div className="mt-8">
          <h2 className="mb-3 text-base font-medium text-zinc-900">
            Factura emitida
          </h2>
          <FacturaResumen factura={factura} credito={factura.credito} />
        </div>
      ) : null}
    </main>
  );
}
