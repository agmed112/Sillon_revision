# Fase 4 — Agenda: estado de avance

## Paso 4.1 — Estados de la cita
- [x] La estudiante definió las cinco transiciones permitidas y justificó cada una con una situación real.
- [x] Decisión registrada en `SPEC.md`.
- [x] `EstadoCita` implementa `puedeCambiarA` y `ocupaAgenda`.
- [x] `OrigenCita` implementa `SALON` y `EN_LINEA`.
- [x] Pruebas de cinco transiciones permitidas y seis bloqueadas.

## Paso 4.2 — Entidad Cita
- [x] Entidad `Cita` con cuatro relaciones `ManyToOne`.
- [x] Precio del servicio copiado en la cita.
- [x] Fechas automáticas de creación y actualización.
- [x] Estado y origen persistidos como texto.
- [x] Repositorio con consultas por salón, día, estilista y cruces.

## Paso 4.3 — ReglasAgenda
- [x] Componente sin dependencias de base de datos.
- [x] Validación de domingo, mismo día, apertura y cierre.
- [x] Cálculo de horarios por intervalo y duración.
- [x] Descarte de horas pasadas mediante Clock.
- [x] Descarte de cruces con estados que ocupan agenda.
- [x] Protección frente a duración de servicio mayor que la jornada, sin desbordamiento de `LocalTime`.
- [x] Pruebas unitarias con reloj fijo.

## Paso 4.4 — Agendar
- [x] Un único método privado reutilizado por agendamiento interno y reserva en línea.
- [x] Servicio del salón y activo.
- [x] Estilista del salón, activo y asignado al servicio.
- [x] Cálculo de la hora de fin desde la duración del servicio.
- [x] Rechazo de citas en el pasado.
- [x] Validación del horario del salón.
- [x] Validación de cruces con PENDIENTE y CONFIRMADA.
- [x] Bloqueo pesimista de la fila del estilista durante la reserva y aislamiento `READ_COMMITTED` para serializar reservas concurrentes del mismo estilista.
- [x] Buscar o crear cliente, copiar precio y guardar estado/origen.
- [x] POST `/api/citas` para DUENO.

## Paso 4.5 — Listar y cambiar estado
- [x] GET `/api/citas?fecha=`; fecha de hoy por defecto.
- [x] Dueño ve todas las citas del salón.
- [x] Estilista ve únicamente sus citas.
- [x] PATCH `/api/citas/{id}/estado`.
- [x] Cita ajena o inexistente responde 404.
- [x] Estilista solo puede cambiar sus citas a COMPLETADA o NO_ASISTIO; otros destinos 403.
- [x] Transiciones inválidas responden 409.
- [x] Lectura con bloqueo pesimista por cita y salón al cambiar estado, para serializar transiciones concurrentes.
- [x] `saveAndFlush` antes de construir la respuesta.
- [x] Pruebas escritas para cita pasada, estilista que no ofrece el servicio, cruce, reserva pública, cita ajena (404), cancelación por estilista (403), finalización válida y transición inválida (409).

## Verificación para la etapa final
- [ ] Compilar con Maven y ejecutar JUnit con dependencias reales.
- [ ] Verificar mapeos JPA y consultas en MySQL.
- [ ] Verificar endpoints con Postman y calificador oficial.
