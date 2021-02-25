package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.Expression;
import net.viperfish.minijava.ast.IfStmt;
import net.viperfish.minijava.ast.Statement;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class IfStmtASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("IfStmt")) {
            throw new IllegalArgumentException("Expected IfStmt, got: " + current.getName());
        }

        if(parsed.size() == 2) {
            return new IfStmt((Expression) parsed.get(0), (Statement) parsed.get(1), parsed.get(0).posn);
        } else if(parsed.size() == 3) {
            return new IfStmt((Expression) parsed.get(0), (Statement) parsed.get(1), (Statement) parsed.get(2), parsed.get(0).posn);
        } else {
            throw new IllegalArgumentException("Expected If Statement, got: " + parsed);
        }
    }
}
