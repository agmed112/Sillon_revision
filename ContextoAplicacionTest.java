package com.devsenior.sillon;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/** Prueba de contexto completo del taller. Requiere MySQL disponible en localhost. */
@SpringBootTest
class ContextoAplicacionTest {

    @Autowired
    private ApplicationContext contexto;

    @Test
    void contextoDeSpringArrancaConLaBaseDeDatosConfigurada() {
        assertNotNull(contexto.getBean(SillonApplication.class));
    }
}
