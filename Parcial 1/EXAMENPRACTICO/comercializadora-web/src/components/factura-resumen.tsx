import { AmortizacionTable } from "@/components/amortizacion-table";
import { fechaHora, formaPagoLegible, usd } from "@/lib/format";
import type { CreditoFactura, Factura } from "@/lib/types";

/**
 * Summary of one issued invoice: customer, phone line, amounts and — when the
 * sale was on credit — the credit the bank granted with its amortization
 * table.
 *
 * Shared by the invoicing screen (right after `POST /api/facturas`) and the
 * invoice detail page. `credito` is passed separately because the detail page
 * reads the table from `/api/facturas/:num/amortizacion`, while the sale
 * already has it in the invoice response.
 */
export function FacturaResumen({
  factura,
  credito,
  cargandoCredito = false,
  errorCredito = null,
}: {
  factura: Factura;
  credito?: CreditoFactura | null;
  cargandoCredito?: boolean;
  errorCredito?: string | null;
}) {
  return (
    <section className="rounded-xl border border-zinc-200 bg-white p-5">
      <div className="flex flex-wrap items-baseline justify-between gap-2">
        <h2 className="text-base font-medium text-zinc-900">
          Factura #{factura.numFactura}
        </h2>
        <span className="text-sm text-zinc-500">{fechaHora(factura.fecha)}</span>
      </div>

      <dl className="mt-4 grid gap-4 text-sm sm:grid-cols-2">
        <div>
          <dt className="text-zinc-500">Cliente</dt>
          <dd className="mt-0.5 text-zinc-900">{factura.cliente.nombre}</dd>
          <dd className="text-zinc-600">
            Cédula {factura.cliente.cedula} · Cliente #{factura.cliente.codCliente}
          </dd>
        </div>
        <div>
          <dt className="text-zinc-500">Forma de pago</dt>
          <dd className="mt-0.5 text-zinc-900">
            {formaPagoLegible(factura.formaPago)}
          </dd>
        </div>
      </dl>

      <div className="mt-5 overflow-x-auto rounded-lg border border-zinc-200">
        <table className="w-full min-w-[24rem] border-collapse text-left text-sm">
          <thead className="bg-zinc-50 text-xs uppercase tracking-wide text-zinc-500">
            <tr>
              <th scope="col" className="px-3 py-2 font-medium">
                Celular
              </th>
              <th scope="col" className="px-3 py-2 text-right font-medium">
                Cantidad
              </th>
              <th scope="col" className="px-3 py-2 text-right font-medium">
                Precio unitario
              </th>
              <th scope="col" className="px-3 py-2 text-right font-medium">
                Subtotal
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-200">
            {factura.detalles.map((detalle) => (
              <tr key={detalle.codDetalle}>
                <td className="px-3 py-2 text-zinc-900">
                  {detalle.celular.marca} {detalle.celular.modelo}
                  {detalle.celular.descripcion ? (
                    <span className="block text-xs text-zinc-500">
                      {detalle.celular.descripcion}
                    </span>
                  ) : null}
                </td>
                <td className="px-3 py-2 text-right tabular-nums text-zinc-700">
                  {detalle.cantidad}
                </td>
                <td className="px-3 py-2 text-right tabular-nums text-zinc-700">
                  {usd.format(detalle.precioUnitario)}
                </td>
                <td className="px-3 py-2 text-right tabular-nums text-zinc-900">
                  {usd.format(detalle.subtotal)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <dl className="mt-4 ml-auto grid max-w-xs gap-1 text-sm">
        <div className="flex justify-between gap-6">
          <dt className="text-zinc-600">Subtotal</dt>
          <dd className="tabular-nums text-zinc-900">
            {usd.format(factura.subtotal)}
          </dd>
        </div>
        <div className="flex justify-between gap-6">
          <dt className="text-zinc-600">
            Descuento {factura.formaPago === "EFE" ? "(42%)" : ""}
          </dt>
          <dd className="tabular-nums text-zinc-900">
            {usd.format(factura.descuento)}
          </dd>
        </div>
        <div className="flex justify-between gap-6 border-t border-zinc-200 pt-1 font-medium">
          <dt className="text-zinc-900">Total</dt>
          <dd className="tabular-nums text-zinc-900">
            {usd.format(factura.total)}
          </dd>
        </div>
      </dl>

      {factura.formaPago === "CRE" ? (
        <div className="mt-6 border-t border-zinc-200 pt-5">
          <h3 className="text-sm font-medium text-zinc-900">
            Crédito directo en el Banco BanQuito
          </h3>
          <p className="mt-1 text-sm text-zinc-600">
            Crédito #{factura.codCredito} · {factura.numCuotas} cuotas de{" "}
            {usd.format(factura.valorCuota ?? 0)}
          </p>

          {cargandoCredito ? (
            <p className="mt-4 text-sm text-zinc-600">
              Consultando la tabla de amortización...
            </p>
          ) : errorCredito ? (
            <p
              role="alert"
              className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700"
            >
              {errorCredito}
            </p>
          ) : credito ? (
            <div className="mt-4">
              <p className="mb-2 text-xs uppercase tracking-wide text-zinc-500">
                Tabla de amortización · {credito.tasaAnual}% anual · cuota fija
                mensual
              </p>
              <AmortizacionTable
                cuotas={credito.tablaAmortizacion}
                saldoInicial={credito.monto}
              />
            </div>
          ) : null}
        </div>
      ) : null}
    </section>
  );
}
