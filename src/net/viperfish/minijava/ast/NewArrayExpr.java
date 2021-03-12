/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class NewArrayExpr extends NewExpr {
    public TypeDenoter eltType;
    public Expression sizeExpr;

    public NewArrayExpr(TypeDenoter et, Expression e, SourcePosition posn) {
        super(posn);
        eltType = et;
        sizeExpr = e;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitNewArrayExpr(this, o);
    }
}