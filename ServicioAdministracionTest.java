package com.devsenior.sillon.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import org.junit.jupiter.api.Test;

class ServicioAdministracionTest {

    @Test
    void listaTodosLosSalonesDeLaPlataforma() {
        SalonRepository salonRepository = mock(SalonRepository.class);
        Salon salon = mock(Salon.class);
        when(salonRepository.findAll()).thenReturn(List.of(salon));

        ServicioAdministracion servicio = new ServicioAdministracion(salonRepository);

        assertEquals(1, servicio.listarSalones().size());
    }

    @Test
    void devuelveListaVaciaCuandoNoHaySalones() {
        SalonRepository salonRepository = mock(SalonRepository.class);
        when(salonRepository.findAll()).thenReturn(List.of());

        ServicioAdministracion servicio = new ServicioAdministracion(salonRepository);

        assertEquals(List.of(), servicio.listarSalones());
    }
}
