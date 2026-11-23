package com.devsenior.sillon.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.devsenior.sillon.servicio.Servicio;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void debeRecortarEspaciosExternosYNormalizarMayusculasDelCorreo() {
        Usuario usuario = new Usuario("Camilo Ríos", "  Camilo.Rios@Example.COM  ",
                "hash", Rol.DUENO, true, null);

        assertEquals("camilo.rios@example.com", usuario.getEmail());
    }

    @Test
    void noEliminaEspaciosInternosDeUnCorreoInvalido() {
        Usuario usuario = new Usuario("Camilo Ríos", "camilo rios@example.com",
                "hash", Rol.DUENO, true, null);

        // La validación @Email debe rechazarlo; el modelo no debe convertirlo en otra dirección.
        assertEquals("camilo rios@example.com", usuario.getEmail());
    }

    @Test
    void debeIndicarSiElEstilistaOfreceUnServicio() {
        Usuario estilista = new Usuario(
                "Mateo Gil", "mateo@example.com", "hash", Rol.ESTILISTA, true, null);
        Servicio corte = mock(Servicio.class);
        when(corte.getId()).thenReturn(15L);
        estilista.asignarServicios(List.of(corte));

        assertTrue(estilista.ofreceServicio(15L));
        assertFalse(estilista.ofreceServicio(20L));
        assertFalse(estilista.ofreceServicio(null));
    }
}
