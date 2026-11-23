# Auditoría lógica y estructural transversal — Sillón

**Fecha:** 8 de noviembre de 2026  
**Base revisada:** `Sillon_avance_Fase6_Fase11_Fase12.zip`  
**Alcance:** estructura, relaciones entre clases, consultas, transacciones, seguridad, reglas de agenda, pagos, pruebas escritas y artefactos de entrega. Se utilizó únicamente el contenido visualmente legible del PDF; la auditoría visual previa no identificó instrucciones blancas ocultas sobre fondo blanco.

## 1. Inventario observado

- 87 clases Java de producción.
- 14 archivos `*Test.java`.
- 70 métodos anotados con `@Test`.
- 2 métodos `@ParameterizedTest` con 11 filas de datos.
- 81 invocaciones de prueba previstas si JUnit ejecuta todos los métodos y filas.
- 9 controladores etiquetados en OpenAPI.
- 21 operaciones HTTP actualmente mapeadas.
- `pom.xml` válido como XML.
- No se encontró `secrets.properties` real en la copia.

Los números anteriores describen archivos y pruebas escritos; no equivalen a una compilación completa ni a pruebas aprobadas.

## 2. Correcciones lógicas incorporadas

### 2.1 Reservas concurrentes y cruces de agenda
**Archivos:** `UsuarioRepository`, `ServicioCitas`.

- Se agregó `findByIdAndSalonIdAndRolForUpdate`, con `PESSIMISTIC_WRITE`, para bloquear al estilista durante la validación y creación de una cita.
- `agendarEnSalon` y `reservarEnLinea` utilizan `READ_COMMITTED`.
- El bloqueo serializa las reservas del mismo estilista; con `READ_COMMITTED`, la consulta de cruces posterior al bloqueo ve la cita que confirmó la transacción anterior.
- La validación de cruce conserva exactamente la regla del PDF: solo `PENDIENTE` y `CONFIRMADA` ocupan agenda; los intervalos contiguos no se cruzan.

### 2.2 Transiciones concurrentes de una cita
**Archivos:** `CitaRepository`, `ServicioCitas`.

- Se agregó `findByIdAndSalonIdForUpdate`, que bloquea la cita por su ID y el salón al que pertenece.
- `cambiarEstado` utiliza `READ_COMMITTED` y lee la cita con bloqueo antes de aplicar la máquina de estados.
- Si dos peticiones intentan cambiar el mismo estado simultáneamente, la segunda evalúa la transición sobre el estado confirmado por la primera y puede recibir 409 en vez de sobrescribirla.

### 2.3 Límite de estilistas del plan GRATIS
**Archivos:** `SalonRepository`, `ServicioEstilistas`.

- Se agregó `findByIdForUpdate` con `PESSIMISTIC_WRITE`.
- `ServicioEstilistas.crear` bloquea la fila del salón y usa `READ_COMMITTED` antes de contar estilistas.
- Esto evita que dos altas simultáneas lean el mismo conteo y creen ambas el tercer estilista GRATIS.
- Se conservó el orden decidido por la estudiante: límite del plan (402), correo (409), pertenencia de servicios (404).

### 2.4 Actualizaciones concurrentes de créditos y suscripción
**Archivos:** `SalonRepository`, `ServicioWebhookStripe`, `Salon`.

- Los webhooks de compra bloquean la fila del salón antes de incrementar créditos o activar PRO.
- Las cancelaciones bloquean el salón localizado por el ID de suscripción.
- `stripe_suscripcion_id` quedó con restricción única para impedir que una suscripción quede asociada a dos salones.
- Se mantiene la idempotencia por clave primaria del evento Stripe y la transacción que incluye registro del evento y modificación del salón.

### 2.5 Disponibilidad para servicios más largos que la jornada
**Archivo:** `ReglasAgenda`.

- Se calcula la duración de la jornada con `Duration`.
- Si el servicio dura más que el horario de apertura a cierre, la disponibilidad devuelve una lista vacía.
- Esto evita que `LocalTime.minusMinutes` envuelva la hora y produzca inicios inválidos.

### 2.6 Conflictos de integridad de datos
**Archivo:** `ManejadorGlobalErrores`.

- `DataIntegrityViolationException` se traduce a HTTP 409 con un mensaje genérico.
- No se filtran SQL, nombres internos de restricciones ni detalles de la base de datos.

