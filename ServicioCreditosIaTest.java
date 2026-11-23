package com.devsenior.sillon.ia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.devsenior.sillon.compartido.PagoRequeridoException;
import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioCreditosIaTest {

    private SalonRepository repository;
    private ServicioCreditosIa servicio;

    @BeforeEach
    void configurar() {
        repository = mock(SalonRepository.class);
        servicio = new ServicioCreditosIa(repository);
    }

    @Test
    void rechazaAntesDeLlamarAIACuandoNoHayCreditos() {
        Salon salon = mock(Salon.class);
        when(repository.findById(4L)).thenReturn(Optional.of(salon));
        when(salon.getCreditosIa()).thenReturn(0);
        assertThrows(PagoRequeridoException.class, () -> servicio.verificar(4L));
    }

    @Test
    void consumeConUnaActualizacionCondicionalYDevuelveSaldo() {
        when(repository.descontarCreditoDisponible(4L)).thenReturn(1);
        Salon salon = mock(Salon.class);
        when(repository.findById(4L)).thenReturn(Optional.of(salon));
        when(salon.getCreditosIa()).thenReturn(8);
        assertEquals(8, servicio.consumir(4L));
        verify(repository).descontarCreditoDisponible(4L);
    }

    @Test
    void noDejaSaldoNegativoSiLaActualizacionNoEncuentraCredito() {
        when(repository.descontarCreditoDisponible(4L)).thenReturn(0);
        assertThrows(PagoRequeridoException.class, () -> servicio.consumir(4L));
    }
}
