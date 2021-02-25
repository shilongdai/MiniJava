package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.Expression;
import net.viperfish.minijava.ast.Statement;
import net.viperfish.minijava.ast.WhileStmt;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class WhileStatementASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("WhileStmt")) {
            throw new IllegalArgumentException("Expected WhileStmt, got: " + current.getName());
        }

        if(parsed.size() != 2) {
            throw new IllegalArgumentException("Expected Expression, Statement, got: " + parsed);
        }

        Expression exp = (Expression) parsed.get(0);
        Statement stmt = (Statement) parsed.get(1);
        return new WhileStmt(exp, stmt, exp.posn);
    }

}
