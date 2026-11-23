package com.devsenior.sillon.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.devsenior.sillon.compartido.PagoRequeridoException;
import com.devsenior.sillon.compartido.RecursoNoEncontradoException;
import com.devsenior.sillon.compartido.ReglaNegocioException;
import com.devsenior.sillon.salon.Plan;
import com.devsenior.sillon.salon.Salon;
import com.devsenior.sillon.salon.SalonRepository;
import com.devsenior.sillon.servicio.Servicio;
import com.devsenior.sillon.servicio.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class ServicioEstilistasTest {

    private UsuarioRepository usuarioRepository;
    private SalonRepository salonRepository;
    private ServicioRepository servicioRepository;
    private PasswordEncoder passwordEncoder;
    private EstilistaMapper estilistaMapper;
    private ServicioEstilistas servicio;

    @BeforeEach
    void configurar() {
        usuarioRepository = mock(UsuarioRepository.class);
        salonRepository = mock(SalonRepository.class);
        servicioRepository = mock(ServicioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        estilistaMapper = mock(EstilistaMapper.class);

        servicio = new ServicioEstilistas(
                usuarioRepository,
                salonRepository,
                servicioRepository,
                passwordEncoder,
                estilistaMapper,
                2
        );
    }

    @Test
    void validaPrimeroElLimiteGratisYNoConsultaCorreoNiServicios() {
        Salon salon = salon(Plan.GRATIS);
        when(salonRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(salon));
        when(usuarioRepository.countBySalonIdAndRol(7L, Rol.ESTILISTA)).thenReturn(2L);

        assertThrows(PagoRequeridoException.class,
                () -> servicio.crear(7L, solicitud()));

        verify(salonRepository).findByIdForUpdate(7L);
        verify(usuarioRepository).countBySalonIdAndRol(7L, Rol.ESTILISTA);
        verify(usuarioRepository, never()).existsByEmail(any());
        verifyNoInteractions(servicioRepository, passwordEncoder, estilistaMapper);
    }

    @Test
    void planProNoAplicaElLimiteGratisYPermiteCrearEstilista() {
        Salon salon = salon(Plan.PRO);
        Servicio corte = mock(Servicio.class);
        EstilistaRespuesta respuestaEsperada = new EstilistaRespuesta(
                12L, "Mateo Gil", "mateo@example.com", true, List.of("Corte"));

        when(salonRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(salon));
        when(usuarioRepository.existsByEmail("mateo@example.com")).thenReturn(false);
        when(servicioRepository.findAllByIdInAndSalonIdOrderByNombreAsc(List.of(4L), 7L))
                .thenReturn(List.of(corte));
        when(passwordEncoder.encode("ClaveSegura123")).thenReturn("hash");
        when(usuarioRepository.saveAndFlush(any(Usuario.class)))
                .thenReturn(mock(Usuario.class));
        when(estilistaMapper.aRespuesta(any(Usuario.class))).thenReturn(respuestaEsperada);

        EstilistaRespuesta respuesta = servicio.crear(7L, solicitud());

        assertEquals(respuestaEsperada, respuesta);
        verify(salonRepository).findByIdForUpdate(7L);
        verify(usuarioRepository, never()).countBySalonIdAndRol(7L, Rol.ESTILISTA);
        verify(usuarioRepository).existsByEmail("mateo@example.com");
        verify(servicioRepository).findAllByIdInAndSalonIdOrderByNombreAsc(List.of(4L), 7L);
        verify(usuarioRepository).saveAndFlush(any(Usuario.class));
    }

    @Test
    void correoDuplicadoSeValidaAntesDeConsultarServicios() {
        Salon salon = salon(Plan.PRO);
        when(salonRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(salon));
        when(usuarioRepository.existsByEmail("mateo@example.com")).thenReturn(true);

        assertThrows(ReglaNegocioException.class,
                () -> servicio.crear(7L, solicitud()));

        verify(usuarioRepository).existsByEmail("mateo@example.com");
        verifyNoInteractions(servicioRepository, passwordEncoder, estilistaMapper);
    }

    @Test
    void servicioDeOtroSalonSeRechazaAntesDeGuardarEstilista() {
        Salon salon = salon(Plan.PRO);
        when(salonRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(salon));
        when(usuarioRepository.existsByEmail("mateo@example.com")).thenReturn(false);
        when(servicioRepository.findAllByIdInAndSalonIdOrderByNombreAsc(List.of(4L), 7L))
                .thenReturn(List.of());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.crear(7L, solicitud()));

        verify(usuarioRepository, never()).saveAndFlush(any(Usuario.class));
    }

    private Salon salon(Plan plan) {
        Salon salon = mock(Salon.class);
        when(salon.getId()).thenReturn(7L);
        when(salon.getPlan()).thenReturn(plan);
        return salon;
    }

    private CrearEstilistaRequest solicitud() {
        return new CrearEstilistaRequest(
                "Mateo Gil",
                "Mateo@Example.com",
                "ClaveSegura123",
                List.of(4L, 4L)
        );
    }
}
