/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public class ReturnStmt extends Statement {
    public Expression returnExpr;

    public ReturnStmt(Expression e, SourcePosition posn) {
        super(posn);
        returnExpr = e;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitReturnStmt(this, o);
    }
}	
