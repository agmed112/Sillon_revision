# Auditoría estructural — Sillón, Fases 0 a 3

Fecha: 8 de noviembre de 2026  
Base de revisión: `sillon_fase3_completa_siguiente_fase4.zip` y el PDF oficial del proyecto.

## Alcance y método

Se revisó el contenido del ZIP y se comparó archivo por archivo con el árbol de trabajo. Se inspeccionaron `pom.xml`, `application.properties`, secretos de ejemplo, reglas de Git/Cursor, documentación, paquetes Java, entidades, repositorios, servicios, controladores, seguridad y pruebas existentes. También se contrastaron los requisitos de las Fases 0 a 3 y las reglas transversales del PDF.

Esta es una auditoría estática y estructural. No se ejecutaron Maven, JUnit real, Spring Boot, Hibernate, MySQL, Postman ni solicitudes HTTP, de acuerdo con la decisión de reservar la ejecución integral. Por tanto, ningún paso se declara probado en funcionamiento.

## Integridad del paquete

- El ZIP contiene 94 archivos.
- Los 94 archivos coinciden byte por byte con el árbol de trabajo usado para generarlo.
- La prueba de integridad del ZIP no detectó entradas dañadas.
- `pom.xml` está en la raíz y se puede analizar como XML.
- No se encontró `secrets.properties`, `.env` ni una llave real en el paquete.
- Se incluyen `secrets.example.properties`, `.gitignore` y `.cursorignore`.
- Hay 56 archivos Java de producción y 6 archivos Java de pruebas.

## Resultado por fase

### Fase 0 — Preparar el terreno

**Presente en el árbol:** `pom.xml`, `src`, `application.properties`, `secrets.example.properties`, `.gitignore`, `.cursorignore`, `SPEC.md`, `AGENTS.md`, `BITACORA-IA.md` y documentación de pendientes.

**Revisión:**
- El POM declara Java 17, Spring Boot 4.1.2, JPA, MySQL, PostgreSQL, Validation, Security, RestClient, springdoc, Stripe, JJWT y dependencias de pruebas.
- No aparecen Lombok ni Spring AI ni SDK de IA.
- `application.properties` contiene puerto configurable, MySQL local, `ddl-auto=update`, `open-in-view=false`, importación opcional de secretos, zona `America/Bogota`, créditos, límite GRATIS, variables de secretos, `STRIPE_URL_API` y `sillon.ia.openai.url`.
- Los secretos reales no están incluidos y `secrets.properties` está ignorado por Git y Cursor.
- `pom.xml` está junto a `README.md` en la raíz del paquete.

