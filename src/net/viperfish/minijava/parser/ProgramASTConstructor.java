package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.ClassDecl;
import net.viperfish.minijava.ast.ClassDeclList;
import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.SourcePosition;

import java.util.List;

public class ProgramASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("Program")) {
            throw new IllegalArgumentException("Expected Program, got: " + current.getName());
        }

        if(parsed.isEmpty()) {
            return new Package(new ClassDeclList(), new SourcePosition(0, 1));
        } else if(parsed.size() == 1) {
            DefaultAST childs = (DefaultAST) parsed.get(0);
            ClassDeclList list = new ClassDeclList();
            for(AST a : childs.getChildASTs()) {
                ClassDecl decl = (ClassDecl) a;
                list.add(decl);
            }
            return new Package(list, list.get(0).posn);
        } else {
            throw new IllegalArgumentException("Expected nothing or list of classes, got: " + parsed);
        }
    }
}
