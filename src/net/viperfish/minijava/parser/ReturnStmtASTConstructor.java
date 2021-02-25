package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.Expression;
import net.viperfish.minijava.ast.ReturnStmt;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class ReturnStmtASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("returnStmt")) {
            throw new IllegalArgumentException("Expected returnStmt, got: " + current.getName());
        }

        if(parsed.size() == 1) {
            return new ReturnStmt((Expression) parsed.get(0), parsed.get(0).posn);
        } else if(parsed.isEmpty()) {
            return new ReturnStmt(null, null);
        } else {
            throw new IllegalArgumentException("Expected expression or nothing, got: " + parsed);
        }
    }

}
