package generated;

import java.util.*;



public class Lexer10 {

    private String input;
    private int position = 0;
    private int line = 1;
    public String yytext;

    public Lexer10(String input) {
        this.input = input;
    }

    public Lexer10(java.io.File file) throws java.io.IOException {
        this.input = new String(java.nio.file.Files.readAllBytes(file.toPath()));
    }

    public String yylex() {
        while (true) {
            if (position >= input.length()) return null;

            int startPosition = position;
            int currentState = 0;
            int lastAcceptingState = -1;
            int lastMatchPosition = -1;

            for (int i = startPosition; i < input.length(); i++) {
                char c = input.charAt(i);
                int nextState = getNextState(currentState, c);
                if (nextState == -1) break;
                currentState = nextState;
                if (isAccepting(currentState)) {
                    lastAcceptingState = currentState;
                    lastMatchPosition = i + 1;
                }
            }

            if (lastAcceptingState != -1) {
                yytext = input.substring(startPosition, lastMatchPosition);
                // Update line counter from consumed lexeme
                for (int k = 0; k < yytext.length(); k++)
                    if (yytext.charAt(k) == '\n') line++;
                position = lastMatchPosition;
                if (isIgnoreState(lastAcceptingState)) continue;
                return doAction(lastAcceptingState);
            } else {
                // Lexical error: print, skip bad char, continue — never stop
                char bad = input.charAt(startPosition);
                System.out.println("ERROR LEXICO: '" + bad + "' no reconocido en linea " + line);
                if (bad == '\n') line++;
                position = startPosition + 1;
            }
        }
    }

    public void scan() {
        String token;
        while ((token = yylex()) != null) {
            System.out.println("TOKEN: [" + token + ", \"" + yytext + "\"]");
        }
    }

    public int getLine() { return line; }

