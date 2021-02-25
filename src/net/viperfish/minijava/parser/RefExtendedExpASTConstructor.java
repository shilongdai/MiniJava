package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class RefExtendedExpASTConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("RefExtendedExp")) {
            throw new IllegalArgumentException("Expected RefExtendedExp, got: " + current.getName());
        }

        if(parsed.size() == 1) {
            Reference ref = (Reference) parsed.get(0);
            return new RefExpr(ref, ref.posn);
        } else if(parsed.size() == 2) {
            Reference ref = (Reference) parsed.get(0);
            AST sec = parsed.get(1);
            if(sec instanceof Expression) {
                return new IxExpr(ref, (Expression) sec, sec.posn);
            } else if(sec instanceof DefaultAST) {
                DefaultAST argList = (DefaultAST) sec;
                if(argList.getChildASTs().isEmpty()) {
                    return new CallExpr(ref, new ExprList(), ref.posn);
                } else {
                    ExprList exprList = ParserUtils.parseArgLists((DefaultAST) argList.getChildASTs().get(0));
                    return new CallExpr(ref, exprList, ref.posn);
                }
            }
        }
        throw new IllegalArgumentException("Expected ref, index, or call, got: " + parsed);
    }
}
