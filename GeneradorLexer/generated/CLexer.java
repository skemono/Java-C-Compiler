package generated;

import java.util.*;



public class CLexer {

    private String input;
    private int position = 0;
    private int line = 1;
    public String yytext;

    public CLexer(String input) {
        this.input = input;
    }

    public CLexer(java.io.File file) throws java.io.IOException {
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
                if ((c >= '\t' && c <= '\n') || c == '\r' || c == ' ') return 1;
                if (c == '!') return 2;
                if (c == '%') return 3;
                if (c == '&') return 4;
                if (c == 'w') return 69;
                if (c == '(') return 5;
                if (c == ')') return 6;
                if (c == '*') return 7;
                if (c == 'v') return 72;
                if (c == '+') return 8;
                if (c == ',') return 9;
                if (c == '-') return 10;
                if (c == '.') return 11;
                if (c == 'b') return 76;
                if (c == '/') return 12;
                if (c == '0') return 13;
                if (c == ';') return 14;
                if (c == '<') return 15;
                if (c == 'c') return 80;
                if (c == '=') return 16;
                if (c == 'f') return 81;
                if (c == '>') return 17;
                if (c == 'e') return 83;
                if (c == '[') return 19;
                if (c == ']') return 20;
                if (c == '{') return 21;
                if (c == '|') return 22;
                if (c == '}') return 23;
                if ((c >= 'A' && c <= 'Z') || c == '_' || c == 'a' || c == 'd' || (c >= 'g' && c <= 'h') || (c >= 'j' && c <= 'q') || c == 'u' || (c >= 'x' && c <= 'z')) return 97;
                if (c == 's') return 98;
                if (c == 'r') return 100;
                if (c == 't') return 102;
                if ((c >= '1' && c <= '9')) return 52;
                if (c == 'i') return 57;
                return -1;
            }
            case 1: {
                if ((c >= '\t' && c <= '\n') || c == '\r' || c == ' ') return 1;
                return -1;
            }
            case 2: {
                if (c == '=') return 24;
                return -1;
            }
            case 3: {
                return -1;
            }
            case 4: {
                if (c == '&') return 25;
                return -1;
            }
            case 5: {
                return -1;
            }
            case 6: {
                return -1;
            }
            case 7: {
                return -1;
            }
            case 8: {
                if (c == '+') return 26;
                if (c == '=') return 27;
                return -1;
            }
            case 9: {
                return -1;
            }
            case 10: {
                if (c == '-') return 28;
                if (c == '=') return 29;
                if (c == '>') return 30;
                return -1;
            }
            case 11: {
                return -1;
            }
            case 12: {
                return -1;
            }
            case 13: {
                if (c == 'X' || c == 'x') return 96;
                if (c == '.') return 51;
                if ((c >= '0' && c <= '9')) return 52;
                return -1;
            }
            case 14: {
                return -1;
            }
            case 15: {
                if (c == '=') return 31;
                return -1;
            }
            case 16: {
                if (c == '=') return 32;
                return -1;
            }
            case 17: {
                if (c == '=') return 33;
                return -1;
            }
            case 18: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'q') || (c >= 's' && c <= 'z')) return 97;
                if (c == 'r') return 92;
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
                if (c == '|') return 35;
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
                return -1;
            }
            case 29: {
                return -1;
            }
            case 30: {
                return -1;
            }
            case 31: {
                return -1;
            }
            case 32: {
                return -1;
            }
            case 33: {
                return -1;
            }
            case 34: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 35: {
                return -1;
            }
            case 36: {
                if ((c >= '0' && c <= '9')) return 36;
                return -1;
            }
            case 37: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) return 37;
                return -1;
            }
            case 38: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 39: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 40: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 41: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 42: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 43: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 44: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 45: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 46: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 47: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 48: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 49: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 50: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 51: {
                if ((c >= '0' && c <= '9')) return 36;
                return -1;
            }
            case 52: {
                if (c == '.') return 51;
                if ((c >= '0' && c <= '9')) return 52;
                return -1;
            }
            case 53: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'c') || (c >= 'e' && c <= 'z')) return 97;
                if (c == 'd') return 42;
                return -1;
            }
            case 54: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 97;
                if (c == 'e') return 41;
                return -1;
            }
            case 55: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 97;
                if (c == 'e') return 45;
                return -1;
            }
            case 56: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 97;
                if (c == 'e') return 50;
                return -1;
            }
            case 57: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'e') || (c >= 'g' && c <= 'm') || (c >= 'o' && c <= 'z')) return 97;
                if (c == 'f') return 34;
                if (c == 'n') return 86;
                return -1;
            }
            case 58: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'e') || (c >= 'g' && c <= 'z')) return 97;
                if (c == 'f') return 47;
                return -1;
            }
            case 59: {
                if (c == 'f') return 49;
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'e') || (c >= 'g' && c <= 'z')) return 97;
                return -1;
            }
            case 60: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 97;
                if (c == 'e') return 59;
                return -1;
            }
            case 61: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'c') || (c >= 'e' && c <= 'z')) return 97;
                if (c == 'd') return 60;
                return -1;
            }
            case 62: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 97;
                if (c == 'e') return 61;
                return -1;
            }
            case 63: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'h') || (c >= 'j' && c <= 'z')) return 97;
                if (c == 'i') return 53;
                return -1;
            }
            case 64: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'j') || (c >= 'l' && c <= 'z')) return 97;
                if (c == 'k') return 43;
                return -1;
            }
            case 65: {
                if (c == 'a') return 64;
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'b' && c <= 'z')) return 97;
                return -1;
            }
            case 66: {
                if (c == 'e') return 65;
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 97;
                return -1;
            }
            case 67: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'k') || (c >= 'm' && c <= 'z')) return 97;
                if (c == 'l') return 55;
                return -1;
            }
            case 68: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'h') || (c >= 'j' && c <= 'z')) return 97;
                if (c == 'i') return 67;
                return -1;
            }
            case 69: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'g') || (c >= 'i' && c <= 'z')) return 97;
                if (c == 'h') return 68;
                return -1;
            }
            case 70: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'm') || (c >= 'o' && c <= 'z')) return 97;
                if (c == 'n') return 46;
                return -1;
            }
            case 71: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'n') || (c >= 'p' && c <= 'z')) return 97;
                if (c == 'o') return 58;
                return -1;
            }
            case 72: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'n') || (c >= 'p' && c <= 'z')) return 97;
                if (c == 'o') return 63;
                return -1;
            }
            case 73: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 97;
                if (c == 'e') return 71;
                return -1;
            }
            case 74: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'o') || (c >= 'q' && c <= 'z')) return 97;
                if (c == 'p') return 62;
                return -1;
            }
            case 75: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'q') || (c >= 's' && c <= 'z')) return 97;
                if (c == 'r') return 40;
                return -1;
            }
            case 76: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'q') || (c >= 's' && c <= 'z')) return 97;
                if (c == 'r') return 66;
                return -1;
            }
            case 77: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'q') || (c >= 's' && c <= 'z')) return 97;
                if (c == 'r') return 70;
                return -1;
            }
            case 78: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'q') || (c >= 's' && c <= 'z')) return 97;
                if (c == 'r') return 38;
                return -1;
            }
            case 79: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'b' && c <= 'z')) return 97;
                if (c == 'a') return 75;
                return -1;
            }
            case 80: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'g') || (c >= 'i' && c <= 'n') || (c >= 'p' && c <= 'z')) return 97;
                if (c == 'o') return 101;
                if (c == 'h') return 79;
                return -1;
            }
            case 81: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'k') || (c >= 'm' && c <= 'n') || (c >= 'p' && c <= 'z')) return 97;
                if (c == 'l') return 89;
                if (c == 'o') return 78;
                return -1;
            }
            case 82: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'r') || (c >= 't' && c <= 'z')) return 97;
                if (c == 's') return 54;
                return -1;
            }
            case 83: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'k') || (c >= 'm' && c <= 'z')) return 97;
                if (c == 'l') return 82;
                return -1;
            }
            case 84: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 's') || (c >= 'u' && c <= 'z')) return 97;
                if (c == 't') return 44;
                return -1;
            }
            case 85: {
                if (c == 't') return 48;
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 's') || (c >= 'u' && c <= 'z')) return 97;
                return -1;
            }
            case 86: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 's') || (c >= 'u' && c <= 'z')) return 97;
                if (c == 't') return 39;
                return -1;
            }
            case 87: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'b' && c <= 'z')) return 97;
                if (c == 'a') return 84;
                return -1;
            }
            case 88: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'b') || (c >= 'd' && c <= 'z')) return 97;
                if (c == 'c') return 85;
                return -1;
            }
            case 89: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'n') || (c >= 'p' && c <= 'z')) return 97;
                if (c == 'o') return 87;
                return -1;
            }
            case 90: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 't') || (c >= 'v' && c <= 'z')) return 97;
                if (c == 'u') return 56;
                return -1;
            }
            case 91: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 't') || (c >= 'v' && c <= 'z')) return 97;
                if (c == 'u') return 77;
                return -1;
            }
            case 92: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 't') || (c >= 'v' && c <= 'z')) return 97;
                if (c == 'u') return 88;
                return -1;
            }
            case 93: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'm') || (c >= 'o' && c <= 'z')) return 97;
                if (c == 'n') return 90;
                return -1;
            }
            case 94: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'h') || (c >= 'j' && c <= 'z')) return 97;
                if (c == 'i') return 93;
                return -1;
            }
            case 95: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 's') || (c >= 'u' && c <= 'z')) return 97;
                if (c == 't') return 94;
                return -1;
            }
            case 96: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) return 37;
                return -1;
            }
            case 97: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z')) return 97;
                return -1;
            }
            case 98: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'h') || (c >= 'j' && c <= 's') || (c >= 'u' && c <= 'z')) return 97;
                if (c == 't') return 18;
                if (c == 'i') return 103;
                return -1;
            }
            case 99: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 's') || (c >= 'u' && c <= 'z')) return 97;
                if (c == 't') return 91;
                return -1;
            }
            case 100: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'd') || (c >= 'f' && c <= 'z')) return 97;
                if (c == 'e') return 99;
                return -1;
            }
            case 101: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'm') || (c >= 'o' && c <= 'z')) return 97;
                if (c == 'n') return 95;
                return -1;
            }
            case 102: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'x') || c == 'z') return 97;
                if (c == 'y') return 74;
                return -1;
            }
            case 103: {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'y')) return 97;
                if (c == 'z') return 73;
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
            case 39: return true;
            case 40: return true;
            case 41: return true;
            case 42: return true;
            case 43: return true;
            case 44: return true;
            case 45: return true;
            case 46: return true;
            case 47: return true;
            case 48: return true;
            case 49: return true;
            case 50: return true;
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
            case 67: return true;
            case 68: return true;
            case 69: return true;
            case 70: return true;
            case 71: return true;
            case 72: return true;
            case 73: return true;
            case 74: return true;
            case 75: return true;
            case 76: return true;
            case 77: return true;
            case 78: return true;
            case 79: return true;
            case 80: return true;
            case 81: return true;
            case 82: return true;
            case 83: return true;
            case 84: return true;
            case 85: return true;
            case 86: return true;
            case 87: return true;
            case 88: return true;
            case 89: return true;
            case 90: return true;
            case 91: return true;
            case 92: return true;
            case 93: return true;
            case 94: return true;
            case 95: return true;
            case 97: return true;
            case 98: return true;
            case 99: return true;
            case 100: return true;
            case 101: return true;
            case 102: return true;
            case 103: return true;
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
                return "NOT";
            }
            case 3: {
                return "MOD";
            }
            case 4: {
                return "AMP";
            }
            case 5: {
                return "LPAREN";
            }
            case 6: {
                return "RPAREN";
            }
            case 7: {
                return "STAR";
            }
            case 8: {
                return "PLUS";
            }
            case 9: {
                return "COMMA";
            }
            case 10: {
                return "MINUS";
            }
            case 11: {
                return "DOT";
            }
            case 12: {
                return "DIV";
            }
            case 13: {
                return "INT_LIT";
            }
            case 14: {
                return "SEMI";
            }
            case 15: {
                return "LT";
            }
            case 16: {
                return "ASSIGN";
            }
            case 17: {
                return "GT";
            }
            case 18: {
                return "ID";
            }
            case 19: {
                return "LBRACK";
            }
            case 20: {
                return "RBRACK";
            }
            case 21: {
                return "LBRACE";
            }
            case 22: {
                return "PIPE";
            }
            case 23: {
                return "RBRACE";
            }
            case 24: {
                return "NEQ";
            }
            case 25: {
                return "AND";
            }
            case 26: {
                return "INC";
            }
            case 27: {
                return "PLUS_ASSIGN";
            }
            case 28: {
                return "DEC";
            }
            case 29: {
                return "MINUS_ASSIGN";
            }
            case 30: {
                return "ARROW";
            }
            case 31: {
                return "LEQ";
            }
            case 32: {
                return "EQ";
            }
            case 33: {
                return "GEQ";
            }
            case 34: {
                return "IF";
            }
            case 35: {
                return "OR";
            }
            case 36: {
                return "FLOAT_LIT";
            }
            case 37: {
                return "HEX_LIT";
            }
            case 38: {
                return "FOR";
            }
            case 39: {
                return "INT";
            }
            case 40: {
                return "CHAR_TYPE";
            }
            case 41: {
                return "ELSE";
            }
            case 42: {
                return "VOID";
            }
            case 43: {
                return "BREAK";
            }
            case 44: {
                return "FLOAT";
            }
            case 45: {
                return "WHILE";
            }
            case 46: {
                return "RETURN";
            }
            case 47: {
                return "SIZEOF";
            }
            case 48: {
                return "STRUCT";
            }
            case 49: {
                return "TYPEDEF";
            }
            case 50: {
                return "CONTINUE";
            }
            case 52: {
                return "INT_LIT";
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
            case 67: {
                return "ID";
            }
            case 68: {
                return "ID";
            }
            case 69: {
                return "ID";
            }
            case 70: {
                return "ID";
            }
            case 71: {
                return "ID";
            }
            case 72: {
                return "ID";
            }
            case 73: {
                return "ID";
            }
            case 74: {
                return "ID";
            }
            case 75: {
                return "ID";
            }
            case 76: {
                return "ID";
            }
            case 77: {
                return "ID";
            }
            case 78: {
                return "ID";
            }
            case 79: {
                return "ID";
            }
            case 80: {
                return "ID";
            }
            case 81: {
                return "ID";
            }
            case 82: {
                return "ID";
            }
            case 83: {
                return "ID";
            }
            case 84: {
                return "ID";
            }
            case 85: {
                return "ID";
            }
            case 86: {
                return "ID";
            }
            case 87: {
                return "ID";
            }
            case 88: {
                return "ID";
            }
            case 89: {
                return "ID";
            }
            case 90: {
                return "ID";
            }
            case 91: {
                return "ID";
            }
            case 92: {
                return "ID";
            }
            case 93: {
                return "ID";
            }
            case 94: {
                return "ID";
            }
            case 95: {
                return "ID";
            }
            case 97: {
                return "ID";
            }
            case 98: {
                return "ID";
            }
            case 99: {
                return "ID";
            }
            case 100: {
                return "ID";
            }
            case 101: {
                return "ID";
            }
            case 102: {
                return "ID";
            }
            case 103: {
                return "ID";
            }
            default: return null;
        }
    }



}
