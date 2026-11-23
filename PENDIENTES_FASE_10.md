# Fase 10 — Tests automáticos: cobertura y pendientes

## Casos mínimos del PDF con pruebas escritas
- [x] Estados de cita: pruebas parametrizadas de transiciones permitidas y bloqueadas.
- [x] Reglas de agenda: apertura/cierre, domingo, cruces, estados que ocupan agenda, horas pasadas, día cerrado y duración superior a jornada.
- [x] Agendar/cambiar estado: cita válida, precio guardado, pasado, cruce, estilista sin servicio, cita ajena (404), cancelación por estilista (403), finalización y transición inválida.
- [x] Plan: tercer estilista en GRATIS (402), creación con PRO, orden de validaciones y servicios ajenos.
- [x] Webhook: PRO, paquete, evento repetido, cancelación, firma/cuerpo inválidos, metadata inválida, pago no confirmado, salón inexistente y deserialización de respaldo.
- [x] Recordatorio: respuesta válida, factura/créditos, JSON sin clave `mensaje` (502), cita no elegible sin consumo, proveedor no disponible sin consumo y límite de 700 caracteres.
- [x] Chatbot: sin créditos no llama a IA, pregunta normal, escalamiento, anti-promesas, solicitud de seguridad, salón inexistente sin IA, historial de varios turnos, términos restringidos en mayúsculas, proveedor no disponible sin consumo y JSON inválido después de consumir.
- [x] Motor IA: fallback, 503 cuando ningún proveedor responde, respuesta no utilizable, lector JSON (incluye nulo/blanco), consumo atómico, biblioteca de prompts y cálculo de factura con tarifas configurables.
- [x] Solicitudes: estado inicial PENDIENTE, límite de mensajes y doble atención (409).
- [x] Aplicación: prueba `@SpringBootTest` del contexto completo.

## Pendiente de ejecución, no de escritura
- [ ] Ejecutar `./mvnw test` (o `mvnw.cmd test`) con el Maven Wrapper oficial.
- [ ] Encender MySQL antes de ejecutar `ContextoAplicacionTest`.
- [ ] Obtener `BUILD SUCCESS` con dependencias Maven reales.
- [ ] Ejecutar la colección Postman contra la API local y el simulador oficial.

## Inventario
Inventario actual: 25 archivos `*Test.java`, 118 métodos `@Test` y 2 métodos `@ParameterizedTest` con 11 filas de datos (129 invocaciones previstas). Las anotaciones son pruebas escritas; no se consideran aprobadas hasta que JUnit las ejecute.

## Límite de verificación
En este entorno no hay `mvn`, Maven Wrapper ni dependencias Maven descargadas. Los subconjuntos modificados compilaron sintácticamente con `javac --release 17` usando stubs temporales; eso no equivale a `BUILD SUCCESS`.
