package parserGen.model;

import java.util.List;
import java.util.Objects;

public class Production {
    public final String head;
    public final List<String> body;

    public Production(String head, List<String> body) {
        this.head = head;
        this.body = List.copyOf(body);
    }

    @Override
    public String toString() {
        return head + " → " + (body.isEmpty() ? "ε" : String.join(" ", body));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Production p)) return false;
        return head.equals(p.head) && body.equals(p.body);
    }

    @Override
    public int hashCode() { return Objects.hash(head, body); }
}
