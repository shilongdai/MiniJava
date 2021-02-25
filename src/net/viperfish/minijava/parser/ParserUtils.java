package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;

public final class ParserUtils {

    public static ExprList parseArgLists(DefaultAST argList) {
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
        return exprList;
    }

    public static ParameterDeclList parseParamList(DefaultAST paramList) {
        if(!paramList.getSymbol().getName().equals("ParameterList")) {
            throw new IllegalArgumentException("Expected ParameterList, got: " + paramList.getSymbol().getName());
        }

        ParameterDeclList paramLst = new ParameterDeclList();
        ParameterDecl first = (ParameterDecl) paramList.getChildASTs().get(0);
        paramLst.add(first);

        if(paramList.getChildASTs().size() == 2) {
            DefaultAST followingParams = (DefaultAST) paramList.getChildASTs().get(1);
            for(AST s : followingParams.getChildASTs()) {
                ParameterDecl decl = (ParameterDecl) s;
                paramLst.add(decl);
            }
        } else if(paramList.getChildASTs().size() > 2) {
            throw new IllegalArgumentException("Expected init param followed by other params, got: " + paramList.getChildASTs());
        }
        return paramLst;
    }

    public static StatementList parseStatementList(DefaultAST statementLists) {
        if(!statementLists.getSymbol().getName().equals("StatementList")) {
            throw new IllegalArgumentException("Expected StatementList, got: " + statementLists.getSymbol().getName());
        }
        StatementList list = new StatementList();
        for(AST a : statementLists.getChildASTs()) {
            Statement s = (Statement) a;
            list.add(s);
        }
        return list;
    }

}