**No cerrada:** no se ha incorporado el proyecto descargado de Spring Initializr ni su Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn`). No hay clon/fork Git, carpeta `.git`, carpeta `.github` ni historial de commits. La estudiante indicó que todavía no tiene el repositorio oficial/fork. El `AGENTS.md` se declara a sí mismo borrador y falta compararlo con Atiende v4. `SPEC.md` todavía debe ser revisado y expresado con las palabras de la estudiante. Cursor tampoco está preparado.

### Fase 1 — Base SaaS

**Código estructural encontrado:** formato común de error, excepciones propias para 400/401/402/403/404/409/502/503, manejador global de errores y reloj `Clock` con zona configurable; entidad `Salon`, `Plan`, `ConfiguracionSalon` embebida, repositorio, generador de slug, entidad `Usuario` y repositorio; registro y endpoints de mi salón.

**Contraste estático:**
- El salón se construye con plan `GRATIS`, créditos inyectados por configuración y horario 08:00–19:00, domingos cerrados e intervalo de 30 minutos.
- Slug elimina diacríticos, normaliza a minúsculas y agrega sufijos numéricos si ya existe.
- Registro crea salón y dueño dentro de una transacción, normaliza correo y codifica contraseña con `PasswordEncoder`.
- La configuración de salón valida apertura anterior al cierre y el intervalo 15–60 mediante DTO.
- Los errores previstos usan `RespuestaError` con `codigo`, `error`, `mensaje` y `fecha`.

**No probado:** mapeo real de Hibernate, arranque, transacciones, serialización, consultas y respuestas HTTP.

### Fase 2 — Seguridad

**Código estructural encontrado:** `UsuarioPrincipal`, `UsuarioDetailsService`, `ServicioJwt`, `FiltroJwt`, `ConfiguracionSeguridad` y `ManejadorErroresSeguridad`.

**Contraste estático:**
- Principal contiene id, salonId, email, contraseña, rol y estado activo.
- Autoridad usa `ROLE_` más el rol.
- JWT usa email como sujeto e incluye rol y salonId; el parser y emisor comparten el `Clock`.
- El filtro vuelve a cargar el usuario y contrasta las claims con los datos actuales.
- Sesión stateless, CSRF deshabilitado y permisos de rutas implementados según los grupos que corresponden a Fases 1–3.
- Las respuestas 401/403 usan el formato común.

**No probado:** autenticación HTTP real, expiración/firma JWT, BCrypt real, cadena de filtros, permisos en contexto Spring y respuestas 401/403 reales.

### Fase 3 — Catálogo y equipo

**Paso 3.1 Servicios:** entidad `Servicio`, DTOs `record`, mapper, repositorio, servicio y controlador. Las consultas por recurso combinan id y salonId. El DTO valida nombre, descripción, duración 15–480, precio entero positivo COP y `activo=true` cuando no llega.

**Paso 3.2 Estilistas:** relación `ManyToMany` en `Usuario` mediante `estilista_servicios`, método `ofreceServicio`, DTO, mapper, servicio y controlador POST/GET. El orden implementado coincide con la decisión de la estudiante: límite del plan, correo global, pertenencia de servicios. La respuesta no expone contraseña.

**Paso 3.3 Clientes:** entidad con `ManyToOne` a `Salon`, unicidad compuesta salón/teléfono, normalización de dígitos y `+`, método `buscarOCrear`, DTO, mapper y GET de solo lectura. No hay POST independiente.

**No probado:** restricciones e índices reales en Hibernate/MySQL, reglas de límite/correo/servicios en HTTP, transacciones, carga diferida, persistencia y tests JUnit reales.

## Correcciones aplicadas durante esta auditoría

1. `ServicioEstilistas` ya no duplica el límite GRATIS como constante: ahora recibe `sillon.limite-estilistas-gratis` desde `application.properties`, de modo que el parámetro configurado realmente gobierna la regla.
2. Se retiraron máximos de longitud para nombre y correo del estilista que no aparecen en el contrato de creación de estilistas del PDF. También se retiró el máximo de nombre de cliente no especificado en el contrato de agenda. Se conservan las restricciones expresamente indicadas, como password 8–72 y teléfono 7–30 en la futura solicitud de cita.
3. Se completó `SPEC.md` con los formatos exactos de fecha/hora y la tabla de significado de códigos HTTP que aparecen en el contrato.
4. Se quitó de `SPEC.md` la frase que presentaba la actualización atómica de créditos como una regla textual del PDF; es una posible decisión de implementación, no una regla literal del documento.

## Observaciones que deben mantenerse visibles

- El paquete actual tiene código estructural para Fases 1–3, pero no se deben etiquetar como fases cerradas hasta pasar las verificaciones requeridas.
- Los 6 archivos de pruebas existentes cubren principalmente normalización de slug/correo/teléfono, configuración por defecto, relación salón-usuario, método `ofreceServicio` y buscar/crear cliente. Todavía no hay pruebas de servicio/controlador para todos los escenarios del catálogo y equipo.
- La colección Postman no está incluida todavía; corresponde a la evidencia ejecutable descrita al final del PDF.
- La Fase 4 y sus clases (`Cita`, `EstadoCita`, `OrigenCita`, `ReglasAgenda`) no se han empezado. No se han añadido porque requieren primero las decisiones personales que el PDF reserva a la estudiante.
- Los paquetes vacíos `cita`, `publico`, `pago`, `chat` e `ia` son espacios reservados para fases posteriores; no se cuentan como funcionalidades construidas.
- `ManejadorGlobalErrores` cubre las excepciones de negocio y los errores de entrada enumerados; no tiene un manejador genérico para excepciones inesperadas de servidor. Este punto se debe resolver al completar el contrato de errores y la revisión de integración, sin inventar un código de negocio nuevo.

## Nota metodológica importante

La guía del proyecto indica que cada fase debe dejar la aplicación funcionando y terminar con un commit, y que cada regla debe tener al menos una prueba en tests o en la colección. Por eso, reservar la ejecución integral final es compatible con el plan, pero posponer toda comprobación de cada fase hasta el final no reproduce literalmente la metodología del PDF. La revisión estática puede continuar ahora; antes de declarar cada fase verde habrá que realizar las comprobaciones que exige la guía y registrar el commit correspondiente.

## Conclusión

La estructura y el código fuente de Fases 1–3 están presentes y se han revisado estáticamente. La Fase 0 no está cerrada por los elementos de repositorio/Initializr/Wrapper y por la adaptación pendiente de AGENTS. No hay evidencia suficiente para declarar el proyecto listo para Fase 4 ni para entrega. Esta auditoría no sustituye la compilación, los tests reales ni las verificaciones de base de datos y API.
