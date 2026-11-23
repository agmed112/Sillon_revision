package com.devsenior.sillon.cita;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.devsenior.sillon.cliente.Cliente;
import com.devsenior.sillon.cliente.ServicioClientes;
import com.devsenior.sillon.compartido.AccionNoPermitidaException;
import com.devsenior.sillon.compartido.RecursoNoEncontradoException;
import com.devsenior.sillon.compartido.ReglaNegocioException;
import com.devsenior.sillon.salon.ConfiguracionSalon;
import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import com.devsenior.sillon.servicio.Servicio;
import com.devsenior.sillon.servicio.ServicioRepository;
import com.devsenior.sillon.usuario.Rol;
import com.devsenior.sillon.usuario.Usuario;
import com.devsenior.sillon.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioCitasTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final Long SALON_ID = 7L;
    private static final Long SERVICIO_ID = 4L;
    private static final Long ESTILISTA_ID = 12L;

    private CitaRepository citaRepository;
    private SalonRepository salonRepository;
    private ServicioRepository servicioRepository;
    private UsuarioRepository usuarioRepository;
    private ServicioClientes servicioClientes;
    private ReglasAgenda reglasAgenda;
    private CitaMapper citaMapper;
    private ServicioCitas servicio;

    @BeforeEach
    void configurar() {
        citaRepository = mock(CitaRepository.class);
        salonRepository = mock(SalonRepository.class);
        servicioRepository = mock(ServicioRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        servicioClientes = mock(ServicioClientes.class);
        citaMapper = mock(CitaMapper.class);

        Clock reloj = Clock.fixed(
                Instant.parse("2026-11-09T13:00:00Z"),
                ZONA
        );
        reglasAgenda = new ReglasAgenda(reloj);

        servicio = new ServicioCitas(
                citaRepository,
                salonRepository,
                servicioRepository,
                usuarioRepository,
                servicioClientes,
                reglasAgenda,
                citaMapper,
                reloj
        );
    }

    @Test
    void agendarBloqueaLaFilaDelEstilistaYConservaPrecioYOrigen() {
        Salon salon = salon();
        Servicio servicioCatalogo = servicioCatalogo();
        Usuario estilista = estilista();
        Cliente cliente = mock(Cliente.class);
        CitaRespuesta respuestaEsperada = mock(CitaRespuesta.class);

        when(salonRepository.findById(SALON_ID)).thenReturn(Optional.of(salon));
        when(servicioRepository.findByIdAndSalonId(SERVICIO_ID, SALON_ID))
                .thenReturn(Optional.of(servicioCatalogo));
        when(usuarioRepository.findByIdAndSalonIdAndRolForUpdate(
                ESTILISTA_ID, SALON_ID, Rol.ESTILISTA))
                .thenReturn(Optional.of(estilista));
        when(citaRepository.buscarCruces(
                eq(SALON_ID), eq(ESTILISTA_ID), any(), any(), any()))
                .thenReturn(List.of());
        when(servicioClientes.buscarOCrear(salon, "Camilo", "+57 300 123 4567"))
                .thenReturn(cliente);
        when(citaRepository.saveAndFlush(any(Cita.class)))
                .thenReturn(mock(Cita.class));
        when(citaMapper.aRespuesta(any(Cita.class))).thenReturn(respuestaEsperada);

        CitaRespuesta respuesta = servicio.agendarEnSalon(SALON_ID, solicitud());

        assertEquals(respuestaEsperada, respuesta);
        verify(usuarioRepository).findByIdAndSalonIdAndRolForUpdate(
                ESTILISTA_ID, SALON_ID, Rol.ESTILISTA);

        var captor = org.mockito.ArgumentCaptor.forClass(Cita.class);
        verify(citaRepository).saveAndFlush(captor.capture());
        Cita guardada = captor.getValue();
        assertEquals(EstadoCita.CONFIRMADA, guardada.getEstado());
        assertEquals(OrigenCita.SALON, guardada.getOrigen());
        assertEquals(35000L, guardada.getPrecio());
        assertEquals(LocalDateTime.of(2026, 11, 10, 10, 0), guardada.getInicio());
        assertEquals(LocalDateTime.of(2026, 11, 10, 10, 45), guardada.getFin());
    }

    @Test
    void cruceDeAgendaSeRechazaAntesDeCrearORecuperarCliente() {
        Salon salon = salon();
        Servicio servicioCatalogo = servicioCatalogo();
        Usuario estilista = estilista();

        when(salonRepository.findById(SALON_ID)).thenReturn(Optional.of(salon));
        when(servicioRepository.findByIdAndSalonId(SERVICIO_ID, SALON_ID))
                .thenReturn(Optional.of(servicioCatalogo));
        when(usuarioRepository.findByIdAndSalonIdAndRolForUpdate(
                ESTILISTA_ID, SALON_ID, Rol.ESTILISTA))
                .thenReturn(Optional.of(estilista));
        when(citaRepository.buscarCruces(
                eq(SALON_ID), eq(ESTILISTA_ID), any(), any(), any()))
                .thenReturn(List.of(mock(Cita.class)));

        assertThrows(ReglaNegocioException.class,
                () -> servicio.agendarEnSalon(SALON_ID, solicitud()));

        verify(servicioClientes, never()).buscarOCrear(any(), any(), any());
        verify(citaRepository, never()).saveAndFlush(any(Cita.class));
    }

    @Test
    void citaEnElPasadoSeRechazaAntesDeConsultarCruces() {
        prepararDatosParaAgendar(true);

        assertThrows(ReglaNegocioException.class, () ->
                servicio.agendarEnSalon(
                        SALON_ID,
                        solicitud(LocalDateTime.of(2026, 11, 8, 10, 0))));

        verify(citaRepository, never()).buscarCruces(any(), any(), any(), any(), any());
        verify(servicioClientes, never()).buscarOCrear(any(), any(), any());
        verify(citaRepository, never()).saveAndFlush(any(Cita.class));
    }

    @Test
    void estilistaQueNoOfreceElServicioSeRechazaAntesDeBuscarCruces() {
        prepararDatosParaAgendar(false);

        assertThrows(ReglaNegocioException.class, () ->
                servicio.agendarEnSalon(SALON_ID, solicitud()));

        verify(citaRepository, never()).buscarCruces(any(), any(), any(), any(), any());
        verify(servicioClientes, never()).buscarOCrear(any(), any(), any());
        verify(citaRepository, never()).saveAndFlush(any(Cita.class));
    }

    @Test
    void reservaEnLineaNacePendienteConOrigenEnLinea() {
        prepararDatosParaAgendar(true);
        Cliente cliente = mock(Cliente.class);
        when(servicioClientes.buscarOCrear(any(), eq("Camilo"), eq("+57 300 123 4567")))
                .thenReturn(cliente);
        when(citaRepository.saveAndFlush(any(Cita.class))).thenReturn(mock(Cita.class));
        CitaRespuesta respuestaEsperada = mock(CitaRespuesta.class);
        when(citaMapper.aRespuesta(any(Cita.class))).thenReturn(respuestaEsperada);

        assertEquals(respuestaEsperada,
                servicio.reservarEnLinea(SALON_ID, solicitud()));

        var captor = org.mockito.ArgumentCaptor.forClass(Cita.class);
        verify(citaRepository).saveAndFlush(captor.capture());
        assertEquals(EstadoCita.PENDIENTE, captor.getValue().getEstado());
        assertEquals(OrigenCita.EN_LINEA, captor.getValue().getOrigen());
    }

    @Test
    void estilistaNoPuedeModificarLaCitaDeOtroEstilista() {
        Cita cita = citaExistente(15L, EstadoCita.CONFIRMADA);
        when(citaRepository.findByIdAndSalonIdForUpdate(99L, SALON_ID)).thenReturn(Optional.of(cita));

        assertThrows(RecursoNoEncontradoException.class, () ->
                servicio.cambiarEstado(
                        SALON_ID, ESTILISTA_ID, Rol.ESTILISTA, 99L, EstadoCita.COMPLETADA));

        verify(citaRepository, never()).saveAndFlush(any(Cita.class));
    }

    @Test
    void estilistaNoPuedeCancelarSuPropiaCita() {
        Cita cita = citaExistente(ESTILISTA_ID, EstadoCita.CONFIRMADA);
        when(citaRepository.findByIdAndSalonIdForUpdate(99L, SALON_ID)).thenReturn(Optional.of(cita));

        assertThrows(AccionNoPermitidaException.class, () ->
                servicio.cambiarEstado(
                        SALON_ID, ESTILISTA_ID, Rol.ESTILISTA, 99L, EstadoCita.CANCELADA));

        verify(citaRepository, never()).saveAndFlush(any(Cita.class));
    }

    @Test
    void estilistaPuedeCompletarSuPropiaCitaConfirmada() {
        Cita cita = citaExistente(ESTILISTA_ID, EstadoCita.CONFIRMADA);
        CitaRespuesta respuestaEsperada = mock(CitaRespuesta.class);
        when(citaRepository.findByIdAndSalonIdForUpdate(99L, SALON_ID)).thenReturn(Optional.of(cita));
        when(citaRepository.saveAndFlush(cita)).thenReturn(cita);
        when(citaMapper.aRespuesta(cita)).thenReturn(respuestaEsperada);

        assertEquals(respuestaEsperada, servicio.cambiarEstado(
                SALON_ID, ESTILISTA_ID, Rol.ESTILISTA, 99L, EstadoCita.COMPLETADA));

        assertEquals(EstadoCita.COMPLETADA, cita.getEstado());
        verify(citaRepository).findByIdAndSalonIdForUpdate(99L, SALON_ID);
        verify(citaRepository).saveAndFlush(cita);
    }

    @Test
    void preparaLosSeisDatosVerificadosEnEspanolColombiano() {
        Cita cita = mock(Cita.class);
        Salon salon = mock(Salon.class);
        Cliente cliente = mock(Cliente.class);
        Servicio servicioCatalogo = mock(Servicio.class);
        Usuario estilista = mock(Usuario.class);

        when(citaRepository.findByIdAndSalonId(99L, SALON_ID)).thenReturn(Optional.of(cita));
        when(cita.getEstado()).thenReturn(EstadoCita.CONFIRMADA);
        when(cita.getInicio()).thenReturn(LocalDateTime.of(2026, 11, 10, 10, 30));
        when(cita.getPrecio()).thenReturn(35000L);
        when(cita.getId()).thenReturn(99L);
        when(cita.getSalon()).thenReturn(salon);
        when(cita.getCliente()).thenReturn(cliente);
        when(cita.getServicio()).thenReturn(servicioCatalogo);
        when(cita.getEstilista()).thenReturn(estilista);
        when(salon.getNombre()).thenReturn("Barbería Sillón");
        when(salon.getDireccion()).thenReturn("Calle 10 # 43-20");
        when(salon.getCiudad()).thenReturn("Medellín");
        when(cliente.getNombre()).thenReturn("Sara Mejía");
        when(cliente.getTelefono()).thenReturn("3001112233");
        when(servicioCatalogo.getNombre()).thenReturn("Corte clásico");
        when(servicioCatalogo.getDuracionMinutos()).thenReturn(45);
        when(estilista.getNombre()).thenReturn("Mateo Gil");

        DatosRecordatorio datos = servicio.obtenerDatosRecordatorio(SALON_ID, 99L);

        assertEquals("Barbería Sillón", datos.nombreSalon());
        assertEquals("Sara Mejía", datos.nombreCliente());
        assertEquals("3001112233", datos.telefonoCliente());
        assertTrue(datos.datosVerificados().contains("Servicio: Corte clásico (45 minutos)"));
        assertTrue(datos.datosVerificados().contains("Estilista: Mateo Gil"));
        assertTrue(datos.datosVerificados().contains("Fecha: martes 10 de noviembre"));
        assertTrue(datos.datosVerificados().contains("Hora: 10:30 a. m."));
        assertTrue(datos.datosVerificados().contains("Valor: $35.000"));
        assertTrue(datos.datosVerificados().contains("Dirección: Calle 10 # 43-20, Medellín"));
        assertFalse(datos.datosVerificados().contains("\u202f"));
    }

    @Test
    void noPreparaRecordatorioParaCitaCompletada() {
        Cita cita = mock(Cita.class);
        when(citaRepository.findByIdAndSalonId(99L, SALON_ID)).thenReturn(Optional.of(cita));
        when(cita.getEstado()).thenReturn(EstadoCita.COMPLETADA);

        assertThrows(ReglaNegocioException.class,
                () -> servicio.obtenerDatosRecordatorio(SALON_ID, 99L));
    }

    @Test
    void transicionDeEstadoInvalidaNoSeGuarda() {
        Cita cita = citaExistente(ESTILISTA_ID, EstadoCita.CONFIRMADA);
        when(citaRepository.findByIdAndSalonIdForUpdate(99L, SALON_ID)).thenReturn(Optional.of(cita));

        assertThrows(ReglaNegocioException.class, () ->
                servicio.cambiarEstado(
                        SALON_ID, 3L, Rol.DUENO, 99L, EstadoCita.PENDIENTE));

        verify(citaRepository, never()).saveAndFlush(any(Cita.class));
    }

    private void prepararDatosParaAgendar(boolean ofreceServicio) {
        Salon salon = salon();
        Servicio servicioCatalogo = servicioCatalogo();
        Usuario estilista = estilista();
        when(estilista.ofreceServicio(SERVICIO_ID)).thenReturn(ofreceServicio);

        when(salonRepository.findById(SALON_ID)).thenReturn(Optional.of(salon));
        when(servicioRepository.findByIdAndSalonId(SERVICIO_ID, SALON_ID))
                .thenReturn(Optional.of(servicioCatalogo));
        when(usuarioRepository.findByIdAndSalonIdAndRolForUpdate(
                ESTILISTA_ID, SALON_ID, Rol.ESTILISTA))
                .thenReturn(Optional.of(estilista));
        when(citaRepository.buscarCruces(
                eq(SALON_ID), eq(ESTILISTA_ID), any(), any(), any()))
                .thenReturn(List.of());
    }

    private Cita citaExistente(Long idEstilista, EstadoCita estado) {
        Salon salon = mock(Salon.class);
        Cliente cliente = mock(Cliente.class);
        Servicio servicioCatalogo = servicioCatalogo();
        Usuario estilista = mock(Usuario.class);
        when(estilista.getId()).thenReturn(idEstilista);

        return new Cita(
                salon,
                cliente,
                servicioCatalogo,
                estilista,
                LocalDateTime.of(2026, 11, 10, 10, 0),
                LocalDateTime.of(2026, 11, 10, 10, 45),
                estado,
                OrigenCita.SALON,
                35000L,
                null
        );
    }

    private CrearCitaRequest solicitud(LocalDateTime inicio) {
        return new CrearCitaRequest(
                "Camilo",
                "+57 300 123 4567",
                SERVICIO_ID,
                ESTILISTA_ID,
                inicio,
                null
        );
    }

    private Salon salon() {
        Salon salon = mock(Salon.class);
        when(salon.getConfiguracion()).thenReturn(new ConfiguracionSalon(
                LocalTime.of(8, 0), LocalTime.of(19, 0), false, 30));
        return salon;
    }

    private Servicio servicioCatalogo() {
        Servicio servicio = mock(Servicio.class);
        when(servicio.isActivo()).thenReturn(true);
        when(servicio.getId()).thenReturn(SERVICIO_ID);
        when(servicio.getDuracionMinutos()).thenReturn(45);
        when(servicio.getPrecio()).thenReturn(35000L);
        return servicio;
    }

    private Usuario estilista() {
        Usuario estilista = mock(Usuario.class);
        when(estilista.getId()).thenReturn(ESTILISTA_ID);
        when(estilista.isActivo()).thenReturn(true);
        when(estilista.ofreceServicio(SERVICIO_ID)).thenReturn(true);
        return estilista;
    }

    private CrearCitaRequest solicitud() {
        return solicitud(LocalDateTime.of(2026, 11, 10, 10, 0));
    }
}
