package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.BinaryExpr;
import net.viperfish.minijava.ast.Expression;
import net.viperfish.minijava.ast.Operator;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.ArrayList;
import java.util.List;

public class OperatorExpressionASTConstructor implements ASTConstructor {

    @Override
    public Expression buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().endsWith("Exp") && !current.getName().equals("Expression")) {
            throw new IllegalArgumentException("Expected Operator Expression, got: " + current.getName());
        }
        if(parsed.size() == 1) {
            return (Expression) parsed.get(0); // pass over if no operation
        }
        if(parsed.size() != 2) {
            throw new IllegalArgumentException("Expected init operands and following operands, got: " + parsed);
        }

        List<AST> expressions = extractExpressions(parsed);
        return buildBinExpr(expressions);
    }

    private List<AST> extractExpressions(List<AST> parsed) {
        List<AST> expressionParts = new ArrayList<>();
        expressionParts.add(parsed.get(0));
        DefaultAST followingOperands = (DefaultAST) parsed.get(1);
        for(AST a : followingOperands.getChildASTs()) {
            DefaultAST enclosed = (DefaultAST) a;
            Operator operator = (Operator) enclosed.getChildASTs().get(0);
            Expression exp = (Expression) enclosed.getChildASTs().get(1);
            expressionParts.add(operator);
            expressionParts.add(exp);
        }
        if(expressionParts.size() < 3 || expressionParts.size() % 2 == 0) {
            throw new IllegalArgumentException("Expected binary expressions, got: " + expressionParts);
        }
        return expressionParts;
    }

    private BinaryExpr buildBinExpr(List<AST> expressions) {
        if(expressions.size() == 3) {
            Expression left = (Expression) expressions.get(0);
            Operator ops = (Operator) expressions.get(1);
            Expression right = (Expression) expressions.get(2);
            return new BinaryExpr(ops, left, right, left.posn);
        }
        Expression left = buildBinExpr(expressions.subList(0, expressions.size() - 2));
        Operator ops = (Operator) expressions.get(expressions.size() - 2);
        Expression right = (Expression) expressions.get(expressions.size() - 1);
        return new BinaryExpr(ops, left, right, left.posn);
    }
}
