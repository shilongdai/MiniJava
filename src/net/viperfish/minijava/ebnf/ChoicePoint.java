package net.viperfish.minijava.ebnf;

import java.util.Objects;

class ChoicePoint {

    private Symbol parentSymbol;
    private int symbolPosition;

    public ChoicePoint(Symbol parentSymbol, int symbolPosition) {
        this.parentSymbol = parentSymbol;
        this.symbolPosition = symbolPosition;
    }

    public Symbol getParentSymbol() {
        return parentSymbol;
    }

    public int getSymbolPosition() {
        return symbolPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChoicePoint that = (ChoicePoint) o;
        return symbolPosition == that.symbolPosition &&
                Objects.equals(parentSymbol, that.parentSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentSymbol, symbolPosition);
    }
}
