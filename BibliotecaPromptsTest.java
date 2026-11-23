package com.devsenior.sillon.ia;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import com.devsenior.sillon.compartido.PasarelaException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class BibliotecaPromptsTest {

    @Test
    void cargaRecordatorioYReemplazaSusTresVariables() {
        BibliotecaPrompts biblioteca = new BibliotecaPrompts(new DefaultResourceLoader());
        String prompt = biblioteca.cargar("recordatorio.txt", Map.of(
                "salon", "Barbería Sillón",
                "cliente", "Sara Mejía",
                "datos", "Servicio: Corte clásico (45 minutos)"));

        assertTrue(prompt.contains("Barbería Sillón"));
        assertTrue(prompt.contains("Sara Mejía"));
        assertTrue(prompt.contains("Servicio: Corte clásico (45 minutos)"));
        assertFalse(prompt.contains("{{salon}}"));
        assertFalse(prompt.contains("{{cliente}}"));
        assertFalse(prompt.contains("{{datos}}"));
    }

    @Test
    void cargaChatbotYReemplazaSusCincoVariables() {
        BibliotecaPrompts biblioteca = new BibliotecaPrompts(new DefaultResourceLoader());
        String prompt = biblioteca.cargar("chatbot.txt", Map.of(
                "salon", "Barbería Sillón",
                "direccion", "Calle 10, Medellín",
                "horario", "Lunes a sábado",
                "catalogo", "Corte clásico (45 minutos): $35.000",
                "cliente", "Camilo"));

        assertTrue(prompt.contains("Corte clásico (45 minutos): $35.000"));
        assertTrue(prompt.contains("Calle 10, Medellín"));
        assertFalse(prompt.contains("{{"));
    }

    @Test
    void archivoDePromptInexistenteSeClasificaComoErrorDePasarela() {
        BibliotecaPrompts biblioteca = new BibliotecaPrompts(new DefaultResourceLoader());
        assertThrows(PasarelaException.class,
                () -> biblioteca.cargar("no-existe.txt", Map.of()));
    }

    @Test
    void rechazaVariablesQueQuedaronSinResolverEnElPrompt() {
        BibliotecaPrompts biblioteca = new BibliotecaPrompts(new DefaultResourceLoader());

        assertThrows(PasarelaException.class,
                () -> biblioteca.cargar("recordatorio.txt", Map.of("salon", "Barbería Sillón")));
    }
}
