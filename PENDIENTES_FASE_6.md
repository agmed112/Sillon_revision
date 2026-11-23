# Fase 6 — pendientes de cierre

## Código
- [x] Configuración tipada y `StripeClient` con `STRIPE_URL_API`.
- [x] Checkout PRO en modo suscripción.
- [x] Checkout de 200 créditos en modo pago único.
- [x] Selección de precio en backend; el cliente no envía importes ni IDs.
- [x] Correo, URLs de éxito/cancelación, referencia del salón y metadata.
- [x] Metadata duplicada en `subscription_data` para PRO.
- [x] Rechazo 409 de segunda suscripción PRO antes de llamar a Stripe.
- [x] Webhook con cuerpo de texto y verificación de `Stripe-Signature`.
- [x] Firma ausente/incorrecta y cuerpo inválido tratados como 400.
- [x] `eventos_stripe` con el ID de Stripe como clave primaria y campos `tipo` y `fecha_procesado`.
- [x] Idempotencia secuencial y protección ante entregas concurrentes.
- [x] Aplicación atómica de evento y cambios de salón en una transacción.
- [x] Bloqueo pesimista del salón al actualizar plan/créditos para evitar incrementos perdidos entre webhooks distintos.
- [x] Restricción única sobre `stripe_suscripcion_id`.
- [x] PRO suma 300 créditos y guarda suscripción.
- [x] Paquete suma 200 créditos.
- [x] Cancelación vuelve a GRATIS y conserva créditos.
- [x] Fallback `deserializeUnsafe()` con registro en log.
- [x] 26 métodos `@Test` escritos para el paquete de pagos.

## Acciones de cuenta (estudiante)
- [ ] Crear en Stripe modo prueba el producto Sillón PRO: 15 USD mensuales.
- [ ] Crear el paquete de 200 créditos: pago único de 5 USD.
- [ ] Copiar ambos IDs `price_...` a `secrets.properties` local.
- [ ] Configurar las llaves de prueba `STRIPE_CLAVE_SECRETA` y `STRIPE_SECRETO_WEBHOOK` localmente. No compartirlas ni subirlas.

## Verificación real
- [ ] Integrar el Maven Wrapper original del fork.
- [ ] Ejecutar `./mvnw test` o `mvnw.cmd test` y corregir errores reales.
- [ ] Ejecutar `stripe listen --forward-to localhost:8080/api/pagos/webhook`.
- [ ] Completar una compra PRO con la tarjeta de prueba 4242 4242 4242 4242.
- [ ] Verificar PRO +300 créditos y creación del tercer estilista.
- [ ] Pedir PRO otra vez y comprobar 409 sin crear sesión.
- [ ] Comprar el paquete y comprobar +200 créditos.
- [ ] Reenviar un evento y comprobar `procesado: false` sin cambio de créditos.
- [ ] Probar webhook sin firma y con firma falsa: 400.
- [ ] Probar compra con rol ESTILISTA: 403.
- [ ] Contrastar los resultados con el simulador oficial del calificador.

## Nota sobre la tabla de eventos
El modelo se alineó con el PDF: `id` es el ID de Stripe y es la clave primaria. Si se creó previamente una tabla local `eventos_stripe` con la estructura prototipo anterior (ID numérico artificial), recrear esa tabla solo en la base local desechable antes de volver a probar. No borrar datos de producción; allí se requiere una migración revisada y respaldo.

## Estado
La implementación de código está incorporada. La Fase 6 no se considera cerrada hasta completar las acciones y verificaciones anteriores.
