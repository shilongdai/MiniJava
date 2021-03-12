/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class WhileStmt extends Statement {
    public Expression cond;
    public Statement body;

    public WhileStmt(Expression e, Statement s, SourcePosition posn) {
        super(posn);
        cond = e;
        body = s;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitWhileStmt(this, o);
    }
}
