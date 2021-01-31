package net.viperfish.comp520.ebnf;

public interface ParsableSymbol extends Symbol {

    public boolean isInstance(String token);

}
