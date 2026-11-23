# Fase 7 — pendientes de cierre

- [x] Interfaz de proveedor IA.
- [x] Proveedor Gemini por protocolo OpenAI y URL configurable.
- [x] Proveedor Claude de respaldo.
- [x] Asistente con fallback y logs.
- [x] Configuración tipada y fábrica HTTP con timeout.
- [x] Imports de IA alineados con Jackson 3 (`tools.jackson.databind`), conforme a Spring Boot 4 y al paso 0.2 del PDF.
- [x] Los valores de relleno de Gemini y Claude se reconocen como no configurados; no se intenta una llamada HTTP con una llave de ejemplo.
- [x] Biblioteca de prompts y lector JSON.
- [x] Factura con proveedor, modelo, tokens, costo estimado y milisegundos.
- [x] Prueba del cálculo del costo estimado con tarifas configurables de entrada/salida, sin inventar tarifas por proveedor.
- [x] Lector JSON rechaza contenido nulo o en blanco como error 502.
- [x] Verificación y consumo atómico de créditos.
- [ ] Comparar/reemplazar con el paquete original de Atiende v4 (no fue proporcionado).
- [ ] Confirmar tarifas reales de tokens para completar el costo estimado sin inventar valores.
- [ ] Ejecutar pruebas JUnit y el simulador oficial.
