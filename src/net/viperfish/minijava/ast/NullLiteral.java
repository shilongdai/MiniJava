package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.Token;

public class NullLiteral extends Terminal {

    public NullLiteral(Token t) {
        super(t);
    }

    @Override
    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitNullLiteral(this, o);
    }
}
