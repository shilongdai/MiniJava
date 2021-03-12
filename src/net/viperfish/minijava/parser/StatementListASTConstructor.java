package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class StatementListASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("StatementList")) {
            throw new IllegalArgumentException("Expected StatementList, got: " + current.getName());
        }
        return new DefaultAST(null, current, parsed);
    }
}
