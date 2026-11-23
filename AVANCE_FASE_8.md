# Fase 8 — Recordatorio con IA

**Estado:** flujo de código implementado; prueba de proveedor real/simulador pendiente.

## Implementado
- `ServicioCitas.obtenerDatosRecordatorio` busca la cita dentro del salón autenticado y solo admite estados PENDIENTE o CONFIRMADA; los demás producen 409.
- Java prepara los seis datos verificados: servicio y duración, estilista, fecha en español de Colombia, hora normalizada sin espacios invisibles, valor y dirección.
- `src/main/resources/prompts/recordatorio.txt` usa las variables `salon`, `cliente` y `datos`.
- El prompt solicita un borrador de WhatsApp para revisión humana, sin envío automático, máximo cuatro frases y JSON con la clave exacta `mensaje`.
- `ServicioRecordatorios` respeta el orden: validar cita, verificar créditos, cargar prompt, llamar IA, consumir crédito y validar JSON.
- Mensaje vacío, JSON inválido o mensaje mayor de 700 caracteres se traduce a 502.
- La respuesta incluye `citaId`, `telefonoCliente`, `mensaje`, `creditosRestantes` y `factura`.
- `POST /api/citas/{id}/recordatorio` queda restringido a DUENO.

## Verificación
- Se añadieron pruebas para asegurar que una cita no elegible se rechaza antes de verificar créditos o llamar a IA, y que un mensaje superior a 700 caracteres se rechaza con 502 después de consumir el crédito, conforme al orden del PDF.
## Verificación
Hay pruebas unitarias escritas para respuesta válida, orden de consumo y JSON inválido. El subconjunto de producción y pruebas compila sintácticamente con `javac --release 17` y stubs temporales. No se ejecutó Maven/JUnit real ni la IA simulada oficial.
