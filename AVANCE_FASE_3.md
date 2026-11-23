# Avance de Fase 3 — Catálogo y equipo

## Paso 3.1 — Servicios

Se incorporó:
- `Servicio`: entidad JPA con relación `ManyToOne` a `Salon`.
- `GuardarServicioRequest` y `ServicioRespuesta`: DTOs `record`.
- `ServicioRepository`: consultas por salón y por combinación `id + salonId`.
- `ServicioMapper`: conversión entre DTO y entidad.
- `ServicioCatalogo`: creación, listado y actualización.
- `ControladorServicio`: POST, GET y PUT en `/api/servicios`.

Reglas reflejadas:
- Nombre máximo 80 caracteres.
- Descripción opcional, máximo 300 caracteres.
- Duración de 15 a 480 minutos.
- Precio entero positivo en pesos colombianos: el DTO valida `fraction = 0` y el mapper convierte a `Long` para persistencia y respuesta.
- `activo` es `true` si no se envía.
- Las consultas y actualizaciones quedan limitadas al `salonId` del principal autenticado.

## Paso 3.2 — Estilistas y relación muchos a muchos

Se incorporó:
- Relación `ManyToMany` desde `Usuario` hacia `Servicio` mediante `estilista_servicios`.
- Método `Usuario.ofreceServicio(Long servicioId)`.
- `CrearEstilistaRequest`: nombre, email, contraseña y lista de al menos un `servicioId`.
- `EstilistaRespuesta`: id, nombre, email, activo y nombres de los servicios; no expone contraseña.
- `EstilistaMapper`: transforma el usuario y sus servicios a la respuesta del contrato.
- `ServicioEstilistas`: crea y lista estilistas.
- `ControladorEstilistas`: POST y GET en `/api/estilistas`.
- Consultas de repositorio para contar estilistas por salón y rol, listar estilistas de un salón y recuperar servicios por ids dentro del salón.

Orden aplicado, decidido por la estudiante:
1. Límite del plan GRATIS (402; máximo dos estilistas).
2. Correo global duplicado (409).
3. Pertenencia de todos los servicios al salón (404).

La creación codifica la contraseña con `PasswordEncoder` (BCrypt configurado en seguridad) y persiste el estilista con rol `ESTILISTA` y activo `true`. El plan PRO no aplica límite.

## Corrección detectada en el paso 3.1

El PDF establece que los precios son números enteros en pesos colombianos, sin decimales. Se corrigió el contrato monetario: `GuardarServicioRequest` recibe `BigDecimal` para poder rechazar decimales con `@Digits(fraction = 0)` y valores fuera del rango de `Long`; `ServicioMapper` convierte con `longValueExact()`, y la entidad y respuesta usan `Long`.

## Paso 3.3 — Clientes

Se incorporó:
- `Cliente`: entidad JPA con relación `ManyToOne` a `Salon`.
- Restricción única compuesta `uk_clientes_salon_telefono` sobre `salon_id + telefono`.
- `Cliente.normalizarTelefono`: conserva únicamente dígitos y el signo `+`.
- `ClienteRepository`: búsqueda por salón y teléfono normalizado, y listado por salón.
- `ServicioClientes.buscarOCrear`: reutiliza al cliente existente dentro del mismo salón o crea uno nuevo si no existe. Si el teléfono no contiene dígitos, responde 400.
- `ClienteRespuesta` y `ClienteMapper`: la salida contiene únicamente `id`, `nombre` y `telefono`.
- `ControladorClientes`: expone únicamente GET `/api/clientes`, usando el `salonId` del usuario autenticado. No se creó POST porque el PDF establece que los clientes nacen al agendar.
- `ClienteTest` y `ServicioClientesTest`: pruebas unitarias para normalización, reutilización, creación y teléfono inválido.

Si el teléfono ya existe en el salón, se devuelve el registro existente y se conserva el nombre guardado originalmente. El mismo teléfono puede existir en otro salón porque la unicidad es compuesta por salón y teléfono.

## Estado de la Fase 3

Los pasos 3.1, 3.2 y 3.3 tienen su estructura de código incorporada. Se realizó compilación sintáctica aislada con `javac --release 17` y stubs temporales para las clases afectadas y los nuevos tests; además se ejecutó una comprobación aislada de la normalización telefónica. No se ejecutó Maven, JUnit real, Hibernate, MySQL ni HTTP. Esas comprobaciones, junto con la revisión integral de todos los archivos, quedan reservadas para el cierre final que acordamos.


## Revisión estática del avance 3.1–3.2

- Compilación sintáctica aislada de las clases de los pasos 3.1 y 3.2 con `javac --release 17` y stubs temporales: correcta.
- Se verificó la secuencia de validaciones en el servicio: límite del plan → correo → pertenencia de servicios.
- Se verificó que las búsquedas de servicios y estilistas se filtran por `salonId`.
- Se verificó que el DTO de respuesta de estilista no contiene contraseña.
- Se verificó la tabla `estilista_servicios` y el método `ofreceServicio`.
- Se verificó que el precio de servicio no admite fracciones en el DTO y se persiste como entero `Long`.
- Esta revisión no equivale a ejecutar Maven, JUnit con dependencias reales, Hibernate ni HTTP.

La decisión de la estudiante quedó incorporada en `SPEC.md`; el paso 3.2 ya no está pendiente.


## Ajustes de auditoría estructural

- El límite de estilistas del plan GRATIS se inyecta desde `sillon.limite-estilistas-gratis`; no queda duplicado como constante en el servicio.
- Los campos de nombre/correo de estilistas y nombre de cliente ya no imponen longitudes no indicadas en sus contratos del PDF.
- La revisión integral estática de las Fases 0–3 se documenta en `AUDITORIA_ESTRUCTURAL_FASES_0_3.md`.
