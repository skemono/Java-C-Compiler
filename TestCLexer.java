import generated.CLexer;
import java.io.File;
import java.util.*;

public class TestCLexer {
    public static void main(String[] args) throws Exception {
        File input = new File("tests/test_program.c");
        CLexer lexer = new CLexer(input);

        // Contar tokens por tipo
        Map<String, Integer> counts = new LinkedHashMap<>();
        List<String[]> tokens = new ArrayList<>();

        String token;
        while ((token = lexer.yylex()) != null) {
            tokens.add(new String[]{token, lexer.yytext});
            counts.merge(token, 1, Integer::sum);
        }

        // Imprimir todos los tokens
        System.out.println("===========================================");
        System.out.println("  TOKENS RECONOCIDOS");
        System.out.println("===========================================");
        for (String[] t : tokens) {
            System.out.printf("  %-15s  \"%s\"%n", t[0], t[1]);
        }

        // Resumen por tipo
        System.out.println("\n===========================================");
        System.out.println("  RESUMEN POR TIPO DE TOKEN");
        System.out.println("===========================================");
        counts.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .forEach(e -> System.out.printf("  %-15s  %d%n", e.getKey(), e.getValue()));

        System.out.println("\n  Total tokens: " + tokens.size());
    }
}
