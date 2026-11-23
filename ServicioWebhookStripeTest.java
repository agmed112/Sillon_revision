package com.devsenior.sillon.pago;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

import com.devsenior.sillon.compartido.PasarelaException;
import com.devsenior.sillon.salon.Plan;
import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicioWebhookStripeTest {

    @Mock
    private EventoStripeRepository eventoRepository;

    @Mock
    private SalonRepository salonRepository;

    private ServicioWebhookStripe servicio;

    @BeforeEach
    void configurar() {
        servicio = new ServicioWebhookStripe(
                eventoRepository,
                salonRepository,
                Clock.fixed(Instant.parse("2026-10-20T15:00:00Z"), ZoneId.of("America/Bogota")),
                300,
                200);
    }

    @Test
    void pagoProActivaPlanYAgregaTrescientosCreditos() {
        Salon salon = mock(Salon.class);
        when(salonRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(salon));
        when(salon.getCreditosIa()).thenReturn(10);
        when(eventoRepository.existsById("evt_pro")).thenReturn(false);

        Session sesion = new Session();
        sesion.setId("cs_pro");
        sesion.setPaymentStatus("paid");
        sesion.setSubscription("sub_123");
        sesion.setMetadata(Map.of(
                "salonId", "7",
                "tipoCompra", TipoCompra.SUSCRIPCION_PRO.name()));

        WebhookRespuesta respuesta = servicio.procesar(evento("evt_pro", "checkout.session.completed", sesion));

        assertTrue(respuesta.recibido());
        assertTrue(respuesta.procesado());
        verify(salonRepository).findByIdForUpdate(7L);
        verify(salon).setPlan(Plan.PRO);
        verify(salon).setStripeSuscripcionId("sub_123");
        verify(salon).setCreditosIa(310);
    }

    @Test
    void compraDeCreditosSumaDoscientosSinCambiarElPlan() {
        Salon salon = mock(Salon.class);
        when(salonRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(salon));
        when(salon.getCreditosIa()).thenReturn(15);
        when(eventoRepository.existsById("evt_creditos")).thenReturn(false);

        Session sesion = new Session();
        sesion.setId("cs_creditos");
        sesion.setPaymentStatus("paid");
        sesion.setMetadata(Map.of(
                "salonId", "7",
                "tipoCompra", TipoCompra.PAQUETE_CREDITOS.name()));

        WebhookRespuesta respuesta = servicio.procesar(
                evento("evt_creditos", "checkout.session.completed", sesion));

        assertTrue(respuesta.procesado());
        verify(salonRepository).findByIdForUpdate(7L);
        verify(salon).setCreditosIa(215);
        verify(salon, never()).setPlan(any());
    }

    @Test
    void eventoRepetidoNoVuelveAAplicarLaCompra() {
        when(eventoRepository.existsById("evt_repetido")).thenReturn(true);

        WebhookRespuesta respuesta = servicio.procesar(
                evento("evt_repetido", "checkout.session.completed", new Session()));

        assertTrue(respuesta.recibido());
        assertFalse(respuesta.procesado());
        verify(eventoRepository, never()).saveAndFlush(any(EventoStripe.class));
        verify(salonRepository, never()).findById(any());
    }

    @Test
    void cancelarSuscripcionRegresaAGratisYConservaCreditos() {
        Salon salon = mock(Salon.class);
        when(salonRepository.findByStripeSuscripcionIdForUpdate("sub_123")).thenReturn(Optional.of(salon));
        when(eventoRepository.existsById("evt_cancelacion")).thenReturn(false);

        Subscription suscripcion = new Subscription();
        suscripcion.setId("sub_123");

        WebhookRespuesta respuesta = servicio.procesar(
                evento("evt_cancelacion", "customer.subscription.deleted", suscripcion));

        assertTrue(respuesta.procesado());
        verify(salonRepository).findByStripeSuscripcionIdForUpdate("sub_123");
        verify(salon).setPlan(Plan.GRATIS);
        verify(salon, never()).setCreditosIa(anyInt());
    }

    @Test
    void pagoProSinIdDeSuscripcionFallaYPermiteReintento() {
        Salon salon = mock(Salon.class);
        when(salonRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(salon));
        when(salon.getCreditosIa()).thenReturn(10);
        when(eventoRepository.existsById("evt_pro_sin_suscripcion")).thenReturn(false);

        Session sesion = new Session();
        sesion.setId("cs_pro_sin_sub");
        sesion.setPaymentStatus("paid");
        sesion.setMetadata(Map.of(
                "salonId", "7",
                "tipoCompra", TipoCompra.SUSCRIPCION_PRO.name()));

        assertThrows(PasarelaException.class, () ->
                servicio.procesar(evento(
                        "evt_pro_sin_suscripcion", "checkout.session.completed", sesion)));

        verify(salon, never()).setPlan(Plan.PRO);
        verify(salon, never()).setCreditosIa(anyInt());
    }

    @Test
    void eventoSinIdentificadorSeRechazaAntesDeConsultarRepositorio() {
        Event evento = mock(Event.class);
        when(evento.getId()).thenReturn(" ");

        assertThrows(PasarelaException.class, () -> servicio.procesar(evento));

        verify(eventoRepository, never()).existsById(any());
        verify(eventoRepository, never()).saveAndFlush(any(EventoStripe.class));
    }

    @Test
    void cancelacionDeUnaSuscripcionDesconocidaNoModificaNingunSalon() {
        when(eventoRepository.existsById("evt_sub_desconocida")).thenReturn(false);
        when(salonRepository.findByStripeSuscripcionIdForUpdate("sub_no_existe"))
                .thenReturn(Optional.empty());

        Subscription suscripcion = new Subscription();
        suscripcion.setId("sub_no_existe");

        WebhookRespuesta respuesta = servicio.procesar(
                evento("evt_sub_desconocida", "customer.subscription.deleted", suscripcion));

        assertTrue(respuesta.recibido());
        assertFalse(respuesta.procesado());
        verify(eventoRepository).saveAndFlush(any(EventoStripe.class));
    }

    @Test
    void checkoutNoPagadoNoOtorgaCreditos() {
        when(eventoRepository.existsById("evt_no_pagado")).thenReturn(false);

        Session sesion = new Session();
        sesion.setPaymentStatus("unpaid");
        sesion.setMetadata(Map.of(
                "salonId", "7",
                "tipoCompra", TipoCompra.PAQUETE_CREDITOS.name()));

        WebhookRespuesta respuesta = servicio.procesar(
                evento("evt_no_pagado", "checkout.session.completed", sesion));

        assertFalse(respuesta.procesado());
        verify(salonRepository, never()).findById(7L);
    }

    @Test
    void checkoutPagadoConMetadataIncompletaFallaParaQueStripeReintente() {
        when(eventoRepository.existsById("evt_metadata_invalida")).thenReturn(false);

        Session sesion = new Session();
        sesion.setPaymentStatus("paid");
        sesion.setMetadata(Map.of("salonId", "7"));

        assertThrows(PasarelaException.class, () ->
                servicio.procesar(evento(
                        "evt_metadata_invalida", "checkout.session.completed", sesion)));

        verify(eventoRepository).saveAndFlush(any(EventoStripe.class));
        verify(salonRepository, never()).findById(7L);
    }

    @Test
    void checkoutPagadoDeUnSalonInexistenteFallaYNoOtorgaCreditos() {
        when(eventoRepository.existsById("evt_salon_inexistente")).thenReturn(false);
        when(salonRepository.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        Session sesion = new Session();
        sesion.setPaymentStatus("paid");
        sesion.setMetadata(Map.of(
                "salonId", "999",
                "tipoCompra", TipoCompra.PAQUETE_CREDITOS.name()));

        assertThrows(PasarelaException.class, () ->
                servicio.procesar(evento(
                        "evt_salon_inexistente", "checkout.session.completed", sesion)));

        verify(eventoRepository).saveAndFlush(any(EventoStripe.class));
    }

    @Test
    void checkoutNoPagadoSeRegistraPeroNoModificaElSalon() {
        when(eventoRepository.existsById("evt_pendiente")).thenReturn(false);

        Session sesion = new Session();
        sesion.setPaymentStatus("unpaid");
        sesion.setMetadata(Map.of(
                "salonId", "7",
                "tipoCompra", TipoCompra.PAQUETE_CREDITOS.name()));

        WebhookRespuesta respuesta = servicio.procesar(
                evento("evt_pendiente", "checkout.session.completed", sesion));

        assertTrue(respuesta.recibido());
        assertFalse(respuesta.procesado());
        verify(eventoRepository).saveAndFlush(any(EventoStripe.class));
        verify(salonRepository, never()).findById(7L);
    }

    @Test
    void eventoNoContempladoSeRegistraSinCambiarElSalon() {
        when(eventoRepository.existsById("evt_no_contemplado")).thenReturn(false);

        Event evento = mock(Event.class);
        when(evento.getId()).thenReturn("evt_no_contemplado");
        when(evento.getType()).thenReturn("invoice.created");

        WebhookRespuesta respuesta = servicio.procesar(evento);

        assertTrue(respuesta.recibido());
        assertFalse(respuesta.procesado());
        verify(eventoRepository).saveAndFlush(any(EventoStripe.class));
        verify(salonRepository, never()).findById(any());
    }

    @Test
    void usaDeserializacionUnsafeCuandoElObjetoSeguroNoEstaDisponible() throws Exception {
        Salon salon = mock(Salon.class);
        when(salonRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(salon));
        when(salon.getCreditosIa()).thenReturn(10);
        when(eventoRepository.existsById("evt_fallback")).thenReturn(false);

        Session sesion = new Session();
        sesion.setPaymentStatus("paid");
        sesion.setMetadata(Map.of(
                "salonId", "7",
                "tipoCompra", TipoCompra.PAQUETE_CREDITOS.name()));

        Event evento = mock(Event.class);
        EventDataObjectDeserializer deserializador = mock(EventDataObjectDeserializer.class);
        when(evento.getId()).thenReturn("evt_fallback");
        when(evento.getType()).thenReturn("checkout.session.completed");
        when(evento.getDataObjectDeserializer()).thenReturn(deserializador);
        when(deserializador.getObject()).thenReturn(Optional.empty());
        when(deserializador.deserializeUnsafe()).thenReturn(sesion);

        WebhookRespuesta respuesta = servicio.procesar(evento);

        assertTrue(respuesta.procesado());
        verify(salon).setCreditosIa(210);
    }

    @Test
    void identificaUnEventoYaRegistradoPorSuIdDeStripe() {
        when(eventoRepository.existsById("evt_existente")).thenReturn(true);

        assertTrue(servicio.yaFueProcesado("evt_existente"));
    }

    @Test
    void idNuloNoSeConsultaComoEventoProcesado() {
        assertFalse(servicio.yaFueProcesado(null));
        verify(eventoRepository, never()).existsById(any());
    }

    private Event evento(String id, String tipo, com.stripe.model.StripeObject objeto) {
        Event evento = mock(Event.class);
        EventDataObjectDeserializer deserializador = mock(EventDataObjectDeserializer.class);
        when(evento.getId()).thenReturn(id);
        when(evento.getType()).thenReturn(tipo);
        when(evento.getDataObjectDeserializer()).thenReturn(deserializador);
        when(deserializador.getObject()).thenReturn(Optional.of(objeto));
        return evento;
    }
}
