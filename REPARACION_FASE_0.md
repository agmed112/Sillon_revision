# Reparación de Fase 0 — revisión documental y configuración

Fecha de revisión: 8 de noviembre de 2026.

## Cambios aplicados
1. Se alinearon los nombres de variables de entorno de `application.properties` con los nombres indicados en el apartado de Render del PDF: `URL_PUBLICA`, `JWT_SECRETO`, `STRIPE_CLAVE_SECRETA`, `STRIPE_SECRETO_WEBHOOK`, `STRIPE_PRECIO_PRO`, `STRIPE_PRECIO_CREDITOS` y `GEMINI_LLAVE`. Se mantuvieron `STRIPE_URL_API` y `SILLON_IA_OPENAI_URL` como pide la Fase 0. La expiración JWT queda en 60 minutos como propiedad de configuración, sin añadir una variable de entorno que el PDF no exige.
2. Se mantuvieron los defaults locales de MySQL y se añadieron los nombres `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` con fallback local, coherentes con la configuración posterior de Render.
3. Se actualizó `secrets.example.properties` para usar las mismas claves que lee `application.properties`. No se creó ni se incluyó `secrets.properties`.
4. Se reforzaron `.gitignore` y `.cursorignore` para excluir secretos locales, archivos `.env` y material de llaves privadas.
5. Se amplió `AGENTS.md` con reglas explícitas derivadas del PDF. Se mantiene marcado como provisional porque no se recibió el `AGENTS.md` v4 de Atiende que la guía exige adaptar.
6. Se reescribió `PENDIENTES_FASE_0.md` como checklist verificable y no se marcaron como hechos el fork, la generación Initializr, Maven Wrapper, `.github`, instalación de Cursor ni pruebas locales.

## Dependencias revisadas el 8-nov-2026
- Spring Boot 4.1.2: versión estable de la rama 4.1 y compatible con Java 17; Maven 3.6.3 o posterior. Fuente: documentación oficial de requisitos y anuncio de lanzamiento.
- springdoc-openapi-starter-webmvc-ui 3.2.0: versión publicada el 24-oct-2026 y documentada en la guía oficial de springdoc.
- stripe-java 34.1.0: versión estable publicada en Maven Central; las versiones 34.2 consultadas eran alpha/beta, no se adoptaron.
- JJWT 0.13.0: versión estable mostrada en Maven Central.

## Verificaciones realizadas en este entorno
- `pom.xml` parsea como XML válido.
- `application.properties` no contiene claves duplicadas y conserva los valores requeridos por la Fase 0.
- Las variables de entorno del PDF aparecen en los placeholders correspondientes.
- `secrets.properties` está ignorado por Git y Cursor.
- No se encontró un archivo `secrets.properties` dentro del paquete.

## Pendiente que no puede resolverse sin material/acción de la estudiante
- Enlace del repositorio oficial y creación del fork.
- Conservar la carpeta `.github` original del repositorio oficial; no debe inventarse ni recrearse.
- Generar el proyecto en Spring Initializr, actividad marcada `SIN IA` en el PDF, y copiar el Maven Wrapper original (`mvnw`, `mvnw.cmd`, `.mvn`).
- Recibir el `AGENTS.md` v4 de Atiende para hacer la adaptación solicitada.
- Instalar/configurar Cursor, verificar Java 17 y Maven en el computador de la estudiante.
- Crear el archivo local `secrets.properties` con credenciales propias y comprobar `git status`.
- Arranque real con MySQL y compilación mediante Maven Wrapper.
- Commit, push y Pull Request.

## Límite de esta revisión
No se ejecutó Maven ni Spring Boot. En el contenedor de revisión hay Java 21 y no hay Maven instalado; eso no determina qué versiones están instaladas en el computador de la estudiante. No se modificó `.github` porque no está disponible y la guía prohíbe tocar el calificador oficial.
