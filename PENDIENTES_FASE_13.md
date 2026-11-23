# Fase 13 — evidencia ejecutable y entrega

## Pieza 1: Postman
- [x] `postman/sillon.postman_collection.json` creado.
- [x] Siete carpetas en el orden y con los nombres solicitados.
- [x] 52 escenarios mínimos incluidos; cada petición tiene prueba Post-response.
- [x] Sufijo dinámico para correos y nombres de salones.
- [x] Fechas de lunes a sábado calculadas al menos dos días adelante y próximo domingo calculado en el pre-request inicial.
- [x] IDs y tokens guardados en variables de colección.
- [x] `sillon.local.postman_environment.json` con una sola variable `baseUrl`.
- [x] `sillon.produccion.postman_environment.json` con una sola variable `baseUrl`.
- [ ] Importar en Postman y ejecutar carpetas 1–6 contra API local y simuladores.
- [ ] Completar pago manual en Stripe y ejecutar carpeta 7.
- [ ] Reemplazar `https://TU-SERVICIO.onrender.com` por la URL real en el entorno de producción y ejecutar la colección contra Render.

## Pieza 2: README
- [x] Descripción de Sillón, stack, configuración local, endpoints, variables de entorno y procedimiento de Postman.
- [ ] Añadir URL real de Render y URL de Swagger.
- [ ] Completar las tres líneas de reflexión personal que el PDF solicita; el borrador debe revisarlo y hacerlo propio la estudiante.

## Pieza 3: calificador y Pull Request
- [ ] Recuperar el fork oficial, su `.github` original y Maven Wrapper; no se recrearon ni modificaron.
- [ ] Integrar el proyecto en la raíz del fork.
- [ ] Hacer push conservando historial de commits por fase.
- [ ] Abrir un único Pull Request hacia el repositorio oficial con el nombre completo de la estudiante.
- [ ] Leer los nueve criterios del calificador y corregir fallos sin cambiar reglas del PDF.

## Seguridad
No se incluyeron `secrets.properties`, tokens, llaves reales ni `.env` en la colección o el ZIP.
