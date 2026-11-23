package com.devsenior.sillon.pago;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "eventos_stripe")
public class EventoStripe {

    /*
     * El id de Stripe es la clave primaria: evita una segunda identidad artificial
     * y permite reconocer el mismo evento con existsById.
     */
    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "tipo", nullable = false, length = 120)
    private String tipo;

    @Column(name = "fecha_procesado", nullable = false)
    private LocalDateTime fechaProcesado;

    protected EventoStripe() {
    }

    public EventoStripe(String id, String tipo, LocalDateTime fechaProcesado) {
        this.id = Objects.requireNonNull(id, "El id del evento es obligatorio");
        this.tipo = Objects.requireNonNull(tipo, "El tipo del evento es obligatorio");
        this.fechaProcesado = Objects.requireNonNull(
                fechaProcesado, "La fecha de procesamiento es obligatoria");
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getFechaProcesado() {
        return fechaProcesado;
    }
}
