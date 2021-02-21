package net.viperfish.minijava.ast;

import net.viperfish.minijava.ebnf.Symbol;

import java.util.ArrayList;
import java.util.List;

public class DefaultAST extends AST {

    private Symbol symbol;
    private List<AST> childASTs;

    public DefaultAST(SourcePosition posn, Symbol symbol, List<AST> childASTs) {
        super(posn);
        this.symbol = symbol;
        this.childASTs = new ArrayList<>(childASTs);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<AST> getChildASTs() {
        return childASTs;
    }

    @Override
    public <A, R> R visit(Visitor<A, R> v, A o) {
        throw new UnsupportedOperationException("Default AST does not support proper visiter");
    }
}
