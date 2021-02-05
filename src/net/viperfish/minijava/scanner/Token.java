package net.viperfish.minijava.scanner;

import java.util.Objects;

public class Token {

    private TokenType tokenType;
    private String spelling;

    public Token(TokenType tokenType, String spelling) {
        this.tokenType = tokenType;
        this.spelling = spelling;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getSpelling() {
        return spelling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return tokenType == token.tokenType &&
                Objects.equals(spelling, token.spelling);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenType, spelling);
    }
}
