package net.viperfish.minijava.ast;

public class NullExpr extends Expression {

    public NullLiteral nullLiteral;

    public NullExpr(NullLiteral literal) {
        super(literal.posn);
        this.nullLiteral = literal;
    }

    @Override
    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitNullExpr(this, o);
    }
}