    private int getNextState(int state, char c) {
        switch (state) {
            case 0: {
                if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'd') || (c >= 'g' && c <= 'h') || (c >= 'j' && c <= 'q') || (c >= 's' && c <= 'u') || (c >= 'x' && c <= 'z')) return 65;
                if ((c >= '\t' && c <= '\n') || c == '\r' || c == ' ') return 1;
                if (c == 'r') return 66;
                if (c == '%') return 2;
                if (c == '(') return 3;
                if (c == ')') return 4;
                if (c == '*') return 5;
                if (c == '+') return 6;
                if (c == ',') return 7;
                if (c == '-') return 8;
                if (c == '.') return 9;
                if (c == '/') return 10;
                if (c == '0') return 11;
                if (c == ';') return 12;
                if (c == '<') return 13;
                if (c == '=') return 14;
                if (c == '>') return 15;
                if (c == '[') return 17;
                if (c == ']') return 18;
                if (c == '{') return 19;
                if (c == '}') return 20;
                if (c == '!') return 39;
                if ((c >= '1' && c <= '9')) return 40;
                if (c == 'i') return 44;
                if (c == '&') return 47;
                if (c == '|') return 50;
                if (c == 'w') return 52;
                if (c == 'v') return 54;
                if (c == 'f') return 57;
                if (c == 'e') return 59;
                return -1;
            }
            case 1: {
                if ((c >= '\t' && c <= '\n') || c == '\r' || c == ' ') return 1;
                return -1;
            }
            case 2: {
                return -1;
            }
            case 3: {
                return -1;
            }
            case 4: {
                return -1;
            }
            case 5: {
                return -1;
            }
            case 6: {
                if (c == '+') return 23;
                return -1;
            }
            case 7: {
                return -1;
            }
            case 8: {
                if (c == '-') return 24;
                return -1;
            }
            case 9: {
                return -1;
            }
            case 10: {
                return -1;
            }
            case 11: {
                if (c == '.') return 48;
                if (c == 'x') return 49;
                if ((c >= '0' && c <= '9')) return 40;
                return -1;
            }
            case 12: {
                return -1;
            }
            case 13: {
                if (c == '=') return 25;
                return -1;
            }
            case 14: {
                if (c == '=') return 26;
                return -1;
            }
            case 15: {
                if (c == '=') return 27;
                return -1;
            }
            case 16: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'k') || (c >= 'm' && c <= 'z')) return 65;
                if (c == 'l') return 42;
                return -1;
            }
            case 17: {
                return -1;
            }
            case 18: {
                return -1;
            }
            case 19: {
                return -1;
            }
            case 20: {
                return -1;
            }
            case 21: {
                return -1;
            }
            case 22: {
                return -1;
            }
            case 23: {
                return -1;
            }
            case 24: {
                return -1;
            }
            case 25: {
                return -1;
            }
            case 26: {
                return -1;
            }
            case 27: {
                return -1;
            }
            case 28: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 29: {
                return -1;
            }
            case 30: {
                if ((c >= '0' && c <= '9')) return 30;
                return -1;
            }
            case 31: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) return 31;
                return -1;
            }
            case 32: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 33: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 34: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 35: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 36: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 37: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 38: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 39: {
                if (c == '=') return 21;
                return -1;
            }
            case 40: {
                if (c == '.') return 48;
                if ((c >= '0' && c <= '9')) return 40;
                return -1;
            }
            case 41: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'c') || (c >= 'e' && c <= 'z')) return 65;
                if (c == 'd') return 35;
                return -1;
            }
            case 42: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 65;
                if (c == 'e') return 37;
                return -1;
            }
            case 43: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 65;
                if (c == 'e') return 34;
                return -1;
            }
            case 44: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'e') || (c >= 'g' && c <= 'm') || (c >= 'o' && c <= 'z')) return 65;
                if (c == 'f') return 28;
                if (c == 'n') return 61;
                return -1;
            }
            case 45: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'h') || (c >= 'j' && c <= 'z')) return 65;
                if (c == 'i') return 41;
                return -1;
            }
            case 46: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 's') || (c >= 'u' && c <= 'z')) return 65;
                if (c == 't') return 36;
                return -1;
            }
            case 47: {
                if (c == '&') return 22;
                return -1;
            }
            case 48: {
                if ((c >= '0' && c <= '9')) return 30;
                return -1;
            }
            case 49: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) return 31;
                return -1;
            }
            case 50: {
                if (c == '|') return 29;
                return -1;
            }
            case 51: {
                if (c == 'i') return 16;
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'h') || (c >= 'j' && c <= 'z')) return 65;
                return -1;
            }
            case 52: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'g') || (c >= 'i' && c <= 'z')) return 65;
                if (c == 'h') return 51;
                return -1;
            }
            case 53: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'm') || (c >= 'o' && c <= 'z')) return 65;
                if (c == 'n') return 38;
                return -1;
            }
            case 54: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'n') || (c >= 'p' && c <= 'z')) return 65;
                if (c == 'o') return 45;
                return -1;
            }
            case 55: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'q') || (c >= 's' && c <= 'z')) return 65;
                if (c == 'r') return 53;
                return -1;
            }
            case 56: {
                if (c == 'r') return 32;
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'q') || (c >= 's' && c <= 'z')) return 65;
                return -1;
            }
            case 57: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'k') || (c >= 'm' && c <= 'n') || (c >= 'p' && c <= 'z')) return 65;
                if (c == 'o') return 56;
                if (c == 'l') return 63;
                return -1;
            }
            case 58: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'r') || (c >= 't' && c <= 'z')) return 65;
                if (c == 's') return 43;
                return -1;
            }
            case 59: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'k') || (c >= 'm' && c <= 'z')) return 65;
                if (c == 'l') return 58;
                return -1;
            }
            case 60: {
                if (c == 't') return 64;
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 's') || (c >= 'u' && c <= 'z')) return 65;
                return -1;
            }
            case 61: {
                if (c == 't') return 33;
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 's') || (c >= 'u' && c <= 'z')) return 65;
                return -1;
            }
            case 62: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'b' && c <= 'z')) return 65;
                if (c == 'a') return 46;
                return -1;
            }
            case 63: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'n') || (c >= 'p' && c <= 'z')) return 65;
                if (c == 'o') return 62;
                return -1;
            }
            case 64: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 't') || (c >= 'v' && c <= 'z')) return 65;
                if (c == 'u') return 55;
                return -1;
            }
            case 65: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 65;
                return -1;
            }
            case 66: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 65;
                if (c == 'e') return 60;
                return -1;
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
            case 17: return true;
            case 18: return true;
            case 19: return true;
            case 20: return true;
            case 21: return true;
            case 22: return true;
            case 23: return true;
            case 24: return true;
            case 25: return true;
            case 26: return true;
            case 27: return true;
            case 28: return true;
            case 29: return true;
            case 30: return true;
            case 31: return true;
            case 32: return true;
            case 33: return true;
            case 34: return true;
            case 35: return true;
            case 36: return true;
            case 37: return true;
            case 38: return true;
            case 40: return true;
            case 41: return true;
            case 42: return true;
            case 43: return true;
            case 44: return true;
            case 45: return true;
            case 46: return true;
            case 51: return true;
            case 52: return true;
            case 53: return true;
            case 54: return true;
            case 55: return true;
            case 56: return true;
            case 57: return true;
            case 58: return true;
            case 59: return true;
            case 60: return true;
            case 61: return true;
            case 62: return true;
            case 63: return true;
            case 64: return true;
            case 65: return true;
            case 66: return true;
            default: return false;
        }
    }

    private boolean isIgnoreState(int state) {
        switch (state) {
            case 1: return true;
            default: return false;
        }
    }

    private String doAction(int state) {
        switch (state) {
            case 2: {
                return "MOD";
            }
            case 3: {
                return "LPAREN";
            }
            case 4: {
                return "RPAREN";
            }
            case 5: {
                return "TIMES";
            }
            case 6: {
                return "PLUS";
            }
            case 7: {
                return "COMMA";
            }
            case 8: {
                return "MINUS";
            }
            case 9: {
                return "DOT";
            }
            case 10: {
                return "DIV";
            }
            case 11: {
                return "INTEGER";
            }
            case 12: {
                return "SEMICOLON";
            }
            case 13: {
                return "LT";
            }
            case 14: {
                return "ASSIGN";
            }
            case 15: {
                return "GT";
            }
            case 16: {
                return "ID";
            }
            case 17: {
                return "LBRACKET";
            }
            case 18: {
                return "RBRACKET";
            }
            case 19: {
                return "LBRACE";
            }
            case 20: {
                return "RBRACE";
            }
            case 21: {
                return "NEQ";
            }
            case 22: {
                return "AND";
            }
            case 23: {
                return "INC";
            }
            case 24: {
                return "DEC";
            }
            case 25: {
                return "LEQ";
            }
            case 26: {
                return "EQ";
            }
            case 27: {
                return "GEQ";
            }
            case 28: {
                return "IF";
            }
            case 29: {
                return "OR";
            }
            case 30: {
                return "DECIMAL";
            }
            case 31: {
                return "HEX";
            }
            case 32: {
                return "FOR";
            }
            case 33: {
                return "INT";
            }
            case 34: {
                return "ELSE";
            }
            case 35: {
                return "VOID";
            }
            case 36: {
                return "FLOAT";
            }
            case 37: {
                return "WHILE";
            }
            case 38: {
                return "RETURN";
            }
            case 40: {
                return "INTEGER";
            }
            case 41: {
                return "ID";
            }
            case 42: {
                return "ID";
            }
            case 43: {
                return "ID";
            }
            case 44: {
                return "ID";
            }
            case 45: {
                return "ID";
            }
            case 46: {
                return "ID";
            }
            case 51: {
                return "ID";
            }
            case 52: {
                return "ID";
            }
            case 53: {
                return "ID";
            }
            case 54: {
                return "ID";
            }
            case 55: {
                return "ID";
            }
            case 56: {
                return "ID";
            }
            case 57: {
                return "ID";
            }
            case 58: {
                return "ID";
            }
            case 59: {
                return "ID";
            }
            case 60: {
                return "ID";
            }
            case 61: {
                return "ID";
            }
            case 62: {
                return "ID";
            }
            case 63: {
                return "ID";
            }
            case 64: {
                return "ID";
            }
            case 65: {
                return "ID";
            }
            case 66: {
                return "ID";
            }
            default: return null;
        }
    }



}
