# SPEC — Sillón

## Propósito
API REST multiinquilino para salones y barberías. Gestiona salones, servicios, estilistas, clientes, citas, reservas públicas, pagos de planes y créditos, recordatorios y chatbot con IA. No incluye interfaz gráfica.

## Roles y planes
- **DUENO:** configura su salón, servicios, estilistas, clientes, citas, pagos y solicitudes.
- **ESTILISTA:** consulta únicamente su agenda y marca sus citas como completadas o no asistidas.
- **ADMIN:** consulta los salones registrados.
- **Público:** consulta catálogo, disponibilidad, reserva y conversa con el chatbot.
- **GRATIS:** 10 créditos al registrar, máximo dos estilistas.
- **PRO:** estilistas ilimitados; la compra suma 300 créditos. Un paquete suma 200 créditos.

## Reglas de negocio
- Aislar los datos por salón. Un recurso ajeno se considera no encontrado (404).
- Correos únicos normalizados; slug único en minúsculas, sin tildes, con sufijos numéricos cuando se repite.
- Horario inicial 08:00–19:00, domingos cerrados, intervalo 30 minutos. La configuración admite intervalos de 15–60 minutos y apertura anterior al cierre.
- Servicios con duración de 15–480 minutos, precio entero positivo en pesos colombianos (COP), sin decimales, y estado activo.
- Estilistas activos ofrecen únicamente los servicios que se les asignan. Un tercero en plan GRATIS responde 402.
- Una cita debe estar en el futuro, dentro del horario, con servicio y estilista válidos; la hora de fin se calcula y el precio se copia del servicio.
- Solo PENDIENTE y CONFIRMADA ocupan agenda; sus intervalos no se pueden cruzar.
- Citas internas: CONFIRMADA / SALON. Reservas públicas: PENDIENTE / EN_LINEA.
- Clientes únicos por teléfono normalizado dentro de cada salón. Al agendar, Java busca el cliente por salón y teléfono normalizado; si no existe, lo crea. El listado de clientes es de solo lectura y no existe un endpoint POST independiente. Validar transiciones de estado.
- Calcular disponibilidad en Java con el reloj del negocio y zona America/Bogota.
- Stripe: precios elegidos por backend, firma obligatoria, webhook idempotente y procesamiento transaccional. Cancelar PRO vuelve a GRATIS sin borrar créditos.
- IA: verificar créditos, consultar proveedor, descontar un crédito cuando la IA responde, validar JSON y aplicar reglas de seguridad. No enviar mensajes automáticamente.
- Recordatorios: Java suministra datos comprobados; IA redacta un borrador. Chatbot: solo catálogo real, escala solicitudes y bloquea promesas de promociones, descuentos y reembolsos.

## Contrato REST obligatorio — sección 4 del PDF

Conservar exactamente los nombres, rutas, roles y formatos indicados. Todas las rutas privadas reciben `Authorization: Bearer <token>`. El error común contiene `codigo`, `error`, `mensaje` y `fecha`.

### Formatos y códigos de error del contrato

- Fechas: `AAAA-MM-DD`.
- Fecha y hora: `AAAA-MM-DDTHH:mm:ss`.
- Horas: se reciben como `HH:mm`; Jackson las devuelve como `HH:mm:ss`.
- Precios: números enteros en pesos colombianos, sin separadores ni decimales.
- Todos los errores, incluidos 401 y 403, usan `codigo`, `error`, `mensaje` y `fecha`.
- `400`: datos inválidos, JSON mal formado, parámetro faltante o firma de webhook inválida.
- `401`: ausencia de token, token inválido o credenciales incorrectas.
- `402`: límite del plan GRATIS o saldo de créditos de IA insuficiente.
- `403`: rol sin permiso o estilista que intenta una transición no permitida para su rol.
- `404`: recurso inexistente o perteneciente a otro salón.
- `409`: conflicto con reglas de negocio, como cruces, horario, transición, correo duplicado o plan PRO ya activo.
- `502`: respuesta de Stripe o de IA que no puede utilizarse.
- `503`: ningún proveedor de IA disponible.

