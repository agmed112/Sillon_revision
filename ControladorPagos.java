package com.devsenior.sillon.pago;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.devsenior.sillon.seguridad.UsuarioPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "7 Pagos")
@RestController
@RequestMapping("/api/pagos")
public class ControladorPagos {

    private final ServicioPagos servicioPagos;

    public ControladorPagos(ServicioPagos servicioPagos) {
        this.servicioPagos = servicioPagos;
    }

    @PostMapping("/suscripcion")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PagoRespuesta> crearSuscripcion(
            @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return ResponseEntity.ok(servicioPagos.crearSuscripcionPro(usuario));
    }

    @PostMapping("/creditos")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PagoRespuesta> comprarCreditos(
            @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return ResponseEntity.ok(servicioPagos.comprarCreditos(usuario));
    }

    @PostMapping("/webhook")
    public ResponseEntity<WebhookRespuesta> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String firma) {
        return ResponseEntity.ok(servicioPagos.recibirWebhook(payload, firma));
    }

    @GetMapping("/resultado")
    public ResponseEntity<ResultadoPagoRespuesta> resultado(@RequestParam String estado) {
        return ResponseEntity.ok(servicioPagos.resultado(estado));
    }
}
