/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public abstract class NewExpr extends Expression {

    public NewExpr(SourcePosition posn) {
        super(posn);
    }
}
