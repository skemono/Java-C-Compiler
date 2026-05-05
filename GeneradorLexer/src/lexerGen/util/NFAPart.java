package lexerGen.util;

// Part of NFA for Thompson construction.
public class NFAPart {
    public State start;
    public State accept;

    public NFAPart(State start, State accept) {
        this.start = start;
        this.accept = accept;
    }
}
