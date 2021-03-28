/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */

package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

import java.util.Objects;

public class ArrayType extends TypeDenoter {

    public TypeDenoter eltType;

    public ArrayType(TypeDenoter eltType, SourcePosition posn) {
        super(TypeKind.ARRAY, posn);
        this.eltType = eltType;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitArrayType(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeDenoter)) return false;
        if (!super.equals(o)) return false;

        TypeDenoter baseType = (TypeDenoter) o;
        if(baseType.typeKind == TypeKind.ERROR) {
            return true;
        }
        if(baseType.typeKind == TypeKind.NULL) {
            return true;
        }

        if (!(baseType instanceof ArrayType)) return false;

        ArrayType arrayType = (ArrayType) o;
        return Objects.equals(eltType, arrayType.eltType);
    }

}

