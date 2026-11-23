package com.devsenior.sillon.cita;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EstadoCitaTest {

    @ParameterizedTest
    @CsvSource({
            "PENDIENTE, CONFIRMADA",
            "PENDIENTE, CANCELADA",
            "CONFIRMADA, COMPLETADA",
            "CONFIRMADA, CANCELADA",
            "CONFIRMADA, NO_ASISTIO"
    })
    void permiteLasCincoTransicionesDefinidas(
            EstadoCita estadoActual,
            EstadoCita destino) {
        assertTrue(estadoActual.puedeCambiarA(destino));
    }

    @ParameterizedTest
    @CsvSource({
            "PENDIENTE, COMPLETADA",
            "PENDIENTE, NO_ASISTIO",
            "CONFIRMADA, PENDIENTE",
            "COMPLETADA, PENDIENTE",
            "CANCELADA, CONFIRMADA",
            "NO_ASISTIO, CONFIRMADA"
    })
    void bloqueaTransicionesNoPermitidas(
            EstadoCita estadoActual,
            EstadoCita destino) {
        assertFalse(estadoActual.puedeCambiarA(destino));
    }

    @Test
    void soloLosEstadosPendienteYConfirmadaOcupanAgenda() {
        assertTrue(EstadoCita.PENDIENTE.ocupaAgenda());
        assertTrue(EstadoCita.CONFIRMADA.ocupaAgenda());
        assertFalse(EstadoCita.COMPLETADA.ocupaAgenda());
        assertFalse(EstadoCita.CANCELADA.ocupaAgenda());
        assertFalse(EstadoCita.NO_ASISTIO.ocupaAgenda());
    }
}
