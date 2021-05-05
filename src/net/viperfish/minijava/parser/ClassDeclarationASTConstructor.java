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
            if(parsed.get(1) instanceof DefaultAST) {
                FieldDeclList fieldList = new FieldDeclList();
                MethodDeclList methdList = new MethodDeclList();
                DefaultAST wildCard = (DefaultAST) parsed.get(1);
                extractMembers(fieldList, methdList, wildCard);
                return new ClassDecl(id.spelling, fieldList, methdList, id.posn);
            } else if(parsed.get(1) instanceof Identifier) {
                ClassDecl result = new ClassDecl(id.spelling, new FieldDeclList(), new MethodDeclList(), id.posn);
                result.superClassId = (Identifier) parsed.get(1);
                return result;
            } else {
                throw new IllegalArgumentException("Expected decls or super class, got: " + parsed);
            }
        } else if(parsed.size() == 3) {
            FieldDeclList fieldList = new FieldDeclList();
            MethodDeclList methdList = new MethodDeclList();
            DefaultAST wildCard = (DefaultAST) parsed.get(2);
            extractMembers(fieldList, methdList, wildCard);
            ClassDecl result = new ClassDecl(id.spelling, fieldList, methdList, id.posn);
            result.superClassId = (Identifier) parsed.get(1);
            return result;
        } else {
            throw new IllegalArgumentException("Expected id decls, got: " + parsed);
        }
    }

    private void extractMembers(FieldDeclList fieldDeclList, MethodDeclList methodDeclList, DefaultAST wildCard) {
        for(AST a : wildCard.getChildASTs()) {
            if(a instanceof FieldDecl) {
                fieldDeclList.add((FieldDecl) a);
            } else if(a instanceof MethodDecl) {
                methodDeclList.add((MethodDecl) a);
            } else {
                throw new IllegalArgumentException("Expected Field or Method Decl, got: " + a);
            }
        }
    }
}
