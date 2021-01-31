package net.viperfish.comp520.ebnf;

import java.util.List;

public interface Symbol {

    public String getName();

    public List<Symbol> getExpression();

    public boolean isDecision();

    public boolean isWildcard();

}
