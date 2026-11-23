package com.devsenior.sillon.ia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.devsenior.sillon.compartido.PasarelaException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LectorJsonIaTest {

    private LectorJsonIa lector;

    @BeforeEach
    void configurar() {
        lector = new LectorJsonIa(new ObjectMapper());
    }

    @Test
    void leeMensajeDelRecordatorio() {
        assertEquals("Hola, Sara.", lector.leerMensaje("{\"mensaje\":\"Hola, Sara.\"}"));
    }

    @Test
    void rechazaJsonSinClaveMensaje() {
        assertThrows(PasarelaException.class,
                () -> lector.leerMensaje("{\"texto\":\"Hola\"}"));
    }

    @Test
    void rechazaContenidoQueNoEsJson() {
        assertThrows(PasarelaException.class, () -> lector.leerMensaje("No es JSON"));
    }

    @Test
    void leeRespuestaYEscalamientoDelChatbot() {
        var datos = lector.leerChatbot("{\"respuesta\":\"Con gusto\",\"escalar\":true}");
        assertEquals("Con gusto", datos.respuesta());
        assertTrue(datos.escalar());
    }

    @Test
    void rechazaEscalamientoQueNoSeaBooleano() {
        assertThrows(PasarelaException.class,
                () -> lector.leerChatbot("{\"respuesta\":\"Hola\",\"escalar\":\"si\"}"));
    }

    @Test
    void rechazaRespuestaVaciaDelChatbot() {
        assertThrows(PasarelaException.class,
                () -> lector.leerChatbot("{\"respuesta\":\" \t\",\"escalar\":false}"));
    }

    @Test
    void rechazaContenidoNuloComoErrorDePasarela() {
        assertThrows(PasarelaException.class, () -> lector.leerMensaje(null));
    }

    @Test
    void rechazaContenidoVacioComoErrorDePasarela() {
        assertThrows(PasarelaException.class, () -> lector.leerChatbot("   "));
    }
}
