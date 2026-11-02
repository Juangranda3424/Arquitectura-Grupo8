import Link from "next/link";

const sections = [
  {
    href: "/catalogo",
    title: "Catálogo",
    description: "Mantenimiento de los teléfonos celulares que están a la venta.",
  },
  {
    href: "/facturacion",
    title: "Facturación",
    description:
      "Ventas en efectivo con 42% de descuento o a crédito directo con el Banco.",
  },
  {
    href: "/facturas",
    title: "Facturas",
    description: "Facturas emitidas y tabla de amortización de cada crédito.",
  },
];

export default function Home() {
  return (
    <main className="mx-auto w-full max-w-5xl flex-1 px-6 py-12">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
        Panel de la comercializadora
      </h1>
      <p className="mt-2 max-w-2xl text-sm text-zinc-600">
        Seleccione un módulo para continuar.
      </p>

      <ul className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {sections.map((section) => (
          <li key={section.href}>
            <Link
              href={section.href}
              className="flex h-full flex-col rounded-xl border border-zinc-200 bg-white p-5 transition-colors hover:border-zinc-300 hover:bg-zinc-100"
            >
              <span className="text-base font-medium text-zinc-900">
                {section.title}
              </span>
              <span className="mt-2 text-sm text-zinc-600">
                {section.description}
              </span>
            </Link>
          </li>
        ))}
      </ul>
    </main>
  );
}
