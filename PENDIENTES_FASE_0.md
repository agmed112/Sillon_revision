# Comprobaciones pendientes de la Fase 0

## Paso 0.1 — Fork y repositorio oficial
- [ ] La estudiante comparte el enlace del repositorio oficial entregado por la instructora.
- [ ] Crear el fork desde la cuenta de GitHub de la estudiante.
- [ ] Clonar el fork y comprobar que existe `.git`.
- [ ] Comprobar que `.github` procede del repositorio oficial y conservarla intacta.
- [ ] Integrar el proyecto en la raíz del fork (`pom.xml` junto al README).
- [ ] Crear commits progresivos; no entregar todo en un solo commit.

## Paso 0.2 — Spring Initializr (actividad SIN IA)
- [ ] La estudiante genera el proyecto en Spring Initializr con Maven, Java, Jar, Java 17, Group `com.devsenior`, Artifact `sillon` y Package `com.devsenior.sillon`.
- [ ] Seleccionar Spring Web, Spring Data JPA, MySQL Driver, PostgreSQL Driver, Validation, Spring Security y HTTP Client.
- [ ] La estudiante descarga el proyecto y copia `pom.xml`, `src`, `mvnw`, `mvnw.cmd` y `.mvn` a la raíz del fork.
- [x] Revisar que el POM actual declara Java 17 y Spring Boot 4.1.2 (versión estable de la línea 4.1 al 8-nov-2026).
- [x] Verificar que el POM contiene las dependencias adicionales requeridas: springdoc 3.x, Stripe Java y JJWT api/impl/jackson.
- [ ] Confirmar que el POM integrado es el generado por Initializr más las dependencias adicionales; el archivo actual no demuestra por sí solo su procedencia.

## Paso 0.3 — Configuración y secretos
- [x] Mantener un único `application.properties` y no usar `application.yml` ni perfiles.
- [x] Puerto desde `PORT`, con 8080 por defecto.
- [x] MySQL local con base `sillon_db` y creación automática de base.
- [x] `ddl-auto=update` y `open-in-view=false`.
- [x] Importación opcional de `secrets.properties` desde la raíz.
- [x] Zona `America/Bogota`, URL pública y valores 10/300/200/2.
- [x] Alinear nombres de variables con el contrato de despliegue del PDF: `URL_PUBLICA`, `JWT_SECRETO`, `STRIPE_CLAVE_SECRETA`, `STRIPE_SECRETO_WEBHOOK`, `STRIPE_PRECIO_PRO`, `STRIPE_PRECIO_CREDITOS`, `GEMINI_LLAVE`, `STRIPE_URL_API` y `SILLON_IA_OPENAI_URL`. La expiración JWT se conserva como propiedad local de 60 minutos; el PDF no exige una variable de entorno para ella.
- [x] Conservar `secrets.example.properties` con valores ficticios.
- [x] Ignorar `secrets.properties`, archivos `.env` y material de llaves privadas en Git y Cursor.
- [ ] La estudiante crea su `secrets.properties` local con valores propios; no compartirlos.
- [ ] Comprobar con `git status` en el fork que `secrets.properties` no aparece.
- [ ] Con MySQL disponible, comprobar que la aplicación puede iniciar sin `secrets.properties`.

## Paso 0.4 — Contexto del agente y documentación
- [x] `SPEC.md` contiene propósito, roles, planes, reglas, contrato REST y fuera de alcance; falta la lectura y validación personal de la estudiante con sus propias palabras.
- [ ] Recibir `AGENTS.md` versión 4 de Atiende y contrastarlo con la adaptación actual.
- [x] `AGENTS.md` provisional recoge las reglas explícitas del PDF; no se declara adaptación final de Atiende v4.
- [x] `BITACORA-IA.md` contiene una tabla de seis columnas y entradas de los usos importantes registrados hasta esta revisión.
- [ ] La estudiante revisa que la bitácora refleje fielmente su proceso y sus decisiones.
- [ ] Instalar/preparar Cursor y abrir el proyecto desde la raíz del fork.

## Evidencia final de Fase 0
- [ ] Resolver dependencias y ejecutar compilación con Maven Wrapper.
- [ ] Arrancar la aplicación con MySQL y sin `secrets.properties`.
- [ ] Crear commit `Proyecto base de Sillón, SPEC y reglas del agente`.
- [ ] Hacer push y abrir un único Pull Request con el título/nombre indicado por la instructora.

**Regla de auditoría:** una casilla solo se marca como completada cuando existe evidencia. Los pasos 0.1 y la generación inicial de 0.2 no se pueden cerrar desde este chat: requieren el repositorio oficial y la acción SIN IA de la estudiante en Spring Initializr.
