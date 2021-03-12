package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.Token;

public class ReturnTerminal extends Terminal {
    public ReturnTerminal(Token t) {
        super(t);
    }

    @Override
    public <A, R> R visit(Visitor<A, R> v, A o) {
        throw new UnsupportedOperationException("This should not be in the final AST Tree");
    }
}
