# Fase 7 — Motor de IA y créditos

**Estado:** implementación equivalente incorporada; reutilización exacta de Atiende v4 y pruebas reales pendientes.

## Implementado según el PDF
- Paquete `com.devsenior.sillon.ia` con interfaz `ProveedorIa`.
- Proveedor principal con protocolo OpenAI compatible dirigido a `sillon.ia.openai.url` (Gemini).
- Proveedor Claude como respaldo, configurable y presente aunque no tenga llave.
- `AsistenteIa` intenta proveedores en orden, registra cada fallo y responde 503 si ninguno está disponible.
- `PropiedadesIa` tipadas con llaves, modelos, URL, tiempos máximos y tarifas configurables.
- `FabricaHttpIa` configura timeout de conexión y respuesta para `RestClient`.
- `BibliotecaPrompts` carga recursos de `src/main/resources/prompts` y sustituye variables.
- `LectorJsonIa` valida JSON estricto y las claves requeridas por cada función.
- Los imports de `ObjectMapper` y `JsonNode` usan `tools.jackson.databind`, y la excepción del lector usa `tools.jackson.core.JacksonException`, de acuerdo con Jackson 3 incluido en Spring Boot 4.
- Los valores de relleno `gemini_placeholder`, `claude_placeholder` y `reemplazar` (usado en la plantilla de secretos) se detectan como configuración ausente; el asistente puede registrar el fallo de ambos proveedores y responder 503 sin enviar llaves de ejemplo a los servicios.
- `FacturaIa` incluye proveedor, modelo, tokens de entrada/salida, costo estimado y milisegundos.
- `ServicioCreditosIa.verificar` devuelve 402 sin llamar a IA cuando el saldo es cero.
- `ServicioCreditosIa.consumir` ejecuta una única actualización condicional (`creditosIa > 0`) para impedir saldos negativos.
- La llamada externa no se envuelve en una transacción JPA; el crédito se consume después de recibir respuesta.

## Dependencia que no se pudo satisfacer literalmente
El PDF exige traer el paquete `ia` de Atiende v4 de la Clase 19. Ese proyecto no está entre los materiales disponibles de esta copia. Para avanzar, se implementó un paquete compatible con los componentes y contratos visibles del PDF, pero **no se afirma que sea el paquete original reutilizado**. Para cerrar esa condición se debe comparar/reemplazar con Atiende v4 cuando esté disponible.

## Costo estimado
El PDF exige el campo `costoEstimadoUsd`, pero no fija tarifas por token. Para no inventar precios, las tarifas de entrada/salida son configurables y quedan en cero por defecto. La factura incluye el campo; el costo real requiere las tarifas verificadas del proveedor o el paquete original.

## Verificación
- Se añadió una prueba de cálculo de `costoEstimadoUsd` con tarifas configuradas, sin fijar tarifas comerciales inventadas.
- El lector JSON rechaza contenido nulo o en blanco como error de pasarela (502), además de JSON mal formado.
## Verificación
Los archivos de producción del paquete IA compilaron sintácticamente con `javac --release 17` usando stubs temporales. Hay pruebas JUnit escritas para fallback, 503, JSON, créditos y la detección de llaves de relleno en ambos proveedores, incluida la palabra `reemplazar` de la plantilla. No se ejecutó Maven/JUnit real ni se llamó a Gemini/Claude.
