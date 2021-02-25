package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.Identifier;
import net.viperfish.minijava.ast.ParameterDecl;
import net.viperfish.minijava.ast.TypeDenoter;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class ParamDeclASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("ParamDecl")) {
            throw new IllegalArgumentException("Expected ParamDecl, got: " + current.getName());
        }

        if(parsed.size() == 2) {
            TypeDenoter type = (TypeDenoter) parsed.get(0);
            Identifier id = (Identifier) parsed.get(1);

            return new ParameterDecl(type, id.spelling, type.posn);
        } else {
            throw new IllegalArgumentException("Expected Type, id, got: " + parsed);
        }
    }
}
