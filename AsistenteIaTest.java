package com.devsenior.sillon.ia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.devsenior.sillon.compartido.ProveedorNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsistenteIaTest {

    private PropiedadesIa propiedades;

    @BeforeEach
    void configurar() {
        propiedades = new PropiedadesIa();
    }

    @Test
    void utilizaElProveedorDeRespaldoCuandoFallaElPrincipal() {
        ProveedorIa principal = mock(ProveedorIa.class);
        ProveedorIa respaldo = mock(ProveedorIa.class);
        when(principal.nombre()).thenReturn("GEMINI");
        when(respaldo.nombre()).thenReturn("CLAUDE");
        when(principal.generar("instrucciones", "mensaje"))
                .thenThrow(new ProveedorNoDisponibleException("sin respuesta"));
        when(respaldo.generar("instrucciones", "mensaje"))
                .thenReturn(new RespuestaProveedorIa("{\"mensaje\":\"ok\"}", "claude-test", 10, 5, 20));

        AsistenteIa asistente = new AsistenteIa(List.of(principal, respaldo), propiedades);
        RespuestaIa resultado = asistente.generar("instrucciones", "mensaje");

        assertEquals("{\"mensaje\":\"ok\"}", resultado.contenido());
        assertEquals("CLAUDE", resultado.factura().proveedor());
        verify(respaldo).generar("instrucciones", "mensaje");
    }

    @Test
    void devuelve503CuandoNingunProveedorResponde() {
        ProveedorIa principal = mock(ProveedorIa.class);
        ProveedorIa respaldo = mock(ProveedorIa.class);
        when(principal.nombre()).thenReturn("GEMINI");
        when(respaldo.nombre()).thenReturn("CLAUDE");
        when(principal.generar("i", "m")).thenThrow(new ProveedorNoDisponibleException("principal"));
        when(respaldo.generar("i", "m")).thenThrow(new ProveedorNoDisponibleException("respaldo"));

        AsistenteIa asistente = new AsistenteIa(List.of(principal, respaldo), propiedades);
        assertThrows(ProveedorNoDisponibleException.class, () -> asistente.generar("i", "m"));
        verify(principal).generar("i", "m");
        verify(respaldo).generar("i", "m");
    }

    @Test
    void noIntentaRespaldoCuandoElProveedorRespondioConContenidoNoUtilizable() {
        ProveedorIa principal = mock(ProveedorIa.class);
        ProveedorIa respaldo = mock(ProveedorIa.class);
        when(principal.nombre()).thenReturn("GEMINI");
        when(respaldo.nombre()).thenReturn("CLAUDE");
        when(principal.generar("i", "m"))
                .thenThrow(new com.devsenior.sillon.compartido.PasarelaException("respuesta inválida"));
        AsistenteIa asistente = new AsistenteIa(List.of(principal, respaldo), propiedades);
        assertThrows(com.devsenior.sillon.compartido.PasarelaException.class,
                () -> asistente.generar("i", "m"));
        verify(respaldo, never()).generar("i", "m");
    }

    @Test
    void calculaCostoEstimadoConLasTarifasConfiguradas() {
        propiedades.getCostos().setEntradaPorMillon(new java.math.BigDecimal("2"));
        propiedades.getCostos().setSalidaPorMillon(new java.math.BigDecimal("8"));

        ProveedorIa principal = mock(ProveedorIa.class);
        when(principal.nombre()).thenReturn("GEMINI");
        when(principal.generar("i", "m")).thenReturn(
                new RespuestaProveedorIa("{\"respuesta\":\"ok\"}", "modelo-prueba",
                        500_000, 250_000, 25));

        RespuestaIa resultado = new AsistenteIa(List.of(principal), propiedades).generar("i", "m");

        assertEquals(new java.math.BigDecimal("3.00000000"),
                resultado.factura().costoEstimadoUsd());
        assertEquals(500_000, resultado.factura().tokensEntrada());
        assertEquals(250_000, resultado.factura().tokensSalida());
    }
}
