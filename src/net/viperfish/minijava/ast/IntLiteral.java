/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.Token;

public class IntLiteral extends Terminal {

    public IntLiteral(Token t) {
        super(t);
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitIntLiteral(this, o);
    }
}