import parserGen.LR0Automaton;
import parserGen.model.*;
import java.util.*;

public class TestLR0 {
    static int passed = 0, failed = 0;

    public static void main(String[] args) throws Exception {
        testAugmentedGrammar();
        testStateCount();
        testStartStateItemCount();
        testStartStateTransitions();
        testClosureExpansion();
        testGoto();
        testPrintAutomaton();
        System.out.println("\n=== " + passed + " passed, " + failed + " failed ===");
    }

    // Grammar: E→E PLUS T | T,  T→ID
    static List<Production> grammar() {
        return List.of(
            new Production("E", List.of("E", "PLUS", "T")),
            new Production("E", List.of("T")),
            new Production("T", List.of("ID"))
        );
    }

    static void testAugmentedGrammar() {
        LR0Automaton a = new LR0Automaton(grammar());
        assertEqual("start symbol", "E'", a.getStartSymbol());
        assertEqual("augmented grammar size", 4, a.getGrammar().size());
        assertEqual("augmented first head", "E'", a.getGrammar().get(0).head);
        assertEqual("augmented first body", List.of("E"), a.getGrammar().get(0).body);
    }

    static void testStateCount() {
        LR0Automaton a = new LR0Automaton(grammar());
        assertEqual("state count", 6, a.getStates().size());
    }

    static void testStartStateItemCount() {
        LR0Automaton a = new LR0Automaton(grammar());
        assertEqual("I0 item count", 4, a.getStartState().items.size());
    }

    static void testStartStateTransitions() {
        LR0Automaton a = new LR0Automaton(grammar());
        LR0State i0 = a.getStartState();
        assert_("I0 has transition on E",  i0.transitions.containsKey("E"));
        assert_("I0 has transition on T",  i0.transitions.containsKey("T"));
        assert_("I0 has transition on ID", i0.transitions.containsKey("ID"));
        assertEqual("I0 transition count", 3, i0.transitions.size());
    }

    static void testClosureExpansion() {
        LR0Automaton a = new LR0Automaton(grammar());
        Set<LR0Item> seed = new LinkedHashSet<>();
        seed.add(new LR0Item("E'", List.of("E"), 0));
        Set<LR0Item> cl = a.closure(seed);
        assertEqual("closure of {E'→•E} size", 4, cl.size());
    }

    static void testGoto() {
        LR0Automaton a = new LR0Automaton(grammar());
        Set<LR0Item> i0Items = a.getStartState().items;
        Set<LR0Item> next = a.goto_(i0Items, "ID");
        assertEqual("goto(I0,ID) item count", 1, next.size());
        LR0Item item = next.iterator().next();
        assertEqual("goto(I0,ID) item head", "T", item.head);
        assert_("goto(I0,ID) item is complete", item.isComplete());
    }

    static void testPrintAutomaton() {
        try {
            LR0Automaton a = new LR0Automaton(grammar());
            a.printAutomaton();
            assert_("printAutomaton does not throw", true);
        } catch (Exception e) {
            assert_("printAutomaton does not throw", false);
        }
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

    static void assert_(String name, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + name);
            passed++;
        } else {
            System.out.println("  [FAIL] " + name);
            failed++;
        }
    }
}
