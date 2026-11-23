# Sillón — estado verificable de esta copia

## Código y funcionalidades presentes
- Fases 0–3: estructura SaaS, registro/login, JWT, roles, salón, servicios, estilistas y clientes.
- Fase 4: agenda, estados, reglas de horario, agendamiento, listado y cambios de estado.
- Fase 5: catálogo público, disponibilidad y reservas en línea.
- Fase 6: Checkout PRO/paquete, webhook firmado, idempotencia, bloqueo de saldo/plan y pruebas escritas.
- Fase 7: paquete IA compatible con los componentes descritos en el PDF, fallback Gemini/Claude, prompts, lector JSON Jackson 3, factura y créditos atómicos. Se corrigieron los imports que no correspondían a Spring Boot 4 y la detección de llaves placeholder. No se pudo reutilizar literalmente Atiende v4 porque ese material no está disponible.
- Fase 8: recordatorio con datos verificados, prompt y consumo de crédito después de respuesta IA.
- Fase 9: chatbot con contexto real, historial separado, anti-promesas y solicitudes de contacto.
- Fase 10: pruebas escritas para agenda, pagos, IA, créditos, recordatorio, chatbot, solicitudes y contexto Spring; ejecución Maven/JUnit real pendiente.
- Fase 11: Swagger con tags 0–9 y operaciones de recordatorio, chat y solicitudes; prueba visual pendiente.
- Fase 12: Dockerfile y configuración PostgreSQL/Render; despliegue externo pendiente.
- Fase 13: colección Postman con 52 escenarios, siete carpetas y dos entornos; ejecución, URL real y PR oficial pendientes.

## Refuerzos lógicos conservados
- Bloqueo pesimista de salón para límite GRATIS y webhooks Stripe.
- Bloqueo pesimista de estilista para reservas concurrentes.
- Bloqueo pesimista de cita para transiciones concurrentes.
- Descuento de créditos IA mediante actualización SQL condicional.
- Conflictos de integridad convertidos a 409 sin exponer SQL.
- Corrección de disponibilidad para servicios más largos que la jornada.

## Correcciones del 19 de noviembre de 2026
- Se corrigieron los imports de Jackson 2 que quedaban en los proveedores y lector IA; ahora usan `tools.jackson.*` según la página 15 del PDF.
- Gemini y Claude no intentan conexiones HTTP cuando las llaves son valores `placeholder`.
- Se agregó `maven-compiler-plugin` con `<parameters>true</parameters>` para conservar los nombres de parámetros que consume Springdoc.
- Se agregaron cuatro casos de prueba de configuración de proveedores (dos por cada proveedor).
- Se añadió guardia para contenido IA nulo/blanco, prueba de costo estimado con tarifas configurables, casos de cita no elegible y límite de 700 caracteres del recordatorio, historial multi-turno, salón inexistente y términos anti-promesas en mayúsculas.

## Segunda revisión incremental — 19 de noviembre de 2026
- `LectorJsonIa` rechaza contenido nulo o en blanco como error de pasarela 502.
- Se ampliaron pruebas para el cálculo de factura IA, proveedor no disponible sin consumo de crédito, recordatorio de cita no elegible, límite de 700 caracteres, salón inexistente, historial multi-turno, términos restringidos en mayúsculas, JSON de chatbot inválido y variables de prompt sin resolver.
- La colección Postman se verificó contra los nombres y orden del PDF: 52 solicitudes, 7 carpetas, prueba de código HTTP en cada petición, 53 scripts que pasan `node --check` y dos entornos con una única variable `baseUrl`.
- Los seis archivos Java modificados se analizaron con `javac` en modo sintáctico sin diagnósticos de sintaxis; no equivale a compilación completa ni a ejecución de pruebas.
- Informe detallado: `REVISION_INCREMENTAL_2026-11-19_2.md`.

## Inventario de pruebas escritas
- 25 archivos `*Test.java`.
- 118 métodos `@Test` y 2 métodos `@ParameterizedTest` con 11 filas: 129 invocaciones previstas.
- No se consideran pruebas aprobadas hasta ejecutarlas con JUnit real.

## Verificación realizada en esta intervención
- `javac --release 17` compiló sintácticamente los nuevos componentes de Fases 7–9, controladores de Fase 11 y pruebas nuevas usando stubs temporales de Spring/Jackson/Mockito.
- Los JSON de colección y entornos Postman se parsearon correctamente.
- La colección tiene 7 carpetas, 52 peticiones y prueba Post-response en cada una.
- Los entornos contienen exactamente una variable `baseUrl` cada uno.
- No se ejecutó Maven/JUnit real, no se conectó MySQL, no se llamó a Gemini/Claude simulados o reales, y no se ejecutó la colección Postman contra la API.

## Bloqueos externos
- No se proporcionó Atiende v4 para reutilización literal.
- No se proporcionó el fork/repositorio oficial; faltan `.github` original y Maven Wrapper. No se recrearon ni modificaron.
- No se proporcionaron cuentas/credenciales de Supabase, Render o Stripe; no hay URL de producción.
- No se puede abrir un Pull Request ni obtener el resultado del calificador oficial sin el fork de GitHub.

## Veredicto
La cobertura de código de las fases 7–13 aumentó de forma sustancial. La entrega todavía no está cerrada: faltan pruebas reales, acciones de cuenta, despliegue, URL de Render, ejecución Postman y Pull Request/calificador.
