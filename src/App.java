import lexerGen.YalParser;
import java.util.List;
import java.util.LinkedHashMap;

public class App {
    public static void main(String[] args) throws Exception {
        YalParser parser = new YalParser("test.yal");

        System.out.println("=== HEADER ===");
        System.out.println(parser.getHeaderSection());

        System.out.println("\n=== DEFINICIONES (expandidas) ===");
        LinkedHashMap<String, String> defs = parser.getDefinitions();
        for (String key : defs.keySet()) {
            System.out.println(key + " = " + defs.get(key));
        }

        System.out.println("\n=== REGLAS (expandidas) ===");
        List<String[]> rules = parser.getRules();
        for (String[] rule : rules) {
            System.out.println("regex: " + rule[0] + "  →  action: " + rule[1]);
        }

        System.out.println("\n=== TRAILER ===");
        System.out.println(parser.getTrailerSection());
    }
}
