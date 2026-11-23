# Retroalimentación técnica — Sillón (revisión de lo disponible)

Fecha: 8 de noviembre de 2026  
Base revisada: `sillon_fase2_avance.zip` y el PDF oficial del proyecto.

## Alcance revisado
Se inspeccionaron los 69 archivos del ZIP, con atención a `pom.xml`, configuración, entidades `Salon` y `Usuario`, repositorios, errores, reloj, generador de slug, autenticación, JWT, filtro, autorización, controladores, DTOs, pruebas y documentos.

## Corrección concreta realizada
- `GeneradorSlugTest` esperaba `IllegalArgumentException` cuando el nombre no genera un slug.
- La implementación lanza `DatosInvalidosException`, que representa HTTP 400 y deriva de `ExcepcionApi`.
- Se corrigió el test para esperar `DatosInvalidosException`. No se cambió el código de producción para satisfacer una expectativa incorrecta.

## Aspectos que sí están alineados con el PDF
- Los nombres de los paquetes y la separación por dominios siguen la estructura indicada.
- `Salon` contiene los atributos base; `ConfiguracionSalon` es `@Embeddable` y su configuración inicial coincide con 08:00–19:00, domingos cerrados e intervalo de 30 minutos.
- `Plan` y `Rol` tienen los valores establecidos; `Usuario` tiene relación `ManyToOne` con `Salon` y email único global.
- El generador de slug elimina marcas diacríticas, normaliza a minúsculas, agrupa separadores y añade sufijos numéricos.
- El error común tiene los cuatro campos del contrato; existen excepciones para los códigos requeridos y manejadores para validación, JSON mal formado y parámetros faltantes o con tipo inválido.
- El reloj se configura con `America/Bogota`.
- Registro y creación de salón/dueño se implementan dentro de una transacción; la contraseña se codifica con `PasswordEncoder` BCrypt; email se normaliza.
- El JWT incluye email como sujeto, rol y salonId como claims. El filtro recarga al usuario y compara rol y salón contra los datos actuales.
- Las rutas y los roles configurados corresponden, en la revisión estática, a la matriz de permisos del PDF.
- `GET /api/salon` y `PUT /api/salon/configuracion` toman el `salonId` del principal autenticado y no reciben un identificador de salón desde URL o cuerpo.

## Observaciones que no deben confundirse con errores confirmados
- El Maven Wrapper no está en el ZIP. El PDF exige generarlo con Spring Initializr; no debe inventarse ni sustituirse por archivos vacíos.
- La duración JWT predeterminada es 60 minutos. El PDF exige devolver `expiraEnMinutos`, pero no fija la duración. Debe confirmarse con material de clase o la instructora si existe un valor obligatorio.
- `SPEC.md` tenía varias reglas de negocio resumidas. Se añadieron precisiones tomadas del PDF: transiciones exactas, reglas del estilista, agenda, pagos, créditos y chatbot. La estudiante debe revisarlas y redactarlas con su comprensión antes de entregar.
- `AGENTS.md` sigue siendo un borrador: el PDF exige adaptarlo desde `AGENTS.md` versión 4 de Atiende. Ese archivo fuente no está disponible en los materiales actuales.
- El ZIP no contiene `secrets.properties` real, lo cual es correcto. Los valores de ejemplo no deben utilizarse como secretos de producción.

## Límites de verificación
- El ZIP se pudo abrir y su integridad se comprobó.
- El XML de `pom.xml` se puede analizar.
- Se verificó estáticamente el código y se realizó una prueba aislada del generador de slug con un arnés temporal; esto no ejecuta JUnit ni sustituye una compilación Maven.
- En este entorno hay Java 21, pero no está instalado Maven, no existe Maven Wrapper en el ZIP y no hay dependencias descargadas en `~/.m2`.
- Por tanto, NO se ha ejecutado `mvn test`, `mvnw test`, el contexto de Spring Boot, Hibernate contra MySQL, JWT con la biblioteca real, BCrypt, ni Postman.
- No se debe afirmar que las Fases 0, 1 o 2 están cerradas. El estado correcto es: código preparado y revisión estática realizada; validación de integración pendiente.

## Próximas comprobaciones en el equipo de desarrollo
1. Generar el proyecto desde Spring Initializr con los valores de la página 14 del PDF e incorporar su Maven Wrapper.
2. Integrar el código en la raíz del fork oficial, conservando `.github`.
3. Ejecutar `mvnw.cmd test` en Windows o `./mvnw test` en macOS/Linux.
4. Resolver cualquier error real que informe Maven; no cambiar pruebas solo para hacerlas pasar.
5. Arrancar con MySQL y probar registro, login, JWT, roles y endpoints de salón según las páginas 16–18.
6. Solo después de revisar el diff y pasar las pruebas, crear el commit de la fase correspondiente.


