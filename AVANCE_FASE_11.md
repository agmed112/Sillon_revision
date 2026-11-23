# Fase 11 — Swagger / OpenAPI

**Estado:** rutas y etiquetas incorporadas; comprobación visual de Swagger pendiente.

## Implementado
- `ConfiguracionOpenApi` mantiene título `Sillón API`, versión y esquema Bearer JWT `bearerAuth`.
- `springdoc.swagger-ui.path=/swagger-ui.html`.
- Tags numerados de controladores: 0 Público, 1 Autenticación, 2 Mi salón, 3 Servicios, 4 Estilistas, 5 Clientes, 6 Agenda, 7 Pagos, 8 Solicitudes y 9 Administración.
- Los controladores protegidos usan `@SecurityRequirement(name = "bearerAuth")`.
- Se incorporaron `POST /api/citas/{id}/recordatorio`, `POST /api/publico/salones/{slug}/chat`, `GET /api/solicitudes` y `PATCH /api/solicitudes/{id}/atendida`.

## Conteo de operaciones
El código contiene 25 mappings HTTP al contar cada combinación método/ruta del contrato de la sección 4. El PDF menciona que Swagger debe mostrar 22 rutas, aunque su tabla parece listar 25. Se conservan las operaciones explícitas del contrato; no se elimina ninguna para forzar el conteo. La diferencia debe contrastarse con la instructora/calificador.

## Pendiente
- [ ] Ejecutar la aplicación y abrir `/swagger-ui.html`.
- [ ] Comprobar que el botón Authorize permite usar JWT.
- [ ] Autorizar con token real de dueño y probar GET `/api/salon` = 200.
- [ ] Confirmar el conteo que utiliza el calificador oficial.
