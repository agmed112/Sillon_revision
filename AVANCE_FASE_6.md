# Fase 6 — Pagos con Stripe

**Estado del código:** implementación estructural completa según los pasos 6.1–6.3.
**Estado de la fase:** todavía no cerrada; faltan las acciones de la cuenta Stripe y las verificaciones reales del paso 6.4.

## Paso 6.1 — Productos y precios (acción de la estudiante)

El taller pide crear en Stripe modo prueba:
- Sillón PRO: precio recurrente mensual de 15 USD.
- Paquete de 200 créditos de IA: pago único de 5 USD.

Los identificadores `price_...` se leen desde `STRIPE_PRECIO_PRO` y `STRIPE_PRECIO_CREDITOS`. No se inventaron IDs ni credenciales.

## Paso 6.2 — Checkout implementado

- `PropiedadesStripe` agrupa la configuración tipada bajo `sillon.stripe`.
- `ConfiguracionStripe` construye un `StripeClient` con `STRIPE_CLAVE_SECRETA` y `STRIPE_URL_API` mediante `setApiKey` y `setApiBase`.
- `ServicioPagos` elige en el backend el ID de precio y el modo: `SUBSCRIPTION` para `SUSCRIPCION_PRO` y `PAYMENT` para `PAQUETE_CREDITOS`.
- El dueño se identifica desde el principal JWT; el correo se envía a Checkout.
- La sesión incluye URL de éxito y cancelación bajo `/api/pagos/resultado`, referencia del salón y metadata `salonId` / `tipoCompra`.
- Para PRO, la misma metadata se copia a `subscription_data`.
- Si el salón ya está en PRO, se lanza 409 antes de llamar a Stripe.
- No se aceptan precio ni `price_...` desde el cuerpo del cliente.

## Paso 6.3 — Webhook implementado

- El controlador recibe el cuerpo como texto y la cabecera `Stripe-Signature`.
- Firma ausente, firma falsa o cuerpo inválido se traducen a HTTP 400.
- El ID del evento de Stripe es ahora la clave primaria de `eventos_stripe`, con los campos `id`, `tipo` y `fecha_procesado`, conforme al modelo de datos del PDF.
- La verificación `existsById` evita volver a aplicar un evento ya registrado.
- La inserción del evento y la modificación de plan/créditos ocurren dentro de la misma transacción `@Transactional`.
- Las compras y cancelaciones usan lectura con bloqueo pesimista del salón antes de modificar el saldo o el plan; esto evita perder incrementos cuando llegan webhooks distintos en paralelo.
- `stripe_suscripcion_id` tiene restricción única en la tabla de salones, ya que una suscripción de Stripe no debe quedar asociada a dos salones.
- La clave primaria también protege entregas simultáneas. Si una inserción concurrente choca, la transacción perdedora se revierte; después se confirma si la otra transacción ya registró el evento y se responde `recibido: true, procesado: false`.
- `checkout.session.completed` solo aplica una compra cuando `payment_status` es `paid`.
- PRO cambia el plan a `PRO`, guarda el ID de suscripción y suma 300 créditos.
- El paquete suma 200 créditos sin cambiar el plan.
- `customer.subscription.deleted` devuelve el plan a `GRATIS` y conserva los créditos.
- Si un Checkout pagado carece de metadata válida o referencia un salón inexistente, se genera 502 dentro de la transacción: ni el evento ni los cambios se confirman y Stripe puede reintentar.
- Si la deserialización segura no entrega objeto, se registra el caso en el log y se intenta `deserializeUnsafe()`, como pide el PDF.

## Pruebas JUnit añadidas (no ejecutadas con Maven real)

Hay 26 métodos `@Test` en el paquete de pagos:
- Checkout PRO: modo, precio backend, correo, URLs, referencia y metadata en sesión/suscripción.
- Paquete: modo de pago único, precio backend y metadata.
- Segunda suscripción PRO: 409 antes de contactar Stripe.
- Fallo de Stripe y respuesta incompleta: 502.
- Webhook sin firma y con firma incorrecta: 400.
- Resultado de pago: acepta `exito` y `cancelado`.
- Webhook PRO, paquete, evento repetido, cancelación, pago no confirmado y eventos no contemplados.
- Metadata inválida, salón inexistente y fallback `deserializeUnsafe()`.
- Consulta por ID del evento y entrega concurrente duplicada.

## Decisiones que no se agregaron

- El PDF no pide cupones/descuentos de Stripe en la Fase 6; no se añadió un sistema de cupones.
- La palabra `cupon` aparece en la regla anti-promesas del chatbot de la Fase 9. Allí el sistema debe impedir prometer cupones, descuentos, regalos, promociones o reembolsos.
- Todo se mantiene en modo prueba; no se implementó cobro en modo live ni Stripe Connect.

## Pendiente para cerrar la fase

1. Crear los dos productos/precios de prueba en la cuenta Stripe de la estudiante.
2. Completar `secrets.properties` local con las llaves y `price_...` reales de prueba, sin subirlo al repositorio.
3. Integrar el Maven Wrapper original y ejecutar JUnit real.
4. Ejecutar Stripe CLI y comprobar compra PRO, paquete, cancelación, repetición, firma falsa y rol ESTILISTA.
5. Comprobar contra el simulador oficial del calificador.

**No se declara BUILD SUCCESS ni integración real probada**, porque este entorno no tiene Maven ni el Maven Wrapper original y no se ha conectado a Stripe.
