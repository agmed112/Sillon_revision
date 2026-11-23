# Avance de Fase 5 — La cara pública del salón

Se incorporó el catálogo público por slug, la lista de servicios activos, estilistas activos con ids de servicios, el texto de horario y la disponibilidad calculada con `ReglasAgenda`.

La reserva en línea reutiliza el método compartido de `ServicioCitas`, por lo que aplica las mismas siete validaciones y guarda la cita como `PENDIENTE` / `EN_LINEA`.

No se exponen correos ni contraseñas en los DTO públicos.

La compilación, ejecución, MySQL y comprobación de respuestas HTTP siguen pendientes.
