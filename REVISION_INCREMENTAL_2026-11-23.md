# Revisión incremental — 23 de noviembre de 2026

## Qué se hizo
- Se verificó el entorno: hay Java 21 y se instaló Maven 3.8.7, pero **Maven Central no es accesible desde el entorno de revisión**, por lo que `mvn compile/test` no pudo ejecutarse. Sigue sin afirmarse `BUILD SUCCESS` ni pruebas aprobadas.
- Revisión sintáctica de los 130+ archivos Java con `javac`: sin errores de sintaxis (no equivale a compilar con dependencias).
- Se eliminaron 23 archivos `.class` que estaban dentro de `src/main/java` y se añadió `*.class` a `.gitignore`.
- Se actualizaron los modelos de IA por defecto, porque `gemini-2.5-flash` tenía fecha de cierre el 16-oct-2026 (ver https://ai.google.dev/gemini-api/docs/deprecations) y `claude-3-5-haiku-latest` era un modelo antiguo:
  - `GEMINI_MODELO`: `gemini-2.5-flash` -> `gemini-3.5-flash`
  - `CLAUDE_MODELO`: `claude-3-5-haiku-latest` -> `claude-haiku-5-5`
  - Ambos siguen siendo configurables por variable de entorno. Confirmar que responden con las llaves reales.
- Revisión de lógica de agenda, webhook de Stripe y reglas de seguridad: sin defectos encontrados.

## Cifras verificadas
- 25 archivos de prueba, 118 métodos `@Test`.
- 25 combinaciones método/ruta en los controladores (coincide con la tabla del README).
- Postman: 7 carpetas, 52 escenarios, dos entornos con la única variable `baseUrl`.

## Riesgo a vigilar
- `application.properties` trae un `JWT_SECRETO` por defecto de relleno. En Render **debe** definirse `JWT_SECRETO` propio; de lo contrario los tokens serían falsificables.

## Sigue pendiente (requiere cuentas/credenciales o ejecución local)
Ejecutar `./mvnw test` con MySQL; probar Stripe CLI; probar Gemini/Claude reales; desplegar en Supabase/Render y obtener la URL; abrir Swagger con token; ejecutar Postman local y en producción; reflexión personal de escalabilidad; Pull Request oficial.
