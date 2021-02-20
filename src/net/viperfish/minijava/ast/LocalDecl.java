/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public abstract class LocalDecl extends Declaration {

    public LocalDecl(String name, TypeDenoter t, SourcePosition posn) {
        super(name, t, posn);
    }

}
