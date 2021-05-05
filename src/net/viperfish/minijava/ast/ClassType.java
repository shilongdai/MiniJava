/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class ClassType extends TypeDenoter {
    public Identifier className;

    public ClassType(Identifier cn, SourcePosition posn) {
        super(TypeKind.CLASS, posn);
        className = cn;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitClassType(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeDenoter)) return false;
        if (!super.equals(o)) return false;

        TypeDenoter baseType = (TypeDenoter) o;

        if(baseType.typeKind == TypeKind.UNSUPPORTED) {
            return false;
        }

        if(baseType.typeKind == TypeKind.ERROR) {
            return true;
        }
        if(baseType.typeKind == TypeKind.NULL) {
            return true;
        }

        if (!(baseType instanceof ClassType)) return false;

        ClassType classType = (ClassType) o;
        return this.instanceOf(classType);
    }

    private boolean instanceOf(ClassType other) {
        ClassDecl current = (ClassDecl) this.className.dominantDecl;
        while(current != null) {
            String currentName = current.name;
            String otherName = other.className.dominantDecl.name;
            if(currentName.equals(otherName)) {
                return true;
            }
            current = current.superClass;
        }
        return false;
    }

}
