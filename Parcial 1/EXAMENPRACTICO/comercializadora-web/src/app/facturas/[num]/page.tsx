import Link from "next/link";
import { Suspense } from "react";
import { FacturaDetalle } from "./factura-detalle";

/**
 * Invoice detail (S11) with the amortization table of its credit (S12).
 *
 * The `num` of the route is runtime data, so the page keeps a static shell
 * (the title and the back link) and reads the invoice inside a `Suspense`
 * boundary, as `cacheComponents` requires for a dynamic segment without
 * `generateStaticParams`.
 */
export default function FacturaPage({
  params,
}: PageProps<"/facturas/[num]">) {
  return (
    <main className="mx-auto w-full max-w-5xl flex-1 px-6 py-12">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
          Detalle de factura
        </h1>
        <Link
          href="/facturas"
          className="rounded-md border border-zinc-300 px-3 py-1.5 text-sm font-medium text-zinc-700 transition-colors hover:bg-zinc-100"
        >
          Volver a facturas
        </Link>
      </div>

      <Suspense
        fallback={
          <p className="mt-6 text-sm text-zinc-600">Cargando factura...</p>
        }
      >
        <FacturaDetalle params={params} />
      </Suspense>
    </main>
  );
}
