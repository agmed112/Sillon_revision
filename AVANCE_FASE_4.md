# Avance de Fase 4 — Agenda

## Decisiones de negocio
- Las cinco transiciones permitidas fueron justificadas por la estudiante y registradas en `SPEC.md`.
- El orden de las siete validaciones de agendamiento se implementó siguiendo literalmente el orden prescrito en el paso 4.4 del PDF. La estudiante aún debe revisar personalmente esa secuencia porque el documento indica que no se delega.

## Pasos implementados
- 4.1: `EstadoCita` y `OrigenCita`.
- 4.2: entidad `Cita`, cuatro relaciones ManyToOne, precio copiado y timestamps automáticos.
- 4.3: `ReglasAgenda`, validación de horarios, disponibilidad, reloj de negocio y cruces.
- 4.4: `ServicioCitas` con un método privado compartido por cita interna y reserva en línea; creación de cita interna mediante POST `/api/citas`.
- 4.5: listado diario por dueño/estilista y cambio de estado con permisos, 404/403/409 y `saveAndFlush`.
- Refuerzo de consistencia: las reservas bloquean la fila del estilista y usan `READ_COMMITTED` para que dos peticiones simultáneas no superen la consulta de cruces con una lectura antigua.
- Los cambios de estado bloquean la cita por id y salón, también con `READ_COMMITTED`, para que dos transiciones simultáneas no validen ambas contra un estado anterior.
- Refuerzo de disponibilidad: un servicio cuya duración supera la jornada completa devuelve una lista vacía; evita el desbordamiento circular de `LocalTime.minusMinutes`.
- Se ampliaron las pruebas unitarias escritas para cubrir reserva pública `PENDIENTE/EN_LINEA`, cita pasada, estilista sin servicio, cruce, permisos del estilista y transiciones inválidas.

## Pendiente de verificación
No se ha ejecutado Maven, JUnit, Spring Boot, Hibernate, MySQL ni HTTP. Los archivos están incorporados y revisados estáticamente; la verificación de ejecución queda pendiente para la revisión final.
