package com.devsenior.sillon.salon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.devsenior.sillon.compartido.DatosInvalidosException;

class GeneradorSlugTest {

    @Test
    void debeQuitarTildesYNormalizarElNombre() {
        SalonRepository repositorio = mock(SalonRepository.class);
        when(repositorio.existsBySlug("barberia-la-navaja")).thenReturn(false);

        GeneradorSlug generador = new GeneradorSlug(repositorio);

        assertEquals("barberia-la-navaja", generador.generar("Barbería La Navaja"));
    }

    @Test
    void debeReemplazarSignosYSeparadoresPorUnSoloGuion() {
        SalonRepository repositorio = mock(SalonRepository.class);
        when(repositorio.existsBySlug("peluqueria-estilo")).thenReturn(false);

        GeneradorSlug generador = new GeneradorSlug(repositorio);

        assertEquals("peluqueria-estilo", generador.generar("  Peluquería & Estilo!! "));
    }

    @Test
    void debeAgregarSufijosCuandoElSlugYaExiste() {
        SalonRepository repositorio = mock(SalonRepository.class);
        when(repositorio.existsBySlug("barberia-la-navaja")).thenReturn(true);
        when(repositorio.existsBySlug("barberia-la-navaja-2")).thenReturn(true);
        when(repositorio.existsBySlug("barberia-la-navaja-3")).thenReturn(false);

        GeneradorSlug generador = new GeneradorSlug(repositorio);

        assertEquals("barberia-la-navaja-3", generador.generar("Barbería La Navaja"));
    }

    @Test
    void debeRechazarUnNombreQueNoGenereUnSlug() {
        SalonRepository repositorio = mock(SalonRepository.class);
        GeneradorSlug generador = new GeneradorSlug(repositorio);

        assertThrows(DatosInvalidosException.class, () -> generador.generar("--- !!! ---"));
    }
}
