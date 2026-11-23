# Fase 9 — Chatbot y solicitudes de contacto

**Estado:** lógica y endpoints implementados; prueba integrada con el simulador oficial pendiente.

## Implementado
- `CatalogoService.obtenerContextoChat` arma el contexto del salón desde sus servicios activos y horario real de base de datos.
- `chatbot.txt` contiene las cinco variables `salon`, `direccion`, `horario`, `catalogo` y `cliente`.
- El historial se convierte a líneas `Cliente:` / `Asistente:` y se envía como mensaje de usuario separado de las instrucciones.
- `ChatbotRequest` valida nombre, contacto y entre 1 y 20 turnos; cada turno admite máximo 500 caracteres.
- `ServicioChatbot` verifica créditos antes de llamar a IA y consume uno después de recibir respuesta.
- El JSON exige las claves `respuesta` y `escalar`.
- Si IA solicita escalar, se crea solicitud `PEDIDO_POR_IA` con los mensajes del cliente, truncados a 2000 caracteres.
- La regla anti-promesas bloquea `descuento`, `cupón`, `cupon`, `gratis`, `promoción`, `promocion` y `reembols` sin distinguir mayúsculas; también intercepta `SILLA50`, el código que el caso de prueba del PDF pide que nunca se devuelva. Devuelve un mensaje fijo seguro y crea solicitud `REGLA_DE_SEGURIDAD`.
- Se creó la entidad `SolicitudContacto`, enums, repositorio, servicio, respuesta y controlador.
- `GET /api/solicitudes` filtra por salón y ordena más recientes primero.
- `PATCH /api/solicitudes/{id}/atendida` bloquea la fila, devuelve 404 si no pertenece al salón y 409 si ya estaba atendida.
- `POST /api/publico/salones/{slug}/chat` es público.

## Verificación
- Se añadieron pruebas de salón inexistente sin consumo de créditos ni llamada a IA, conservación del historial completo en el mensaje del modelo y persistencia exclusiva de los mensajes del cliente al escalar.
- Se añadió un caso explícito para bloquear términos restringidos escritos en mayúsculas.
## Verificación
Hay pruebas JUnit escritas para conversación normal, escalamiento, anti-promesas, ausencia de créditos y estado de solicitudes. El subconjunto modificado compiló sintácticamente con `javac --release 17` y stubs temporales. No se ha probado con el simulador oficial ni con MySQL real.
