package com.devsenior.sillon.pago;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import com.devsenior.sillon.compartido.DatosInvalidosException;
import com.devsenior.sillon.compartido.FirmaInvalidaException;
import com.devsenior.sillon.compartido.PasarelaException;
import com.devsenior.sillon.compartido.ReglaNegocioException;
import com.devsenior.sillon.salon.Plan;
import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import com.devsenior.sillon.seguridad.UsuarioPrincipal;
import com.devsenior.sillon.usuario.Rol;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.dao.DataIntegrityViolationException;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ServicioPagosTest {

    private StripeClient stripeClient;
    private PropiedadesStripe propiedadesStripe;
    private SalonRepository salonRepository;
    private ServicioWebhookStripe servicioWebhookStripe;
    private ServicioPagos servicio;

    @BeforeEach
    void configurar() {
        stripeClient = mock(StripeClient.class, RETURNS_DEEP_STUBS);

        propiedadesStripe = new PropiedadesStripe();
        propiedadesStripe.setClaveSecreta("sk_test_prueba");
        propiedadesStripe.setWebhookSecreto("whsec_prueba");
        propiedadesStripe.setPrecioPro("price_pro_prueba");
        propiedadesStripe.setPrecioCreditos("price_creditos_prueba");
        propiedadesStripe.setUrlApi("https://stripe-simulador.test");

        salonRepository = mock(SalonRepository.class);
        servicioWebhookStripe = mock(ServicioWebhookStripe.class);
        servicio = new ServicioPagos(
                stripeClient,
                propiedadesStripe,
                salonRepository,
                servicioWebhookStripe,
                "https://sillon-prueba.onrender.com/"
        );
    }

    @Test
    void checkoutProUsaSuscripcionPrecioDelBackendCorreoYMetadataDuplicada() throws Exception {
        Salon salon = salon(7L, Plan.GRATIS);
        when(salonRepository.findById(7L)).thenReturn(Optional.of(salon));
        Session sesion = sesion("cs_pro", "https://checkout.stripe.test/pro");
        when(stripeClient.v1().checkout().sessions().create(any(SessionCreateParams.class)))
                .thenReturn(sesion);

        PagoRespuesta respuesta = servicio.crearSuscripcionPro(dueno());

        assertEquals(TipoCompra.SUSCRIPCION_PRO.name(), respuesta.tipo());
        assertEquals("cs_pro", respuesta.sesionId());
        assertEquals("https://checkout.stripe.test/pro", respuesta.urlPago());

        ArgumentCaptor<SessionCreateParams> captor =
                ArgumentCaptor.forClass(SessionCreateParams.class);
        verify(stripeClient.v1().checkout().sessions()).create(captor.capture());

        SessionCreateParams parametros = captor.getValue();
        assertEquals(SessionCreateParams.Mode.SUBSCRIPTION, parametros.getMode());
        assertEquals("dueno@sillon.test", parametros.getCustomerEmail());
        assertEquals(
                "https://sillon-prueba.onrender.com/api/pagos/resultado?estado=exito",
                parametros.getSuccessUrl());
        assertEquals(
                "https://sillon-prueba.onrender.com/api/pagos/resultado?estado=cancelado",
                parametros.getCancelUrl());
        assertEquals("7", parametros.getClientReferenceId());
        assertEquals(
                Map.of("salonId", "7", "tipoCompra", "SUSCRIPCION_PRO"),
                parametros.getMetadata());
        assertEquals(
                Map.of("salonId", "7", "tipoCompra", "SUSCRIPCION_PRO"),
                parametros.getSubscriptionData().getMetadata());
        assertEquals("price_pro_prueba", parametros.getLineItems().get(0).getPrice());
        assertEquals(1L, parametros.getLineItems().get(0).getQuantity());
    }

    @Test
    void checkoutDeCreditosUsaPagoUnicoYPrecioDelBackend() throws Exception {
        Salon salon = salon(7L, Plan.GRATIS);
        when(salonRepository.findById(7L)).thenReturn(Optional.of(salon));
        Session sesion = sesion("cs_creditos", "https://checkout.stripe.test/creditos");
        when(stripeClient.v1().checkout().sessions().create(any(SessionCreateParams.class)))
                .thenReturn(sesion);

        PagoRespuesta respuesta = servicio.comprarCreditos(dueno());

        assertEquals(TipoCompra.PAQUETE_CREDITOS.name(), respuesta.tipo());
        ArgumentCaptor<SessionCreateParams> captor =
                ArgumentCaptor.forClass(SessionCreateParams.class);
        verify(stripeClient.v1().checkout().sessions()).create(captor.capture());

        SessionCreateParams parametros = captor.getValue();
        assertEquals(SessionCreateParams.Mode.PAYMENT, parametros.getMode());
        assertEquals("dueno@sillon.test", parametros.getCustomerEmail());
        assertEquals("7", parametros.getClientReferenceId());
        assertEquals(
                Map.of("salonId", "7", "tipoCompra", "PAQUETE_CREDITOS"),
                parametros.getMetadata());
        assertEquals("price_creditos_prueba", parametros.getLineItems().get(0).getPrice());
        assertNull(parametros.getSubscriptionData());
    }

    @Test
    void segundaSolicitudProResponde409AntesDeContactarStripe() {
        Salon salon = salon(7L, Plan.PRO);
        when(salonRepository.findById(7L)).thenReturn(Optional.of(salon));

        assertThrows(ReglaNegocioException.class,
                () -> servicio.crearSuscripcionPro(dueno()));

        verifyNoInteractions(stripeClient);
    }

    @Test
    void errorDeStripeAlCrearCheckoutSeTraduceA502() throws Exception {
        Salon salon = salon(7L, Plan.GRATIS);
        when(salonRepository.findById(7L)).thenReturn(Optional.of(salon));
        StripeException errorStripe = mock(StripeException.class);
        when(stripeClient.v1().checkout().sessions().create(any(SessionCreateParams.class)))
                .thenThrow(errorStripe);

        assertThrows(PasarelaException.class, () -> servicio.comprarCreditos(dueno()));
    }

    @Test
    void checkoutSinIdOSinUrlNoSeEntregaComoSesionValida() throws Exception {
        Salon salon = salon(7L, Plan.GRATIS);
        when(salonRepository.findById(7L)).thenReturn(Optional.of(salon));
        Session sesionIncompleta = mock(Session.class);
        when(sesionIncompleta.getId()).thenReturn("cs_sin_url");
        when(sesionIncompleta.getUrl()).thenReturn(null);
        when(stripeClient.v1().checkout().sessions().create(any(SessionCreateParams.class)))
                .thenReturn(sesionIncompleta);

        assertThrows(PasarelaException.class, () -> servicio.comprarCreditos(dueno()));
    }

    @Test
    void webhookSinCabeceraDeFirmaResponde400() {
        assertThrows(FirmaInvalidaException.class,
                () -> servicio.recibirWebhook("{\"id\":\"evt_test\"}", null));
        verifyNoInteractions(servicioWebhookStripe);
    }

    @Test
    void webhookConCuerpoVacioResponde400() {
        assertThrows(FirmaInvalidaException.class,
                () -> servicio.recibirWebhook("   ", "t=1,v1=firma_falsa"));
        verifyNoInteractions(servicioWebhookStripe);
    }

    @Test
    void webhookConJsonQueNoEsObjetoResponde400() {
        assertThrows(FirmaInvalidaException.class,
                () -> servicio.recibirWebhook("[]", "t=1,v1=firma_falsa"));
        verifyNoInteractions(servicioWebhookStripe);
    }

    @Test
    void webhookConFirmaIncorrectaResponde400() {
        assertThrows(FirmaInvalidaException.class,
                () -> servicio.recibirWebhook("{}", "t=1,v1=firma_falsa"));
        verifyNoInteractions(servicioWebhookStripe);
    }

    @Test
    void entregaConcurrenteDeUnEventoYaGuardadoSeRespondeComoDuplicada() throws Exception {
        String payload = """
                {
                  "id": "evt_concurrente",
                  "object": "event",
                  "created": 1794000000,
                  "data": {"object": {}},
                  "livemode": false,
                  "pending_webhooks": 1,
                  "type": "invoice.created"
                }
                """;
        String firma = Webhook.Signature.generateSignatureHeader(
                payload, propiedadesStripe.getWebhookSecreto());

        when(servicioWebhookStripe.procesar(any(Event.class)))
                .thenThrow(new DataIntegrityViolationException("evento duplicado"));
        when(servicioWebhookStripe.yaFueProcesado("evt_concurrente")).thenReturn(true);

        WebhookRespuesta respuesta = servicio.recibirWebhook(payload, firma);

        assertEquals(true, respuesta.recibido());
        assertEquals(false, respuesta.procesado());
    }

    @Test
    void resultadoAceptaSoloExitoOCancelado() {
        assertEquals(
                "Pago recibido. La actualización del plan o los créditos se confirma mediante el webhook de Stripe.",
                servicio.resultado("exito").mensaje());
        assertEquals(
                "El proceso de pago fue cancelado. No se realizaron cambios.",
                servicio.resultado("cancelado").mensaje());
        assertThrows(DatosInvalidosException.class, () -> servicio.resultado("pendiente"));
    }

    private Salon salon(Long id, Plan plan) {
        Salon salon = mock(Salon.class);
        when(salon.getId()).thenReturn(id);
        when(salon.getPlan()).thenReturn(plan);
        return salon;
    }

    private UsuarioPrincipal dueno() {
        return new UsuarioPrincipal(
                3L, 7L, "dueno@sillon.test", "hash", Rol.DUENO, true);
    }

    private Session sesion(String id, String url) {
        Session sesion = mock(Session.class);
        when(sesion.getId()).thenReturn(id);
        when(sesion.getUrl()).thenReturn(url);
        return sesion;
    }
}
