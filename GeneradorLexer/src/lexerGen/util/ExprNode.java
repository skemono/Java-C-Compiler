package lexerGen.util;

/**
 * Node of a regex expression tree.
 * Internal nodes are operators (·, |, *, +, ?, #).
 * Leaf nodes are symbols (CHAR, CLASS, ANY).
 */
public class ExprNode {
    private static int counter = 0;

    public int id;          // unique ID for DOT node naming
    public String label;    // display label: operator symbol or char description
    public ExprNode left;   // left child (null for leaves)
    public ExprNode right;  // right child (null for leaves and unary operators)

    public ExprNode(String label) {
        this.id    = counter++;
        this.label = label;
        this.left  = null;
        this.right = null;
    }

    public ExprNode(String label, ExprNode left, ExprNode right) {
        this.id    = counter++;
        this.label = label;
        this.left  = left;
        this.right = right;
    }

    public ExprNode(String label, ExprNode child) {
        this.id    = counter++;
        this.label = label;
        this.left  = child;
        this.right = null;
    }

    public static void resetCounter() { counter = 0; }
}
