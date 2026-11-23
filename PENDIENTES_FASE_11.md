# Fase 11 — pendientes de cierre

- [x] Swagger UI configurado en `/swagger-ui.html`.
- [x] Título exacto `Sillón API`.
- [x] Esquema Bearer JWT `bearerAuth`.
- [x] Tags numerados 0 a 9, incluido `8 Solicitudes`.
- [x] `POST /api/citas/{id}/recordatorio`.
- [x] `POST /api/publico/salones/{slug}/chat`.
- [x] `GET /api/solicitudes`.
- [x] `PATCH /api/solicitudes/{id}/atendida`.
- [ ] Abrir Swagger UI en una aplicación ejecutándose y comprobar visualmente todos los tags y rutas.
- [ ] Autorizar con JWT real de dueño y comprobar GET `/api/salon` = 200.
- [ ] Confirmar con el calificador la diferencia entre las 22 rutas mencionadas y las 25 operaciones enumeradas en el contrato.

## Nota de consistencia del PDF
La tabla de contrato de la sección 4 parece enumerar 25 combinaciones método/ruta, mientras que la comprobación de Fase 11 menciona 22 rutas. Se mantienen las operaciones explícitas del contrato sin suprimir ninguna.
