package generated;

import java.util.*;



public class Yylex {

    private String input;
    private int position = 0;
    public String yytext;

    public Yylex(String input) {
        this.input = input;
    }

    public Object yylex() throws Exception {
        while (true) {
            if (position >= input.length()) {
                return null; // End of input
            }

            int startPosition = position;
            int currentState = 0;
            int lastAcceptingState = -1;
            int lastMatchPosition = -1;

            for (int i = startPosition; i < input.length(); i++) {
                char c = input.charAt(i);
                int nextState = getNextState(currentState, c);
                if (nextState == -1) { break; }
                currentState = nextState;
                if (isAccepting(currentState)) {
                    lastAcceptingState = currentState;
                    lastMatchPosition = i + 1;
                }
            }

            if (lastAcceptingState != -1) {
                yytext = input.substring(startPosition, lastMatchPosition);
                position = lastMatchPosition;
                if (isIgnoreState(lastAcceptingState)) {
                    continue; // Ignore token and restart loop for next token
                }
                return doAction(lastAcceptingState);
            } else {
                yytext = input.substring(startPosition, Math.min(startPosition + 1, input.length()));
                position = startPosition + 1;
                throw new Exception("Invalid character: '" + yytext + "' at position " + startPosition);
            }
        }
    }

    private int getNextState(int state, char c) {
        switch (state) {
            case 0: {
                if ((c >= '\t' && c <= '\n') || c == ' ') return 1;
                if (c == '(') return 2;
                if (c == ')') return 3;
                if (c == '*') return 4;
                if (c == '+') return 5;
                if (c == ',') return 6;
                if (c == '-') return 7;
                if (c == '/') return 8;
                if ((c >= '0' && c <= '9')) return 9;
                if (c == ';') return 10;
                if (c == '=') return 11;
                if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) return 12;
                if (c == '[') return 13;
                if (c == ']') return 14;
                if (c == '{') return 15;
                if (c == '}') return 16;
                return -1; // No transition for this character
            }
            case 1: {
                if ((c >= '\t' && c <= '\n') || c == ' ') return 1;
                return -1; // No transition for this character
            }
            case 2: {
            }
            case 3: {
            }
            case 4: {
            }
            case 5: {
            }
            case 6: {
            }
            case 7: {
            }
            case 8: {
            }
            case 9: {
                if ((c >= '0' && c <= '9')) return 9;
                return -1; // No transition for this character
            }
            case 10: {
            }
            case 11: {
            }
            case 12: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 12;
                return -1; // No transition for this character
            }
            case 13: {
            }
            case 14: {
            }
            case 15: {
            }
            case 16: {
            }
            default: return -1;
        }
    }

    private boolean isAccepting(int state) {
        switch (state) {
            case 1: return true;
            case 2: return true;
            case 3: return true;
            case 4: return true;
            case 5: return true;
            case 6: return true;
            case 7: return true;
            case 8: return true;
            case 9: return true;
            case 10: return true;
            case 11: return true;
            case 12: return true;
            case 13: return true;
            case 14: return true;
            case 15: return true;
            case 16: return true;
            default: return false;
        }
    }

    private boolean isIgnoreState(int state) {
        switch (state) {
            default: return false;
        }
    }

    private Object doAction(int state) throws Exception {
        switch (state) {
            case 1: {
                return "WHITESPACE";
            }
            case 2: {
                return "LPAREN";
            }
            case 3: {
                return "RPAREN";
            }
            case 4: {
                return "TIMES";
            }
            case 5: {
                return "PLUS";
            }
            case 6: {
                return "COMMA";
            }
            case 7: {
                return "MINUS";
            }
            case 8: {
                return "DIV";
            }
            case 9: {
                return "NUMBER";
            }
            case 10: {
                return "SEMICOLON";
            }
            case 11: {
                return "ASSIGN";
            }
            case 12: {
                return "ID";
            }
            case 13: {
                return "LBRACKET";
            }
            case 14: {
                return "RBRACKET";
            }
            case 15: {
                return "LBRACE";
            }
            case 16: {
                return "RBRACE";
            }
            default: throw new Exception("Internal lexer error: No action for state " + state);
        }
    }



}
