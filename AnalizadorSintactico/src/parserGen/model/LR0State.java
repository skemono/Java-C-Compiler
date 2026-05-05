package parserGen.model;

import java.util.*;

public class LR0State {
    public final int id;
    public final Set<LR0Item> items;
    public final Map<String, Integer> transitions;

    public LR0State(int id, Set<LR0Item> items) {
        this.id = id;
        this.items = Collections.unmodifiableSet(new LinkedHashSet<>(items));
        this.transitions = new LinkedHashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("I" + id + ":\n");
        for (LR0Item item : items) sb.append("  ").append(item).append("\n");
        if (!transitions.isEmpty()) {
            sb.append("  Transitions:\n");
            transitions.forEach((sym, target) ->
                sb.append("    ").append(sym).append(" → I").append(target).append("\n"));
        }
        return sb.toString();
    }
}
