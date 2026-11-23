# Material y acciones necesarios para cerrar el taller sin inventar nada

Este archivo separa lo que ya está implementado en la copia de lo que depende de materiales, cuentas o verificaciones que no están disponibles en el entorno.

## Material original todavía necesario

1. **Fork/repositorio oficial de Sillón**: debe aportar `.github`, Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn`) y la guía original. No se recrearon ni modificaron estos artefactos porque el PDF indica conservar el calificador oficial.
2. **Proyecto Atiende v4**: el PDF exige reutilizar su paquete `ia` de Clase 19 y adaptar el `AGENTS.md` v4. Como ese proyecto no se recibió, Fase 7 se reconstruyó de forma compatible con los componentes y contratos descritos, pero no puede acreditarse reutilización literal.
3. **Evidencia de Spring Initializr**: la generación inicial se marca `SIN IA` en el taller y requiere la acción/evidencia de la estudiante.

## Estado de las fases 7–13 en esta copia

- **Fase 7:** paquete IA, proveedores Gemini/Claude, fallback, configuración tipada, RestClient con timeout, prompts, lector JSON, factura y créditos atómicos implementados. Falta comparar con Atiende v4 y validar tarifas reales de tokens; el PDF no fija precios por token.
- **Fase 8:** datos verificados, prompt de recordatorio, servicio, validación de salida y endpoint implementados.
- **Fase 9:** chatbot con contexto del catálogo real, historial separado, anti-promesas, solicitudes y endpoints implementados.
- **Fase 10:** pruebas JUnit/Mockito escritas; falta ejecutarlas con Maven real y MySQL encendido.
- **Fase 11:** tags 0–9 y endpoints faltantes incorporados; falta abrir Swagger y autorizar con JWT real. El contrato parece listar 25 operaciones y la comprobación menciona 22.
- **Fase 12:** Dockerfile, `.dockerignore` y configuración preparados; Supabase/Render/webhook real no creados.
- **Fase 13:** colección Postman con 52 escenarios, siete carpetas y dos entornos preparada; falta ejecutar contra local/simuladores y producción, completar pago manual, URL real y Pull Request.

## Acciones externas que debe hacer la estudiante

- Crear en Stripe modo prueba los dos productos/precios y copiar los `price_...` en secretos locales.
- Configurar Gemini y, si se dispone, Claude en local; nunca enviar las llaves por chat.
- Crear Supabase y usar Session Pooler puerto 5432.
- Crear el Web Service Docker en Render y configurar variables privadas.
- Crear el webhook de producción en Stripe con `checkout.session.completed` y `customer.subscription.deleted`.
- Integrar la copia en el fork oficial sin modificar `.github`; recuperar Maven Wrapper y `.mvn` originales.
- Ejecutar `./mvnw test`, Stripe CLI, Postman, Swagger y la colección oficial del calificador.
- Abrir un único Pull Request hacia el repositorio oficial con el título que indique la instructora.

## Verificación técnica actual

Los subconjuntos modificados de fases 7–11 y las pruebas nuevas compilaron sintácticamente con `javac --release 17` usando stubs temporales. Los JSON Postman se parsearon y sus scripts JavaScript pasaron `node --check`. No se ejecutó Maven/JUnit real, no se levantó Spring Boot ni MySQL, no se llamaron los simuladores y no se desplegó.
