# Cierre de implementación — Fases 7 a 13

## Implementado en esta versión

| Fase | Entregables de código/documentación |
|---|---|
| 7 | Proveedores Gemini/OpenAI-compatible y Claude, fallback, propiedades tipadas, RestClient con timeout, biblioteca de prompts, lector JSON, factura y créditos atómicos |
| 8 | Datos verificados de cita, prompt `recordatorio.txt`, servicio de borrador, validación JSON/máximo 700 caracteres y endpoint de recordatorio |
| 9 | Contexto de catálogo real, prompt `chatbot.txt`, historial por turnos, escalamiento, anti-promesas Java, entidad/servicio/controlador de solicitudes y endpoint público de chat |
| 10 | Pruebas unitarias escritas de IA, créditos, recordatorio, chatbot, solicitudes y contexto Spring |
| 11 | Tags OpenAPI 0–9 y los cuatro endpoints faltantes |
| 12 | Dockerfile de dos etapas, `.dockerignore`, configuración de datasource PostgreSQL y guía de Render/Supabase/webhook |
| 13 | Colección Postman 52 escenarios, 7 carpetas, dos entornos con `baseUrl` y README de verificación |

## Límites no resueltos

1. El código de Atiende v4 no está disponible. El paquete IA se reconstruyó desde los componentes y comportamiento descritos en el PDF; no se puede acreditar reutilización literal.
2. El Maven Wrapper oficial y `.github` original no están en la copia. Deben recuperarse desde el fork oficial; el taller prohíbe modificar el calificador.
3. No hay Maven instalado en el entorno. La compilación parcial con `javac` y stubs es una comprobación sintáctica, no sustituye `./mvnw test`.
4. El contexto Spring requiere MySQL encendido y no se ha levantado.
5. La IA y Stripe no se probaron con simuladores oficiales/CLI.
6. Supabase, Render, webhook de producción, URL real y Pull Request requieren acceso a las cuentas de la estudiante.
7. El PDF menciona 22 rutas en la comprobación Swagger, pero la tabla del contrato parece enumerar 25 operaciones; se conservó la tabla explícita.

## Estado de entrega
Código de fases 7–13 implementado en esta copia, con verificación sintáctica parcial y artefactos Postman/README creados. No declarar entrega 100 %, BUILD SUCCESS, despliegue activo ni tests aprobados hasta ejecutar los pasos externos pendientes.
