# Verificación técnica de este avance

Fecha de revisión: 2026-11-08.

## Comprobaciones realizadas

1. `pom.xml`: el documento XML se pudo analizar sin errores.
2. Configuración de dependencias: Spring Boot 4.1.2, springdoc 3.2.0, Stripe Java 34.1.0 y JJWT 0.13.0 fueron contrastados con las publicaciones/documentación actuales. La versión Boot 4.1.2 pertenece a la línea estable 4.1.x permitida por el PDF; springdoc 3.2.x es compatible con Boot 4.1.x.
3. Java: todo el código fuente y los archivos de prueba actuales compilaron sintácticamente con `javac --release 17` usando stubs temporales de las APIs JPA/Spring/JUnit/Mockito. No quedaron advertencias del código del proyecto.
4. Prueba manual aislada: 13 comprobaciones pasaron para normalización de slug, eliminación de signos, sufijos por colisión, rechazo de nombre que no produce slug, horario predeterminado, plan, créditos, relación Salon-Usuario y normalización del correo.
5. Seguridad de archivos: no existe `secrets.properties` real en el paquete; `.gitignore` excluye ese archivo.
6. Se revisó que los valores de `Plan` y `Rol` coincidan con el documento y que las entidades guarden los enums como texto.

## Límites de esta verificación

- No hay Maven instalado en el entorno de generación y no se pudo acceder a Maven Central desde la terminal.
- Los stubs temporales solo permiten revisar sintaxis y comportamiento aislado; no validan el mapeo real de Hibernate ni la compatibilidad binaria de las dependencias.
- No se ejecutaron los tests JUnit/Mockito con sus librerías reales.
- No se conectó MySQL ni se inició Spring Boot.

## Verificación que debe hacerse en el computador de desarrollo

Cuando el proyecto generado con Spring Initializr y su Maven Wrapper se integren en la raíz del fork:

- Windows: `mvnw.cmd test`
- macOS/Linux: `./mvnw test`

Después, configurar MySQL local y arrancar la aplicación. No marcar la Fase 0 ni la Fase 1 como terminadas hasta que esas comprobaciones reales pasen y la estudiante revise el diff.


## Fase 2 — revisión estática adicional

- El código de producción de las fases 1 y 2 se compiló con `javac --release 17` usando stubs temporales para las APIs de Spring, Jakarta, JPA, Jackson 3 y JJWT. El compilador no reportó errores en el código del proyecto; los avisos que aparecen corresponden a clases de excepción de los stubs temporales.
- Se revisó el orden de los matchers de seguridad y se contrastó cada grupo de rutas con la matriz de roles de la página 6 del PDF.
- Se verificó que el ejemplo Base64 del JWT decodifica a 32 bytes.
- Se revisó la implementación de firma/verificación JWT contra la API de JJWT 0.13.0 y la referencia de Spring Security.
- Se revisó que Spring Boot 4 use Jackson 3 (`tools.jackson.*`), de acuerdo con la guía de migración.
- La documentación oficial de springdoc indica que 3.2.x es compatible con Spring Boot 4.1.x; la versión 3.2.0 fue publicada para la línea Boot 4.1.2.

### Límites que siguen vigentes

- No se ejecutó Maven: el entorno no tiene `mvn` ni Maven Wrapper, y no pudo resolver DNS para Maven Central.
- No se ejecutaron pruebas JUnit/Mockito con dependencias reales ni pruebas de contexto Spring.
- No se probó firma JWT real, BCrypt real, autorización HTTP, serialización Jackson 3, MySQL ni Postman.
- Por tanto, las fases 0, 1 y 2 siguen **sin cierre técnico** hasta que se ejecute el Maven Wrapper generado por Spring Initializr en el equipo de desarrollo.
- Se compiló con `javac --release 17` el conjunto de 42 archivos Java de producción y pruebas (incluidos los tests existentes de Fase 1) contra stubs temporales. No hubo errores ni advertencias originadas en archivos del proyecto; las advertencias fueron de clases de excepción de los stubs.
- Se ejecutó una comprobación aislada adicional de siete comportamientos: normalización de correo en registro y login, horario por defecto, plan GRATIS y 10 créditos, autoridad `ROLE_DUENO`, estado activo del principal y códigos 400/401/403/409. Las siete pasaron. No sustituye JUnit ni una ejecución Spring real.


## Aclaración de reproducibilidad de la revisión del 8 de noviembre de 2026

