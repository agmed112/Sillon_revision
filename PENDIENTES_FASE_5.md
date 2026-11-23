# Fase 5 — La cara pública del salón

## Paso 5.1 — Catálogo público
- [x] Buscar salón por slug.
- [x] Mostrar datos públicos del salón y horario.
- [x] Mostrar solo servicios activos.
- [x] Mostrar solo estilistas activos y sus ids de servicios.
- [x] No exponer correos, contraseñas ni datos internos.

## Paso 5.2 — Disponibilidad y reservas
- [x] Validar que servicio y estilista pertenezcan al salón.
- [x] Validar que servicio y estilista estén activos.
- [x] Validar que el estilista ofrezca el servicio.
- [x] Consultar citas del estilista para la fecha solicitada.
- [x] Calcular disponibilidad reutilizando ReglasAgenda.
- [x] Reservar en línea reutilizando ServicioCitas.reservarEnLinea.
- [x] La reserva pública nace PENDIENTE / EN_LINEA.

## Verificación final
- [ ] Ejecutar pruebas Maven.
- [ ] Verificar serialización JSON y horario español de Colombia.
- [ ] Verificar endpoints públicos con Postman y calificador.
