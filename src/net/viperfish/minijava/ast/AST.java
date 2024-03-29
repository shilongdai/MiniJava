/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;


import net.viperfish.minijava.scanner.SourcePosition;

public abstract class AST {

    public SourcePosition posn;

    public AST(SourcePosition posn) {
        this.posn = posn;
    }

    public String toString() {
        String fullClassName = this.getClass().getName();
        String cn = fullClassName.substring(1 + fullClassName.lastIndexOf('.'));
        if (ASTDisplay.showPosition) {
            String posnStr = "null";
            if(posn != null) {
                posnStr = posn.toString();
            }
            cn = cn + " " + posnStr;
        }
        return cn;
    }

    public abstract <A, R> R visit(Visitor<A, R> v, A o);
}
