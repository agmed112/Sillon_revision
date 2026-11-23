# Guía de configuración — Supabase y Render

Esta guía no contiene valores reales ni credenciales. Los valores se copian desde las cuentas privadas de la estudiante.

## 1. Supabase

1. Crear un proyecto PostgreSQL en Supabase.
2. Abrir la sección de conexión y seleccionar **Session Pooler**.
3. Utilizar puerto `5432`; no utilizar la conexión directa IPv6 porque Render puede no alcanzarla.
4. Formar el valor JDBC con el host, puerto y base de datos que Supabase muestra:

   `jdbc:postgresql://HOST_DEL_SESSION_POOLER:5432/postgres`

5. Guardar por separado el usuario y la contraseña del Session Pooler.

## 2. Render

1. Crear un **Web Service** conectado al fork oficial.
2. Seleccionar **Docker** como entorno.
3. Configurar estas variables, copiando los valores privados de las cuentas:

   | Variable | Valor |
   |---|---|
   | `SPRING_DATASOURCE_URL` | URL JDBC del Session Pooler de Supabase |
   | `SPRING_DATASOURCE_USERNAME` | Usuario del Session Pooler |
   | `SPRING_DATASOURCE_PASSWORD` | Contraseña del Session Pooler |
   | `JWT_SECRETO` | Secreto aleatorio codificado en Base64, derivado de al menos 32 bytes |
   | `URL_PUBLICA` | URL HTTPS asignada por Render al Web Service |
   | `STRIPE_CLAVE_SECRETA` | Clave secreta de Stripe en modo prueba |
   | `STRIPE_PRECIO_PRO` | ID `price_...` del PRO mensual de 15 USD |
   | `STRIPE_PRECIO_CREDITOS` | ID `price_...` del paquete de 200 créditos por 5 USD |
   | `STRIPE_SECRETO_WEBHOOK` | Secreto del webhook de producción |
   | `GEMINI_LLAVE` | Llave privada de Gemini |

4. No definir `STRIPE_URL_API` ni `SILLON_IA_OPENAI_URL` en Render: el PDF indica conservar sus valores por defecto para usar Stripe y Gemini reales.

## 3. Webhook de producción en Stripe

1. En Stripe Dashboard, abrir Workbench → Webhooks.
2. Crear un destino que apunte a `https://TU-SERVICIO.onrender.com/api/pagos/webhook`.
3. Seleccionar exclusivamente los eventos solicitados por el taller:
   - `checkout.session.completed`
   - `customer.subscription.deleted`
4. Copiar el secreto de firma al valor privado de `STRIPE_SECRETO_WEBHOOK` en Render.
5. En producción no se utiliza Stripe CLI.

## 4. Comprobación después del despliegue

- Abrir `https://TU-SERVICIO.onrender.com/swagger-ui.html`.
- Registrar una cuenta y comprobar login.
- Autorizar Swagger con el JWT del dueño y probar `GET /api/salon`.
- Completar un pago de prueba y comprobar que el webhook actualiza el plan/créditos en Supabase.
- Reemplazar en README cualquier texto de ejemplo por la URL real de Render.

**Seguridad:** no pegar aquí, en README ni en GitHub contraseñas, tokens, llaves o `secrets.properties`.
