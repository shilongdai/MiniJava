/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

abstract public class TypeDenoter extends AST {

    public TypeKind typeKind;

    public TypeDenoter(TypeKind type, SourcePosition posn) {
        super(posn);
        typeKind = type;
    }

}

        