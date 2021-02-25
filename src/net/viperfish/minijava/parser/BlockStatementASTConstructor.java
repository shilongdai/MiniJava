package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class BlockStatementASTConstructor implements ASTConstructor {

    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if(!current.getName().equals("StatementsBlock")) {
            throw new IllegalArgumentException("Expected StatementsBlock, got: " + current.getName());
        }

        DefaultAST blockStmts = (DefaultAST) parsed.get(0);
        StatementList list = ParserUtils.parseStatementList(blockStmts);
        SourcePosition pos = null;
        if(list.size() > 0) {
            pos = list.get(0).posn;
        }
        return new BlockStmt(list, pos);
    }

}