| Método y ruta | Acceso | Campos de entrada | Salida principal y errores |
|---|---|---|---|
| POST `/api/auth/registro` | Público | `salonNombre` (100), `ciudad` (60), `direccion` (150), `telefono` (30), `nombre` (100), `email`, `password` (8–72) | 201: `token`, `tipo`, `expiraEnMinutos`, `usuarioId`, `nombre`, `rol`, `salonId`, `salonSlug`; 400/409 |
| POST `/api/auth/login` | Público | `email`, `password` | 200: mismo objeto de registro; 401 |
| GET `/api/salon` | DUENO | — | 200: `id`, `nombre`, `slug`, `ciudad`, `direccion`, `telefono`, `plan`, `creditosIa`, `configuracion`, `fechaCreacion` |
| PUT `/api/salon/configuracion` | DUENO | `horaApertura`, `horaCierre`, `abreDomingos`, `intervaloMinutos` (15–60) | 200 salón actualizado; 400/409 |
| POST `/api/servicios` | DUENO | `nombre` (80), `descripcion` (300 opcional), `duracionMinutos` (15–480), `precio` (entero COP >0, sin decimales), `activo` (opcional, true por defecto) | 201: `id`, `nombre`, `descripcion`, `duracionMinutos`, `precio`, `activo`; 400 |
| GET `/api/servicios` | DUENO/ESTILISTA | — | 200: servicios del salón |
| PUT `/api/servicios/{id}` | DUENO | Mismos campos de POST | 200; 400/404 |
| POST `/api/estilistas` | DUENO | `nombre`, `email`, `password` (8–72), `servicioIds` (mínimo 1) | 201: `id`, `nombre`, `email`, `activo`, `servicios` (nombres); 400/402/404/409 |
| GET `/api/estilistas` | DUENO | — | 200: estilistas del salón |
| GET `/api/clientes` | DUENO | — | 200: `id`, `nombre`, `telefono` por cliente |
| POST `/api/citas` | DUENO | `clienteNombre`, `clienteTelefono` (7–30), `servicioId`, `estilistaId`, `inicio`, `nota` (300 opcional) | 201 cita; 400/404/409 |
| GET `/api/citas?fecha=` | DUENO/ESTILISTA | `fecha` opcional (hoy por defecto) | 200 citas del día por hora; estilista solo las suyas |
| PATCH `/api/citas/{id}/estado` | DUENO/ESTILISTA | `estado` | 200 cita; 400/403/404/409 |
| POST `/api/citas/{id}/recordatorio` | DUENO | — | 200: `citaId`, `telefonoCliente`, `mensaje`, `creditosRestantes`, `factura`; 402/404/409/502/503 |
| GET `/api/publico/salones/{slug}` | Público | — | 200: `nombre`, `slug`, `ciudad`, `direccion`, `telefono`, `horario`, `servicios`, `estilistas`; 404 |
| GET `/api/publico/salones/{slug}/disponibilidad` | Público | `servicioId`, `estilistaId`, `fecha` obligatorios | 200: `fecha`, `servicio`, `estilista`, `duracionMinutos`, `horariosLibres`; 400/404/409 |
| POST `/api/publico/salones/{slug}/reservas` | Público | Igual que POST `/api/citas` | 201 cita `PENDIENTE` / `EN_LINEA`; 400/404/409 |
| POST `/api/publico/salones/{slug}/chat` | Público | `clienteNombre`, `clienteContacto` (120), `historial` (1–20 turnos con `rol` CLIENTE/ASISTENTE y `texto` máximo 500) | 200: `respuesta`, `escalado`, `solicitudId`, `factura`; 400/402/404/502/503 |
| GET `/api/solicitudes` | DUENO | — | 200: `id`, `clienteNombre`, `clienteContacto`, `mensajes`, `motivo`, `estado`, `fechaCreacion` |
| PATCH `/api/solicitudes/{id}/atendida` | DUENO | — | 200; 404/409 |
| POST `/api/pagos/suscripcion` | DUENO | — | 200: `tipo`, `sesionId`, `urlPago`; 409/502 |
| POST `/api/pagos/creditos` | DUENO | — | 200: `tipo`, `sesionId`, `urlPago`; 502 |
| POST `/api/pagos/webhook` | Stripe | Cuerpo evento y cabecera `Stripe-Signature` | 200: `recibido`, `procesado`; 400 firma inválida |
| GET `/api/pagos/resultado?estado=` | Público | `estado` (`exito` o `cancelado`) | 200 mensaje |
| GET `/api/admin/salones` | ADMIN | — | 200 todos los salones; 403 para otros roles |

