package com.devsenior.sillon.ia;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.devsenior.sillon.compartido.ProveedorNoDisponibleException;
import org.junit.jupiter.api.Test;

class ProveedorClaudeTest {

    @Test
    void proveedorDeRespaldoSinLlaveDevuelve503SinIntentarUnaPeticionHttp() {
        PropiedadesIa propiedades = new PropiedadesIa();
        propiedades.getClaude().setClave("claude_placeholder");
        propiedades.getClaude().setModelo("claude-haiku-5-5");
        propiedades.getClaude().setUrl("https://api.anthropic.com/v1/messages");
        FabricaHttpIa fabrica = mock(FabricaHttpIa.class);

        ProveedorClaude proveedor = new ProveedorClaude(fabrica, propiedades);

        assertThrows(ProveedorNoDisponibleException.class,
                () -> proveedor.generar("instrucciones", "mensaje"));
        verifyNoInteractions(fabrica);
    }
    @Test
    void noIntentaConectarseCuandoLaPlantillaConservaLaPalabraReemplazar() {
        PropiedadesIa propiedades = new PropiedadesIa();
        propiedades.getClaude().setClave("reemplazar");
        propiedades.getClaude().setModelo("claude-haiku-5-5");
        propiedades.getClaude().setUrl("https://api.anthropic.com/v1/messages");
        FabricaHttpIa fabrica = mock(FabricaHttpIa.class);

        ProveedorClaude proveedor = new ProveedorClaude(fabrica, propiedades);

        assertThrows(ProveedorNoDisponibleException.class,
                () -> proveedor.generar("instrucciones", "mensaje"));
        verifyNoInteractions(fabrica);
    }

}
