package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.Expression;
import net.viperfish.minijava.ast.Operator;
import net.viperfish.minijava.ast.UnaryExpr;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class UExpEnclosedASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("UExpEnclosed")) {
            throw new IllegalArgumentException("Expected UExpEnclosed, got: " + current.getName());
        }

        if(parsed.size() == 1) {
            return parsed.get(0);
        } else if(parsed.size() == 2) {
            return new UnaryExpr((Operator) parsed.get(0), (Expression) parsed.get(1), parsed.get(0).posn);
        } else {
            throw new IllegalArgumentException("Expected BExp, or ops Exp, got: " + parsed);
        }
    }
}
