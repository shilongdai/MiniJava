package net.viperfish.minijava.ebnf;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WildCardSymbol implements Symbol {

    private Symbol src;
    private String name;

    public WildCardSymbol(Symbol src) {
        this.src = src;
        this.name = src.getName() + "*";
    }

    public WildCardSymbol(String name, Symbol src) {
        this(src);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Symbol> getExpression() {
        return Collections.singletonList(src);
    }

    @Override
    public boolean isDecision() {
        return false;
    }

    @Override
    public boolean isWildcard() {
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WildCardSymbol that = (WildCardSymbol) o;
        return Objects.equals(src, that.src);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src);
    }
}
