package lexerGen.util;
import java.util.*;

// Represents a state in the NFA/DFA
public class State {
    public int id;
    private static int counter = 0; // Static counter to assign unique IDs to states

    // Map char to a set of next states, its a set because NFA can have multiple transitions for a char.
    public Map<Character, Set<State>> transitions;

    // Epsilon transitions are separated for easy access in Subset Construction algorithm.
    public Set<State> epsilonTransitions;

    // Null by default for non-accepting states, set to token name for accepting states.
    public String tokenName;


    //INIT
    public State() {
        this.id = counter++;
        this.transitions = new HashMap<>(); // HashMap to store transitions for each character
        this.epsilonTransitions = new HashSet<>(); // Set to see if it contains a state with epsilon transition no key needed
        this.tokenName = null;  
    }

    // Adds transition for a character to a target state
    public void addTransition(char c, State target) {
        // computeIfAbsent runs lambda as key value if key is not in HashMap, then returns the value.
        this.transitions.computeIfAbsent(c, key -> new HashSet<>()).add(target);
    }

    // Adds an epsilon transition to a target state
    public void addEpsilonTransition(State target){
        this.epsilonTransitions.add(target);
    }

    @Override
    // method for debugging, shows state ID and if its an accepting state with token name.
    public String toString() {
        return "State " + this.id + (this.tokenName != null ? " (Accepting: " + this.tokenName + ")" : "");
    }
    
}
