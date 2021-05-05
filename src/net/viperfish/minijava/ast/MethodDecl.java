/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class MethodDecl extends MemberDecl {

    public int descriptorOffset;
    public ParameterDeclList parameterDeclList;
    public StatementList statementList;

    public MethodDecl(MemberDecl md, ParameterDeclList pl, StatementList sl, SourcePosition posn) {
        super(md, posn);
        parameterDeclList = pl;
        statementList = sl;
        descriptorOffset = -1;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitMethodDecl(this, o);
    }

}
