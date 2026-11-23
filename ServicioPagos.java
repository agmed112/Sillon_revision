package com.devsenior.sillon.pago;

import com.devsenior.sillon.compartido.DatosInvalidosException;
import com.devsenior.sillon.compartido.FirmaInvalidaException;
import com.devsenior.sillon.compartido.PasarelaException;
import com.devsenior.sillon.compartido.RecursoNoEncontradoException;
import com.devsenior.sillon.compartido.ReglaNegocioException;
import com.devsenior.sillon.salon.Plan;
import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import com.devsenior.sillon.seguridad.UsuarioPrincipal;
import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ServicioPagos {

    private static final Logger log = LoggerFactory.getLogger(ServicioPagos.class);

    private final StripeClient stripeClient;
    private final PropiedadesStripe propiedadesStripe;
    private final SalonRepository salonRepository;
    private final ServicioWebhookStripe servicioWebhookStripe;
    private final String urlPublica;

    public ServicioPagos(
            StripeClient stripeClient,
            PropiedadesStripe propiedadesStripe,
            SalonRepository salonRepository,
            ServicioWebhookStripe servicioWebhookStripe,
            @Value("${sillon.url-publica}") String urlPublica) {
        this.stripeClient = stripeClient;
        this.propiedadesStripe = propiedadesStripe;
        this.salonRepository = salonRepository;
        this.servicioWebhookStripe = servicioWebhookStripe;
        this.urlPublica = quitarBarraFinal(urlPublica);
    }

    public PagoRespuesta crearSuscripcionPro(UsuarioPrincipal usuario) {
        Salon salon = buscarSalon(usuario.getSalonId());
        if (salon.getPlan() == Plan.PRO) {
            throw new ReglaNegocioException("El salón ya tiene activo el plan PRO");
        }
        return crearSesion(salon, usuario.getEmail(), TipoCompra.SUSCRIPCION_PRO);
    }

    public PagoRespuesta comprarCreditos(UsuarioPrincipal usuario) {
        Salon salon = buscarSalon(usuario.getSalonId());
        return crearSesion(salon, usuario.getEmail(), TipoCompra.PAQUETE_CREDITOS);
    }

    public WebhookRespuesta recibirWebhook(String payload, String firma) {
        if (firma == null || firma.isBlank()) {
            throw new FirmaInvalidaException("Falta la firma de Stripe");
        }
        if (payload == null || payload.isBlank() || !payload.stripLeading().startsWith("{")) {
            throw new FirmaInvalidaException("El cuerpo del webhook debe ser un objeto JSON de Stripe");
        }

        Event evento;
        try {
            evento = Webhook.constructEvent(payload, firma, propiedadesStripe.getWebhookSecreto());
        } catch (SignatureVerificationException | JsonSyntaxException | IllegalArgumentException ex) {
            log.warn("Stripe envió un webhook con firma o cuerpo inválido", ex);
            throw new FirmaInvalidaException("La firma o el cuerpo del webhook de Stripe no es válido");
        }
        try {
            return servicioWebhookStripe.procesar(evento);
        } catch (DataIntegrityViolationException ex) {
            /*
             * Dos entregas simultáneas pueden superar el existsById inicial.
             * La clave primaria del evento hace que solo una transacción pueda
             * registrarlo. Tras el rollback de la transacción perdedora,
             * confirmamos si la otra ya lo guardó y respondemos como duplicado.
             */
            if (servicioWebhookStripe.yaFueProcesado(evento.getId())) {
                log.info("Se ignoró la entrega concurrente del evento Stripe {}", evento.getId());
                return new WebhookRespuesta(true, false);
            }
            throw ex;
        }
    }

    public ResultadoPagoRespuesta resultado(String estado) {
        if ("exito".equalsIgnoreCase(estado)) {
            return new ResultadoPagoRespuesta("Pago recibido. La actualización del plan o los créditos se confirma mediante el webhook de Stripe.");
        }
        if ("cancelado".equalsIgnoreCase(estado)) {
            return new ResultadoPagoRespuesta("El proceso de pago fue cancelado. No se realizaron cambios.");
        }
        throw new DatosInvalidosException("El estado debe ser exito o cancelado");
    }

    private PagoRespuesta crearSesion(Salon salon, String email, TipoCompra tipoCompra) {
        String salonId = salon.getId().toString();
        String tipo = tipoCompra.name();
        String priceId = tipoCompra == TipoCompra.SUSCRIPCION_PRO
                ? propiedadesStripe.getPrecioPro()
                : propiedadesStripe.getPrecioCreditos();

        SessionCreateParams.Builder parametros = SessionCreateParams.builder()
                .setMode(tipoCompra == TipoCompra.SUSCRIPCION_PRO
                        ? SessionCreateParams.Mode.SUBSCRIPTION
                        : SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(email)
                .setSuccessUrl(urlResultado("exito"))
                .setCancelUrl(urlResultado("cancelado"))
                .setClientReferenceId(salonId)
                .putMetadata("salonId", salonId)
                .putMetadata("tipoCompra", tipo)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(priceId)
                        .setQuantity(1L)
                        .build());

        if (tipoCompra == TipoCompra.SUSCRIPCION_PRO) {
            parametros.setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                    .putMetadata("salonId", salonId)
                    .putMetadata("tipoCompra", tipo)
                    .build());
        }

        try {
            Session sesion = stripeClient.v1().checkout().sessions().create(parametros.build());
            if (sesion.getId() == null || sesion.getUrl() == null) {
                throw new PasarelaException("Stripe no devolvió el id y la URL de Checkout");
            }
            return new PagoRespuesta(tipo, sesion.getId(), sesion.getUrl());
        } catch (StripeException ex) {
            log.error("Stripe no pudo crear una sesión de Checkout para el salón {}", salonId, ex);
            throw new PasarelaException("No se pudo crear la sesión de pago en Stripe");
        }
    }

    private Salon buscarSalon(Long salonId) {
        return salonRepository.findById(salonId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el salón"));
    }

    private String urlResultado(String estado) {
        return urlPublica + "/api/pagos/resultado?estado=" + estado;
    }

    private static String quitarBarraFinal(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La URL pública de Sillón es obligatoria");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
