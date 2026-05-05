package lexerGen.util;

import java.util.Set;

public class RegexToken {
    // Enum for different types of regex tokens.
    public enum Type {
        CHAR, CLASS, ANY, STAR, PLUS, QUESTION, CONCAT, UNION, DIFF, LPAREN, RPAREN
    }

    public Type type;
    public char singleChar; // For CHAR tokens, holds the character.
    public boolean isComplement; // For CLASS tokens, indicates if it's a complement class (e.g., [^abc]).
    public Set<Character> charSet; // For CLASS tokens, holds the set of characters in the class.

    // Constructor for tokens without additional data.
    public RegexToken(Type type) {
        this.type = type;
    }

    // Constructor for single character tokens.
    public RegexToken(Type type, char singleChar) {
        this.type = type;
        this.singleChar = singleChar;
    }

    // Constructor for character class tokens.
    public RegexToken(Type type, Set<Character> charSet) {
        this(type, charSet, false); // Default to non-complement class
    }
    
    // Constructor for character class tokens with isComplement.
    public RegexToken(Type type, Set<Character> charSet, boolean isComplement) {
        this.type = type;
        this.charSet = charSet;
        this.isComplement = isComplement;
    }

    @Override
    // Method for debugging, shows token type and relevant data.
    public String toString() {
        if (this.type == Type.CHAR) return "CHAR('" + singleChar + "')";
        if (this.type == Type.CLASS) return "CLASS(" + charSet + ")";
        return this.type.toString();
    }
}