El registro anterior menciona una compilación sintáctica de 42 archivos Java contra stubs temporales. Esos stubs y el comando exacto no están incluidos en el ZIP, por lo que esa comprobación histórica no se puede reproducir desde este paquete. En esta retroalimentación se volvió a inspeccionar el código y se ejecutó una prueba aislada del generador de slug con `javac --release 17` y stubs mínimos creados para esa prueba. Esto no equivale a compilar toda la aplicación ni a ejecutar JUnit.

La única corrección de prueba identificada y aplicada en esta revisión fue la expectativa de `GeneradorSlugTest`: ahora espera `DatosInvalidosException`, que es el tipo que lanza la implementación para HTTP 400. El test JUnit corregido aún debe ejecutarse con Maven Wrapper.


## Segunda revisión (8 de noviembre de 2026)
- Ajuste preventivo: el parser de JJWT ahora valida `exp` usando el mismo `Clock` inyectado que genera el token.
- Ajuste preventivo: `FiltroJwt` sigue integrado en `SecurityFilterChain`, pero se deshabilita su registro automático como filtro servlet mediante `FilterRegistrationBean`.
- Se verificaron estáticamente ambos cambios y la coherencia de configuración.
- La compilación completa y el arranque aún no se han podido ejecutar: el paquete sigue sin `mvnw`/`.mvn` y el entorno no tiene Maven ni acceso de red a Maven Central.


## Fase 3 — revisión estructural de pasos 3.1 y 3.2

- Se compiló sintácticamente con `javac --release 17` y stubs temporales el conjunto de clases de producción afectadas de los pasos 3.1 y 3.2. El compilador no reportó errores en esos archivos.
- También se compiló sintácticamente `UsuarioTest` con stubs temporales de JUnit y Mockito; esto no ejecuta el test.
- Se verificó que `Usuario` declara `@ManyToMany` mediante la tabla `estilista_servicios` y expone `ofreceServicio(Long servicioId)`.
- Se verificó estáticamente el orden del alta de estilistas: límite GRATIS (402), correo único (409), pertenencia de servicios al salón (404).
- Se verificó el máximo de dos estilistas para GRATIS, la ausencia de límite para PRO, la codificación con `PasswordEncoder`, la respuesta sin contraseña y las rutas POST/GET `/api/estilistas`.
- Se corrigió el precio de servicio: el DTO usa `BigDecimal` solo para validar `@Digits(fraction = 0)` y el máximo de `Long`; el mapper convierte con `longValueExact()` y la entidad/respuesta usan `Long`.
- Se validó el XML de `pom.xml` y la integridad del ZIP.

**Límite:** no se ejecutaron Maven, JUnit real, Hibernate, MySQL, Spring Boot ni solicitudes HTTP. Estas verificaciones siguen pendientes para la etapa de ejecución acordada.

## Fase 3.3 — clientes

- Se compiló sintácticamente con `javac --release 17` el código de producción de los pasos 3.1, 3.2 y 3.3 usando stubs temporales de APIs externas; no se reportaron errores en las clases del proyecto.
- Se compiló sintácticamente el código de los tests nuevos de clientes con stubs temporales de JUnit y Mockito; esto no equivale a ejecutar los tests.
- Se ejecutó de forma aislada el método de normalización: `+57 (300) 111-22-33` produjo `+573001112233`.
- Se verificó estáticamente la unicidad compuesta `salon_id + telefono`, el filtro de consultas por `salonId`, el uso de la identidad del usuario autenticado en GET `/api/clientes` y la ausencia de un endpoint POST.
- Maven, JUnit con dependencias reales, Hibernate, MySQL y pruebas HTTP siguen pendientes para el cierre final. No se ha realizado todavía la auditoría integral de carpetas y archivos.


## Auditoría estructural de Fases 0–3 (8 de noviembre de 2026)

La auditoría completa está en `AUDITORIA_ESTRUCTURAL_FASES_0_3.md`. Durante ella se corrigió la lectura del límite GRATIS para utilizar `sillon.limite-estilistas-gratis`, se eliminaron máximos de longitud no estipulados para nombre/correo de estilista y nombre de cliente, y se completó SPEC con formatos de fecha/hora y códigos HTTP.

El ZIP y el árbol de trabajo coincidían byte por byte (94 archivos en la versión revisada antes de incorporar este informe); `pom.xml` se analizó como XML y el ZIP pasó su comprobación de integridad. No se ejecutaron Maven, JUnit real, Spring Boot, Hibernate, MySQL ni Postman.