**Forma de la cita:** `id`, `inicio`, `fin`, `estado`, `origen`, `precio`, `servicioId`, `servicioNombre`, `estilistaId`, `estilistaNombre`, `clienteNombre`, `clienteTelefono`, `nota`, `fechaCreacion`, `fechaActualizacion`.

**Forma de la factura IA:** `proveedor`, `modelo`, `tokensEntrada`, `tokensSalida`, `costoEstimadoUsd`, `milisegundos`.

**Configuración del salón:** `horaApertura`, `horaCierre`, `abreDomingos`, `intervaloMinutos`.

## Fuera de alcance
Sin frontend, envío automático de WhatsApp o correo, recordatorios programados, reportes periódicos, cobros a clientes finales ni funciones ajenas al documento.

## Evidencia
Desarrollar fases 0–13 con commits progresivos, pruebas JUnit/Mockito y Postman, documentación Swagger, Docker, Render, Supabase, bitácora de IA y un único Pull Request hacia el repositorio oficial.

## Pendientes antes de considerar cerrada la Fase 0
- Incorporar los archivos de Spring Initializr, incluido Maven Wrapper.
- Comparar este SPEC con la sección 4 campo por campo y reescribir las reglas en palabras de la estudiante.
- Adaptar AGENTS.md desde Atiende v4 cuando esté disponible.
- Ejecutar Maven y verificar arranque local; todavía no se ha comprobado.


## Precisiones de las reglas de negocio de la sección 3 del PDF

Estas precisiones completan puntos que en el borrador anterior estaban resumidos. No agregan reglas nuevas: se derivan de la sección 3 del documento oficial y deben ser revisadas por la estudiante y expresadas con su propia comprensión antes de la entrega.

### Salones y cuentas
- El registro crea el salón y su dueño dentro de una sola transacción. El salón inicia en plan `GRATIS`, con 10 créditos de IA y la configuración predeterminada.
- El correo es único en toda la plataforma; un correo ya utilizado produce HTTP 409.
- La apertura debe ser anterior al cierre; si no se cumple, HTTP 409. El intervalo debe estar entre 15 y 60 minutos; si no, HTTP 400.

### Catálogo y equipo
- Cada estilista debe ofrecer uno o más servicios que pertenezcan al mismo salón. Si se intenta asignar un servicio de otro salón, la respuesta es HTTP 404.
- En plan `GRATIS`, el tercer estilista no se crea y produce HTTP 402. En plan `PRO` no existe ese límite.

### Agenda
- La duración de la cita es exactamente la duración del servicio; Java calcula la hora final.
- La cita conserva el precio del servicio que tenía al momento de agendarse, aunque el precio del catálogo cambie después.
- Agendar en el pasado, fuera del horario, un domingo cuando el salón está cerrado o con un estilista que no ofrece el servicio produce HTTP 409.
- Solo las citas `PENDIENTE` y `CONFIRMADA` ocupan agenda y bloquean cruces.
- Al agendar en el salón, la cita nace `CONFIRMADA` con origen `SALON`. Una reserva pública nace `PENDIENTE` con origen `EN_LINEA`.
- Al agendar, se busca al cliente por teléfono dentro del salón; si no existe, se crea. El teléfono se normaliza para conservar solo dígitos y el signo `+`.
- Transiciones permitidas: `PENDIENTE` → `CONFIRMADA` o `CANCELADA`; `CONFIRMADA` → `COMPLETADA`, `CANCELADA` o `NO_ASISTIO`. Los estados `COMPLETADA`, `CANCELADA` y `NO_ASISTIO` son finales. Las demás transiciones producen HTTP 409.
- Un estilista solo puede cambiar citas propias y únicamente a `COMPLETADA` o `NO_ASISTIO`. Una cita de otro estilista debe responder 404; una transición no permitida para su rol responde 403.
- La disponibilidad la calcula Java: se consideran inicios separados por el intervalo del salón desde la apertura, el servicio debe caber antes del cierre, no debe haber cruce con citas que ocupan agenda y el inicio no puede estar en el pasado.
- Las reglas de “hoy” y “ahora” usan la zona `America/Bogota`, aunque el servidor esté en UTC.

