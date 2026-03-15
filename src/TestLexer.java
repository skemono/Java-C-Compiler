import generated.Yylex;

public class TestLexer {
    public static void main(String[] args) {
        // This input string is designed to be parsed by the lexer generated from 'test4_c_lexer.yal'.
        // It contains an ID, whitespace, an assignment, a number, a plus, and a semicolon.
        String input = "my_var = 123 + 45;";
        Yylex lexer = new Yylex(input);

        System.out.println("\nTokenizing input: \"" + input + "\"");
        System.out.println("----------------------------------------");

        try {
            Object token;
            // The yylex() method returns null when it reaches the end of the input.
            while ((token = lexer.yylex()) != null) {
                // The 'token' object is whatever your .yal rule actions return (e.g., "ID", "NUMBER").
                // We skip printing the WHITESPACE token for a cleaner output.
                if (token.equals("WHITESPACE")) continue;
                System.out.printf("Token: %-10s Lexeme: '%s'\n", token, lexer.yytext);
            }
            System.out.println("----------------------------------------");
            System.out.println("Successfully reached end of input.");
        } catch (Exception e) {
            System.err.println("\nLexer error: " + e.getMessage());
        }
    }
}