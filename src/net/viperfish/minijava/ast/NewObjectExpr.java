/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public class NewObjectExpr extends NewExpr {
    public ClassType classtype;

    public NewObjectExpr(ClassType ct, SourcePosition posn) {
        super(posn);
        classtype = ct;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitNewObjectExpr(this, o);
    }
}
