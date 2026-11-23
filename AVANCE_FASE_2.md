# Avance de la Fase 2 — Seguridad con JWT y roles

Implementación preparada a partir de los pasos 2.1–2.5 de las páginas 17–18 y del contrato de las páginas 6, 9 y 10 del PDF.

## Paso 2.1 — Usuario autenticado
- `UsuarioPrincipal` implementa `UserDetails` y contiene `id`, `salonId`, `email`, contraseña BCrypt, rol y estado activo.
- La autoridad se forma exactamente como `ROLE_` + nombre del rol.
- `UsuarioDetailsService` carga la cuenta por el correo normalizado.

## Paso 2.2 — JWT y filtro
- `ServicioJwt` firma tokens con el correo como sujeto y los claims `rol` y `salonId`.
- El secreto se decodifica desde Base64 y se valida que tenga al menos 32 bytes para HMAC-SHA.
- El token tiene expiración configurable mediante `sillon.jwt.expiracion-minutos` / `JWT_EXPIRATION_MINUTES`.
- `FiltroJwt` valida el token, vuelve a cargar el usuario desde la base de datos y comprueba que rol y salón del token coincidan con el estado actual del usuario.
- Un token ausente o inválido no autentica la petición; las rutas privadas producen 401 mediante Spring Security.

## Paso 2.3 — Registro y login
- `POST /api/auth/registro` valida los campos del contrato, normaliza el correo, verifica unicidad, genera slug y crea salón + dueño dentro de una transacción.
- El salón comienza con plan `GRATIS`, créditos de registro configurados y horario por defecto.
- La contraseña se guarda con `BCryptPasswordEncoder`.
- Registro responde 201 con `token`, `tipo`, `expiraEnMinutos`, `usuarioId`, `nombre`, `rol`, `salonId` y `salonSlug`.
- `POST /api/auth/login` responde 200 con el mismo formato. Correo inexistente, contraseña incorrecta o usuario inactivo comparten el mismo 401 genérico.

## Paso 2.4 — Reglas de acceso
- Sesiones `STATELESS`; CSRF, formulario y HTTP Basic deshabilitados.
- Rutas públicas: `/api/auth/**`, `/api/publico/**`, webhook, resultado de pago, Swagger y `/error`.
- Se aplicó la matriz de roles del PDF para salón, servicios, estilistas, clientes, citas, recordatorios, solicitudes, pagos y administración.
- `ManejadorErroresSeguridad` devuelve 401 y 403 con `codigo`, `error`, `mensaje` y `fecha`.

## Paso 2.5 — Mi salón
- `GET /api/salon` consulta el salón asociado al `salonId` del principal autenticado.
- `PUT /api/salon/configuracion` recibe solo horario, apertura dominical e intervalo; no acepta un id de salón.
- Los campos son obligatorios, el intervalo se valida entre 15 y 60 (400) y apertura igual o posterior al cierre responde 409.

## Decisión técnica pendiente de confirmar
El PDF exige `expiraEnMinutos`, pero no fija la duración del JWT. Se dejó un valor predeterminado configurable de 60 minutos (`JWT_EXPIRATION_MINUTES`). Si la instructora definió otro valor en clase o en el repositorio oficial, debe sustituirse por ese valor antes de la entrega.

## Verificación disponible
- Código de producción compilado sintácticamente con `javac --release 17` contra stubs temporales de las APIs externas: sin errores del código del proyecto.
- Revisados estáticamente los matchers de autorización contra la matriz de roles del PDF.
- Verificada la decodificación Base64 del valor de ejemplo: 32 bytes.
- Revisado que el registro sea transaccional, que las credenciales se almacenen con BCrypt y que el salón se obtenga solo desde el principal.
- No se ejecutó Maven, JUnit, Spring Boot, JWT real, MySQL ni Postman. Los stubs no sustituyen una compilación con dependencias reales.

## Pendiente antes de cerrar la Fase 2
- La estudiante debe revisar el diff y entender la configuración de seguridad.
- Ejecutar Maven Wrapper real y corregir incompatibilidades que aparezcan.
- Probar los escenarios de COMPRUEBAS del PDF: registro 201 y rol DUENO; correo repetido 409; datos inválidos 400 con campos; login incorrecto 401 genérico; GET `/api/salon` sin token 401 con formato único; salón recién registrado con plan GRATIS y 10 créditos; apertura posterior al cierre 409.
- Comprobar 403 para rol sin permiso y 401/403 con el JSON exacto.
- No crear el commit de la fase hasta que las verificaciones reales pasen y la estudiante revise el diff.
- Se añadieron compact constructors en los DTOs de registro y login para normalizar el correo antes de ejecutar `@Email`, de modo que los espacios se eliminen y el correo quede en minúsculas antes de validar.
- Se ajustaron los matchers para incluir las rutas base (`/api/salon`, `/api/solicitudes`, etc.) además de sus subrutas; esto evita depender de si el matcher `/**` incluye o no el path raíz.


### Ajustes de la revisión posterior
- El parser JWT valida expiración con el mismo `Clock` inyectado que se utiliza al emitir el token.
- Se deshabilitó el registro automático de `FiltroJwt` en el contenedor servlet; el filtro permanece dentro de la cadena de Spring Security.
- Estos ajustes están revisados estáticamente; requieren compilación y prueba de integración real.
