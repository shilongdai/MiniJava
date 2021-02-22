package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class BaseExpressionASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("BExp")) {
            throw new IllegalArgumentException("Expected BExp, got: " + current.getName());
        }

        if(parsed.size() == 1) {
            AST soleParsed = parsed.get(0);
            if(soleParsed instanceof IntLiteral || soleParsed instanceof BooleanLiteral) {
                return new LiteralExpr((Terminal) soleParsed, soleParsed.posn);
            }
            if(soleParsed instanceof NewExpr) {
                return soleParsed;
            }
            if(soleParsed instanceof Expression) {
                return soleParsed;
            }
            throw new IllegalArgumentException("Expected Literal, NewExpr, or Expression, got: " + soleParsed);
        } else {
            throw new IllegalArgumentException("Expected Pre-parsed expressions, got: " + parsed);
        }
    }

}
