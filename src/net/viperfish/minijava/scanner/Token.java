package net.viperfish.minijava.scanner;

import java.util.Objects;

public class Token {

    private TokenType tokenType;
    private String spelling;
    private SourcePosition position;

    public Token(TokenType tokenType, String spelling, SourcePosition pos) {
        this.tokenType = tokenType;
        this.spelling = spelling;
        this.position = pos;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getSpelling() {
        return spelling;
    }

    public SourcePosition getPosition() {
        return this.position;
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
