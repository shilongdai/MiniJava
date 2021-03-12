/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class AssignStmt extends Statement {
    public Reference ref;
    public Expression val;

    public AssignStmt(Reference r, Expression e, SourcePosition posn) {
        super(posn);
        ref = r;
        val = e;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitAssignStmt(this, o);
    }
}