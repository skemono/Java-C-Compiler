import java.util.*;
import lexerGen.*;
import lexerGen.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        NFA nfa = new NFA();

        List<String[]> rules = new ArrayList<>();
        rules.add(new String[]{"('a'|'b'|'c')*", "TOKEN_ABC"});
        rules.add(new String[]{"'a''b''c'", "TOKEN_EXACT"});

        State start = nfa.buildGlobalNFA(rules);

        System.out.println("Estado inicial: " + start);
        System.out.println("Epsilons desde start:");
        for (State s : start.epsilonTransitions) {
            System.out.println("  -> " + s);
        }
    }
}
