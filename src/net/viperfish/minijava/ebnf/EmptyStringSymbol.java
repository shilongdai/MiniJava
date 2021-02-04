package net.viperfish.minijava.ebnf;

class EmptyStringSymbol extends StandardTerminalSymbol {

    public EmptyStringSymbol() {
        super("ε");
    }

    @Override
    public boolean isInstance(String token) {
        return true;
    }

    @Override
    public String toString() {
        return super.getName();
    }
}