### Pagos
- El cliente de la API no envía precios ni identificadores de precio. El backend decide el precio según el producto solicitado.
- Si el salón ya tiene plan `PRO`, volver a solicitar la suscripción produce HTTP 409.
- El webhook debe validar la firma de Stripe; una firma ausente o incorrecta produce HTTP 400.
- Cada identificador de evento de Stripe se procesa una sola vez. Si el evento llega repetido, se ignora.
- Una suscripción PRO confirmada cambia el salón a `PRO`, guarda el identificador de suscripción y suma 300 créditos. El paquete de créditos suma 200. Si Stripe cancela la suscripción, el salón vuelve a `GRATIS` y conserva los créditos acumulados.

### Inteligencia artificial
- Cada respuesta de IA consume un crédito. Antes de llamar al proveedor se comprueba el saldo; si no hay créditos, se responde 402 y no se llama al proveedor. El crédito se descuenta cuando la IA responde.
- El recordatorio solo se prepara para citas `PENDIENTE` o `CONFIRMADA`; cualquier otro estado produce HTTP 409. Java proporciona servicio, estilista, fecha, hora, valor y dirección ya verificados; la IA únicamente redacta el mensaje. Una persona debe aprobarlo. El sistema no lo envía.
- El chatbot responde usando el catálogo real del salón. No agenda, modifica ni cancela citas; esos casos se escalan y se crea una solicitud de contacto con los mensajes del cliente.
- Si el chatbot menciona descuentos, cupones, algo gratis, promociones o reembolsos, Java sustituye la respuesta por un mensaje seguro fijo y escala la solicitud con motivo `REGLA_DE_SEGURIDAD`.
- Las respuestas de IA se solicitan en JSON y se validan. JSON inválido o incompleto produce HTTP 502; si ningún proveedor responde, HTTP 503.
- Las llaves se guardan localmente en `secrets.properties` o como variables de entorno en Render. Nunca se incluyen en el código, Git ni prompts.

### Persistencia de enumeraciones
Todos los enums del proyecto se almacenan como texto (`EnumType.STRING`) y conservan exactamente los identificadores definidos en el PDF: `DUENO` sin tilde, además de los valores de `Plan`, `EstadoCita`, `OrigenCita`, `TipoCompra`, `MotivoSolicitud` y `EstadoSolicitud`.


## Decisión de la estudiante — orden de validaciones al crear estilistas (Fase 3.2)

La estudiante definió el siguiente orden antes de implementar el código:

1. **Límite del plan (402):** se valida primero. Si el salón ya alcanzó el máximo de dos estilistas del plan GRATIS, la solicitud se rechaza de inmediato. No tiene sentido continuar con las validaciones de correo o servicios cuando la cuenta ya no puede incorporar otro estilista.
2. **Correo electrónico duplicado (409):** se valida después de confirmar que el plan permite sumar un estilista. Así se evita crear una identidad con un correo que ya pertenece a otro usuario.
3. **Pertenencia de los servicios (404):** se valida al final. Se comprueba que todos los servicios asignados pertenezcan al salón que registra al estilista, como último control de consistencia y aislamiento entre salones.

Este orden prioriza la regla comercial del plan, después la unicidad global de identidad y finalmente la pertenencia multiinquilino de las relaciones.


## Decisión de la estudiante — transiciones de estado de las citas (Fase 4.1)

La estudiante justificó las cinco transiciones permitidas con situaciones reales de una barbería:

1. **PENDIENTE → CONFIRMADA:** el cliente aparta una cita por la aplicación; el barbero revisa que tenga espacio disponible en su agenda y decide aceptarla oficialmente para esperarlo.
2. **PENDIENTE → CANCELADA:** el cliente se da cuenta de que se equivocó de hora o tiene un imprevisto antes de que el barbero acepte la cita, por lo que la anula.
3. **CONFIRMADA → COMPLETADA:** el cliente llega a la barbería, el estilista realiza el corte de pelo o arreglo de barba, el servicio termina con éxito y el cliente se retira.
4. **CONFIRMADA → CANCELADA:** el cliente ya tenía la cita aceptada, pero avisa a última hora que no podrá asistir por un problema personal o una emergencia.
5. **CONFIRMADA → NO_ASISTIO:** el cliente tenía la cita confirmada, el barbero lo esperó a la hora acordada, pero el cliente nunca llegó ni avisó.

Los estados `COMPLETADA`, `CANCELADA` y `NO_ASISTIO` son finales. Cualquier transición distinta de las cinco anteriores se rechaza con HTTP 409.