## Segunda revisión: dos ajustes preventivos de seguridad

### 1. Reloj de validación JWT
Antes, el token se emitía con el `Clock` inyectado, pero JJWT validaba su expiración con el reloj del sistema por defecto. Eso podía hacer que pruebas con reloj fijo fueran inconsistentes y no aprovechaba el reloj centralizado del proyecto. `ServicioJwt.leerClaims` ahora entrega a JJWT un reloj derivado del mismo `Clock` inyectado. Se conserva la expiración configurada y no se cambia el contrato del token.

### 2. Registro del filtro JWT
`FiltroJwt` es un bean `@Component` que también se agrega explícitamente a `SecurityFilterChain`. Spring Boot puede registrar automáticamente los beans de tipo `Filter` en el contenedor servlet; para evitar una segunda inscripción fuera de la cadena de Spring Security, se añadió un `FilterRegistrationBean` deshabilitado. El filtro continúa activo dentro de `SecurityFilterChain`.

### Verificación de estos ajustes
- Se verificó por inspección que `ServicioJwt` utiliza el mismo `Clock` para emitir y validar tokens.
- Se verificó por inspección que el registro servlet automático de `FiltroJwt` está deshabilitado y que permanece agregado con `addFilterBefore`.
- No se ha ejecutado el contexto real de Spring Boot ni pruebas de integración por falta del Maven Wrapper y de Maven en este entorno. Por tanto, ambos ajustes requieren validación de compilación y arranque en el equipo local.


## Implementación estructural del paso 3.1 (8 de noviembre de 2026)

Se desarrolló el CRUD del catálogo de servicios conforme a las páginas 10 y 18:
- Entidad `Servicio` con relación `ManyToOne` a `Salon`.
- DTOs `record` de entrada y salida.
- Mapper propio.
- Repositorio con listado por salón y búsqueda por `id + salonId`.
- Servicio de aplicación transaccional.
- Controlador con POST, GET y PUT en `/api/servicios`.

Se mantuvo la restricción multiinquilino: las actualizaciones nunca buscan por `id` sin combinarlo con `salonId`. El DTO de entrada valida los límites indicados; `activo` toma `true` cuando no se envía. En la primera versión el precio se representó con `BigDecimal`; la revisión posterior corrigió el contrato para aceptar solo enteros COP, validarlos sin fracciones y persistirlos como `Long`.

En esa revisión inicial, el paso 3.2 se dejó pendiente porque el PDF exige que la estudiante decida sin IA el orden de validación del límite del plan, correo único y pertenencia de los servicios al salón. Después de que la estudiante documentó su decisión, el paso 3.2 se implementó; el paso 3.3 continúa pendiente.


## Fase 3.2 y corrección del contrato monetario

La estudiante definió el orden de validación del alta de estilistas: (1) límite del plan, (2) correo único, (3) pertenencia de todos los servicios al salón. Se conservó su razonamiento en `SPEC.md`.

Se implementó la relación `Usuario`–`Servicio` mediante `@ManyToMany` y la tabla `estilista_servicios`, el método `ofreceServicio`, DTOs, mapper, servicio de aplicación y controlador POST/GET `/api/estilistas`. La contraseña se codifica con `PasswordEncoder`; el DTO de salida no contiene password. Las búsquedas de servicios se filtran por `salonId`.

Además, la revisión del contrato detectó que el precio del servicio se había representado como `BigDecimal`, aunque el PDF (p. 9) exige números enteros en pesos colombianos, sin decimales. Se corrigió la entidad y la respuesta para usar `Long`; el DTO recibe `BigDecimal` con `@Digits(fraction = 0)` y el mapper convierte con `longValueExact()`. Así se rechaza una entrada con parte decimal en lugar de depender de una posible conversión silenciosa de Jackson.

La revisión de estas clases es estática. No se ejecutó Maven ni Hibernate; la compilación integral y pruebas de integración siguen pendientes para la etapa final.


## Paso 3.3 — Clientes (8 de noviembre de 2026)

Se implementó el dominio de clientes según las páginas 7, 8, 10 y 11 del PDF: entidad `Cliente` con relación `ManyToOne` a `Salon`, unicidad compuesta por salón y teléfono, normalización conservando dígitos y `+`, método `ServicioClientes.buscarOCrear`, DTO de salida y listado de solo lectura en GET `/api/clientes`. No se añadió endpoint POST: el contrato establece que el cliente se crea al agendar.

Se añadieron pruebas unitarias para la normalización telefónica, reutilización de un cliente existente, creación de uno nuevo y rechazo de un teléfono sin dígitos. La sintaxis de las clases de producción y los tests se compiló de forma aislada con `javac --release 17` y stubs temporales; también se ejecutó una comprobación aislada del normalizador. No se ejecutó Maven/JUnit real, Hibernate, MySQL ni HTTP. Esta no es la verificación final del proyecto.
