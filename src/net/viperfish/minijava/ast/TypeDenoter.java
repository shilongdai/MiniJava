/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

abstract public class TypeDenoter extends AST {

    public TypeKind typeKind;

    public TypeDenoter(TypeKind type, SourcePosition posn) {
        super(posn);
        typeKind = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeDenoter)) return false;

        TypeDenoter that = (TypeDenoter) o;

        if(that.typeKind == TypeKind.UNSUPPORTED || this.typeKind == TypeKind.UNSUPPORTED) {
            return false;
        }

        if(that.typeKind == TypeKind.ERROR || this.typeKind == TypeKind.ERROR) {
            return true;
        }

        if(that.typeKind == TypeKind.META || this.typeKind == TypeKind.META) {
            return false;
        }
        if(that.typeKind == TypeKind.VOID || this.typeKind == TypeKind.VOID) {
            return false;
        }

        if(that.typeKind == TypeKind.NULL && (this.typeKind == TypeKind.CLASS || this.typeKind == TypeKind.ARRAY)) {
            return true;
        }
        if(this.typeKind == TypeKind.NULL && (that.typeKind == TypeKind.CLASS || that.typeKind == TypeKind.ARRAY)) {
            return true;
        }

        return typeKind == that.typeKind;
    }

}

        