# Fase 9 — pendientes de cierre

- [x] Contexto de salón, dirección, horario y catálogo activo desde base de datos.
- [x] Prompt `chatbot.txt` con cinco variables y reglas del PDF.
- [x] Historial en mensaje separado de las instrucciones.
- [x] Validación de 1–20 turnos y 500 caracteres por turno.
- [x] JSON exacto `respuesta` y `escalar`.
- [x] Escalamiento `PEDIDO_POR_IA` y persistencia de mensajes de cliente (máximo 2000 caracteres).
- [x] Regla anti-promesas en Java con respuesta fija y motivo `REGLA_DE_SEGURIDAD`; también bloquea el código `SILLA50` que el caso del PDF exige que nunca aparezca en la respuesta.
- [x] Entidad, repositorio, servicio y controlador de solicitudes.
- [x] Listado por salón y más recientes primero; doble atención produce 409.
- [x] Endpoints públicos/protegidos con roles adecuados.
- [x] Pruebas JUnit escritas para salón inexistente sin llamada a IA, historial de varios turnos, mensajes de cliente al escalar y términos restringidos en mayúsculas.
- [ ] Ejecutar pruebas con Maven, MySQL y simulador IA oficial.
