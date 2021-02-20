/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public class UnaryExpr extends Expression {
    public Operator operator;
    public Expression expr;

    public UnaryExpr(Operator o, Expression e, SourcePosition posn) {
        super(posn);
        operator = o;
        expr = e;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitUnaryExpr(this, o);
    }
}