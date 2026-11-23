# Fase 8 — pendientes de cierre

- [x] Datos verificados calculados por Java para citas PENDIENTE/CONFIRMADA.
- [x] Fecha y hora colombianas, con normalización de espacios invisibles.
- [x] Prompt `recordatorio.txt` con variables y reglas del taller.
- [x] Servicio de recordatorio con verificación y consumo de créditos.
- [x] JSON exacto `mensaje`, límite de 700 caracteres y respuesta con factura.
- [x] Endpoint DUENO `POST /api/citas/{id}/recordatorio`.
- [x] Pruebas JUnit escritas: cita no elegible no verifica créditos ni llama a IA; mensaje de más de 700 caracteres devuelve 502 tras el consumo indicado por el orden del PDF.
- [ ] Ejecutar JUnit con Maven y MySQL.
- [ ] Comprobar respuesta del simulador oficial y fallback con llaves inválidas.
