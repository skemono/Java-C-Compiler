package lexerGen;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lexerGen.util.ExprNode;

public class CodeGen {
    

    /**
     * Generates the source code for a Java lexer class (Yylex) based on the minimized DFA.
     * @param minimizer The minimized DFA containing the transition table and accepting states.
     * @param parser The YalParser instance containing header and trailer code.
     * @return A string containing the complete Java source code for the lexer.
     */
    public static String generate(Minimizer minimizer, YalParser parser, String className) {
        StringBuilder sb = new StringBuilder();

        // 1. Package and Header
        sb.append("package generated;\n\n");
        sb.append("import java.util.*;\n\n");
        sb.append(parser.getHeaderSection()).append("\n\n");

        // 2. Class Definition
        sb.append("public class ").append(className).append(" {\n\n");

        // 3. Member Variables
        sb.append("    private String input;\n");
        sb.append("    private int position = 0;\n");
        sb.append("    private int line = 1;\n");
        sb.append("    public String yytext;\n\n");

        // 4. Constructors
        // String constructor
        sb.append("    public ").append(className).append("(String input) {\n");
        sb.append("        this.input = input;\n");
        sb.append("    }\n\n");
        // File constructor — reads plain text file, as required by project spec
        sb.append("    public ").append(className).append("(java.io.File file) throws java.io.IOException {\n");
        sb.append("        this.input = new String(java.nio.file.Files.readAllBytes(file.toPath()));\n");
        sb.append("    }\n\n");

        // 5. Main yylex() — returns next token name, null at EOF, never throws
        // Note: className already set above, yylex/scan methods don't reference it
        sb.append("    public String yylex() {\n");
        sb.append("        while (true) {\n");
        sb.append("            if (position >= input.length()) return null;\n\n");
        sb.append("            int startPosition = position;\n");
        sb.append("            int currentState = ").append(minimizer.getStartId()).append(";\n");
        sb.append("            int lastAcceptingState = -1;\n");
        sb.append("            int lastMatchPosition = -1;\n\n");
        sb.append("            for (int i = startPosition; i < input.length(); i++) {\n");
        sb.append("                char c = input.charAt(i);\n");
        sb.append("                int nextState = getNextState(currentState, c);\n");
        sb.append("                if (nextState == -1) break;\n");
        sb.append("                currentState = nextState;\n");
        sb.append("                if (isAccepting(currentState)) {\n");
        sb.append("                    lastAcceptingState = currentState;\n");
        sb.append("                    lastMatchPosition = i + 1;\n");
        sb.append("                }\n");
        sb.append("            }\n\n");
        sb.append("            if (lastAcceptingState != -1) {\n");
        sb.append("                yytext = input.substring(startPosition, lastMatchPosition);\n");
        sb.append("                // Update line counter from consumed lexeme\n");
        sb.append("                for (int k = 0; k < yytext.length(); k++)\n");
        sb.append("                    if (yytext.charAt(k) == '\\n') line++;\n");
        sb.append("                position = lastMatchPosition;\n");
        sb.append("                if (isIgnoreState(lastAcceptingState)) continue;\n");
        sb.append("                return doAction(lastAcceptingState);\n");
        sb.append("            } else {\n");
        sb.append("                // Lexical error: print, skip bad char, continue — never stop\n");
        sb.append("                char bad = input.charAt(startPosition);\n");
        sb.append("                System.out.println(\"ERROR LEXICO: '\" + bad + \"' no reconocido en linea \" + line);\n");
        sb.append("                if (bad == '\\n') line++;\n");
        sb.append("                position = startPosition + 1;\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n\n");

        // 6. scan() — runs full input, prints TOKEN: [X, "lexeme"] for each token
        sb.append("    public void scan() {\n");
        sb.append("        String token;\n");
        sb.append("        while ((token = yylex()) != null) {\n");
        sb.append("            System.out.println(\"TOKEN: [\" + token + \", \\\"\" + yytext + \"\\\"]\");\n");
        sb.append("        }\n");
        sb.append("    }\n\n");

        // 7. getLine() accessor
        sb.append("    public int getLine() { return line; }\n\n");

        // 8. Helper methods
        generateGetNextState(sb, minimizer);
        generateIsAccepting(sb, minimizer);
        generateIsIgnoreState(sb, minimizer);
        generateDoAction(sb, minimizer);

        // 9. Trailer and closing brace
        sb.append(parser.getTrailerSection()).append("\n\n");
        sb.append("}\n");

        return sb.toString();
    }

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
            }
            sb.append("                return -1;\n");
            sb.append("            }\n");
        }

        sb.append("            default: return -1;\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }

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

    private static void generateDoAction(StringBuilder sb, Minimizer minimizer) {
        sb.append("    private String doAction(int state) {\n");
        sb.append("        switch (state) {\n");
        for (Map.Entry<Integer, String> entry : minimizer.getAccepting().entrySet()) {
            if (!entry.getValue().trim().isEmpty()) {
                sb.append("            case ").append(entry.getKey()).append(": {\n");
                String action = entry.getValue().trim();

                // Normalize 'return TOKEN' -> return "TOKEN"
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("^return\\s+([a-zA-Z_][a-zA-Z0-9_]*)$");
                java.util.regex.Matcher m = p.matcher(action);
                if (m.matches()) {
                    action = "return \"" + m.group(1) + "\";";
                } else if (!action.endsWith(";") && !action.endsWith("}")) {
                    action += ";";
                }
                sb.append("                ").append(action).append("\n");
                sb.append("            }\n");
            }
        }
        sb.append("            default: return null;\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }

    // ─── GRAPHVIZ EXPRESSION TREE ────────────────────────────────────────────

    /**
     * Generates a .dot file with one subgraph per rule showing the expression tree,
     * then calls Graphviz to render it as a .png.
     *
     * @param rules      list of {expandedRegex, tokenName}
     * @param nfa        NFA instance (used to tokenize + postfix each regex)
     * @param outputDir  directory where .dot and .png are written
     */
    public static void generateGraphviz(List<String[]> rules, NFA nfa, String outputDir) {
        try {
            Files.createDirectories(Paths.get(outputDir));
            ExprNode.resetCounter();

            StringBuilder dot = new StringBuilder();
            dot.append("digraph ExpressionTrees {\n");
            dot.append("    graph [rankdir=TB, fontname=\"Helvetica\"];\n");
            dot.append("    node  [shape=circle, fontname=\"Helvetica\", style=filled, fillcolor=lightblue];\n");
            dot.append("    edge  [fontname=\"Helvetica\"];\n\n");

            for (int i = 0; i < rules.size(); i++) {
                String regex     = rules.get(i)[0];
                String tokenName = rules.get(i)[1];

                // Build postfix then expression tree
                List<lexerGen.util.RegexToken> tokens  = nfa.tokenize(regex);
                List<lexerGen.util.RegexToken> concated = nfa.insertConcat(tokens);
                List<lexerGen.util.RegexToken> postfix  = nfa.toPostfix(concated);
                ExprNode root = nfa.buildExprTree(postfix);

                if (root == null) continue;

                dot.append("    subgraph cluster_").append(i).append(" {\n");
                dot.append("        label=\"").append(escapeDotLabel(tokenName)).append("\";\n");
                dot.append("        style=rounded;\n");
                dot.append("        color=gray;\n");

                // Emit nodes and edges via DFS
                emitNodes(root, dot);
                emitEdges(root, dot);

                dot.append("    }\n\n");
            }

            dot.append("}\n");

            // Write .dot file
            String dotPath = outputDir + "/expression_tree.dot";
            String pngPath = outputDir + "/expression_tree.png";
            Files.write(Paths.get(dotPath), dot.toString().getBytes());
            System.out.println("  [OK] DOT file written: " + Paths.get(dotPath).toAbsolutePath());

            // Call Graphviz (dot must be installed and in PATH)
            try {
                Process p = Runtime.getRuntime().exec(
                        new String[]{"dot", "-Tpng", dotPath, "-o", pngPath});
                int exitCode = p.waitFor();
                if (exitCode == 0) {
                    System.out.println("  [OK] PNG rendered:  " + Paths.get(pngPath).toAbsolutePath());
                } else {
                    // Print stderr from dot for diagnosis
                    String err = new String(p.getErrorStream().readAllBytes());
                    System.out.println("  [WARN] Graphviz exited with code " + exitCode + ": " + err.trim());
                    System.out.println("         .dot file is available for manual rendering.");
                }
            } catch (IOException e) {
                System.out.println("  [WARN] Graphviz not found in PATH. .dot file written but not rendered.");
                System.out.println("         Install Graphviz and run: dot -Tpng " + dotPath + " -o " + pngPath);
            }

        } catch (Exception e) {
            System.out.println("  [ERROR] generateGraphviz: " + e.getMessage());
        }
    }

    /** DFS: emit one DOT node declaration per ExprNode. */
    private static void emitNodes(ExprNode node, StringBuilder sb) {
        if (node == null) return;
        String shape = (node.left == null) ? "shape=rectangle, fillcolor=lightyellow" : "shape=circle, fillcolor=lightblue";
        sb.append("        n").append(node.id)
          .append(" [label=\"").append(escapeDotLabel(node.label)).append("\", ").append(shape).append("];\n");
        emitNodes(node.left,  sb);
        emitNodes(node.right, sb);
    }

    /** DFS: emit one DOT edge per parent→child relationship. */
    private static void emitEdges(ExprNode node, StringBuilder sb) {
        if (node == null) return;
        if (node.left != null) {
            sb.append("        n").append(node.id).append(" -> n").append(node.left.id).append(";\n");
            emitEdges(node.left, sb);
        }
        if (node.right != null) {
            sb.append("        n").append(node.id).append(" -> n").append(node.right.id).append(";\n");
            emitEdges(node.right, sb);
        }
    }

    /** Escapes special characters for DOT label strings. */
    private static String escapeDotLabel(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("<", "\\<")
                .replace(">", "\\>")
                .replace("{", "\\{")
                .replace("}", "\\}");
    }

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
