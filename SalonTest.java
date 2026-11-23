package com.devsenior.sillon.salon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.devsenior.sillon.usuario.Rol;
import com.devsenior.sillon.usuario.Usuario;

class SalonTest {

    @Test
    void debeIniciarEnPlanGratisConConfiguracionPredeterminada() {
        Salon salon = new Salon(
                "Barbería La Navaja", "barberia-la-navaja", "Medellín",
                "Calle 10 # 43-20", "3001234567", 10,
                LocalDateTime.of(2026, 11, 8, 10, 0)
        );

        assertEquals(Plan.GRATIS, salon.getPlan());
        assertEquals(10, salon.getCreditosIa());
        assertEquals(30, salon.getConfiguracion().getIntervaloMinutos());
    }

    @Test
    void debeMantenerLaRelacionEntreSalonYUsuario() {
        Salon salon = new Salon(
                "Barbería La Navaja", "barberia-la-navaja", "Medellín",
                "Calle 10 # 43-20", "3001234567", 10,
                LocalDateTime.of(2026, 11, 8, 10, 0)
        );
        Usuario usuario = new Usuario("Camilo Ríos", "camilo@example.com",
                "hash", Rol.DUENO, true, null);

        salon.agregarUsuario(usuario);

        assertSame(salon, usuario.getSalon());
        assertEquals(1, salon.getUsuarios().size());
        assertSame(usuario, salon.getUsuarios().get(0));
    }
}
