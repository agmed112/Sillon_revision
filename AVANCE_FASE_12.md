# Fase 12 — Docker, Supabase y Render

**Estado:** artefactos de contenedor y configuración de variables están preparados; el despliegue depende de las cuentas de la estudiante.

## Paso 12.1 — Dockerfile

- `Dockerfile` en la raíz con dos etapas.
- Etapa de construcción `maven:3.9.9-eclipse-temurin-17`.
- Empaqueta con `mvn -B -DskipTests clean package`.
- Etapa final `eclipse-temurin:17-jre`, sin Maven ni fuentes.
- Ejecuta el JAR como usuario no root.
- Expone el puerto 8080; la aplicación usa `PORT` y 8080 por defecto.

## Paso 12.2 — Supabase

- La dependencia PostgreSQL está incluida en `pom.xml` con alcance runtime.
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` sobrescriben la conexión local MySQL.
- La guía exige usar Session Pooler de Supabase en puerto 5432; no se modifican entidades JPA.

## Paso 12.3 — Render

`application.properties` ya lee `PORT`, datasource, JWT, URL pública, Stripe, precios y Gemini desde variables de entorno.
La URL de producción no se inventa: se agrega al README después de que Render cree el servicio.

## Paso 12.4 — Webhook de producción

La guía de configuración incluye los dos eventos requeridos:
- `checkout.session.completed`
- `customer.subscription.deleted`

El secreto de ese destino se configura en `STRIPE_SECRETO_WEBHOOK`. En producción no se utiliza Stripe CLI.

## Pendiente

Crear el proyecto Supabase, crear el Web Service Docker en Render, introducir los valores privados, configurar el webhook de producción, desplegar y probar Swagger/registro/login/pago. No se ha accedido a las cuentas ni se ha hecho un despliegue desde esta intervención.
