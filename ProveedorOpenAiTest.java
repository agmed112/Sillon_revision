package com.devsenior.sillon.ia;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.devsenior.sillon.compartido.ProveedorNoDisponibleException;
import org.junit.jupiter.api.Test;

class ProveedorOpenAiTest {

    @Test
    void noIntentaConectarseCuandoLaLlaveEsElRellenoDeConfiguracion() {
        PropiedadesIa propiedades = new PropiedadesIa();
        propiedades.getGemini().setClave("gemini_placeholder");
        propiedades.getGemini().setModelo("gemini-3.5-flash");
        propiedades.getOpenai().setUrl("https://generativelanguage.googleapis.com/v1beta/openai/chat/completions");
        FabricaHttpIa fabrica = mock(FabricaHttpIa.class);

        ProveedorOpenAi proveedor = new ProveedorOpenAi(fabrica, propiedades);

        assertThrows(ProveedorNoDisponibleException.class,
                () -> proveedor.generar("instrucciones", "mensaje"));
        verifyNoInteractions(fabrica);
    }
    @Test
    void noIntentaConectarseCuandoLaPlantillaConservaLaPalabraReemplazar() {
        PropiedadesIa propiedades = new PropiedadesIa();
        propiedades.getGemini().setClave("reemplazar");
        propiedades.getGemini().setModelo("gemini-3.5-flash");
        propiedades.getOpenai().setUrl("https://generativelanguage.googleapis.com/v1beta/openai/chat/completions");
        FabricaHttpIa fabrica = mock(FabricaHttpIa.class);

        ProveedorOpenAi proveedor = new ProveedorOpenAi(fabrica, propiedades);

        assertThrows(ProveedorNoDisponibleException.class,
                () -> proveedor.generar("instrucciones", "mensaje"));
        verifyNoInteractions(fabrica);
    }

}
