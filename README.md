# Sillón — API REST SaaS para barberías y salones

Sillón permite a barberías y salones administrar servicios, estilistas, clientes, citas, recordatorios asistidos por IA y solicitudes de atención. El backend aplica aislamiento por salón, roles, planes, créditos de IA y pagos de suscripción en modo prueba.

## Estado de la entrega

- Fases 0–6: código base incorporado; ejecución Maven, MySQL y Stripe CLI real/simulador pendiente.
- Fases 7–9: motor IA, créditos, recordatorio, chatbot, prompts y solicitudes implementados con base en el contrato visible del PDF. Los imports de proveedores y lector JSON se alinearon con Jackson 3, requerido por Spring Boot 4. La copia original de Atiende v4 no estaba disponible, por lo que el paquete IA se reconstruyó con los componentes y contratos descritos; no se afirma reutilización literal.
- Fase 10: pruebas JUnit/Mockito escritas, incluida prueba de contexto que requiere MySQL. No se ha ejecutado JUnit real. La suite escrita contiene 25 archivos, 118 métodos `@Test` y 11 filas parametrizadas (129 invocaciones previstas).
- Fase 11: OpenAPI, Bearer JWT, tags 0–9 y endpoints de solicitudes/chat/recordatorio incorporados. Falta abrir Swagger y probarlo con token real.
- Fase 12: Dockerfile y configuración de Supabase/Render preparados. El despliegue externo aún no se ha realizado.
- Fase 13: colección Postman de 52 escenarios y dos entornos preparados. Falta ejecutarla localmente y contra Render, completar el pago manual, URL real y Pull Request oficial.

Revisión más reciente: `REVISION_INCREMENTAL_2026-11-23.md`.

Consulta `CIERRE_FASES_7_A_13.md`, `PENDIENTES_FASE_7.md` a `PENDIENTES_FASE_13.md` y `BLOQUEOS_PARA_CIERRE.md` para el estado detallado. La fuente de verdad del alcance y los códigos HTTP es el PDF oficial del taller. No se declara `BUILD SUCCESS` ni pruebas aprobadas sin ejecutar Maven/JUnit reales.

## Producción

- **URL de Render:** pendiente de crear el Web Service y obtener la URL real. No se coloca un enlace ficticio.
- **Swagger:** `https://TU-SERVICIO.onrender.com/swagger-ui.html` (reemplazar `TU-SERVICIO` después del despliegue).

## Stack

- Java 17 y Spring Boot 4.1.x.
- Maven, Spring Data JPA, MySQL local y PostgreSQL en Supabase.
- Spring Security, JWT y BCrypt.
- Springdoc OpenAPI / Swagger UI.
- Stripe Checkout y webhook firmado en modo prueba.
- IA por HTTP con `RestClient`: Gemini con protocolo OpenAI y Claude como respaldo.
- JUnit 5 y Mockito.
- Docker y Render.

## Configuración local

1. Integrar este proyecto en la raíz del fork oficial, conservando `.github` y el Maven Wrapper originales.
2. Crear la base MySQL `sillon_db` o permitir su creación automática.
3. Copiar `secrets.example.properties` como `secrets.properties` y completar solo valores privados locales. Nunca subir `secrets.properties`.
4. Configurar `GEMINI_LLAVE` para usar Gemini real. Claude queda como respaldo y requiere `CLAUDE_LLAVE`; sin ella se registra el fallo de configuración y el sistema termina en 503 si Gemini tampoco responde.
5. En otra terminal ejecutar Stripe CLI: `stripe listen --forward-to localhost:8080/api/pagos/webhook`. Copiar el `whsec_` generado en `secrets.properties` y reiniciar la aplicación.
6. Con el Maven Wrapper oficial ejecutar `./mvnw test` (Windows: `mvnw.cmd test`). La prueba de contexto completo requiere MySQL encendido.
7. Arrancar con `./mvnw spring-boot:run`.

## IA y créditos

