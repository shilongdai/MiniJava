package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.ArrayList;
import java.util.List;

public class SQBracketASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("sqBrackets")) {
            throw new IllegalArgumentException("Expected sqBrackets, got: " + current.getName());
        }

        return new DefaultAST(null, current, new ArrayList<>());
    }

}
