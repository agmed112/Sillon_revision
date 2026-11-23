package com.devsenior.sillon.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.usuario.Rol;
import com.devsenior.sillon.usuario.Usuario;
import com.devsenior.sillon.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

class UsuarioDetailsServiceTest {

    @Test
    void construyeElPrincipalConElIdDelSalon() {
        UsuarioRepository repositorio = mock(UsuarioRepository.class);
        UsuarioDetailsService servicio = new UsuarioDetailsService(repositorio);
        Usuario usuario = mock(Usuario.class);
        Salon salon = mock(Salon.class);

        when(repositorio.findByEmail("dueno@sillon.test")).thenReturn(Optional.of(usuario));
        when(usuario.getId()).thenReturn(12L);
        when(usuario.getSalon()).thenReturn(salon);
        when(salon.getId()).thenReturn(7L);
        when(usuario.getEmail()).thenReturn("dueno@sillon.test");
        when(usuario.getPassword()).thenReturn("hash-bcrypt");
        when(usuario.getRol()).thenReturn(Rol.DUENO);
        when(usuario.isActivo()).thenReturn(true);

        UsuarioPrincipal principal = (UsuarioPrincipal) servicio.loadUserByUsername("dueno@sillon.test");

        assertEquals(12L, principal.getId());
        assertEquals(7L, principal.getSalonId());
        assertEquals("dueno@sillon.test", principal.getEmail());
        assertEquals(Rol.DUENO, principal.getRol());
        assertTrue(principal.isEnabled());
    }

    @Test
    void cargaElPrincipalDentroDeUnaTransaccionDeSoloLectura() throws Exception {
        var metodo = UsuarioDetailsService.class.getMethod("loadUserByUsername", String.class);
        var transaccion = metodo.getAnnotation(Transactional.class);

        assertTrue(transaccion != null);
        assertTrue(transaccion.readOnly());
    }

    @Test
    void usuarioInexistenteProduceUsernameNotFoundException() {
        UsuarioRepository repositorio = mock(UsuarioRepository.class);
        UsuarioDetailsService servicio = new UsuarioDetailsService(repositorio);
        when(repositorio.findByEmail("no-existe@sillon.test")).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> servicio.loadUserByUsername("no-existe@sillon.test"));
    }
}
