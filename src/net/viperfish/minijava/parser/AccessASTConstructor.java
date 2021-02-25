package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.DefaultAST;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class AccessASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("Access")) {
            throw new IllegalArgumentException("Expected Access, got: " + current.getName());
        }

        return new DefaultAST(null, current, parsed);
    }
}
