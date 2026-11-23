package com.devsenior.sillon.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ClienteTest {

    @Test
    void normalizaElTelefonoConservandoDigitosYSignoMas() {
        assertEquals(
                "+573001112233",
                Cliente.normalizarTelefono("+57 (300) 111-22-33")
        );
    }

    @Test
    void eliminaEspaciosYCaracteresQueNoSeanDigitosNiMas() {
        assertEquals("3102223344", Cliente.normalizarTelefono("310 222 33.44 ext. 5"));
    }

    @Test
    void rechazaTelefonoNulo() {
        assertThrows(NullPointerException.class, () -> Cliente.normalizarTelefono(null));
    }
}
