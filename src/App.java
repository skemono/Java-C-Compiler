import lexerGen.YalParser;
import java.util.List;
import java.util.LinkedHashMap;

public class App {
    public static void main(String[] args) throws Exception {
        String[] tests = {
            "tests/test1_basic.yal",
            "tests/test2_header_trailer.yal",
            "tests/test3_comments.yal",
            "tests/test4_c_lexer.yal",
            "tests/test5_chain_expansion.yal",
            "tests/test6_no_definitions.yal",
            "tests/test7_single_rule.yal"
        };

        for (String testFile : tests) {
            System.out.println("========================================");
            System.out.println("TEST: " + testFile);
            System.out.println("========================================");

            try {
                YalParser parser = new YalParser(testFile);

                System.out.println("HEADER: " + (parser.getHeaderSection().isEmpty() ? "(vacio)" : parser.getHeaderSection()));
                System.out.println();

                System.out.println("DEFINICIONES:");
                LinkedHashMap<String, String> defs = parser.getDefinitions();
                if (defs.isEmpty()) {
                    System.out.println("  (ninguna)");
                } else {
                    for (String key : defs.keySet()) {
                        System.out.println("  " + key + " = " + defs.get(key));
                    }
                }
                System.out.println();

                System.out.println("REGLAS:");
                List<String[]> rules = parser.getRules();
                for (int i = 0; i < rules.size(); i++) {
                    System.out.println("  [" + i + "] regex: " + rules.get(i)[0]);
                    System.out.println("       action: " + rules.get(i)[1]);
                }
                System.out.println();

                System.out.println("TRAILER: " + (parser.getTrailerSection().isEmpty() ? "(vacio)" : parser.getTrailerSection()));

            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();
        }
    }
}
