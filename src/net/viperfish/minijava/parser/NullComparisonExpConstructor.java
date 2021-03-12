package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class NullComparisonExpConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("nullEqComposite")) {
            throw new IllegalArgumentException("Expected nullEqComposite, got: " + current.getName());
        }

        if(parsed.size() != 3) {
            throw new IllegalArgumentException("Expected Null operator RExp, got: " + parsed);
        }
        NullExpr nullExpr = (NullExpr) parsed.get(0);
        Operator op = (Operator) parsed.get(1);
        Expression rhs = (Expression) parsed.get(2);
        return new BinaryExpr(op, nullExpr, rhs, nullExpr.posn);
    }
}
