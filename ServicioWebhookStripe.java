package com.devsenior.sillon.pago;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

import com.devsenior.sillon.compartido.PasarelaException;
import com.devsenior.sillon.salon.Plan;
import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.model.EventDataObjectDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioWebhookStripe {

    private static final Logger log = LoggerFactory.getLogger(ServicioWebhookStripe.class);
    private static final String CHECKOUT_COMPLETADO = "checkout.session.completed";
    private static final String SUSCRIPCION_CANCELADA = "customer.subscription.deleted";
    private static final String METADATA_SALON_ID = "salonId";
    private static final String METADATA_TIPO_COMPRA = "tipoCompra";

    private final EventoStripeRepository eventoRepository;
    private final SalonRepository salonRepository;
    private final Clock reloj;
    private final int creditosPlanPro;
    private final int creditosPaquete;

    public ServicioWebhookStripe(
            EventoStripeRepository eventoRepository,
            SalonRepository salonRepository,
            Clock reloj,
            @Value("${sillon.creditos.pro}") int creditosPlanPro,
            @Value("${sillon.creditos.paquete}") int creditosPaquete) {
        this.eventoRepository = eventoRepository;
        this.salonRepository = salonRepository;
        this.reloj = reloj;
        this.creditosPlanPro = creditosPlanPro;
        this.creditosPaquete = creditosPaquete;
    }

    /**
     * Registra y aplica el evento en una sola transacción. La restricción única de la tabla
     * complementa la consulta previa para protegerse de entregas concurrentes del mismo evento.
     */
    @Transactional
    public WebhookRespuesta procesar(Event evento) {
        if (evento.getId() == null || evento.getId().isBlank()) {
            throw new PasarelaException("Stripe envió un evento sin identificador");
        }

        if (eventoRepository.existsById(evento.getId())) {
            return new WebhookRespuesta(true, false);
        }

        eventoRepository.saveAndFlush(new EventoStripe(
                evento.getId(), evento.getType(), LocalDateTime.now(reloj)));

        boolean procesado = switch (evento.getType()) {
            case CHECKOUT_COMPLETADO -> procesarCheckout(evento);
            case SUSCRIPCION_CANCELADA -> procesarCancelacion(evento);
            default -> false;
        };

        return new WebhookRespuesta(true, procesado);
    }

    @Transactional(readOnly = true)
    public boolean yaFueProcesado(String eventoId) {
        return eventoId != null && eventoRepository.existsById(eventoId);
    }

    private boolean procesarCheckout(Event evento) {
        StripeObject objeto = obtenerObjeto(evento);
        if (!(objeto instanceof Session sesion)) {
            log.error("El evento {} no contiene una Checkout Session", evento.getId());
            throw new PasarelaException("Stripe envió un evento de Checkout sin una sesión utilizable");
        }

        if (!"paid".equals(sesion.getPaymentStatus())) {
            return false;
        }

        Map<String, String> metadata = sesion.getMetadata();
        if (metadata == null) {
            throw new PasarelaException("El Checkout pagado no contiene metadata de Sillón");
        }

        Long salonId = convertirSalonId(metadata.get(METADATA_SALON_ID));
        TipoCompra tipoCompra = convertirTipoCompra(metadata.get(METADATA_TIPO_COMPRA));
        if (salonId == null || tipoCompra == null) {
            log.error("El evento {} no contiene metadata de compra reconocible", evento.getId());
            throw new PasarelaException("La metadata del Checkout pagado no es válida");
        }

        Salon salon = salonRepository.findByIdForUpdate(salonId).orElse(null);
        if (salon == null) {
            log.error("El evento {} referencia un salón inexistente: {}", evento.getId(), salonId);
            throw new PasarelaException("El Checkout pagado referencia un salón inexistente");
        }

        if (tipoCompra == TipoCompra.SUSCRIPCION_PRO) {
            String suscripcionId = sesion.getSubscription();
            if (suscripcionId == null || suscripcionId.isBlank()) {
                log.error("La sesión {} no contiene el id de la suscripción PRO", sesion.getId());
                throw new PasarelaException("Stripe no incluyó la suscripción en la sesión pagada");
            }
            salon.setPlan(Plan.PRO);
            salon.setStripeSuscripcionId(suscripcionId);
            salon.setCreditosIa(salon.getCreditosIa() + creditosPlanPro);
            return true;
        }

        salon.setCreditosIa(salon.getCreditosIa() + creditosPaquete);
        return true;
    }

    private boolean procesarCancelacion(Event evento) {
        StripeObject objeto = obtenerObjeto(evento);
        if (!(objeto instanceof Subscription suscripcion)) {
            log.error("El evento {} no contiene una Subscription", evento.getId());
            throw new PasarelaException("Stripe envió un evento de cancelación sin una suscripción utilizable");
        }

        Salon salon = salonRepository.findByStripeSuscripcionIdForUpdate(suscripcion.getId()).orElse(null);
        if (salon == null) {
            log.warn("No se encontró un salón para la suscripción cancelada {}", suscripcion.getId());
            return false;
        }

        salon.setPlan(Plan.GRATIS);
        // La regla del taller indica conservar los créditos acumulados al volver a GRATIS.
        return true;
    }

    private StripeObject obtenerObjeto(Event evento) {
        EventDataObjectDeserializer deserializador = evento.getDataObjectDeserializer();
        var objetoSeguro = deserializador.getObject();
        if (objetoSeguro.isPresent()) {
            return objetoSeguro.get();
        }

        log.warn("El evento Stripe {} usa una versión de API incompatible; se intentará deserializeUnsafe()",
                evento.getId());
        try {
            return deserializador.deserializeUnsafe();
        } catch (EventDataObjectDeserializationException ex) {
            log.error("No se pudo deserializar el objeto del evento Stripe {}", evento.getId(), ex);
            throw new PasarelaException("No se pudo leer el objeto del evento de Stripe");
        }
    }

    private Long convertirSalonId(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(valor);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private TipoCompra convertirTipoCompra(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return TipoCompra.valueOf(valor);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
