package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class VisibilityASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("Visibility")) {
            throw new IllegalArgumentException("Expected Visibility, got: " + current.getName());
        }

        if(parsed.size() > 1) {
            throw new IllegalArgumentException("Expected Visibility, got: " + parsed);
        }
        return new DefaultAST(null, current, parsed);
    }
}
