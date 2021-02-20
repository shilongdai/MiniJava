/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public class ClassDecl extends Declaration {

    public FieldDeclList fieldDeclList;
    public MethodDeclList methodDeclList;

    public ClassDecl(String cn, FieldDeclList fdl, MethodDeclList mdl, SourcePosition posn) {
        super(cn, null, posn);
        fieldDeclList = fdl;
        methodDeclList = mdl;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitClassDecl(this, o);
    }
}
