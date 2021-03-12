/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class IxAssignStmt extends Statement {
    public Reference ref;
    public Expression ix;
    public Expression exp;
    public IxAssignStmt(Reference r, Expression i, Expression e, SourcePosition posn) {
        super(posn);
        ref = r;
        ix = i;
        exp = e;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitIxAssignStmt(this, o);
    }
}
