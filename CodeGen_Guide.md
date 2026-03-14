# Guía: CodeGen.java — Generación de Código Java

## Antes de empezar

Este archivo se encarga del último paso de tu compilador: la **Generación de Código**. 

Toma el DFA minimizado (que tiene la lógica de cómo reconocer los tokens) y el `YalParser` (que tiene el código literal del usuario, como imports y acciones) y los une para escupir un `String`. Este String es literalmente el código fuente de tu nuevo escáner léxico (`Yylex.java`), el cual compilarás y usarás en la vida real.

La salida de esta clase implementa el principio de **"Maximal Munch"** (mordida máxima): el lexer siempre intentará consumir la mayor cantidad de caracteres posibles que formen un token válido.

---

## Bloque 1 — Package e imports

```java
package lexerGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
```

- `package lexerGen` — El paquete donde reside tu generador.
- `import java.util.*` — Necesitas estas colecciones para agrupar caracteres y procesar las transiciones del autómata de forma eficiente antes de escribirlas como código Java.

---

## Bloque 2 — Método principal `generate` y el esqueleto de la clase

```java
public class CodeGen {

    public static String generate(Minimizer minimizer, YalParser parser) {
        StringBuilder sb = new StringBuilder();

        // 1. Package and Header
        sb.append("package generated;\n\n");
        sb.append("import java.util.*;\n\n");
        sb.append(parser.getHeaderSection()).append("\n\n");

        // 2. Class Definition
        sb.append("public class Yylex {\n\n");

        // 3. Member Variables
        sb.append("    private String input;\n");
        sb.append("    private int position = 0;\n");
        sb.append("    public String yytext;\n\n");

        // 4. Constructor
        sb.append("    public Yylex(String input) {\n");
        sb.append("        this.input = input;\n");
        sb.append("    }\n\n");

        // ... (continuación en el siguiente bloque)
```

Este bloque prepara el "esqueleto" de tu clase generada. 
**Puntos clave:**
- Crea la clase dentro de un paquete llamado `generated`.
- Inyecta el `Header` que el usuario definió en el `.yal` (por ejemplo, si el usuario incluyó sus propios imports).
- Define las variables de estado del lexer: `input` (el texto a escanear), `position` (dónde vamos en el texto) y `yytext` (el lexema encontrado).

---

## Bloque 3 — El corazón del Lexer: Método `yylex()`

```java
        // 5. Main yylex() method
        sb.append("    public Object yylex() throws Exception {\n");
        sb.append("        while (true) {\n");
        sb.append("            if (position >= input.length()) {\n");
        sb.append("                return null; // End of input\n");
        sb.append("            }\n\n");
        sb.append("            int startPosition = position;\n");
        sb.append("            int currentState = ").append(minimizer.getStartId()).append(";\n");
        sb.append("            int lastAcceptingState = -1;\n");
        sb.append("            int lastMatchPosition = -1;\n\n");
        sb.append("            for (int i = startPosition; i < input.length(); i++) {\n");
        sb.append("                char c = input.charAt(i);\n");
        sb.append("                int nextState = getNextState(currentState, c);\n");
        sb.append("                if (nextState == -1) { break; }\n");
        sb.append("                currentState = nextState;\n");
        sb.append("                if (isAccepting(currentState)) {\n");
        sb.append("                    lastAcceptingState = currentState;\n");
        sb.append("                    lastMatchPosition = i + 1;\n");
        sb.append("                }\n");
        sb.append("            }\n\n");
        sb.append("            if (lastAcceptingState != -1) {\n");
        sb.append("                yytext = input.substring(startPosition, lastMatchPosition);\n");
        sb.append("                position = lastMatchPosition;\n");
        sb.append("                if (isIgnoreState(lastAcceptingState)) {\n");
        sb.append("                    continue; // Ignore token and restart loop for next token\n");
        sb.append("                }\n");
        sb.append("                return doAction(lastAcceptingState);\n");
        sb.append("            } else {\n");
        sb.append("                yytext = input.substring(startPosition, Math.min(startPosition + 1, input.length()));\n");
        sb.append("                position = startPosition + 1;\n");
        sb.append("                throw new Exception(\"Invalid character: '\" + yytext + \"' at position \" + startPosition);\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
```

