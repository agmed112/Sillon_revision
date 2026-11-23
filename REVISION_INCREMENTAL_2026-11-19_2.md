# Revisión incremental de Sillón — 19 de noviembre de 2026 (segunda ronda)

## Alcance
Se revisó la última versión de `Sillon_revision_2026-11-19.zip` contra las instrucciones visibles del PDF del taller, especialmente los pasos 7.2, 8.3, 9.3–9.4, la lista mínima de pruebas de la Fase 10 y la organización/escenarios de Postman de la Fase 13.

## Correcciones de código
1. `LectorJsonIa.leerObjeto` ahora rechaza explícitamente contenido `null` o en blanco antes de llamar a Jackson. El resultado se clasifica como `PasarelaException` (HTTP 502), evitando una excepción inesperada si el proveedor devuelve contenido vacío.
2. Se conserva el orden de la Fase 7: validar el recurso, verificar créditos, llamar al proveedor, consumir cuando el proveedor ya respondió y después validar el JSON. No se movió el consumo antes de la llamada externa.
3. No se inventaron tarifas comerciales de Gemini o Claude. Se agregó una prueba de la fórmula de `costoEstimadoUsd` con tarifas de ejemplo configuradas exclusivamente dentro del test: 500.000 tokens de entrada a USD 2 por millón y 250.000 de salida a USD 8 por millón producen USD 3.00000000.

## Pruebas añadidas
- Lector JSON: contenido nulo y contenido en blanco.
- Factura IA: cálculo del costo estimado con tarifas configurables.
- Recordatorio: cita no elegible no consulta créditos ni llama a IA; proveedor no disponible no consume crédito; mensaje de más de 700 caracteres se rechaza con 502 después del consumo que establece el PDF.
- Chatbot: salón inexistente falla antes de consultar créditos/IA; conserva el historial completo al llamar al modelo y guarda solo mensajes del cliente al escalar; bloquea términos restringidos escritos en mayúsculas; proveedor no disponible no consume crédito; JSON inválido se rechaza después de consumir, siguiendo el orden del PDF.
- Biblioteca de prompts: rechaza variables que quedaron sin resolver.

## Inventario actualizado
- 115 clases Java de producción.
- 25 archivos `*Test.java`.
- 118 métodos `@Test`.
- 2 métodos `@ParameterizedTest` con 11 filas de datos.
- 129 invocaciones previstas si JUnit ejecuta todos los métodos y filas.

Las 129 invocaciones son pruebas escritas, no pruebas aprobadas. No se ha ejecutado Maven/JUnit real.

## Verificaciones estáticas de esta ronda
- `pom.xml` se parsea como XML.
- Los seis archivos Java modificados se revisaron con `javac` en modo de análisis sintáctico; no se detectaron diagnósticos de sintaxis. El comando no pudo completar compilación semántica porque no están disponibles las dependencias Maven.
- La colección Postman contiene 52 peticiones y siete carpetas con los nombres/orden del PDF.
- Las 52 peticiones tienen prueba Post-response y comprobación del código HTTP esperado.
- Los 53 scripts Postman (pre-request y post-response) pasaron `node --check`.
- Cada entorno Postman contiene una única variable: `baseUrl`.
- Los imports internos del proyecto, las rutas de paquetes y los nombres de tipos públicos coinciden con sus archivos.
- No se encontró ningún import residual de `com.fasterxml.jackson` en Java.
- El ZIP se empaquetó sin `secrets.properties`, `.env`, `target` ni metadatos `.git`; su integridad se comprobó después de crearlo.

## Límites que siguen vigentes
- Maven y el Maven Wrapper oficial no están disponibles en este entorno; no se puede declarar `BUILD SUCCESS`.
- No se ejecutó JUnit, no se levantó Spring Boot con MySQL y no se probó la persistencia en PostgreSQL.
- No se ejecutaron Gemini/Claude ni los simuladores oficiales de IA/Stripe.
- No se ejecutó Postman Runner contra una API activa.
- No se creó Supabase/Render ni un webhook de producción.
- Atiende v4 y el fork oficial de GitHub siguen sin estar disponibles; por eso no se puede afirmar reutilización literal del paquete original ni completar el Pull Request.

## Dictamen
Esta ronda mejora la robustez de las respuestas IA vacías y amplía la cobertura escrita de casos límite de las fases 7–10. La estructura de Postman se mantiene alineada con la Fase 13. La aplicación sigue pendiente de compilación y ejecución real; no se declara terminada ni desplegada.
