package com.devsenior.sillon.cita;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.devsenior.sillon.compartido.PasarelaException;
import com.devsenior.sillon.compartido.ReglaNegocioException;
import com.devsenior.sillon.ia.AsistenteIa;
import com.devsenior.sillon.ia.BibliotecaPrompts;
import com.devsenior.sillon.ia.FacturaIa;
import com.devsenior.sillon.ia.LectorJsonIa;
import com.devsenior.sillon.ia.RespuestaIa;
import com.devsenior.sillon.ia.ServicioCreditosIa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class ServicioRecordatoriosTest {

    private ServicioCitas servicioCitas;
    private ServicioCreditosIa creditos;
    private BibliotecaPrompts prompts;
    private AsistenteIa asistente;
    private LectorJsonIa lector;
    private ServicioRecordatorios servicio;

    @BeforeEach
    void configurar() {
        servicioCitas = mock(ServicioCitas.class);
        creditos = mock(ServicioCreditosIa.class);
        prompts = mock(BibliotecaPrompts.class);
        asistente = mock(AsistenteIa.class);
        lector = mock(LectorJsonIa.class);
        servicio = new ServicioRecordatorios(servicioCitas, creditos, prompts, asistente, lector);
    }

    @Test
    void generaBorradorYConsumeDespuesDeLaRespuestaDeIa() {
        DatosRecordatorio datos = new DatosRecordatorio(9L, "Barbería", "Sara", "+573001112233",
                "Servicio: Corte (45 minutos)\\nEstilista: Sara\\nFecha: martes 20 de octubre\\nHora: 10:30 a. m.\\nValor: $35.000\\nDirección: Calle 10");
        FacturaIa factura = new FacturaIa("GEMINI", "modelo", 10, 20, java.math.BigDecimal.ZERO, 30);
        when(servicioCitas.obtenerDatosRecordatorio(2L, 9L)).thenReturn(datos);
        when(prompts.cargar("recordatorio.txt", Map.of(
                "salon", "Barbería", "cliente", "Sara", "datos", datos.datosVerificados())))
                .thenReturn("prompt");
        when(asistente.generar("prompt", "Redacta el borrador de recordatorio con los datos verificados proporcionados."))
                .thenReturn(new RespuestaIa("{json}", factura));
        when(creditos.consumir(2L)).thenReturn(9);
        when(lector.leerMensaje("{json}")).thenReturn("Hola Sara, su cita es a las 10:30.");

        RecordatorioRespuesta respuesta = servicio.generar(2L, 9L);

        assertEquals(9L, respuesta.citaId());
        assertEquals(9, respuesta.creditosRestantes());
        assertEquals("Hola Sara, su cita es a las 10:30.", respuesta.mensaje());
        assertEquals(factura, respuesta.factura());
        InOrder orden = inOrder(servicioCitas, creditos, asistente, lector);
        orden.verify(servicioCitas).obtenerDatosRecordatorio(2L, 9L);
        orden.verify(creditos).verificar(2L);
        orden.verify(asistente).generar("prompt", "Redacta el borrador de recordatorio con los datos verificados proporcionados.");
        orden.verify(creditos).consumir(2L);
        orden.verify(lector).leerMensaje("{json}");
    }

    @Test
    void respuestaJsonInvalidaSeRechazaDespuesDeConsumirCredito() {
        DatosRecordatorio datos = new DatosRecordatorio(9L, "Barbería", "Sara", "+573001112233", "datos");
        when(servicioCitas.obtenerDatosRecordatorio(2L, 9L)).thenReturn(datos);
        when(prompts.cargar(org.mockito.ArgumentMatchers.eq("recordatorio.txt"), anyMap())).thenReturn("prompt");
        when(asistente.generar(org.mockito.ArgumentMatchers.eq("prompt"), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(new RespuestaIa("{}", new FacturaIa("GEMINI", "modelo", 0, 0,
                        java.math.BigDecimal.ZERO, 1)));
        when(creditos.consumir(2L)).thenReturn(9);
        when(lector.leerMensaje("{}")).thenThrow(new PasarelaException("sin mensaje"));
        assertThrows(PasarelaException.class, () -> servicio.generar(2L, 9L));
        verify(creditos).consumir(2L);
    }

    @Test
    void citaNoElegibleFallaAntesDeVerificarCreditosOLlamarAIA() {
        when(servicioCitas.obtenerDatosRecordatorio(2L, 9L))
                .thenThrow(new ReglaNegocioException("La cita ya está completada"));

        assertThrows(ReglaNegocioException.class, () -> servicio.generar(2L, 9L));

        verifyNoInteractions(creditos, prompts, asistente, lector);
    }

    @Test
    void mensajeDeMasDe700CaracteresSeRechazaDespuesDeConsumirCredito() {
        DatosRecordatorio datos = new DatosRecordatorio(
                9L, "Barbería", "Sara", "+573001112233", "datos");
        when(servicioCitas.obtenerDatosRecordatorio(2L, 9L)).thenReturn(datos);
        when(prompts.cargar(org.mockito.ArgumentMatchers.eq("recordatorio.txt"), anyMap()))
                .thenReturn("prompt");
        when(asistente.generar(org.mockito.ArgumentMatchers.eq("prompt"),
                org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(new RespuestaIa("json", new FacturaIa(
                        "GEMINI", "modelo", 10, 20, java.math.BigDecimal.ZERO, 10)));
        when(creditos.consumir(2L)).thenReturn(9);
        when(lector.leerMensaje("json")).thenReturn("x".repeat(701));

        assertThrows(PasarelaException.class, () -> servicio.generar(2L, 9L));
        verify(creditos).consumir(2L);
    }

    @Test
    void proveedorNoDisponibleNoConsumeCredito() {
        DatosRecordatorio datos = new DatosRecordatorio(
                9L, "Barbería", "Sara", "+573001112233", "datos");
        when(servicioCitas.obtenerDatosRecordatorio(2L, 9L)).thenReturn(datos);
        when(prompts.cargar(org.mockito.ArgumentMatchers.eq("recordatorio.txt"), anyMap()))
                .thenReturn("prompt");
        when(asistente.generar(org.mockito.ArgumentMatchers.eq("prompt"),
                org.mockito.ArgumentMatchers.anyString()))
                .thenThrow(new com.devsenior.sillon.compartido.ProveedorNoDisponibleException("sin proveedores"));

        assertThrows(com.devsenior.sillon.compartido.ProveedorNoDisponibleException.class,
                () -> servicio.generar(2L, 9L));
        verify(creditos).verificar(2L);
        org.mockito.Mockito.verify(creditos, org.mockito.Mockito.never()).consumir(2L);
        verify(lector, org.mockito.Mockito.never()).leerMensaje(org.mockito.ArgumentMatchers.anyString());
    }
}
