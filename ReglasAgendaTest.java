package com.devsenior.sillon.cita;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import com.devsenior.sillon.compartido.ReglaNegocioException;
import com.devsenior.sillon.salon.ConfiguracionSalon;
import org.junit.jupiter.api.Test;

class ReglasAgendaTest {

    private static final ZoneId ZONA_NEGOCIO = ZoneId.of("America/Bogota");
    private static final LocalDate HOY = LocalDate.of(2026, 11, 9);

    private final Clock relojFijo = Clock.fixed(
            Instant.parse("2026-11-09T13:00:00Z"),
            ZONA_NEGOCIO
    );

    private final ReglasAgenda reglas = new ReglasAgenda(relojFijo);
    private final ConfiguracionSalon configuracion = new ConfiguracionSalon(
            LocalTime.of(8, 0),
            LocalTime.of(19, 0),
            false,
            30
    );

    @Test
    void aceptaUnaCitaDentroDelHorario() {
        reglas.validarHorario(HOY.atTime(10, 0), HOY.atTime(10, 45), configuracion);
    }

    @Test
    void rechazaUnaCitaQueTerminaDespuesDelCierre() {
        assertThrows(ReglaNegocioException.class, () ->
                reglas.validarHorario(HOY.atTime(18, 45), HOY.atTime(19, 30), configuracion));
    }

    @Test
    void rechazaUnaCitaQueEmpiezaAntesDeLaApertura() {
        assertThrows(ReglaNegocioException.class, () ->
                reglas.validarHorario(HOY.atTime(7, 45), HOY.atTime(8, 30), configuracion));
    }

    @Test
    void rechazaUnaCitaEnDomingoCerrado() {
        LocalDate domingo = LocalDate.of(2026, 11, 15);
        assertThrows(ReglaNegocioException.class, () ->
                reglas.validarHorario(
                        domingo.atTime(10, 0), domingo.atTime(10, 45), configuracion));
    }

    @Test
    void unaCitaQueTerminaCuandoEmpiezaOtraNoSeCruza() {
        assertTrue(!reglas.seCruzan(
                HOY.atTime(10, 0),
                HOY.atTime(10, 45),
                HOY.atTime(10, 45),
                HOY.atTime(11, 30)
        ));
    }

    @Test
    void calculaHorariosLibresExcluyendoCrucesYEstadosQueNoOcupanAgenda() {
        List<ReglasAgenda.IntervaloOcupado> ocupadas = List.of(
                new ReglasAgenda.IntervaloOcupado(
                        HOY.atTime(10, 0), HOY.atTime(10, 45), EstadoCita.CONFIRMADA),
                new ReglasAgenda.IntervaloOcupado(
                        HOY.atTime(12, 0), HOY.atTime(12, 45), EstadoCita.CANCELADA)
        );

        List<LocalTime> horarios = reglas.calcularHorariosLibres(
                HOY, configuracion, 45, ocupadas);

        assertTrue(!horarios.contains(LocalTime.of(9, 30)));
        assertTrue(!horarios.contains(LocalTime.of(10, 0)));
        assertTrue(!horarios.contains(LocalTime.of(10, 30)));
        assertTrue(horarios.contains(LocalTime.of(11, 0)));
        assertTrue(horarios.contains(LocalTime.of(12, 0)));
    }

    @Test
    void noOfreceHorasQueYaPasaronHoy() {
        List<LocalTime> horarios = reglas.calcularHorariosLibres(
                HOY, configuracion, 45, List.of());

        assertTrue(horarios.stream().allMatch(hora ->
                !LocalDateTime.of(HOY, hora).isBefore(LocalDateTime.now(relojFijo))));
    }

    @Test
    void noOfreceHorariosCuandoElServicioDuraMasQueLaJornada() {
        // Evita que LocalTime.minusMinutes envuelva la hora y genere turnos fuera del cierre.
        assertTrue(reglas.calcularHorariosLibres(
                HOY, configuracion, 12 * 60, List.of()).isEmpty());
    }

    @Test
    void unDomingoCerradoNoTieneHorariosDisponibles() {
        LocalDate domingo = LocalDate.of(2026, 11, 15);

        assertTrue(reglas.calcularHorariosLibres(
                domingo, configuracion, 45, List.of()).isEmpty());
    }
}
