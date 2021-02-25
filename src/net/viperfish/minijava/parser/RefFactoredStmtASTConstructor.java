package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class RefFactoredStmtASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("RefFactoredStmt")) {
            throw new IllegalArgumentException("Expected: RefFactoredStmt, got: " + current.getName());
        }

        Reference ref = (Reference) parsed.get(0);
        DefaultAST options = (DefaultAST) parsed.get(1);
        List<AST> optionTrees = options.getChildASTs();
        if(optionTrees.size() != 1) {
            throw new IllegalArgumentException("Expected Expression, Idx, or Call, got: " + optionTrees);
        }

        AST firstChild = optionTrees.get(0);
        if(firstChild instanceof Expression) {
            return new AssignStmt(ref, (Expression) firstChild, ref.posn);
        } else if(firstChild instanceof DefaultAST) {
            DefaultAST alternative = (DefaultAST) firstChild;
            if(alternative.getSymbol().getName().equals("RefIdxEqExpStmt")) {
                return parseIxAssignment(ref, alternative);
            } else if(alternative.getSymbol().getName().equals("CallArguments")) {
                if(alternative.getChildASTs().isEmpty()) {
                    return new CallStmt(ref, new ExprList(), ref.posn);
                }
                return parseCallStmt(ref, (DefaultAST) alternative.getChildASTs().get(0));
            } else {
                throw new IllegalArgumentException("Expected RefIdxEqExpStmt, or ArgumentList, got: " + alternative.getSymbol());
            }
        } else {
            throw new IllegalArgumentException("Expected Expected, Idx, or Call, got:" + firstChild);
        }
    }

    private IxAssignStmt parseIxAssignment(Reference ref, DefaultAST ixAssignment) {
        if(!ixAssignment.getSymbol().getName().equals("RefIdxEqExpStmt")) {
            throw new IllegalArgumentException("Expected RefIdxEqExpStmt, got: "+ ixAssignment.getSymbol().getName());
        }
        List<AST> childs = ixAssignment.getChildASTs();
        if(childs.size() != 2) {
            throw new IllegalArgumentException("Expected Expression, Expression, got: " + childs);
        }
        Expression idx = (Expression) childs.get(0);
        Expression val = (Expression) childs.get(1);

        return new IxAssignStmt(ref, idx, val, ref.posn);
    }

    private CallStmt parseCallStmt(Reference ref, DefaultAST callStmt) {
        if(!callStmt.getSymbol().getName().equals("ArgumentList")) {
            throw new IllegalArgumentException("Expected CallArguments, got: " + callStmt.getSymbol().getName());
        }

        ExprList exprs = ParserUtils.parseArgLists(callStmt);
        return new CallStmt(ref, exprs, ref.posn);
    }

}
