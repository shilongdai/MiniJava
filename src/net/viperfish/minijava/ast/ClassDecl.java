/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class ClassDecl extends Declaration {

    public Identifier superClassId;
    public ClassDecl superClass;
    public FieldDeclList fieldDeclList;
    public MethodDeclList methodDeclList;

    public KnownAddress classDescriptor;

    public ClassDecl(String cn, FieldDeclList fdl, MethodDeclList mdl, SourcePosition posn) {
        super(cn, null, posn);
        fieldDeclList = fdl;
        methodDeclList = mdl;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitClassDecl(this, o);
    }
}