### 2.7 Normalización de correo
**Archivos:** `RegistroRequest`, `LoginRequest`, `ServicioAutenticacion`, `ServicioEstilistas`, `Usuario`.

- Se recortan espacios externos y se pasa el correo a minúsculas.
- No se eliminan espacios internos: un correo mal formado no se transforma silenciosamente en otra dirección; la validación `@Email` puede rechazarlo.
- Se actualizó `UsuarioTest` para cubrir ambos comportamientos.

## 3. Pruebas escritas ampliadas

- `ReglasAgendaTest`: caso de duración mayor que la jornada.
- `ServicioCitasTest`: cita pasada, estilista sin servicio, reserva pública `PENDIENTE/EN_LINEA`, cita ajena (404), intento de cancelación por estilista (403), finalización permitida y transición inválida (409), además de los casos válidos y de cruce.
- `ServicioEstilistasTest`: límite GRATIS primero, creación con PRO, correo duplicado antes de consultar servicios y servicio ajeno al salón.
- `ServicioWebhookStripeTest`: comprobación de acceso con bloqueo al modificar plan y créditos.
- Se conservó la suite de pagos existente.

Estas pruebas están escritas. No se afirma que JUnit haya pasado.

## 4. Verificaciones técnicas que sí se ejecutaron

1. `pom.xml` se pudo analizar como XML.
2. Los archivos modificados de entidades, repositorios, servicios de agenda/estilistas/pagos y las pruebas asociadas compilaron sintácticamente con `javac --release 17`, usando stubs temporales de APIs externas.
3. Los DTOs `RegistroRequest` y `LoginRequest` compilaron con `javac --release 17` y stubs de validación.
4. Se ejecutó un smoke test aislado de `ReglasAgenda`: duración mayor que jornada, horario normal y límite entre intervalos. Resultado: `PASS`.
5. El inventario de rutas se generó directamente de los controladores; el código actual tiene 21 mappings HTTP.
6. Se revisaron los patrones de búsqueda derivados del repositorio frente a los atributos de las entidades.
7. El escaneo de secretos no encontró claves `sk_live`, `rk_live`, llaves privadas ni archivos `secrets.properties`. Las coincidencias `whsec_` son únicamente placeholders de ejemplo/prueba.

**Límite:** los stubs no son las dependencias reales de Maven. No validan la compatibilidad binaria, el arranque de Spring, el mapeo real de Hibernate ni el comportamiento de bloqueo de MySQL/PostgreSQL.

## 5. Hallazgos pendientes que no deben ocultarse

### Requisitos bloqueados por materiales originales
- No está el ZIP/enlace del repositorio oficial de Sillón; por eso faltan `.github`, `.mvn`, `mvnw` y `mvnw.cmd`. No deben recrearse ni sustituirse.
- No está el proyecto Atiende v4; por eso no se puede reutilizar fielmente el paquete `ia` ni adaptar el `AGENTS.md` versión 4.
- La generación inicial en Spring Initializr es una actividad `SIN IA` del taller y requiere evidencia/acción de la estudiante.

### Fases todavía incompletas
- Fase 7: paquete IA reutilizado, proveedores, fallback, configuración, prompts, lector JSON, factura y consumo atómico de créditos.
- Fase 8: datos verificados de recordatorio, prompt `recordatorio.txt` y servicio de borrador.
- Fase 9: catálogo como contexto, prompt `chatbot.txt`, anti-promesas, solicitudes y escalamiento.
- Fase 10: pruebas de IA, recordatorio, chatbot, prueba de contexto MySQL y ejecución JUnit real.
- Fase 11: falta tag 8 Solicitudes y cuatro operaciones del contrato: recordatorio, chat público, listar solicitudes y marcar solicitud atendida.
- Fase 12: no se creó Supabase, no se desplegó Render y no se probó el webhook de producción.
- Fase 13: falta colección Postman, dos entornos, 7 carpetas, 52 escenarios mínimos, README final con URL real y Pull Request oficial.

### Diferencia de conteo en el PDF
La tabla de contrato de la sección 4 parece enumerar 25 combinaciones método/ruta, mientras que la comprobación de la Fase 11 dice que Swagger debe mostrar 22 rutas. El código actual tiene 21. No se ocultará ni eliminará una ruta del contrato para forzar el número; se debe contrastar la discrepancia con el calificador oficial.

