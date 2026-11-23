# Verificación local pendiente — Fases 0, 1 y 2

Este documento contiene comprobaciones que deben ejecutarse en el equipo de desarrollo después de incorporar el Maven Wrapper generado por Spring Initializr. No son resultados ya obtenidos.

## 1. Compilación y pruebas unitarias

Windows:
```powershell
.\mvnw.cmd clean test
```

macOS/Linux:
```bash
./mvnw clean test
```

Criterio: el proceso termina con código 0 y todos los tests aparecen en verde. Si falla, conservar el mensaje original; no cambiar un test para forzar el resultado.

## 2. Arranque local

1. Iniciar MySQL.
2. Confirmar que `sillon_db` se crea con la URL configurada.
3. Arrancar con `.\mvnw.cmd spring-boot:run` (Windows) o `./mvnw spring-boot:run` (macOS/Linux).
4. Confirmar que el servidor escucha en el puerto configurado y que Hibernate crea/actualiza el esquema.

La aplicación debe arrancar sin `secrets.properties`, usando los valores de relleno del `application.properties`.

## 3. Pruebas HTTP de Fase 1 y 2

1. `POST /api/auth/registro`: comprobar HTTP 201, token Bearer, rol `DUENO`, `salonId`, `salonSlug`.
2. Repetir el registro con el mismo email: HTTP 409.
3. Enviar campos obligatorios vacíos o con formato inválido: HTTP 400 y el mensaje debe nombrar los campos.
4. `POST /api/auth/login` con contraseña incorrecta: HTTP 401 con mensaje genérico.
5. `GET /api/salon` sin token: HTTP 401 y JSON con `codigo`, `error`, `mensaje`, `fecha`.
6. `GET /api/salon` con token válido: HTTP 200, `plan=GRATIS`, `creditosIa=10`.
7. `PUT /api/salon/configuracion` con apertura posterior o igual al cierre: HTTP 409.
8. `PUT /api/salon/configuracion` con intervalo inferior a 15 o superior a 60: HTTP 400.
9. Probar un token expirado o alterado en una ruta privada: HTTP 401.
10. Probar un rol sin permiso en una ruta privada: HTTP 403 con el mismo formato de error.

## 4. Evidencia
Guardar la salida de Maven y las respuestas de Postman según los requisitos de evidencia del PDF. No publicar tokens ni contraseñas en capturas o repositorios.
