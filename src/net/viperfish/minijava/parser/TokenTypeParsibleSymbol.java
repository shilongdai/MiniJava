package net.viperfish.minijava.parser;

import net.viperfish.minijava.ebnf.ParsableSymbol;
import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TokenTypeParsibleSymbol implements ParsableSymbol {

    private TokenType type;

    public TokenTypeParsibleSymbol(TokenType type) {
        this.type = type;
    }

    @Override
    public boolean isInstance(Token token) {
        return token.getTokenType() == type;
    }

    @Override
    public String getName() {
        return type.name();
    }

    @Override
    public List<Symbol> getExpression() {
        return new ArrayList<>();
    }

    @Override
    public boolean isDecision() {
        return false;
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenTypeParsibleSymbol that = (TokenTypeParsibleSymbol) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return getName();
    }
}
