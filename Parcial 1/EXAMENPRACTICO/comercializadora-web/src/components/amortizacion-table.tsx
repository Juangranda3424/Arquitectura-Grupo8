import { monto } from "@/lib/format";
import type { CuotaAmortizacion } from "@/lib/types";

/**
 * Amortization table of a credit (rubrics 7, 10 and 11).
 *
 * Columns are the ones the statement asks for — `# Cuota | Valor Cuota |
 * Interés Pagado | Capital Pagado | Saldo` — and, like the example of the
 * statement, row 0 shows the initial balance before the first quota.
 *
 * It is a plain component (no hooks), so both the invoicing screen and the
 * invoice detail can render it.
 */
export function AmortizacionTable({
  cuotas,
  saldoInicial,
}: {
  cuotas: CuotaAmortizacion[];
  /** Amount of the credit, the balance before the first quota. */
  saldoInicial: number;
}) {
  return (
    <div className="overflow-x-auto rounded-xl border border-zinc-200 bg-white">
      <table className="w-full min-w-[34rem] border-collapse text-left text-sm">
        <thead className="bg-zinc-50 text-xs uppercase tracking-wide text-zinc-500">
          <tr>
            <th scope="col" className="px-4 py-3 font-medium">
              # Cuota
            </th>
            <th scope="col" className="px-4 py-3 text-right font-medium">
              Valor Cuota
            </th>
            <th scope="col" className="px-4 py-3 text-right font-medium">
              Interés Pagado
            </th>
            <th scope="col" className="px-4 py-3 text-right font-medium">
              Capital Pagado
            </th>
            <th scope="col" className="px-4 py-3 text-right font-medium">
              Saldo
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-zinc-200">
          <tr className="bg-zinc-50/60">
            <td className="px-4 py-2 tabular-nums text-zinc-500">0</td>
            <td className="px-4 py-2 text-right text-zinc-400">—</td>
            <td className="px-4 py-2 text-right text-zinc-400">—</td>
            <td className="px-4 py-2 text-right text-zinc-400">—</td>
            <td className="px-4 py-2 text-right tabular-nums text-zinc-900">
              {monto.format(saldoInicial)}
            </td>
          </tr>
          {cuotas.map((cuota) => (
            <tr key={cuota.numCuota}>
              <td className="px-4 py-2 tabular-nums text-zinc-700">
                {cuota.numCuota}
              </td>
              <td className="px-4 py-2 text-right tabular-nums text-zinc-900">
                {monto.format(cuota.valorCuota)}
              </td>
              <td className="px-4 py-2 text-right tabular-nums text-zinc-700">
                {monto.format(cuota.interesPagado)}
              </td>
              <td className="px-4 py-2 text-right tabular-nums text-zinc-700">
                {monto.format(cuota.capitalPagado)}
              </td>
              <td className="px-4 py-2 text-right tabular-nums text-zinc-900">
                {monto.format(cuota.saldo)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
