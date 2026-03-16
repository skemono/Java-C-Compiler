package lexerGen.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Stack;

public class Tree {

    public static class Node {
        public String value;
        public Node left;
        public Node right;
        public int id; // Necesario para identificar nodos únicos en Graphviz

        public Node(String value, Node left, Node right, int id) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.id = id;
        }
    }

    private Node root;
    private int nodeCounter = 0;

    /**
     * Construye el AST (Abstract Syntax Tree) a partir de una lista en notación Postfix.
     * @param postfixTokens Tu lista de tokens postfix generada por el algoritmo de Shunting Yard.
     */
    public void buildFromPostfix(List<RegexToken> postfixTokens) {
        Stack<Node> stack = new Stack<>();
        nodeCounter = 0;

        for (RegexToken token : postfixTokens) {
            String val = formatTokenValue(token);

            switch (token.type) {
                // Operadores unarios (1 hijo)
                case STAR:
                case PLUS:
                case QUESTION:
                    if (!stack.isEmpty()) {
                        Node child = stack.pop();
                        stack.push(new Node(val, child, null, nodeCounter++));
                    }
                    break;
                // Operadores binarios (2 hijos)
                case CONCAT:
                case UNION:
                case DIFF:
                    if (stack.size() >= 2) {
                        Node right = stack.pop();
                        Node left = stack.pop();
                        stack.push(new Node(val, left, right, nodeCounter++));
                    }
                    break;
                // Operandos (hojas)
                default:
                    stack.push(new Node(val, null, null, nodeCounter++));
                    break;
            }
        }

        if (!stack.isEmpty()) {
            root = stack.pop();
        }
    }

    // Formatea el valor del token para que se vea bien en el gráfico
    private String formatTokenValue(RegexToken token) {
        if (token.type == RegexToken.Type.CHAR) {
            if (token.singleChar == '\n') return "\\\\n";
            if (token.singleChar == '\t') return "\\\\t";
            if (token.singleChar == '\r') return "\\\\r";
            if (token.singleChar == '"') return "\\\"";
            if (token.singleChar == '\\') return "\\\\";
            return String.valueOf(token.singleChar);
        } else if (token.type == RegexToken.Type.CLASS) {
            String comp = token.isComplement ? "^" : "";
            return "[" + comp + "...]"; // Simplificado para no hacer nodos gigantes
        } else {
            return token.type.toString();
        }
    }

    /**
     * Genera un archivo .dot para visualizar el árbol con Graphviz.
     */
    public void generateDotFile(String filename) {
        if (root == null) return;

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("digraph AST {");
            out.println("  node [shape=circle, fontname=\"Helvetica\", style=filled, fillcolor=lightblue];");
            out.println("  edge [color=gray50];");
            generateDotNodes(root, out);
            out.println("}");
            System.out.println("  [OK] Árbol Sintáctico guardado en: " + filename);
        } catch (IOException e) {
            System.out.println("  [ERROR] No se pudo guardar el árbol: " + e.getMessage());
            return; // Si falla al escribir el .dot, no intentamos generar la imagen
        }

        // --- Generación Automática de la Imagen PNG usando Graphviz ---
        try {
            String pngFilename = filename.replace(".dot", ".png");
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", filename, "-o", pngFilename);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("  [OK] Imagen del árbol generada en: " + pngFilename);
            } else {
                System.out.println("  [WARN] Graphviz devolvió un error al intentar generar la imagen.");
            }
        } catch (Exception e) {
            System.out.println("  [WARN] No se pudo ejecutar Graphviz automáticamente. Asegúrate de tenerlo instalado: " + e.getMessage());
        }
    }

    private void generateDotNodes(Node node, PrintWriter out) {
        out.printf("  node%d [label=\"%s\"];\n", node.id, node.value);
        if (node.left != null) {
            out.printf("  node%d -> node%d;\n", node.id, node.left.id);
            generateDotNodes(node.left, out);
        }
        if (node.right != null) {
            out.printf("  node%d -> node%d;\n", node.id, node.right.id);
            generateDotNodes(node.right, out);
        }
    }
}