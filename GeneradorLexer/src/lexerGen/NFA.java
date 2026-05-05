package lexerGen;
import java.util.*;
import lexerGen.util.*;

public class NFA {

    // Full alphabet: printable ASCII + common whitespace chars
    public static final Set<Character> ALPHABET = new HashSet<>();
    static {
        for (char c = 32; c <= 126; c++) ALPHABET.add(c);
        ALPHABET.add('\n');
        ALPHABET.add('\t');
        ALPHABET.add('\r');
    }

    // ─── THOMPSON CONSTRUCTION ───────────────────────────────────────────────

    public NFAPart buildNFA(List<RegexToken> postfix) {
        Stack<NFAPart> stack = new Stack<>();

        for (RegexToken token : postfix) {
            switch (token.type) {
                case CHAR:     stack.push(atomChar(token.singleChar)); break;
                case CLASS:    stack.push(atomClass(token));           break;
                case ANY:      stack.push(atomAny());                  break;
                case CONCAT:   stack.push(buildConcat(stack));         break;
                case UNION:    stack.push(buildUnion(stack));          break;
                case STAR:     stack.push(buildStar(stack));           break;
                case PLUS:     stack.push(buildPlus(stack));           break;
                case QUESTION: stack.push(buildQuestion(stack));       break;
                case DIFF:     stack.push(buildDiff(stack));           break;
                default:
                    throw new RuntimeException("Unexpected token in postfix: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new RuntimeException("Malformed postfix, " + stack.size() + " fragments remaining");
        }

        return stack.pop();
    }

    // Two states, one transition by c
    private NFAPart atomChar(char c) {
        State s0 = new State();
        State s1 = new State();
        s0.addTransition(c, s1);
        return new NFAPart(s0, s1);
    }

    // Two states, one transition per char in the set (or complement)
    private NFAPart atomClass(RegexToken token) {
        State s0 = new State();
        State s1 = new State();

        Set<Character> effective;
        if (token.isComplement) {
            effective = new HashSet<>(ALPHABET);
            effective.removeAll(token.charSet);
        } else {
            effective = token.charSet;
        }

        for (char c : effective) {
            s0.addTransition(c, s1);
        }

        return new NFAPart(s0, s1);
    }

    // Two states, transitions for every char in the alphabet
    private NFAPart atomAny() {
        State s0 = new State();
        State s1 = new State();
        for (char c : ALPHABET) {
            s0.addTransition(c, s1);
        }
        return new NFAPart(s0, s1);
    }

    // f1.accept --ε--> f2.start
    private NFAPart buildConcat(Stack<NFAPart> stack) {
        NFAPart f2 = stack.pop();
        NFAPart f1 = stack.pop();
        f1.accept.addEpsilonTransition(f2.start);
        return new NFAPart(f1.start, f2.accept);
    }

    // new sStart --ε--> f1.start, f2.start  |  f1.accept, f2.accept --ε--> sAccept
    private NFAPart buildUnion(Stack<NFAPart> stack) {
        NFAPart f2 = stack.pop();
        NFAPart f1 = stack.pop();

        State sStart  = new State();
        State sAccept = new State();

        sStart.addEpsilonTransition(f1.start);
        sStart.addEpsilonTransition(f2.start);
        f1.accept.addEpsilonTransition(sAccept);
        f2.accept.addEpsilonTransition(sAccept);

        return new NFAPart(sStart, sAccept);
    }

    // sStart --ε--> f.start, sAccept  |  f.accept --ε--> f.start, sAccept
    private NFAPart buildStar(Stack<NFAPart> stack) {
        NFAPart f = stack.pop();

        State sStart  = new State();
        State sAccept = new State();

        sStart.addEpsilonTransition(f.start);
        sStart.addEpsilonTransition(sAccept);
        f.accept.addEpsilonTransition(f.start);
        f.accept.addEpsilonTransition(sAccept);

        return new NFAPart(sStart, sAccept);
    }

    // f.accept --ε--> f.start, sAccept  |  start stays at f.start (forces at least one pass)
    private NFAPart buildPlus(Stack<NFAPart> stack) {
        NFAPart f = stack.pop();

        State sAccept = new State();

        f.accept.addEpsilonTransition(f.start);
        f.accept.addEpsilonTransition(sAccept);

        return new NFAPart(f.start, sAccept);
    }

    // f.start --ε--> f.accept  (direct skip)
    private NFAPart buildQuestion(Stack<NFAPart> stack) {
        NFAPart f = stack.pop();
        f.start.addEpsilonTransition(f.accept);
        return new NFAPart(f.start, f.accept);
    }

    // LocalDFA is a simplified DFA representation used for product construction in the difference operation.
    private static class LocalDFA {
        int startId = 0;
        int stateCount = 0;
        Map<Integer, Map<Character, Integer>> transitions = new HashMap<>();
        Set<Integer> acceptStates = new HashSet<>();
    }

    // Builds an NFA for the difference of two languages by converting to local DFAs and applying product construction.
    private NFAPart buildDiff(Stack<NFAPart> stack) {
        NFAPart f2 = stack.pop(); // language to subtract
        NFAPart f1 = stack.pop(); // base language

        LocalDFA dfa1 = toLocalDFA(f1);
        LocalDFA dfa2 = toLocalDFA(f2);

        return productDiff(dfa1, dfa2);
    }

    /** Converts an NFAPart to a local DFA via subset construction. */
    private LocalDFA toLocalDFA(NFAPart fragment) {
        LocalDFA result = new LocalDFA();
        Map<Set<State>, Integer> stateIds = new LinkedHashMap<>();
        Queue<Set<State>> worklist = new LinkedList<>();

        Set<State> startSet = localEpsilonClosure(Collections.singleton(fragment.start));
        stateIds.put(startSet, 0);
        result.startId = 0;
        result.stateCount = 1;
        worklist.add(startSet);

        if (startSet.contains(fragment.accept)) result.acceptStates.add(0);

        while (!worklist.isEmpty()) {
            Set<State> current = worklist.poll();
            int currentId = stateIds.get(current);

            for (char c : ALPHABET) {
                Set<State> moved = localMove(current, c);
                if (moved.isEmpty()) continue;
                Set<State> next = localEpsilonClosure(moved);
                if (next.isEmpty()) continue;

                if (!stateIds.containsKey(next)) {
                    int newId = result.stateCount++;
                    stateIds.put(next, newId);
                    worklist.add(next);
                    if (next.contains(fragment.accept)) result.acceptStates.add(newId);
                }
                result.transitions.computeIfAbsent(currentId, k -> new HashMap<>())
                                  .put(c, stateIds.get(next));
            }
        }
        return result;
    }

    /**
     * Product construction: L1 - L2.
     * Accept pairs (s1, s2) where s1 ∈ accept(DFA1) AND s2 ∉ accept(DFA2).
     * s2 = -1 represents a dead state in DFA2 (non-accepting by definition).
     */
    private NFAPart productDiff(LocalDFA dfa1, LocalDFA dfa2) {
        Map<String, Integer> productIds = new LinkedHashMap<>();
        Map<Integer, Map<Character, Integer>> productTrans = new HashMap<>();
        Set<Integer> productAccept = new HashSet<>();
        Queue<String> worklist = new LinkedList<>();

        String startKey = dfa1.startId + "," + dfa2.startId;
        productIds.put(startKey, 0);
        worklist.add(startKey);

        if (dfa1.acceptStates.contains(dfa1.startId)
                && !dfa2.acceptStates.contains(dfa2.startId)) {
            productAccept.add(0);
        }

        while (!worklist.isEmpty()) {
            String key = worklist.poll();
            int pid = productIds.get(key);
            String[] parts = key.split(",");
            int s1 = Integer.parseInt(parts[0]);
            int s2 = Integer.parseInt(parts[1]); // -1 = dead

            for (char c : ALPHABET) {
                Map<Character, Integer> t1 = dfa1.transitions.get(s1);
                int next1 = (t1 != null && t1.containsKey(c)) ? t1.get(c) : -1;
                if (next1 == -1) continue; // DFA1 has no transition → skip

                Map<Character, Integer> t2 = (s2 >= 0) ? dfa2.transitions.get(s2) : null;
                int next2 = (t2 != null && t2.containsKey(c)) ? t2.get(c) : -1;

                String nextKey = next1 + "," + next2;
                if (!productIds.containsKey(nextKey)) {
                    int newId = productIds.size();
                    productIds.put(nextKey, newId);
                    worklist.add(nextKey);
                    if (dfa1.acceptStates.contains(next1)
                            && !dfa2.acceptStates.contains(next2)) {
                        productAccept.add(newId);
                    }
                }
                productTrans.computeIfAbsent(pid, k -> new HashMap<>())
                            .put(c, productIds.get(nextKey));
            }
        }

        // Convert product DFA states to State objects
        int n = productIds.size();
        State[] dfaStates = new State[n];
        for (int i = 0; i < n; i++) dfaStates[i] = new State();

        for (Map.Entry<Integer, Map<Character, Integer>> e : productTrans.entrySet()) {
            for (Map.Entry<Character, Integer> t : e.getValue().entrySet()) {
                dfaStates[e.getKey()].addTransition(t.getKey(), dfaStates[t.getValue()]);
            }
        }

        // Merge all product accept states into one sAccept via ε-transitions
        State sAccept = new State();
        for (int aid : productAccept) {
            dfaStates[aid].addEpsilonTransition(sAccept);
        }

        return new NFAPart(dfaStates[0], sAccept);
    }

    /** Epsilon closure — local copy to avoid dependency on DFA module. */
    private Set<State> localEpsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Queue<State> queue = new LinkedList<>(states);
        while (!queue.isEmpty()) {
            for (State next : queue.poll().epsilonTransitions) {
                if (closure.add(next)) queue.add(next);
            }
        }
        return closure;
    }

    /** Move function — local copy to avoid dependency on DFA module. */
    private Set<State> localMove(Set<State> states, char c) {
        Set<State> result = new HashSet<>();
        for (State s : states) {
            Set<State> targets = s.transitions.get(c);
            if (targets != null) result.addAll(targets);
        }
        return result;
    }

    // ─── REGEX TO POSTFIX & EXPR TREE ────────────────────────────────────────
    public ExprNode buildExprTree(List<RegexToken> postfix) {
        Stack<ExprNode> stack = new Stack<>();

        for (RegexToken token : postfix) {
            switch (token.type) {
                case CHAR: {
                    String lbl = "'" + escapeLabel(token.singleChar) + "'";
                    stack.push(new ExprNode(lbl));
                    break;
                }
                case CLASS: {
                    String lbl = token.isComplement ? "[^set]" : "[set]";
                    stack.push(new ExprNode(lbl));
                    break;
                }
                case ANY:
                    stack.push(new ExprNode("_"));
                    break;
                case CONCAT: {
                    ExprNode right = stack.pop();
                    ExprNode left  = stack.pop();
                    stack.push(new ExprNode("·", left, right));
                    break;
                }
                case UNION: {
                    ExprNode right = stack.pop();
                    ExprNode left  = stack.pop();
                    stack.push(new ExprNode("|", left, right));
                    break;
                }
                case DIFF: {
                    ExprNode right = stack.pop();
                    ExprNode left  = stack.pop();
                    stack.push(new ExprNode("#", left, right));
                    break;
                }
                case STAR:
                    stack.push(new ExprNode("*", stack.pop()));
                    break;
                case PLUS:
                    stack.push(new ExprNode("+", stack.pop()));
                    break;
                case QUESTION:
                    stack.push(new ExprNode("?", stack.pop()));
                    break;
                default:
                    break;
            }
        }
        return stack.isEmpty() ? null : stack.pop();
    }

    private String escapeLabel(char c) {
        switch (c) {
            case '\n': return "\\n";
            case '\t': return "\\t";
            case '\r': return "\\r";
            case '\'': return "\\'";
            case '\\': return "\\\\";
            case '"':  return "\\\"";
            default:   return String.valueOf(c);
        }
    }

    // ─── GLOBAL NFA ───────────────────────────────────────────────────────────

    // rules: list of {expandedRegex, tokenName} in priority order (first = highest priority)
    public State buildGlobalNFA(List<String[]> rules) {
        State startGlobal = new State();

        for (String[] rule : rules) {
            String regex     = rule[0];
            String tokenName = rule[1];

            List<RegexToken> tokens   = tokenize(regex);
            List<RegexToken> concated = insertConcat(tokens);
            List<RegexToken> postfix  = toPostfix(concated);
            NFAPart fragment          = buildNFA(postfix);

            startGlobal.addEpsilonTransition(fragment.start);
            fragment.accept.tokenName = tokenName;
        }

        return startGlobal;
    }


    public List<RegexToken> tokenize(String regex){
        List<RegexToken> tokens = new ArrayList<>();
        int i = 0;

        while (i < regex.length()) {
            // Skip whitespace
            char c = regex.charAt(i);
            if (c == ' ' || c == '\t' || c == '\n') {
                i++;
                continue;
            }
            // Handle character literals enclosed in single quotes, with support for escape sequences.
            if (c == '\''){
                char literal;
                if (regex.charAt(i + 1) == '\\'){
                    char escaped = regex.charAt(i + 2);
                    switch (escaped) {
                        case 'n': literal = '\n'; break;
                        case 't': literal = '\t'; break;
                        case 'r': literal = '\r'; break;
                        case '\\': literal = '\\'; break;
                        case '\'': literal = '\''; break;
                        default: literal = escaped;
                    }
                    i += 4; // Skip past the escaped character
                }
                else {
                    literal = regex.charAt(i + 1);
                    i += 3; // Skip past the literal character
                }
                tokens.add(new RegexToken(RegexToken.Type.CHAR, literal));
                continue;
            }
            
            // Handle strings "abc" as a sequence of CHAR tokens.
            if (c == '"'){
                i++; // Consume the opening quote "
                while (i < regex.length() && regex.charAt(i) != '"') {
                    tokens.add(new RegexToken(RegexToken.Type.CHAR, regex.charAt(i)));
                    i++;
                }
                i++; // Consume the closing quote "
                continue;
            }

            // Handle character classes [abc] and [^abc], with support for ranges like [a-z].
            if (c == '['){
                i++; // Consume the opening bracket [
                boolean complement = false;
                Set<Character> charSet = new HashSet<>();

                if (i < regex.length() && regex.charAt(i) == '^') {
                    complement = true;
                    i++; // Consume the ^
                }

                while (i < regex.length() && regex.charAt(i) != ']') {
                    char startChar = parseSingleChar(regex, i);
                    int advance = charTokenLength(regex, i);
                    i += advance;

                    // Check for character range like a-z
                    if (i < regex.length() && regex.charAt(i) == '-'){
                        i++; // Consume the '-'
                        char to = parseSingleChar(regex, i);
                        i += charTokenLength(regex, i);
                        for (char ch = startChar; ch <= to; ch++) {
                            charSet.add(ch);
                        }
                    } else {
                        charSet.add(startChar);
                    }
                }
                i++; // Consume the closing bracket ]

                if (complement){
                    tokens.add(new RegexToken(RegexToken.Type.CLASS, charSet, true));
                } else {
                    tokens.add(new RegexToken(RegexToken.Type.CLASS, charSet));
                }
                continue;
            }
            // Handle operators and parentheses.
            switch (c) {
                case '_': tokens.add(new RegexToken(RegexToken.Type.ANY));      break;
                case '*': tokens.add(new RegexToken(RegexToken.Type.STAR));     break;
                case '+': tokens.add(new RegexToken(RegexToken.Type.PLUS));     break;
                case '?': tokens.add(new RegexToken(RegexToken.Type.QUESTION)); break;
                case '|': tokens.add(new RegexToken(RegexToken.Type.UNION));    break;
                case '#': tokens.add(new RegexToken(RegexToken.Type.DIFF));     break;
                case '(': tokens.add(new RegexToken(RegexToken.Type.LPAREN));   break;
                case ')': tokens.add(new RegexToken(RegexToken.Type.RPAREN));   break;
                default:
                    throw new RuntimeException("Character not expected: '" + c + "'");
            }
            i++;
        }
        return tokens;
    }

    // Insert explicit CONCAT tokens where needed.
    public List<RegexToken> insertConcat(List<RegexToken> tokens) {
        List<RegexToken> tokensWithConcat = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            RegexToken current = tokens.get(i);
            tokensWithConcat.add(current);

            if (i + 1 < tokens.size()) { // Lookahead to the next token while ensuring we dont go out of bounds
                RegexToken next = tokens.get(i + 1);

                if (isLeftOperand(current) && isRightOperand(next)) { // If current can be a left operand and next can be a right operand insert CONCAT
                    tokensWithConcat.add(new RegexToken(RegexToken.Type.CONCAT));
                }
            }
        }
        return tokensWithConcat;
    }
    
