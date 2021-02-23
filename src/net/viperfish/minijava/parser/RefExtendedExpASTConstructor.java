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
                if(!argList.getSymbol().getName().equals("ArgumentList")) {
                    throw new IllegalArgumentException("Expected ArgumentList, got: " + argList.getSymbol().getName());
                }
                ExprList exprList = new ExprList();
                Expression initExp = (Expression) argList.getChildASTs().get(0);
                exprList.add(initExp);
                if(argList.getChildASTs().size() == 2) {
                    DefaultAST followingArgs = (DefaultAST) argList.getChildASTs().get(1);
                    for(AST a : followingArgs.getChildASTs()) {
                        Expression arg = (Expression) a;
                        exprList.add(arg);
                    }
                } else if(argList.getChildASTs().size() > 2) {
                    throw new IllegalArgumentException("Expected init expression followed by other arguments, got: " + argList.getChildASTs());
                }
                return new CallExpr(ref, exprList, ref.posn);
            }
        }
        throw new IllegalArgumentException("Expected ref, index, or call, got: " + parsed);
    }
}
