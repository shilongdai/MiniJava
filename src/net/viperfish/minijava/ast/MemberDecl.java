/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

abstract public class MemberDecl extends Declaration {

    public boolean isPrivate;
    public boolean isStatic;

    public MemberDecl(boolean isPrivate, boolean isStatic, TypeDenoter mt, String name, SourcePosition posn) {
        super(name, mt, posn);
        this.isPrivate = isPrivate;
        this.isStatic = isStatic;
    }
    public MemberDecl(MemberDecl md, SourcePosition posn) {
        super(md.name, md.type, posn);
        this.isPrivate = md.isPrivate;
        this.isStatic = md.isStatic;
    }
}