    // Convert infix regex tokens to postfix notation using the shunting yard algorithm.
    public List<RegexToken> toPostfix(List<RegexToken> tokens){
        List<RegexToken> output = new ArrayList<>(); // Output list for the resulting postfix expression.
        Stack<RegexToken> operators = new Stack<>(); // Stack to hold operators during processing.

        for (RegexToken token : tokens) {
            switch (token.type){
                // If the token is an operand, add it to the output list.
                case CHAR:
                case CLASS:
                case ANY:
                    output.add(token);
                    break;

                // If the token is a left parenthesis, push it onto the operator stack.
                case LPAREN:
                    operators.push(token);
                    break;

                // Right parenthesis: Pop operators from the stack to the output until a left parenthesis is encountered.
                case RPAREN:
                    while (!operators.isEmpty() && operators.peek().type != RegexToken.Type.LPAREN) {
                        output.add(operators.pop());
                    }
                    if (operators.isEmpty()) {
                        throw new RuntimeException("Parenthesis unbalanced: no matching '(' for ')'");
                    }
                    operators.pop(); // Pop the left parenthesis
                    break;

                // For unary operators, pop from the stack to the output based on precedence rules before pushing the current operator.
                // Precedence should be higher than binary operators to ensure they are applied to the correct operands.
                case STAR:
                case PLUS:
                case QUESTION:
                    while (!operators.isEmpty() && operators.peek().type != RegexToken.Type.LPAREN && precedence(operators.peek().type) > precedence(token.type)) {
                        output.add(operators.pop());
                    }
                    operators.push(token);
                    break;

                // For binary operators, pop from the stack to the output based on precedence rules before pushing the current operator.
                case CONCAT:
                case UNION:
                case DIFF:
                    while (!operators.isEmpty() && operators.peek().type != RegexToken.Type.LPAREN && precedence(operators.peek().type) >= precedence(token.type)) {
                        output.add(operators.pop());
                    }
                    operators.push(token);
                    break;
                    
            }
        }

        // After processing all tokens, pop any remaining operators from the stack to the output.
        while (!operators.isEmpty()){
            RegexToken top = operators.pop();
            if (top.type == RegexToken.Type.LPAREN){
                throw new RuntimeException("Parenthesis unbalanced: no matching ')' for '('"); 
            }
            output.add(top);
        }
        return output;
    }
    