Cada respuesta de IA consume un crédito después de que un proveedor responde. El saldo se descuenta mediante una actualización condicional en base de datos. La llamada externa no mantiene abierta una transacción JPA. El costo estimado se calcula con las tarifas configurables `IA_COSTO_ENTRADA_POR_MILLON` e `IA_COSTO_SALIDA_POR_MILLON`; por defecto son cero porque el PDF no fija tarifas y no se inventan precios.

## Verificación con Postman

Importar:

- `postman/sillon.postman_collection.json`
- `postman/sillon.local.postman_environment.json`
- `postman/sillon.produccion.postman_environment.json`

La colección tiene siete carpetas y 52 escenarios. Ejecutar carpetas 1 a 6 en orden con el entorno local y la API configurada para usar los simuladores oficiales o credenciales de prueba válidas. La carpeta 7 es manual: abrir `checkoutProUrl`, completar el pago de prueba con `4242 4242 4242 4242`, cualquier fecha futura y cualquier CVC; después ejecutar las dos peticiones de esa carpeta.

Antes de ejecutar el entorno de producción, reemplazar su única variable `baseUrl` por la URL HTTPS real asignada por Render.

## Endpoints del contrato

| Grupo | Operaciones |
|---|---|
| Público | `GET /api/publico/salones/{slug}`, `GET /api/publico/salones/{slug}/disponibilidad`, `POST /api/publico/salones/{slug}/reservas`, `POST /api/publico/salones/{slug}/chat` |
| Autenticación | `POST /api/auth/registro`, `POST /api/auth/login` |
| Mi salón | `GET /api/salon`, `PUT /api/salon/configuracion` |
| Servicios | `POST /api/servicios`, `GET /api/servicios`, `PUT /api/servicios/{id}` |
| Estilistas | `POST /api/estilistas`, `GET /api/estilistas` |
| Clientes | `GET /api/clientes` |
| Agenda | `POST /api/citas`, `GET /api/citas`, `PATCH /api/citas/{id}/estado`, `POST /api/citas/{id}/recordatorio` |
| Solicitudes | `GET /api/solicitudes`, `PATCH /api/solicitudes/{id}/atendida` |
| Pagos | `POST /api/pagos/suscripcion`, `POST /api/pagos/creditos`, `POST /api/pagos/webhook`, `GET /api/pagos/resultado` |
| Administración | `GET /api/admin/salones` |

La tabla del contrato enumera 25 combinaciones método/ruta, aunque la comprobación de Fase 11 del PDF menciona 22. Se conservaron todas las operaciones explícitas del contrato; la diferencia debe verificarse con la instructora/calificador.

## Variables de entorno de Render

Configurar los nombres siguientes en Render, sin escribir sus valores en este README:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRETO`
- `URL_PUBLICA`
- `STRIPE_CLAVE_SECRETA`
- `STRIPE_PRECIO_PRO`
- `STRIPE_PRECIO_CREDITOS`
- `STRIPE_SECRETO_WEBHOOK`
- `GEMINI_LLAVE`

Usar Supabase Session Pooler, puerto 5432. No configurar `STRIPE_URL_API` ni `SILLON_IA_OPENAI_URL` en Render; conservar sus valores por defecto para Stripe y Gemini reales.

## Escalabilidad — reflexión personal pendiente

El PDF solicita tres líneas propias sobre qué parte del sistema cuidar primero si Sillón tuviera 500 salones. Borrador para que la estudiante revise y adapte con su propia argumentación:

> Si Sillón tuviera 500 salones, primero cuidaría el aislamiento de datos entre negocios, porque ninguna cuenta debe consultar información de otra. También vigilaría la base de datos y la concurrencia de citas y créditos, para evitar reservas duplicadas o saldos incorrectos. Finalmente, revisaría la capacidad de los servicios de IA y los webhooks, porque una falla en esas integraciones afecta a muchos salones al mismo tiempo.

## Seguridad

No subir `secrets.properties`, `.env`, tokens, llaves privadas ni credenciales. Si una llave real se filtra en el historial de Git, revocarla en el proveedor y generar otra.
