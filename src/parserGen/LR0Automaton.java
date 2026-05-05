package parserGen;

import parserGen.model.*;
import java.util.*;

public class LR0Automaton {
    private final List<LR0State> states = new ArrayList<>();
    private final List<Production> grammar;
    private final String startSymbol;

    public LR0Automaton(List<Production> originalGrammar) {
        if (originalGrammar.isEmpty())
            throw new IllegalArgumentException("Grammar must have at least one production");

        String originalStart = originalGrammar.get(0).head;
        this.startSymbol = originalStart + "'";
        this.grammar = new ArrayList<>();
        this.grammar.add(new Production(startSymbol, List.of(originalStart)));
        this.grammar.addAll(originalGrammar);

        build();
    }

    private void build() {
        LR0Item initial = new LR0Item(startSymbol, grammar.get(0).body, 0);
        Set<LR0Item> initialClosure = closure(Set.of(initial));

        Map<Set<LR0Item>, Integer> stateIndex = new LinkedHashMap<>();
        Queue<Set<LR0Item>> worklist = new LinkedList<>();

        stateIndex.put(initialClosure, 0);
        states.add(new LR0State(0, initialClosure));
        worklist.add(initialClosure);

        while (!worklist.isEmpty()) {
            Set<LR0Item> current = worklist.poll();
            int currentId = stateIndex.get(current);

            Set<String> symbols = new LinkedHashSet<>();
            for (LR0Item item : current) {
                if (!item.isComplete()) symbols.add(item.symbolAfterDot());
            }

            for (String sym : symbols) {
                Set<LR0Item> next = goto_(current, sym);
                if (next.isEmpty()) continue;

                if (!stateIndex.containsKey(next)) {
                    int newId = states.size();
                    stateIndex.put(next, newId);
                    states.add(new LR0State(newId, next));
                    worklist.add(next);
                }
                states.get(currentId).transitions.put(sym, stateIndex.get(next));
            }
        }
    }

    public Set<LR0Item> closure(Set<LR0Item> items) {
        Set<LR0Item> result = new LinkedHashSet<>(items);
        Queue<LR0Item> queue = new LinkedList<>(items);
        while (!queue.isEmpty()) {
            LR0Item item = queue.poll();
            String sym = item.symbolAfterDot();
            if (sym == null) continue;
            for (Production p : grammar) {
                if (p.head.equals(sym)) {
                    LR0Item newItem = new LR0Item(p.head, p.body, 0);
                    if (result.add(newItem)) queue.add(newItem);
                }
            }
        }
        return result;
    }

    public Set<LR0Item> goto_(Set<LR0Item> items, String symbol) {
        Set<LR0Item> kernel = new LinkedHashSet<>();
        for (LR0Item item : items) {
            if (!item.isComplete() && item.symbolAfterDot().equals(symbol))
                kernel.add(item.advance());
        }
        return kernel.isEmpty() ? kernel : closure(kernel);
    }

    public List<LR0State> getStates() { return Collections.unmodifiableList(states); }
    public List<Production> getGrammar() { return Collections.unmodifiableList(grammar); }
    public String getStartSymbol() { return startSymbol; }
    public LR0State getStartState() { return states.get(0); }

    public void printAutomaton() {
        System.out.println("=== LR(0) Automaton ===");
        System.out.println("Augmented grammar:");
        for (Production p : grammar) System.out.println("  " + p);
        System.out.println("\nStates (" + states.size() + " total):");
        for (LR0State s : states) System.out.println(s);
    }
}
