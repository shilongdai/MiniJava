/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public abstract class Declaration extends AST {

    public String name;
    public TypeDenoter type;
    public Declaration(String name, TypeDenoter type, SourcePosition posn) {
        super(posn);
        this.name = name;
        this.type = type;
    }
}
