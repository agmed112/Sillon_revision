package com.devsenior.sillon.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.devsenior.sillon.compartido.ReglaNegocioException;
import com.devsenior.sillon.salon.Salon;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

class SolicitudContactoTest {

    @Test
    void nuevaSolicitudNacePendienteYTruncaMensajesA2000() {
        String mensajes = "x".repeat(2200);
        SolicitudContacto solicitud = new SolicitudContacto(
                mock(Salon.class), "Camilo", "+573001112233", mensajes,
                MotivoSolicitud.PEDIDO_POR_IA);
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertEquals(2000, solicitud.getMensajes().length());
    }

    @Test
    void solicitudAtendidaNoPuedeAtenderseDosVeces() {
        SolicitudContacto solicitud = new SolicitudContacto(
                mock(Salon.class), "Camilo", "+573001112233", "Quiero reservar",
                MotivoSolicitud.PEDIDO_POR_IA);
        solicitud.marcarAtendida();
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
        assertThrows(ReglaNegocioException.class, solicitud::marcarAtendida);
    }
}
