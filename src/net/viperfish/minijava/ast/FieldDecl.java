/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class FieldDecl extends MemberDecl implements ParticularDecl {

    public FieldDecl(boolean isPrivate, boolean isStatic, TypeDenoter t, String name, SourcePosition posn) {
        super(isPrivate, isStatic, t, name, posn);
    }

    public FieldDecl(MemberDecl md, SourcePosition posn) {
        super(md, posn);
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitFieldDecl(this, o);
    }

    @Override
    public ClassDecl getClassDecl() {
        if(this.type.typeKind.equals(TypeKind.CLASS)) {
            return (ClassDecl) ((ClassType) this.type).className.dominantDecl;
        }
        return null;
    }
}

