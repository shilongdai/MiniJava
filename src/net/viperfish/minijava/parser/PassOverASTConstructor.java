package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class PassOverASTConstructor implements ASTConstructor {

    private String symbolName;

    public PassOverASTConstructor(String symbolName) {
        this.symbolName = symbolName;
    }

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals(symbolName)) {
            throw new IllegalArgumentException(String.format("Expected %s, got: %s", symbolName, current.getName()));
        }
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
