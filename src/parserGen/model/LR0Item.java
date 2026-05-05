package parserGen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LR0Item {
    public final String head;
    public final List<String> body;
    public final int dot;

    public LR0Item(String head, List<String> body, int dot) {
        this.head = head;
        this.body = List.copyOf(body);
        this.dot = dot;
    }

    public boolean isComplete() { return dot >= body.size(); }

    public String symbolAfterDot() {
        return isComplete() ? null : body.get(dot);
    }

    public LR0Item advance() { return new LR0Item(head, body, dot + 1); }

    @Override
    public String toString() {
        List<String> display = new ArrayList<>(body);
        display.add(dot, "•");
        return head + " → " + String.join(" ", display);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LR0Item i)) return false;
        return head.equals(i.head) && body.equals(i.body) && dot == i.dot;
    }

    @Override
    public int hashCode() { return Objects.hash(head, body, dot); }
}
