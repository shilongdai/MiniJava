package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class ProgramASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("Program")) {
            throw new IllegalArgumentException("Expected Program, got: " + current.getName());
        }

        if(parsed.isEmpty()) {
            return new Package(new ClassDeclList(), null);
        } else if(parsed.size() == 1) {
            DefaultAST childs = (DefaultAST) parsed.get(0);
            ClassDeclList list = new ClassDeclList();
            for(AST a : childs.getChildASTs()) {
                ClassDecl decl = (ClassDecl) a;
                list.add(decl);
            }
            return new Package(list, null);
        } else {
            throw new IllegalArgumentException("Expected nothing or list of classes, got: " + parsed);
        }
    }
}
