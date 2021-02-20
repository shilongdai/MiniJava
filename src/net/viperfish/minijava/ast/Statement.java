/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public abstract class Statement extends AST {

    public Statement(SourcePosition posn) {
        super(posn);
    }

}
