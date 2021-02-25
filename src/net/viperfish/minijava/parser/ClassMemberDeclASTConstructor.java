package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.TokenType;

import java.util.List;

public class ClassMemberDeclASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("ClassMemberDecl")) {
            throw new IllegalArgumentException("Expected ClassMemberDecl, got: " + current.getName());
        }

        if(parsed.size() == 3) {
            DefaultAST visibility = (DefaultAST) parsed.get(0);
            DefaultAST access = (DefaultAST) parsed.get(1);
            boolean isPrivate = false;
            boolean isStatic = false;

            if(!visibility.getChildASTs().isEmpty()) {
                DefaultAST v = (DefaultAST) visibility.getChildASTs().get(0);
                TokenType t = TokenType.valueOf(v.getSymbol().getName());
                if(t == TokenType.PRIVATE) {
                    isPrivate = true;
                }
            }
            if(!access.getChildASTs().isEmpty()) {
                DefaultAST a = (DefaultAST) access.getChildASTs().get(0);
                TokenType t = TokenType.valueOf(a.getSymbol().getName());
                if(t == TokenType.STATIC) {
                    isStatic = true;
                } else {
                    throw new IllegalArgumentException("Expected static, got: " + a.getSymbol().getName());
                }
            }

            DefaultAST decision = (DefaultAST) parsed.get(2);
            if(decision.getSymbol().getName().equals("TypedDecl")) {
                return handleTypedDecl(decision, isPrivate, isStatic);
            } else if(decision.getSymbol().getName().equals("VoidDecl")) {
                return handleVoidDecl(decision, isPrivate, isStatic);
            } else {
                throw new IllegalArgumentException("Expected either TypedDecl or VoidDecl, got: " + decision.getSymbol().getName());
            }
        } else {
            throw new IllegalArgumentException("Expected Visibility Access Decl, got: " + parsed);
        }
    }

    private MemberDecl handleTypedDecl(DefaultAST ast, boolean isPrivate, boolean isStatic) {
        List<AST> childs = ast.getChildASTs();
        TypeDenoter type = (TypeDenoter) childs.get(0);
        Identifier id = (Identifier) childs.get(1);
        FieldDecl baseDecl = new FieldDecl(isPrivate, isStatic, type, id.spelling, id.posn);
        if(childs.size() == 2) {
            return baseDecl;
        } else if(childs.size() == 3) {
            DefaultAST methodDecl = (DefaultAST) childs.get(2);
            return handleMethodDecl(baseDecl, id, methodDecl);
        } else {
            throw new IllegalArgumentException("Expected typed method or field decl, got: " + childs);
        }
    }

    private MethodDecl handleVoidDecl(DefaultAST ast, boolean isPrivate, boolean isStatic) {
        List<AST> childs = ast.getChildASTs();
        TypeDenoter type = (TypeDenoter) childs.get(0);
        Identifier id = (Identifier) childs.get(1);
        FieldDecl baseDecl = new FieldDecl(isPrivate, isStatic, type, id.spelling, id.posn);
        if(childs.size() == 3) {
            BlockStmt stmt = (BlockStmt) childs.get(2);
            return new MethodDecl(baseDecl, new ParameterDeclList(), stmt.sl, id.posn);
        } else if(childs.size() == 4) {
            ParameterDeclList params = ParserUtils.parseParamList((DefaultAST) childs.get(2));
            BlockStmt stmt = (BlockStmt) childs.get(3);
            return new MethodDecl(baseDecl, params, stmt.sl, id.posn);
        } else {
            throw new IllegalArgumentException("Expected void method decl, got: " + childs);
        }
    }

    private MethodDecl handleMethodDecl(FieldDecl baseDecl, Identifier id, DefaultAST methodDecl) {
        if(methodDecl.getChildASTs().size() == 1) {
            BlockStmt stmt = (BlockStmt) methodDecl.getChildASTs().get(0);
            return new MethodDecl(baseDecl, new ParameterDeclList(), stmt.sl, id.posn);
        } else if(methodDecl.getChildASTs().size() == 2) {
            ParameterDeclList params = ParserUtils.parseParamList((DefaultAST) methodDecl.getChildASTs().get(0));
            BlockStmt stmt = (BlockStmt) methodDecl.getChildASTs().get(1);
            return new MethodDecl(baseDecl, params, stmt.sl, id.posn);
        } else {
            throw new IllegalArgumentException("Expected ParamList? Statements, got: " + methodDecl.getChildASTs());
        }
    }

}
