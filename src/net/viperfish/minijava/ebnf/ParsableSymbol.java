package net.viperfish.minijava.ebnf;

public interface ParsableSymbol extends Symbol {

    public boolean isInstance(String token);

}
