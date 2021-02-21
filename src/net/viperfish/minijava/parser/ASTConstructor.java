package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public interface ASTConstructor {

    public AST buildTree(Symbol current, List<AST> parsed);

}
