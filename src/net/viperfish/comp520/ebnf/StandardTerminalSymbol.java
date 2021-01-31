package net.viperfish.comp520.ebnf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StandardTerminalSymbol implements ParsableSymbol {

    private String token;

    public StandardTerminalSymbol(String token) {
        this.token = token;
    }

    @Override
    public String getName() {
        return token;
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
    public boolean isInstance(String token) {
        return token.equals(this.token);
    }

    @Override
    public String toString() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardTerminalSymbol that = (StandardTerminalSymbol) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
