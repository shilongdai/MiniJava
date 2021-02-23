package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class NewExpressionASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("NewExpression")) {
            throw new IllegalArgumentException("Expected NewExpression, got: " + current.getName());
        }

        if(parsed.size() == 1) {
            DefaultAST newExpTree = (DefaultAST) parsed.get(0);
            Symbol newType = newExpTree.getSymbol();
            if(newType.getName().equals("NewIdRelated")) {
                return parseIdRelated(newExpTree.getChildASTs());
            } else if(newType.getName().equals("NewIntArray")) {
                return parseNewIntArray(newExpTree.getChildASTs());
            } else {
                throw new IllegalArgumentException("Expected NewIdRelated or NewIntArray, got: " + newType.getName());
            }
        } else {
            throw new IllegalArgumentException("Expected a new expression, got: " + parsed);
        }
    }

    private AST parseIdRelated(List<AST> parsed) {
        if(parsed.size() == 1) {
            Identifier id = (Identifier) parsed.get(0);
            return new NewObjectExpr(new ClassType(id, id.posn), id.posn);
        } else if(parsed.size() == 2) {
            Identifier id = (Identifier) parsed.get(0);
            Expression size = (Expression) parsed.get(1);
            return new NewArrayExpr(new ClassType(id, id.posn), size, id.posn);
        } else {
            throw new IllegalArgumentException("Expected new Id or new Id array, got: " + parsed);
        }
    }

    private AST parseNewIntArray(List<AST> parsed) {
        if (parsed.size() == 2) {
            BaseType intType = (BaseType) parsed.get(0);
            Expression size = (Expression) parsed.get(1);
            return new NewArrayExpr(intType, size, intType.posn);
        } else {
            throw new IllegalArgumentException("Expected new int array, got: " + parsed);
        }
    }

}
