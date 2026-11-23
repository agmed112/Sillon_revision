# Pendientes de Fase 3

## Paso 3.1 — Servicios
- [x] Entidad y relación ManyToOne con Salon.
- [x] DTOs, mapper, servicio y controlador.
- [x] Rutas POST, GET y PUT con aislamiento por salonId.
- [x] Duración 15–480, nombre 80, descripción 300 y activo true por defecto.
- [x] Precio entero positivo en pesos colombianos; se rechazan fracciones y valores fuera del rango de Long.
- [ ] Revisión final del contrato y ejecución de pruebas reales en la etapa final.

## Paso 3.2 — Estilistas
- [x] ManyToMany Usuario–Servicio mediante `estilista_servicios`.
- [x] Método `Usuario.ofreceServicio(Long servicioId)`.
- [x] Alta y listado de estilistas por salón.
- [x] Validaciones en el orden decidido por la estudiante: límite 402, email 409, pertenencia de servicios 404.
- [x] El límite GRATIS se lee de `sillon.limite-estilistas-gratis`.
- [x] Password BCrypt y respuesta sin contraseña.
- [ ] Pruebas HTTP y persistencia JPA reales en la etapa final.

## Paso 3.3 — Clientes
- [x] Entidad `Cliente` con relación ManyToOne a Salon.
- [x] Restricción única compuesta por salón y teléfono (`uk_clientes_salon_telefono`).
- [x] Normalización del teléfono conservando solo dígitos y `+`.
- [x] Método `ServicioClientes.buscarOCrear`: busca por `salonId + telefono` normalizado y crea solo si no existe.
- [x] Listado de clientes solo lectura mediante GET `/api/clientes`; no existe endpoint POST independiente.
- [x] DTO `ClienteRespuesta`, mapper y consulta ordenada por nombre.
- [x] Pruebas unitarias de normalización y del flujo buscar/crear preparadas.
- [ ] Ejecutar las pruebas con Maven y verificar persistencia/índice único en MySQL en la etapa de ejecución final.

## Verificación general (se reserva para la revisión final)
- [ ] Compilación Maven y pruebas automatizadas con dependencias reales.
- [ ] Verificación del esquema JPA en MySQL.
- [ ] Colección Postman y comprobaciones oficiales.
