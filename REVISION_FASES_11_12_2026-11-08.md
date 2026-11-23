# Revisión técnica — Fases 11 y 12

Fecha: 2026-11-08.

## Fase 11 — Swagger/OpenAPI

### Comprobado estáticamente
- `ConfiguracionOpenApi` define título `Sillón API`.
- Esquema de seguridad `bearerAuth` con HTTP Bearer y formato JWT.
- `application.properties` define `springdoc.swagger-ui.path=/swagger-ui.html`.
- Hay 9 controladores etiquetados: 0 Público, 1 Autenticación, 2 Mi salón, 3 Servicios, 4 Estilistas, 5 Clientes, 6 Agenda, 7 Pagos y 9 Administración.
- Hay 21 mappings HTTP en los controladores actuales.
- Los controladores protegidos existentes declaran `@SecurityRequirement`; los endpoints de webhook/resultado y los controladores públicos permanecen sin requisito Bearer.
- `GET /api/admin/salones` está protegido por `hasRole("ADMIN")`.
- `ServicioAdministracion` es de solo lectura y se añadieron dos pruebas unitarias.

### Pendiente
- Tag 8 Solicitudes y operaciones de solicitud dependen de la Fase 9 no implementada.
- El recordatorio de cita depende de Fase 8 y el chat público de Fase 9.
- No se ha iniciado Spring Boot ni se abrió Swagger UI en navegador.
- No se probó Authorize con JWT real ni `GET /api/salon` con respuesta 200.
- Por tanto, no se afirma que Swagger muestre todas las rutas del taller.

## Fase 12 — Docker/Supabase/Render

### Comprobado estáticamente
- Dockerfile contiene exactamente dos etapas: Maven/Temurin 17 para construir y Temurin 17 JRE para ejecutar.
- El build ejecuta `mvn -B -DskipTests clean package`.
- `.dockerignore` excluye `.git`, `target` y `secrets.properties`.
- `server.port` utiliza `PORT` con 8080 como valor predeterminado.
- POM incluye el driver PostgreSQL runtime además de MySQL.
- `application.properties` permite sobrescribir datasource, JWT, URL pública, Stripe y Gemini con las variables del PDF.
- `springdoc.swagger-ui.path` y el endpoint admin están incorporados.

### Pendiente
- No se creó proyecto Supabase ni se verificó la conexión Session Pooler.
- No se creó Web Service en Render ni se ejecutó un build Docker real.
- No se configuraron variables privadas ni webhook de producción.
- No hay URL real de Render; no se inventó.
- No se probó registro/login/pago contra producción.

## Verificación del código
- El paquete `pago` y el nuevo paquete `admin` compilaron sintácticamente con `javac --release 17` contra stubs temporales de APIs externas.
- El smoke test aislado de webhook siguió pasando tras estos cambios.
- El XML del POM es válido.
- No se ejecutó Maven, JUnit real, Docker, Spring Boot, PostgreSQL, Stripe CLI ni Render.

## Dictamen
Fase 11: parcialmente implementada, no cerrada.
Fase 12: artefactos y guía preparados, no desplegada.
