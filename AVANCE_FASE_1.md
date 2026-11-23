# Avance de la Fase 1 — Sillón

Este registro distingue código incorporado de código cuya ejecución todavía no ha podido comprobarse.

## Pasos incorporados

### Paso 1.1 — Errores
- `RespuestaError` con los cuatro campos exigidos: `codigo`, `error`, `mensaje` y `fecha`.
- Excepciones propias para 400, 401, 402, 403, 404, 409, 502 y 503.
- `ManejadorGlobalErrores` para excepciones propias, validaciones, JSON mal formado, parámetros faltantes y parámetros con tipo inválido.

### Paso 1.2 — Reloj
- Bean `Clock` con zona horaria configurable desde `sillon.zona-horaria`.
- La propiedad está establecida en `America/Bogota`.

### Paso 1.3 — Salón, plan y configuración
- Enum `Plan` con `GRATIS` y `PRO`.
- Entidad `Salon` con los atributos definidos en el modelo de datos.
- `ConfiguracionSalon` como `@Embeddable` y método `porDefecto()` (08:00, 19:00, domingos cerrados, intervalo de 30 minutos).
- `SalonRepository` con búsqueda por slug, existencia por slug y búsqueda por id de suscripción de Stripe.

### Paso 1.4 — Slug
- `GeneradorSlug` elimina marcas diacríticas, normaliza a minúsculas, convierte secuencias no alfanuméricas en guiones, recorta guiones externos y agrega sufijos `-2`, `-3`, etc. cuando el slug ya existe.

### Paso 1.5 — Usuario
- Enum `Rol` con `DUENO`, `ESTILISTA` y `ADMIN`.
- Entidad `Usuario` con los campos del modelo de datos y relación `ManyToOne` obligatoria hacia `Salon`.
- No se agregó todavía la relación con `Servicio`: el PDF la reserva para la Fase 3.
- Las relaciones inversas de `Salon` con `Servicio`, `Cliente`, `Cita` y `SolicitudContacto` se incorporarán al crear esas entidades en sus fases; no se añadieron entidades vacías para adelantar trabajo.
- El correo se normaliza a minúsculas y sin espacios.

## Pruebas incorporadas
- Configuración predeterminada del salón.
- Normalización de slug, signos, colisiones y nombre sin slug válido.
- Plan y créditos iniciales recibidos por el constructor.
- Relación bidireccional en memoria entre salón y usuario.
- Normalización del correo.

## Verificación ejecutada
- Se inspeccionaron los nombres de campos, relaciones y enumeraciones contra las páginas 5–6 y los pasos 1.3–1.5 de las páginas 16–17 del PDF.
- Se revisaron versiones de Spring Boot, springdoc, Stripe Java y JJWT contra sus publicaciones/documentación actuales. El POM quedó en Spring Boot 4.1.2, springdoc 3.2.0, Stripe Java 34.1.0 y JJWT 0.13.0.
- El XML de `pom.xml` se analizó correctamente.
- Las clases nuevas y los archivos de pruebas se compilaron con `javac --release 17` contra stubs temporales de las APIs JPA/Spring/JUnit/Mockito; no hubo advertencias ni errores.
- Se ejecutó una prueba manual aislada de las clases nuevas: 13 comprobaciones de slug, sufijos, valores por defecto, plan, créditos, relación y correo pasaron.
- **No se ejecutó Maven:** este entorno no tiene Maven instalado y no tiene acceso de red a Maven Central. La compilación anterior verifica sintaxis y lógica aislada, pero no sustituye `./mvnw test` con las dependencias reales. Por tanto, no se afirma que la aplicación completa compile ni que los tests JUnit pasen.
- La estudiante debe ejecutar Maven Wrapper desde su computador una vez que el proyecto de Spring Initializr se integre correctamente.

## Pendiente de la Fase 1
- Revisar personalmente el diff y confirmar los cambios.
- Ejecutar las pruebas con Maven.
- Corregir cualquier incompatibilidad que revele la compilación real.
- Crear el commit de la Fase 1 cuando esté comprobada.

## Pendiente de la Fase 0
- Fork y clon del repositorio oficial.
- Proyecto generado con Spring Initializr y Maven Wrapper original.
- Configuración local de MySQL y arranque comprobado.
- Revisar `SPEC.md` y adaptar `AGENTS.md` desde Atiende v4 cuando estén disponibles esos materiales.
- Primer commit y Pull Request único hacia el repositorio oficial.


### Corrección detectada en la retroalimentación
- En `GeneradorSlugTest`, el caso de nombre sin caracteres alfanuméricos esperaba `IllegalArgumentException`, pero `GeneradorSlug` lanza `DatosInvalidosException` (HTTP 400), que es la excepción definida para datos inválidos en el proyecto. Se corrigió la expectativa del test para comprobar el tipo correcto. La ejecución con JUnit real queda pendiente de Maven Wrapper.
