# Revisión incremental de Sillón — 19 de noviembre de 2026

## Alcance
Se continuó desde `Sillon_fases_7_a_13_cierre.zip`, contrastando el código con el PDF oficial `Sillon-Proyecto-Integrador-Modulos-4-y-5.pdf`, especialmente los pasos 0.2, 7.1, 7.2, 8, 9, 10, 11, 12 y 13.

## Correcciones aplicadas

### 1. Jackson 3 y Spring Boot 4
El PDF establece en la página 15 que Spring Boot 4 utiliza Jackson 3 y que `ObjectMapper`/`JsonNode` están en `tools.jackson.databind`. Se corrigieron los imports incompatibles que permanecían en:

- `ProveedorOpenAi.java`
- `ProveedorClaude.java`
- `LectorJsonIa.java`
- `LectorJsonIaTest.java`

En `LectorJsonIa` se sustituyó `JsonProcessingException` por `tools.jackson.core.JacksonException`, que es el tipo base de excepciones de Jackson 3.

### 2. Proveedores IA sin credenciales reales
`application.properties` utiliza valores de relleno cuando no hay llaves configuradas. El proveedor Gemini antes interpretaba `gemini_placeholder` como una llave no vacía y trataba de realizar una llamada HTTP con ella. Ahora Gemini reconoce los valores que contienen `placeholder` o son exactamente `reemplazar` (como aparece en la plantilla de secretos) como configuración ausente y lanza `ProveedorNoDisponibleException` sin abrir una conexión.

El proveedor Claude ahora normaliza la comprobación del placeholder sin distinguir mayúsculas, también reconoce el valor `reemplazar` y valida que la URL no esté vacía. Esto permite que el asistente registre el fallo de configuración de ambos proveedores y responda con el 503 previsto, sin intentar llamadas con credenciales ficticias.

### 3. Parámetros visibles en Swagger
Se configuró `maven-compiler-plugin` con `<parameters>true</parameters>` para conservar los nombres de parámetros Java en los metadatos de compilación. Esto ayuda a que Springdoc documente correctamente nombres de parámetros de ruta y consulta; la documentación oficial de Springdoc indica este ajuste cuando los nombres de parámetros no aparecen en el OpenAPI generado.

### 4. Pruebas nuevas
Se agregaron cuatro casos de prueba:

- `ProveedorOpenAiTest`: verifica que Gemini no intente conectarse con `gemini_placeholder` ni con `reemplazar`.
- `ProveedorClaudeTest`: verifica que Claude no intente conectarse con `claude_placeholder` ni con `reemplazar`.

El inventario actual queda en 25 archivos `*Test.java`, 106 métodos `@Test` y 2 métodos parametrizados con 11 filas: 117 invocaciones previstas. Son pruebas escritas; no se declaran aprobadas porque no se ejecutó JUnit real.

## Verificaciones realizadas

- No quedan imports de `com.fasterxml.jackson.databind` ni `com.fasterxml.jackson.core.JsonProcessingException` en `src/main/java` o `src/test/java`.
- Los componentes de producción afectados (`ProveedorOpenAi`, `ProveedorClaude`, `LectorJsonIa` y `AsistenteIa`) compilaron con `javac --release 17` contra stubs temporales de las APIs externas. Esta comprobación confirma sintaxis y compatibilidad con las firmas modeladas, no sustituye una compilación Maven con dependencias reales.
- Los tres JSON de Postman se parsearon correctamente.
- Se revisaron las 52 peticiones de la colección: 7 carpetas, cada petición tiene script Post-response y los scripts de pre-request/post-response pasaron `node --check`.
- Cada entorno Postman tiene una sola variable, `baseUrl`.
- `pom.xml` sigue siendo XML válido y conserva la configuración explícita de nombres de parámetros para OpenAPI.

## Bloqueos que permanecen

1. No hay Maven instalado, Maven Wrapper ni dependencias locales; además, este entorno no resuelve Maven Central. No fue posible ejecutar `mvn test` ni obtener `BUILD SUCCESS`.
2. No se encendió MySQL ni se ejecutó `ContextoAplicacionTest`.
3. No se ejecutó Docker; el contenedor no está instalado en este entorno.
4. No se probaron Gemini/Claude con el simulador oficial, ni Stripe CLI, ni Postman Runner contra una API levantada.
5. No se proporcionó Atiende v4; por eso la implementación IA es compatible con el contrato visible, pero no puede acreditarse como reutilización literal del paquete original.
6. No se proporcionó el fork oficial ni acceso a cuentas de Supabase, Render o Stripe; no se puede hacer el despliegue ni abrir el Pull Request desde esta copia.

## Resultado
Se corrigió un problema de imports que podía impedir la compilación con Spring Boot 4/Jackson 3 y se mejoró el comportamiento de configuración de proveedores IA sin llaves. La siguiente verificación decisiva sigue siendo ejecutar el Maven Wrapper oficial en el proyecto local con MySQL disponible.
