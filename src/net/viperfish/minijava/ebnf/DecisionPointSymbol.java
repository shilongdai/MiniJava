package net.viperfish.minijava.ebnf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DecisionPointSymbol implements Symbol {

    private List<Symbol> src;
    private String name;

    public DecisionPointSymbol(List<Symbol> src) {
        this.src = new ArrayList<>(src);

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Symbol s : this.src) {
            sb.append(s.getName()).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        this.name = sb.toString();
    }

    public DecisionPointSymbol(String name, List<Symbol> src) {
        this(src);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Symbol> getExpression() {
        return new ArrayList<>(src);
    }

    @Override
    public boolean isDecision() {
        return true;
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
        DecisionPointSymbol that = (DecisionPointSymbol) o;
        return Objects.equals(src, that.src);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src);
    }
}