## 6. Veredicto de esta revisión

**Mejora estructural real, pero no cierre del proyecto.**

Las correcciones reducen riesgos concretos de concurrencia, disponibilidad y consistencia. La estructura por dominios sigue siendo aprovechable y no se modificaron las reglas de negocio del taller.

No se declara `BUILD SUCCESS`, no se declaran tests aprobados y no se asigna una nueva nota global en esta revisión. La calificación debe recalcularse después de completar la implementación y ejecutar el calificador oficial.

## 7. Siguiente trabajo

1. Obtener del repositorio oficial el Wrapper, `.mvn` y `.github` intactos.
2. Obtener Atiende v4 para reutilizar el paquete IA exigido.
3. Completar Fases 7–9 sin inventar prompts ni reglas.
4. Completar pruebas y ejecutar Maven/JUnit.
5. Terminar Swagger, despliegue y Postman con evidencias reales.

# Addendum — implementación de Fases 7–13 (9 de noviembre de 2026)

Este addendum actualiza el estado de las fases posteriores descrito en la auditoría original del 8 de noviembre.

## Implementación incorporada
- Fase 7: paquete `ia` compatible con el contrato visible del PDF, proveedor Gemini por protocolo OpenAI, proveedor Claude de respaldo, fallback, configuración tipada, `RestClient` con timeout, biblioteca de prompts, lector JSON, factura y descuento atómico de créditos.
- Fase 8: método de lectura de datos verificados, formato español de Colombia, prompt `recordatorio.txt`, servicio de recordatorio, validación JSON/máximo 700 caracteres y endpoint protegido.
- Fase 9: contexto de catálogo real, prompt `chatbot.txt`, historial enviado separado de instrucciones, escalamiento, anti-promesas en Java, entidad/servicio/repositorio/controlador de solicitudes y endpoint público de chat.
- Fase 10: se añadieron pruebas de fallback, lector JSON, créditos, recordatorios, chatbot, solicitudes y contexto Spring.
- Fase 11: se añadieron los cuatro mappings faltantes y el tag 8 Solicitudes; el código contiene 25 operaciones método/ruta según el conteo estático.
- Fase 13: colección Postman de 52 escenarios, siete carpetas y dos entornos con una variable `baseUrl` cada uno; scripts verificados sintácticamente.
- Se alineó la colección con los datos de prueba del anexo del PDF: Barbería La Navaja, Camilo Ríos, Salón Aurora/Diana Torres, servicios con valores del taller, Mateo/Sara con sus contraseñas indicadas, clientes Valentina/Andrés y chatbot Juliana/Pedro. El historial de Juliana se conserva entre las peticiones 41 y 42.
- Se reforzó el formato del recordatorio para incluir dirección y ciudad, se confirmó la sustitución del espacio invisible de `a. m.` y el catálogo del chatbot muestra precios con formato colombiano. La regla anti-promesas también bloquea el código SILLA50 que aparece en el caso de prueba del PDF.

## Limitaciones que siguen vigentes
- El proyecto Atiende v4 no está disponible: el paquete IA fue reconstruido según la descripción del PDF, no reutilizado literalmente.
- No hay Maven ni Maven Wrapper en el entorno. `javac` con stubs solo comprueba sintaxis parcial; no es `BUILD SUCCESS`.
- `ContextoAplicacionTest` requiere MySQL y no se ejecutó.
- No se ejecutaron Stripe CLI, simuladores IA/Stripe, colección Postman contra el backend, Swagger en navegador ni Docker build.
- No se crearon Supabase, Render, webhook de producción ni Pull Request; requieren las cuentas y el fork oficial.
- La URL de Render sigue sin asignarse y no se inventa.

## Inventario actualizado de pruebas escritas
25 archivos `*Test.java`, 106 métodos `@Test` y 2 métodos `@ParameterizedTest` con 11 filas: 117 invocaciones previstas. Ninguna se declara aprobada hasta ejecutar JUnit real.

## Verificación de artefactos Postman
El JSON de colección y los dos entornos se parsearon. Se verificó que hay 52 peticiones, las carpetas tienen 9/5/20/4/8/4/2 elementos, todas las peticiones tienen prueba Post-response y los 53 scripts de pre-request/test pasaron `node --check`.
