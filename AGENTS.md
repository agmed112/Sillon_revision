# AGENTS.md — Reglas de trabajo para Sillón

> Estado: adaptación provisional basada en las instrucciones del PDF de Sillón. Falta contrastarla con `AGENTS.md` versión 4 de Atiende, que debe proporcionar la estudiante. No afirmar que esta es la adaptación final hasta hacer esa comparación.

## Contexto del proyecto
- Proyecto: API REST SaaS multiinquilino para barberías y salones.
- Lenguaje y plataforma: Java 17, Spring Boot 4.1.x y Maven.
- Paquete base: `com.devsenior.sillon`.
- Dominios: `compartido`, `seguridad`, `auth`, `salon`, `usuario`, `servicio`, `cliente`, `cita`, `publico`, `pago`, `chat` e `ia`.
- Persistencia: Spring Data JPA; MySQL en local y PostgreSQL en producción.
- Seguridad: Spring Security, JWT, BCrypt y roles `DUENO`, `ESTILISTA`, `ADMIN`.

## Restricciones obligatorias
- No usar Lombok, Spring AI ni SDKs de IA. Las llamadas a proveedores de IA se hacen por HTTP con `RestClient`.
- No agregar dependencias ni ampliar el alcance sin preguntar primero.
- No cambiar rutas, nombres de campos, enums, códigos HTTP ni formatos del contrato REST del PDF.
- No modificar `.github`: contiene el calificador oficial.
- No incluir secretos en código, Git, prompts, documentación ni respuestas de API.
- No modificar un test para forzar que pase; primero investigar la causa y corregir producción si corresponde.

## Arquitectura y reglas de negocio
- Organizar el código por los dominios anteriores y mantener responsabilidades claras.
- Todo dato privado debe consultarse y modificarse usando el `salonId` del usuario autenticado; nunca confiar en un `salonId` enviado por el cliente.
- Si un recurso no existe o pertenece a otro salón, responder 404 según el contrato.
- Usar DTOs para entrada y salida; no devolver entidades JPA directamente desde controladores.
- Usar repositorios para consultas persistentes y servicios para reglas de negocio.
- Respetar las relaciones y enumeraciones del modelo del PDF; los enums se persisten como texto (`EnumType.STRING`).
- Las reglas de agenda, permisos, precios, créditos y transiciones se calculan en Java, no en la IA.
- La IA solo redacta a partir de datos verificados por Java; no agenda ni cambia datos de negocio.
- Guardar los prompts como archivos de recursos cuando se implemente la fase de IA.

## Fechas, configuración y pruebas
- Usar el `Clock` inyectado del proyecto y la zona `America/Bogota` para fechas y horas del negocio; no consultar directamente el reloj del sistema en la lógica de negocio.
- Mantener configuración común en `src/main/resources/application.properties`; no crear perfiles ni `application.yml` para este taller.
- Mantener `secrets.properties` fuera de Git y de la indexación de Cursor.
- Escribir pruebas JUnit 5 y Mockito a partir de los casos de negocio definidos en el PDF; no llamar a Stripe ni a Gemini reales desde tests unitarios.
- Revisar los cambios y comprender el diff antes de aceptarlos. No dar por verificado un cambio hasta tener evidencia de la comprobación correspondiente.
- Crear un commit por cada paso que quede en verde, siguiendo el historial progresivo requerido por el taller.

## Forma de trabajo del agente
1. Leer `SPEC.md` y este archivo antes de proponer cambios.
2. Proponer un plan pequeño y limitado a la fase actual.
3. No implementar decisiones que el PDF marca como `SIN IA`; pedir primero la decisión de la estudiante.
4. Mantener los cambios dentro del alcance del paso solicitado.
5. Informar archivos modificados, verificaciones realizadas, verificaciones pendientes y cualquier supuesto.
