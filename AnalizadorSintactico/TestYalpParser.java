import parserGen.YalpParser;
import parserGen.model.Production;
import java.util.List;

public class TestYalpParser {
    static int passed = 0, failed = 0;

    public static void main(String[] args) throws Exception {
        testTokenParsing();
        testMultipleTokensOnOneLine();
        testIgnoreTokens();
        testProductions();
        testCommentsAreRemoved();
        testFileLoad();
        System.out.println("\n=== " + passed + " passed, " + failed + " failed ===");
    }

    static void testTokenParsing() throws Exception {
        String c = "%token ID\n%token PLUS\n%%\nexpr:\n  ID\n;\n";
        YalpParser p = new YalpParser(c, true);
        assertEqual("token count", 2, p.getTokens().size());
        assertEqual("first token", "ID", p.getTokens().get(0));
        assertEqual("second token", "PLUS", p.getTokens().get(1));
    }

    static void testMultipleTokensOnOneLine() throws Exception {
        String c = "%token A B C\n%%\nexpr:\n  A\n;\n";
        YalpParser p = new YalpParser(c, true);
        assertEqual("multi-token count", 3, p.getTokens().size());
        assertEqual("third token", "C", p.getTokens().get(2));
    }

    static void testIgnoreTokens() throws Exception {
        String c = "%token WS\nIGNORE WS\n%%\nexpr:\n  WS\n;\n";
        YalpParser p = new YalpParser(c, true);
        assertEqual("ignored count", 1, p.getIgnoredTokens().size());
        assertEqual("ignored token", "WS", p.getIgnoredTokens().get(0));
    }

    static void testProductions() throws Exception {
        String c = "%token A B\n%%\nexpr:\n  expr A term\n  | term\n;\nterm:\n  B\n;\n";
        YalpParser p = new YalpParser(c, true);
        List<Production> prods = p.getProductions();
        assertEqual("production count", 3, prods.size());
        assertEqual("prod[0] head", "expr", prods.get(0).head);
        assertEqual("prod[0] body size", 3, prods.get(0).body.size());
        assertEqual("prod[1] body", List.of("term"), prods.get(1).body);
        assertEqual("prod[2] head", "term", prods.get(2).head);
    }

    static void testCommentsAreRemoved() throws Exception {
        String c = "/* comment */\n%token ID\n%%\n/* inline comment */\nexpr:\n  ID\n;\n";
        YalpParser p = new YalpParser(c, true);
        assertEqual("tokens after comment removal", 1, p.getTokens().size());
        assertEqual("productions after comment removal", 1, p.getProductions().size());
    }

    static void testFileLoad() throws Exception {
        YalpParser p = new YalpParser("tests/grammars/simple.yalp");
        assertEqual("file: token count", 3, p.getTokens().size());
        assertEqual("file: ignored count", 1, p.getIgnoredTokens().size());
        assertEqual("file: production count", 3, p.getProductions().size());
    }

    static void assertEqual(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.println("  [PASS] " + name);
            passed++;
        } else {
            System.out.println("  [FAIL] " + name + " — expected: " + expected + ", got: " + actual);
            failed++;
        }
    }
}