    // HELPERS FOR TOKENIZATION

    // Extracts a single character from a character literal, handling escape sequences if present.
    private char parseSingleChar(String regex, int i) {
        // asume que regex.charAt(i) == '\''
        if (regex.charAt(i + 1) == '\\') {
            char escaped = regex.charAt(i + 2);
            switch (escaped) {
                case 'n':  return '\n';
                case 't':  return '\t';
                case 'r':  return '\r';
                case '\\': return '\\';
                case '\'': return '\'';
                default:   return escaped;
            }
        }
        return regex.charAt(i + 1);
    }

    // Determines the length of a character token, which can be 3 for 'x' or 4 for '\x'.
    private int charTokenLength(String regex, int i) {
          if (regex.charAt(i + 1) == '\\') return 4; // ' \ x '
          return 3; // ' x '
    }

    // HELPERS FOR CONCAT INSERTION

    // Determines if a token can be a left operand for concatenation.
    private boolean isLeftOperand(RegexToken t) {
        switch (t.type) {
            case CHAR:
            case CLASS:
            case ANY:
            case RPAREN:
            case STAR:
            case PLUS:
            case QUESTION:
                return true;
            default:
                return false;
        }
    }

    // Determines if a token can be a right operand for concatenation.
    private boolean isRightOperand(RegexToken t) {
        switch (t.type) {
            case CHAR:
            case CLASS:
            case ANY:
            case LPAREN:
                return true;
            default:
                return false;
        }
    }


    // HELPERS FOR SHUNTING YARD
    // Define operator precedence for the shunting yard algorithm. 
    // Order is defined in Consideraciones Yalex document.
    private int precedence(RegexToken.Type type) {
        switch (type) {
            case DIFF:     
                return 4;
            case STAR:
            case PLUS:
            case QUESTION: 
                return 3;
            case CONCAT:   
                return 2;
            case UNION:    
                return 1;
            default:       
                return 0;
        }
    }
}
