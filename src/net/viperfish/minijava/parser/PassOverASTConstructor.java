package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class PassOverASTConstructor implements ASTConstructor {


    public PassOverASTConstructor() {

    }

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(parsed.size() > 1) {
            throw new IllegalArgumentException("Expected Single AST to pass over, got: " + parsed);
        }
        if(parsed.isEmpty()) {
            return null;
        } else {
            return parsed.get(0);
        }
    }
}
