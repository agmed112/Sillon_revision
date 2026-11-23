package com.devsenior.sillon.servicio;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuardarServicioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String nombre,

        @Size(max = 300, message = "La descripción no puede superar 300 caracteres")
        String descripcion,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 15, message = "La duración mínima es de 15 minutos")
        @Max(value = 480, message = "La duración máxima es de 480 minutos")
        Integer duracionMinutos,

        @NotNull(message = "El precio es obligatorio")
        @Digits(integer = 19, fraction = 0, message = "El precio debe ser un número entero sin decimales")
        @DecimalMax(value = "9223372036854775807", message = "El precio supera el máximo permitido")
        @Positive(message = "El precio debe ser mayor que cero")
        BigDecimal precio,

        Boolean activo
) {
}
