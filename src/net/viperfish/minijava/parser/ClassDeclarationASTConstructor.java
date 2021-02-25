package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class ClassDeclarationASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("ClassDeclaration")) {
            throw new IllegalArgumentException("Expected Class Declaration, got: " + current.getName());
        }

        Identifier id = (Identifier) parsed.get(0);
        if(parsed.size() == 1) {
            return new ClassDecl(id.spelling, new FieldDeclList(), new MethodDeclList(), id.posn);
        } else if(parsed.size() == 2) {
            FieldDeclList fieldList = new FieldDeclList();
            MethodDeclList methdList = new MethodDeclList();
            DefaultAST wildCard = (DefaultAST) parsed.get(1);
            for(AST a : wildCard.getChildASTs()) {
                if(a instanceof FieldDecl) {
                    fieldList.add((FieldDecl) a);
                } else if(a instanceof MethodDecl) {
                    methdList.add((MethodDecl) a);
                } else {
                    throw new IllegalArgumentException("Expected Field or Method Decl, got: " + a);
                }
            }
            return new ClassDecl(id.spelling, fieldList, methdList, id.posn);
        } else {
            throw new IllegalArgumentException("Expected id decls, got: " + parsed);
        }
    }
}