Aquí se escribe la lógica pura del escáner en el archivo final. 
**Cómo funciona (Maximal Munch):**
1. Empieza en el estado inicial del DFA.
2. Lee caracteres uno por uno. Cada vez que llega a un estado de aceptación, "guarda el progreso" en `lastAcceptingState` y `lastMatchPosition`.
3. Sigue leyendo hasta que el DFA se "traba" (retorna `-1`).
4. En lugar de fallar, retrocede hasta el último estado de aceptación que guardó. Ese es tu token ganador.
5. Extrae el lexema a la variable `yytext`, actualiza la posición, ignora si es un espacio en blanco, o ejecuta la acción asociada (ej. `return ID`).

---

## Bloque 4 — Cerrando el generador principal

```java
        // 6. Helper methods
        generateGetNextState(sb, minimizer);
        generateIsAccepting(sb, minimizer);
        generateIsIgnoreState(sb, minimizer);
        generateDoAction(sb, minimizer);

        // 7. Trailer and closing brace
        sb.append(parser.getTrailerSection()).append("\n\n");
        sb.append("}\n");

        return sb.toString();
    }
```
Llama a métodos auxiliares para inyectar las tablas gigantes del DFA y finaliza pegando el `Trailer` (código extra que el usuario puso al final del `.yal`) y cerrando la clase con `}`.

---

## Bloque 5 — Generación de Transiciones (`getNextState`)

```java
    private static void generateGetNextState(StringBuilder sb, Minimizer minimizer) {
        sb.append("    private int getNextState(int state, char c) {\n");
        sb.append("        switch (state) {\n");

        Map<Integer, Map<Character, Integer>> transitions = minimizer.getTransitions();
        for (int stateId = 0; stateId < minimizer.getStateCount(); stateId++) {
            sb.append("            case ").append(stateId).append(": {\n");
            Map<Character, Integer> stateTransitions = transitions.get(stateId);
            if (stateTransitions != null && !stateTransitions.isEmpty()) {
                // Group characters by their target state to generate more efficient code
                Map<Integer, List<Character>> targetToChars = new HashMap<>();
                for (Map.Entry<Character, Integer> entry : stateTransitions.entrySet()) {
                    targetToChars.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
                }

                // For each target state, generate conditions for the characters that lead to it
                for (Map.Entry<Integer, List<Character>> entry : targetToChars.entrySet()) {
                    int targetState = entry.getKey();
                    List<Character> chars = entry.getValue();
                    java.util.Collections.sort(chars);

                    if (!chars.isEmpty()) {
                        sb.append("                if (");
                        boolean firstCondition = true;
                        int i = 0;
                        while (i < chars.size()) {
                            char rangeStart = chars.get(i);
                            int j = i;
                            while (j + 1 < chars.size() && chars.get(j + 1) == chars.get(j) + 1) {
                                j++;
                            }
                            char rangeEnd = chars.get(j);

                            if (!firstCondition) {
                                sb.append(" || ");
                            }

                            if (rangeStart == rangeEnd) {
                                sb.append("c == '").append(escapeChar(rangeStart)).append("'");
                            } else {
                                sb.append("(c >= '").append(escapeChar(rangeStart)).append("' && c <= '").append(escapeChar(rangeEnd)).append("')");
                            }
                            firstCondition = false;
                            i = j + 1;
                        }
                        sb.append(") return ").append(targetState).append(";\n");
                    }
                }
                sb.append("                return -1; // No transition for this character\n");
            }
            sb.append("            }\n");
        }

        sb.append("            default: return -1;\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }
```
**Esta es la magia de la optimización:**
En lugar de escribir un `if` para cada letra del abecedario, este código agrupa rangos continuos. Si un estado transiciona al estado 5 con las letras de la `a` a la `z`, genera un solo bloque: `if (c >= 'a' && c <= 'z') return 5;`. Esto hace que el código final sea super limpio, legible y rápido de ejecutar.

---

## Bloque 6 — Identificando estados especiales

```java
    private static void generateIsAccepting(StringBuilder sb, Minimizer minimizer) {
        sb.append("    private boolean isAccepting(int state) {\n");
        sb.append("        switch (state) {\n");
        for (int id : minimizer.getAccepting().keySet()) {
            sb.append("            case ").append(id).append(": return true;\n");
        }
        sb.append("            default: return false;\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }

    private static void generateIsIgnoreState(StringBuilder sb, Minimizer minimizer) {
        sb.append("    private boolean isIgnoreState(int state) {\n");
        sb.append("        switch (state) {\n");
        for (Map.Entry<Integer, String> entry : minimizer.getAccepting().entrySet()) {
            if (entry.getValue().trim().isEmpty()) {
                sb.append("            case ").append(entry.getKey()).append(": return true;\n");
            }
        }
        sb.append("            default: return false;\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }
```
Estos son booleanos simples:
- `isAccepting`: Devuelve `true` si el estado en el que estamos forma un token válido.
- `isIgnoreState`: Sirve para cosas como el `whitespace`. Si la acción en el `.yal` está vacía (sin código de return), asumimos que es un token que se debe consumir pero **ignorar** (para no ahogar al parser con espacios y saltos de línea).

