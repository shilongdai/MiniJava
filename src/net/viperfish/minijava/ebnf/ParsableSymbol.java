package net.viperfish.minijava.ebnf;

import net.viperfish.minijava.scanner.Token;

public interface ParsableSymbol extends Symbol {

    public boolean isInstance(Token token);

}
