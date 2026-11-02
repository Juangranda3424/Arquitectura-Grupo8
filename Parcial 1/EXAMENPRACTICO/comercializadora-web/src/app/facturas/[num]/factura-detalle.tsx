"use client";

import { use, useEffect, useState } from "react";
import { FacturaResumen } from "@/components/factura-resumen";
import { apiRequest, errorMessage } from "@/lib/api";
import type { Amortizacion, Factura } from "@/lib/types";

/**
 * Loads one invoice and, when it was sold on credit, its amortization table
 * from `/api/facturas/:num/amortizacion` (the bank's numbers, rubric 11).
 *
 * The route segment is a client hook (`use(params)`), so the page that renders
 * this component wraps it in a `Suspense` boundary.
 */
export function FacturaDetalle({
  params,
}: {
  params: Promise<{ num: string }>;
}) {
  const { num } = use(params);

  const [factura, setFactura] = useState<Factura | null>(null);
  const [credito, setCredito] = useState<Amortizacion | null>(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [errorCredito, setErrorCredito] = useState<string | null>(null);

  useEffect(() => {
    let cancelado = false;

    const cargar = async () => {
      try {
        const data = await apiRequest<Factura>(`/api/facturas/${num}`);

        if (cancelado) return;

        setFactura(data);
        setCargando(false);

        if (data.formaPago !== "CRE") {
          return;
        }

        try {
          const tabla = await apiRequest<Amortizacion>(
            `/api/facturas/${num}/amortizacion`,
          );

          if (!cancelado) {
            setCredito(tabla);
          }
        } catch (tableError) {
          if (!cancelado) {
            setErrorCredito(errorMessage(tableError));
          }
        }
      } catch (loadError) {
        if (cancelado) return;
        setError(errorMessage(loadError));
        setCargando(false);
      }
    };

    void cargar();

    return () => {
      cancelado = true;
    };
  }, [num]);

  if (cargando) {
    return <p className="mt-6 text-sm text-zinc-600">Cargando factura...</p>;
  }

  if (error !== null) {
    return (
      <p
        role="alert"
        className="mt-6 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700"
      >
        {error}
      </p>
    );
  }

  if (factura === null) {
    return (
      <p className="mt-6 text-sm text-zinc-600">No se encontró la factura.</p>
    );
  }

  return (
    <div className="mt-6">
      <FacturaResumen
        factura={factura}
        credito={credito}
        cargandoCredito={
          factura.formaPago === "CRE" && credito === null && errorCredito === null
        }
        errorCredito={errorCredito}
      />
    </div>
  );
}
