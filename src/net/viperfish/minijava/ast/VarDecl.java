/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class VarDecl extends LocalDecl implements ParticularDecl {

    public VarDecl(TypeDenoter t, String name, SourcePosition posn) {
        super(name, t, posn);
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitVarDecl(this, o);
    }

    @Override
    public ClassDecl getClassDecl() {
        if(this.type.typeKind.equals(TypeKind.CLASS)) {
            return (ClassDecl) ((ClassType) this.type).className.dominantDecl;
        }
        return null;
    }
}
