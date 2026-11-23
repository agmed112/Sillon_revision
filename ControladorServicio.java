package com.devsenior.sillon.servicio;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

import com.devsenior.sillon.seguridad.UsuarioPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@Tag(name = "3 Servicios")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/servicios")
@Validated
public class ControladorServicio {

    private final ServicioCatalogo servicioCatalogo;

    public ControladorServicio(ServicioCatalogo servicioCatalogo) {
        this.servicioCatalogo = servicioCatalogo;
    }

    @PostMapping
    public ResponseEntity<ServicioRespuesta> crear(
            @AuthenticationPrincipal UsuarioPrincipal usuario,
            @Valid @RequestBody GuardarServicioRequest solicitud) {
        ServicioRespuesta respuesta = servicioCatalogo.crear(usuario.getSalonId(), solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<ServicioRespuesta>> listar(
            @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return ResponseEntity.ok(servicioCatalogo.listarPorSalon(usuario.getSalonId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioRespuesta> actualizar(
            @AuthenticationPrincipal UsuarioPrincipal usuario,
            @PathVariable Long id,
            @Valid @RequestBody GuardarServicioRequest solicitud) {
        return ResponseEntity.ok(
                servicioCatalogo.actualizar(usuario.getSalonId(), id, solicitud));
    }
}
