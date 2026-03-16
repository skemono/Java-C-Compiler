import lexerGen.YalParser;
import lexerGen.NFA;
import lexerGen.DFA;
import lexerGen.Minimizer;
import lexerGen.CodeGen;
import lexerGen.util.State;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) throws Exception {
        // Parse CLI: App <file.yal> [-o <OutputClassName>]
        if (args.length == 0) {
            System.out.println("Usage: java App <file.yal> [-o <OutputClassName>]");
            System.out.println("  <file.yal>          YALex specification file");
            System.out.println("  -o <OutputName>     Name for generated lexer class (default: Yylex)");
            return;
        }

        String yalFile    = args[0];
        String outputName = "Yylex";
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("-o") && i + 1 < args.length) {
                outputName = args[++i];
            }
        }

        String[] tests = { yalFile };
        for (String testFile : tests) {
            System.out.println("========================================");
            System.out.println("TEST: " + testFile);
            System.out.println("========================================");

            try {
                // --- Module 1: YalParser ---
                YalParser parser = new YalParser(testFile);
                List<String[]> rules = parser.getRules();

                System.out.println("Parser: " + rules.size() + " rule(s)");
                for (int i = 0; i < rules.size(); i++) {
                    System.out.println("  [" + i + "] regex:  " + rules.get(i)[0]);
                    System.out.println("       action: " + rules.get(i)[1]);
                }
                System.out.println();

                // --- Module 2: NFA ---
                NFA nfa = new NFA();
                State startGlobal = nfa.buildGlobalNFA(rules);

                // BFS traversal to collect all reachable states
                Set<State> visited = new LinkedHashSet<>();
                Queue<State> queue = new LinkedList<>();
                queue.add(startGlobal);
                visited.add(startGlobal);

                while (!queue.isEmpty()) {
                    State current = queue.poll();
                    for (State next : current.epsilonTransitions) {
                        if (visited.add(next)) queue.add(next);
                    }
                    for (Set<State> targets : current.transitions.values()) {
                        for (State next : targets) {
                            if (visited.add(next)) queue.add(next);
                        }
                    }
                }

                // Collect accepting states
                List<State> accepting = new ArrayList<>();
                for (State s : visited) {
                    if (s.tokenName != null) accepting.add(s);
                }

                System.out.println("NFA: " + visited.size() + " total states, "
                        + accepting.size() + " accepting");
                System.out.println("  Start state: " + startGlobal);
                System.out.println("  Accepting states:");
                for (State s : accepting) {
                    System.out.println("    " + s);
                }

                if (accepting.size() == rules.size()) {
                    System.out.println("  [OK] One accepting state per rule");
                } else {
                    System.out.println("  [WARN] Expected " + rules.size()
                            + " accepting states, got " + accepting.size());
                }
                System.out.println();

                // --- Module 3: DFA ---
                DFA dfa = new DFA();
                dfa.build(startGlobal, rules);

                System.out.println("DFA: " + dfa.getStateCount() + " states (vs " + visited.size() + " NFA states)");
                System.out.println("  Start state: " + dfa.getStartId());
                System.out.println("  Accepting states:");
                for (Map.Entry<Integer, String> entry : dfa.getAccepting().entrySet()) {
                    System.out.println("    State " + entry.getKey() + " -> " + entry.getValue());
                }

                Set<String> coveredTokens = new HashSet<>(dfa.getAccepting().values());
                Set<String> expectedTokens = new HashSet<>();
                for (String[] rule : rules) expectedTokens.add(rule[1]);
                if (coveredTokens.equals(expectedTokens)) {
                    System.out.println("  [OK] All tokens covered");
                } else {
                    expectedTokens.removeAll(coveredTokens);
                    System.out.println("  [WARN] Missing tokens: " + expectedTokens);
                }
                System.out.println();

                // --- Module 4: Minimizer ---
                Minimizer minimizer = new Minimizer();
                minimizer.minimize(dfa);

                System.out.println("Minimizer: " + minimizer.getStateCount() + " states (vs " + dfa.getStateCount() + " DFA states)");
                System.out.println("  Start state: " + minimizer.getStartId());
                System.out.println("  Accepting states:");
                for (Map.Entry<Integer, String> entry : minimizer.getAccepting().entrySet()) {
                    System.out.println("    State " + entry.getKey() + " -> " + entry.getValue());
                }

                Set<String> minCovered = new HashSet<>(minimizer.getAccepting().values());
                Set<String> minExpected = new HashSet<>();
                for (String[] rule : rules) minExpected.add(rule[1]);
                if (minCovered.equals(minExpected)) {
                    System.out.println("  [OK] All tokens covered");
                } else {
                    minExpected.removeAll(minCovered);
                    System.out.println("  [WARN] Missing tokens: " + minExpected);
                }

                // --- Module 5: CodeGen ---
                System.out.println("  --- Module 5: CodeGen ---");

                // 5a. Generate lexer source
                String generatedCode = CodeGen.generate(minimizer, parser, outputName);
                Path generatedDir = Paths.get("generated");
                if (!Files.exists(generatedDir)) Files.createDirectories(generatedDir);
                Path outputPath = generatedDir.resolve(outputName + ".java");
                Files.write(outputPath, generatedCode.getBytes(StandardCharsets.UTF_8));
                System.out.println("  [OK] Lexer source:  " + outputPath.toAbsolutePath());

                // 5b. Generate expression tree (DOT + PNG via Graphviz)
                System.out.println("  --- Expression Tree ---");
                CodeGen.generateGraphviz(rules, nfa, "output");


            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();
        }
    }
}
