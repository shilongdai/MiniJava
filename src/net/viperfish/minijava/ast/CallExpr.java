/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public class CallExpr extends Expression {
    public Reference functionRef;
    public ExprList argList;

    public CallExpr(Reference f, ExprList el, SourcePosition posn) {
        super(posn);
        functionRef = f;
        argList = el;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitCallExpr(this, o);
    }
}