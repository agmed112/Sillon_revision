# Verificaciones pendientes — Fase 2

- [ ] Revisar personalmente todos los archivos de `auth`, `seguridad`, `usuario` y `salon`.
- [ ] Confirmar con la instructora o material de clase la duración esperada del JWT; el PDF no la especifica y se dejó 60 minutos configurable.
- [ ] Integrar el Maven Wrapper oficial generado por Spring Initializr.
- [ ] Ejecutar `mvnw.cmd test` en Windows o `./mvnw test` en macOS/Linux.
- [ ] Arrancar Spring Boot con MySQL local.
- [ ] Registro: 201, token Bearer, rol DUENO, salonId y salonSlug.
- [ ] Correo repetido: 409.
- [ ] Datos inválidos: 400 y mensaje con los campos que fallaron.
- [ ] Login con contraseña incorrecta: 401 con mensaje genérico.
- [ ] GET `/api/salon` sin token: 401 con `codigo`, `error`, `mensaje`, `fecha`.
- [ ] GET `/api/salon` autenticado: plan GRATIS, 10 créditos y configuración por defecto.
- [ ] Horario con apertura igual o posterior al cierre: 409.
- [ ] Intervalo fuera de 15–60 o campos obligatorios ausentes: 400.
- [ ] Verificar permisos por rol con la matriz de la página 6 del PDF.
- [ ] Verificar que el token inválido no autentica y que una ruta privada responde 401.
- [ ] Verificar 403 con el formato único para un rol autenticado sin permiso.
- [ ] Verificar que `GET` y `PUT /api/salon` usan el `salonId` del principal y no aceptan un id externo.
- [ ] Crear commit «Seguridad con JWT, roles y mi salón» solo después de pasar las pruebas.


- [ ] Confirmar en una prueba de integración que el filtro JWT se ejecuta solo dentro de Spring Security.
- [ ] Probar JWT válido, expirado, alterado y emitido con reloj fijo; validar `exp` usando el mismo `Clock`.
