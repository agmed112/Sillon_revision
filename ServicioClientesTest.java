package com.devsenior.sillon.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.devsenior.sillon.compartido.DatosInvalidosException;
import com.devsenior.sillon.salon.Salon;
import org.junit.jupiter.api.Test;

class ServicioClientesTest {

    @Test
    void devuelveElClienteExistenteDelMismoSalonYTelefonoNormalizado() {
        ClienteRepository repositorio = mock(ClienteRepository.class);
        ClienteMapper mapper = new ClienteMapper();
        ServicioClientes servicio = new ServicioClientes(repositorio, mapper);
        Salon salon = mock(Salon.class);
        when(salon.getId()).thenReturn(8L);
        Cliente existente = mock(Cliente.class);

        when(repositorio.findBySalonIdAndTelefono(8L, "+573001112233"))
                .thenReturn(Optional.of(existente));

        Cliente resultado = servicio.buscarOCrear(
                salon, "Valentina Ruiz", "+57 (300) 111-22-33");

        assertSame(existente, resultado);
        verify(repositorio, never()).save(any(Cliente.class));
    }

    @Test
    void creaElClienteCuandoElTelefonoNoExisteEnElSalon() {
        ClienteRepository repositorio = mock(ClienteRepository.class);
        ClienteMapper mapper = new ClienteMapper();
        ServicioClientes servicio = new ServicioClientes(repositorio, mapper);
        Salon salon = mock(Salon.class);
        when(salon.getId()).thenReturn(8L);
        when(repositorio.findBySalonIdAndTelefono(8L, "3001112233"))
                .thenReturn(Optional.empty());
        when(repositorio.save(any(Cliente.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Cliente resultado = servicio.buscarOCrear(
                salon, "Valentina Ruiz", "300 111 22 33");

        assertEquals("Valentina Ruiz", resultado.getNombre());
        assertEquals("3001112233", resultado.getTelefono());
        assertSame(salon, resultado.getSalon());
        verify(repositorio).save(any(Cliente.class));
    }

    @Test
    void rechazaUnTelefonoQueNoContieneDigitos() {
        ClienteRepository repositorio = mock(ClienteRepository.class);
        ServicioClientes servicio = new ServicioClientes(repositorio, new ClienteMapper());
        Salon salon = mock(Salon.class);
        when(salon.getId()).thenReturn(8L);

        assertThrows(DatosInvalidosException.class,
                () -> servicio.buscarOCrear(salon, "Valentina Ruiz", "(---) +"));
        verify(repositorio, never()).findBySalonIdAndTelefono(8L, "+");
    }
}