## Fase 6 — Stripe (revisión estática)

- Se incorporaron `PropiedadesStripe`, `ConfiguracionStripe`, `ServicioPagos`, `ServicioWebhookStripe`, `EventoStripe`, su repositorio y el controlador con las cuatro rutas del contrato.
- Se contrastaron los métodos usados de stripe-java 34.1.0 con la documentación Java de Stripe: `StripeClient.builder()`, `setApiKey`, `setApiBase`, Checkout Sessions, `SessionCreateParams` y `EventDataObjectDeserializer.deserializeUnsafe()`.
- Se verificó estáticamente que la sesión selecciona precio y modo en el servidor, añade metadata al Checkout y a la suscripción PRO, y que el salón PRO se rechaza antes de llamar a Stripe.
- El webhook valida `Stripe-Signature`, guarda el id del evento en una tabla con restricción única y aplica plan/créditos dentro de una transacción. El evento duplicado no vuelve a aplicar los cambios.
- Se añadieron cinco pruebas unitarias para el procesamiento de eventos. No se ejecutaron: no hay Maven ni Maven Wrapper en esta copia.
- No se ejecutó Stripe CLI, no se crearon productos ni precios de prueba, no se probó el simulador del calificador y no se verificó el webhook en producción. Por ello Fase 6 permanece **incorporada pero no cerrada**.


## Auditoría lógica transversal — 8 de noviembre de 2026

- Se reforzó la concurrencia del límite GRATIS con bloqueo pesimista de la fila del salón y `READ_COMMITTED`.
- Se reforzó la agenda con bloqueo pesimista de la fila del estilista y `READ_COMMITTED`, para que una reserva concurrente espere y vuelva a comprobar los cruces.
- Los cambios de estado de citas bloquean la fila de la cita por id/salón y usan `READ_COMMITTED` para evaluar las transiciones contra el estado vigente.
- Los webhooks de Stripe bloquean la fila del salón al incrementar créditos/cambiar plan; el ID de suscripción es único.
- `ReglasAgenda` evita el desbordamiento de `LocalTime` cuando la duración supera la jornada.
- `ManejadorGlobalErrores` traduce conflictos de integridad de datos a 409 sin exponer detalles SQL.
- Se ajustó la normalización del correo para recortar solo espacios externos; los espacios internos no se eliminan antes de `@Email`.
- Se ampliaron pruebas de agenda, permisos de citas, orden de validaciones de estilistas y bloqueos de webhooks.
- Los subconjuntos modificados compilaron con `javac --release 17` contra stubs temporales; los DTOs de registro/login también compilaron. Se ejecutó un smoke test aislado de agenda con resultado PASS.
- Hay 14 archivos `*Test.java`, 70 métodos `@Test` y 2 métodos parametrizados con 11 filas (81 invocaciones previstas). Ninguna de estas cifras significa que JUnit haya pasado.

### Límites vigentes
- No se ejecutó Maven ni JUnit real: falta el Maven Wrapper original y el entorno no tiene `mvn`.
- No se probó el bloqueo pesimista en MySQL/PostgreSQL real.
- No se levantó Spring Boot ni se ejecutó Postman/Stripe CLI/Render.
- La compilación con stubs es únicamente una comprobación sintáctica parcial, no un BUILD SUCCESS.

## Revisión de Fases 7–13 — 9 de noviembre de 2026

- Los nuevos componentes Java de Fases 7–9, controladores de Fase 11 y pruebas nuevas compilaron sintácticamente con `javac --release 17` usando stubs temporales de Spring, Jackson, JUnit y Mockito.
- Los archivos Postman se validaron como JSON; la colección contiene siete carpetas y 52 peticiones. Los dos entornos tienen una sola variable `baseUrl`. Los scripts JavaScript de pre-request y test pasaron `node --check`.
- El entorno no tiene Maven (`mvn: command not found`) y no se pudo resolver `repo.maven.apache.org`; por eso no se descargaron dependencias ni se ejecutó Maven/JUnit.
- No se levantó Spring Boot, MySQL, Stripe CLI, simuladores IA/Stripe ni Postman Runner. No se construyó Docker ni se desplegó en Render.
- La implementación de IA se reconstruyó desde los componentes descritos en el PDF porque Atiende v4 no estaba disponible; no se afirma reutilización literal.
