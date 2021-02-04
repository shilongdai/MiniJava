package net.viperfish.minijava.ebnf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompositeSymbol implements Symbol {

    private List<Symbol> symbols;
    private String name;

    public CompositeSymbol(List<Symbol> symbols) {
        this.symbols = new ArrayList<>(symbols);

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Symbol s : this.symbols) {
            sb.append(s.getName());
        }
        sb.append(")");
        this.name = sb.toString();
    }

    public CompositeSymbol(String name, List<Symbol> symbols) {
        this(symbols);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Symbol> getExpression() {
        return new ArrayList<>(symbols);
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
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeSymbol that = (CompositeSymbol) o;
        return Objects.equals(symbols, that.symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbols);
    }
}
