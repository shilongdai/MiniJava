package net.viperfish.minijava.ebnf;

import net.viperfish.minijava.scanner.Token;

class EmptyStringSymbol extends StandardTerminalSymbol {

    public EmptyStringSymbol() {
        super("ε");
    }

    @Override
    public boolean isInstance(Token token) {
        return true;
    }

    @Override
    public String toString() {
        return super.getName();
    }
}
