# Informe de avance y verificación — Sillón, Fases 7–13

**Versión revisada:** cierre de implementación de Fases 7–13.  
**Base:** última copia `Sillon_revision_logica_transversal.zip` y PDF visible del taller.

## Resultado de esta ronda

Se incorporó código para el motor IA, recordatorio, chatbot, solicitudes, cuatro rutas de API, pruebas unitarias adicionales, documentación de despliegue y colección Postman con los 52 escenarios indicados por el taller. La colección se alineó con el anexo de datos de prueba (La Navaja/Camilo, Aurora/Diana, Corte clásico/Arreglo de barba, Mateo/Sara, Valentina/Andrés y Juliana/Pedro).

### Avance de implementación (estimación, no nota oficial)

**Fases 7–13: aproximadamente 87/100 en artefactos de implementación.** Este indicador mide que el código, prompts, controladores, pruebas escritas, documentación y colección Postman ya están preparados. No mide que todo haya sido ejecutado en servicios reales.

No convierto ese 87 % en nota final del taller: siguen sin verificarse Maven/JUnit, MySQL, simuladores, Swagger en ejecución, Stripe, Render, Postman Runner, fork oficial y Pull Request.

## Correcciones incrementales del 19 de noviembre de 2026

- Se alinearon los imports de los proveedores y el lector JSON con Jackson 3 (`tools.jackson.databind`), exigido por Spring Boot 4 según la página 15 del PDF.
- Gemini y Claude ahora identifican las llaves `placeholder` como configuración ausente; no se envían valores de ejemplo a servicios externos.
- Se configuró `maven-compiler-plugin` con `<parameters>true</parameters>` para conservar nombres de parámetros en OpenAPI.
- Se agregaron cuatro casos de prueba de proveedor, dos para Gemini y dos para Claude. La suite escrita queda en 25 archivos, 118 métodos `@Test` y 11 filas parametrizadas (129 invocaciones previstas), sin afirmar que JUnit las haya ejecutado.
- El detalle se encuentra en `REVISION_INCREMENTAL_2026-11-19.md`.

## Mejoras adicionales de esta revisión
- `LectorJsonIa` rechaza respuestas nulas o en blanco de forma controlada como error 502, evitando que una respuesta vacía termine en una excepción inesperada.
- `AsistenteIaTest` comprueba el cálculo de factura con tarifas configurables: 500.000 tokens de entrada a USD 2/millón y 250.000 de salida a USD 8/millón producen USD 3.00000000. Son valores de prueba para verificar la fórmula, no tarifas comerciales recomendadas.
- `ServicioRecordatoriosTest` verifica que una cita no elegible no consulta créditos ni llama a IA y que el límite de 700 caracteres se aplica después de consumir el crédito, tal como ordena el PDF.
- `ServicioChatbotTest` verifica que un salón inexistente no consulta créditos ni llama a IA, que se conserva el historial completo y que al escalar solo se guardan mensajes del cliente; también prueba términos restringidos en mayúsculas.
- `BibliotecaPromptsTest` verifica que un prompt con variables sin resolver se rechaza.

## Verificaciones realizadas

- Compilación sintáctica con `javac --release 17` de los subconjuntos de producción y pruebas nuevos/modificados, usando stubs temporales de Spring, Jackson, JUnit y Mockito. No es una compilación contra dependencias Maven reales.
- JSON de colección y entornos Postman parseados correctamente.
- 52 peticiones en 7 carpetas con tamaños 9/5/20/4/8/4/2.
- Cada petición contiene Post-response; los 53 scripts de pre-request/test pasaron `node --check`.
- Los dos entornos tienen exactamente una variable: `baseUrl`.
- Las variables de los dos prompts coinciden con las variables que el código reemplaza.
- Se verificó el conteo estático de rutas: 25 operaciones método/ruta y tags OpenAPI del 0 al 9.
- `pom.xml` es XML válido; no hay claves reales ni `secrets.properties` en el paquete.

## Lo que no se pudo ejecutar

- `mvn` no está instalado; el Maven Wrapper oficial no está en la copia.
- No se pudo descargar Maven porque el entorno no resuelve `repo.maven.apache.org`.
- Docker tampoco está instalado.
- No se levantó Spring Boot ni MySQL, no se ejecutó JUnit real, y no se llamó a los simuladores IA/Stripe.
- No se ejecutó Postman Runner contra la API.
- No se crearon proyectos Supabase/Render ni webhook de producción.
- No hay fork oficial conectado, por lo que no se puede hacer push, respetar el historial oficial ni abrir el Pull Request.

## Condiciones que siguen bloqueando el 100 %

1. **Atiende v4:** el PDF exige reutilizar literalmente su paquete IA. No se recibió ese proyecto; el paquete actual se reconstruyó a partir del contrato visible. Se debe comparar/reemplazar con el original cuando se entregue.
2. **Fork oficial:** faltan `.github` y Maven Wrapper originales. No se recrearon para no sustituir el calificador.
3. **Tarifas IA:** el PDF pide costo estimado, pero no fija tarifas por token. El campo existe y las tarifas son configurables; por defecto el costo queda en cero hasta confirmar las tarifas del proveedor/paquete original.
4. **Ejecución:** faltan `./mvnw test`, MySQL, Stripe CLI, simuladores, Swagger y Postman Runner reales.
5. **Despliegue y entrega:** faltan credenciales/cuentas de Supabase, Render y Stripe, URL HTTPS real, entorno Postman de producción actualizado y Pull Request.
6. **Discrepancia de rutas:** el contrato parece enumerar 25 operaciones, mientras que la comprobación del PDF menciona 22. Se conservaron las operaciones explícitas del contrato.
7. **Reflexión personal:** el README contiene un borrador de tres líneas para que la estudiante lo revise y lo haga propio.

## Dictamen

La parte de implementación de las fases 7–13 avanzó y sus artefactos ya están en el ZIP. La aplicación todavía no se puede declarar funcional de extremo a extremo ni lista para entrega oficial hasta resolver los bloqueos anteriores y ejecutar las verificaciones reales.