---

## Bloque 7 — Inyectando el código del usuario (`doAction`)

```java
    private static void generateDoAction(StringBuilder sb, Minimizer minimizer) {
        sb.append("    private Object doAction(int state) throws Exception {\n");
        sb.append("        switch (state) {\n");
        for (Map.Entry<Integer, String> entry : minimizer.getAccepting().entrySet()) {
            if (!entry.getValue().trim().isEmpty()) {
                sb.append("            case ").append(entry.getKey()).append(": {\n");
                String action = entry.getValue();
                String trimmedAction = action.trim();

                // Heuristic to fix common action patterns like "return TOKEN"
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("^return\\s+([a-zA-Z_][a-zA-Z0-9_]*)$");
                java.util.regex.Matcher m = p.matcher(trimmedAction);
                if (m.matches()) {
                    // It's a simple 'return TOKEN', convert to 'return "TOKEN";' which TestLexer expects.
                    action = "return \"" + m.group(1) + "\";";
                } else if (!trimmedAction.endsWith(";") && !trimmedAction.endsWith("}") && !trimmedAction.endsWith("{")) {
                    // For other actions, just ensure it ends with a semicolon if it's a simple statement.
                    action += ";";
                }
                sb.append("                ").append(action).append("\n");
                sb.append("            }\n");
            }
        }
        sb.append("            default: throw new Exception(\"Internal lexer error: No action for state \" + state);\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }
```
Este método pega el bloque de código `{ ... }` del archivo `.yal` en el Java resultante.
Contiene una heurística muy inteligente: si el usuario pone algo inválido en Java como `{ return NUMBER }` (sin comillas y sin punto y coma), esta función detecta que te refieres a un string y lo convierte dinámicamente a `return "NUMBER";`. Así asegura que el código generado siempre recompile.

---

## Bloque 8 — Método `escapeChar`

```java
    private static String escapeChar(char c) {
        return switch (c) {
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case '\'' -> "\\'";
            case '\\' -> "\\\\";
            default -> (c < 32 || c > 126) ? String.format("\\u%04x", (int) c) : String.valueOf(c);
        };
    }
}
```
Como estamos escribiendo código fuente Java como si fuera un texto, caracteres como el "salto de línea" literalmente romperían el String generado. Este método los convierte a su representación visual de escape (ej. convierte el salto de línea real en los caracteres visuales `\` y `n`).

---

## ¿Qué verificar al terminar?

Cuando corras `App.java` y abras la carpeta `generated/`:
1. Asegúrate de que existe un archivo llamado `Yylex.java`.
2. Al abrirlo, el código de los `switch` cases debe verse muy limpio (con condiciones cortas unidas por `||`).
3. El archivo debe **compilar a la primera** si ejecutas `javac generated/Yylex.java`. No debería haber errores de que "falta punto y coma" o "símbolo no encontrado".
4. Al correr `TestLexer.java` usando la clase generada, el scanner léxico debe ignorar el espacio blanco pero detectar correctamente números, signos y palabras reservadas usando un loop infinito hasta llegar al final del string.

---

### Comandos para compilar y probar

Si alguna vez modificas el generador y tienes problemas de compilación porque `TestLexer` intenta leer un `Yylex.java` roto o viejo, sigue esta secuencia exacta de comandos en tu terminal (desde la raíz de tu proyecto `Java-C-Compiler`):

**1. Eliminar la carpeta generada vieja:**
```bash
rm -rf generated
```
**2. Compilar SOLO el generador (ignorando los tests por ahora):**
```bash
javac -d bin src/App.java src/lexerGen/*.java src/lexerGen/util/*.java
```
**3. Ejecutar el generador (esto crea el nuevo `Yylex.java` corregido):**
```bash
java -cp bin App
```
**4. Compilar el programa de prueba y el lexer recién generado:**
```bash
javac -d bin -cp src src/TestLexer.java generated/Yylex.java
```
**5. Ejecutar la prueba final:**
```bash
java -cp bin TestLexer
```
