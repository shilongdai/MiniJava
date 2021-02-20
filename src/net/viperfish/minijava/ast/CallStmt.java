/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public class CallStmt extends Statement {
    public Reference methodRef;
    public ExprList argList;

    public CallStmt(Reference m, ExprList el, SourcePosition posn) {
        super(posn);
        methodRef = m;
        argList = el;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitCallStmt(this, o);
    }
}