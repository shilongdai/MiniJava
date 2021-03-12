/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class RefExpr extends Expression {
    public Reference ref;

    public RefExpr(Reference r, SourcePosition posn) {
        super(posn);
        ref = r;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitRefExpr(this, o);
    }
}
