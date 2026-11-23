package com.devsenior.sillon.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.devsenior.sillon.compartido.RecursoNoEncontradoException;
import com.devsenior.sillon.compartido.ReglaNegocioException;
import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioSolicitudesTest {

    private SolicitudContactoRepository solicitudRepository;
    private SalonRepository salonRepository;
    private ServicioSolicitudes servicio;

    @BeforeEach
    void configurar() {
        solicitudRepository = mock(SolicitudContactoRepository.class);
        salonRepository = mock(SalonRepository.class);
        servicio = new ServicioSolicitudes(solicitudRepository, salonRepository);
    }

    @Test
    void creaSolicitudConElSalonYDevuelveSuId() {
        Salon salon = mock(Salon.class);
        SolicitudContacto guardada = mock(SolicitudContacto.class);
        when(salonRepository.findById(8L)).thenReturn(Optional.of(salon));
        when(solicitudRepository.saveAndFlush(any(SolicitudContacto.class))).thenReturn(guardada);
        when(guardada.getId()).thenReturn(31L);

        assertEquals(31L, servicio.crear(8L, "Camilo", "3001112233", "Quiero reservar",
                MotivoSolicitud.PEDIDO_POR_IA));
    }

    @Test
    void listaSoloLasSolicitudesDelSalonYLasConvierteARespuesta() {
        SolicitudContacto solicitud = mock(SolicitudContacto.class);
        when(solicitud.getId()).thenReturn(31L);
        when(solicitud.getClienteNombre()).thenReturn("Camilo");
        when(solicitud.getClienteContacto()).thenReturn("3001112233");
        when(solicitud.getMensajes()).thenReturn("Quiero reservar");
        when(solicitud.getMotivo()).thenReturn(MotivoSolicitud.PEDIDO_POR_IA);
        when(solicitud.getEstado()).thenReturn(EstadoSolicitud.PENDIENTE);
        when(solicitudRepository.findAllBySalonIdOrderByFechaCreacionDesc(8L))
                .thenReturn(List.of(solicitud));

        List<SolicitudContactoRespuesta> respuesta = servicio.listar(8L);
        assertEquals(1, respuesta.size());
        assertEquals(31L, respuesta.get(0).id());
        assertEquals(EstadoSolicitud.PENDIENTE, respuesta.get(0).estado());
    }

    @Test
    void marcarAtendidaBloqueaLaSolicitudYDevuelveEstadoAtendida() {
        SolicitudContacto solicitud = new SolicitudContacto(
                mock(Salon.class), "Camilo", "3001112233", "Quiero reservar",
                MotivoSolicitud.PEDIDO_POR_IA);
        when(solicitudRepository.findByIdAndSalonIdForUpdate(31L, 8L))
                .thenReturn(Optional.of(solicitud));
        when(solicitudRepository.saveAndFlush(solicitud)).thenReturn(solicitud);

        SolicitudContactoRespuesta respuesta = servicio.marcarAtendida(8L, 31L);
        assertEquals(EstadoSolicitud.ATENDIDA, respuesta.estado());
    }

    @Test
    void solicitudDeOtroSalonDevuelve404() {
        when(solicitudRepository.findByIdAndSalonIdForUpdate(31L, 8L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.marcarAtendida(8L, 31L));
    }

    @Test
    void solicitudYaAtendidaDevuelve409() {
        SolicitudContacto solicitud = new SolicitudContacto(
                mock(Salon.class), "Camilo", "3001112233", "Quiero reservar",
                MotivoSolicitud.PEDIDO_POR_IA);
        solicitud.marcarAtendida();
        when(solicitudRepository.findByIdAndSalonIdForUpdate(31L, 8L))
                .thenReturn(Optional.of(solicitud));
        assertThrows(ReglaNegocioException.class, () -> servicio.marcarAtendida(8L, 31L));
    }
}
