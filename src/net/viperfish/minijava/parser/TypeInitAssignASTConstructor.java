package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class TypeInitAssignASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("TypeInitAssign")) {
            throw new IllegalArgumentException("Expected TypeInitAssign, got: " + current.getName());
        }

        if(parsed.size() == 3) {
            TypeDenoter type = (TypeDenoter) parsed.get(0);
            Identifier id = (Identifier) parsed.get(1);
            Expression exp = (Expression) parsed.get(2);
            VarDecl decl = new VarDecl(type, id.spelling, id.posn);
            return new VarDeclStmt(decl, exp, decl.posn);
        } else {
            throw new IllegalArgumentException("Expected Type Id Exp, got: " + parsed);
        }
    }

}
