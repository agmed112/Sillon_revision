# Fase 12 — pendientes de cierre

## Código y contenedor
- [x] Dockerfile de dos etapas: Maven + Java 17 para compilar y JRE 17 para ejecutar.
- [x] Compilación Docker sin ejecutar tests, según paso 12.1.
- [x] `.dockerignore` excluye `target`, `.git` y `secrets.properties`.
- [x] `server.port=${PORT:8080}`.
- [x] Driver PostgreSQL runtime disponible.
- [x] Datasource configurable por `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
- [x] `URL_PUBLICA`, `JWT_SECRETO`, Stripe y Gemini configurables por entorno.
- [x] Valores por defecto de `STRIPE_URL_API` y `SILLON_IA_OPENAI_URL` conservados.

## Acciones externas que requieren las cuentas de la estudiante
- [ ] Crear proyecto Supabase.
- [ ] Copiar conexión **Session Pooler**, puerto 5432.
- [ ] Crear Web Service en Render conectado al fork oficial y elegir Docker.
- [ ] Configurar variables privadas enumeradas en `GUIA_RENDER_SUPABASE.md`.
- [ ] Crear el webhook de producción con `checkout.session.completed` y `customer.subscription.deleted`.
- [ ] Desplegar y probar Swagger, registro, login y pago de prueba.
- [ ] Reemplazar en README y entorno Postman el marcador de URL por la URL real de Render.

## Estado
La preparación de código está incorporada. No se declara desplegado ni probado: no se accedió a cuentas de Supabase, Render o Stripe y no se recibieron credenciales. No se inventa una URL de producción.
