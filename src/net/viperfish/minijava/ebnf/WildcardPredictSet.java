package net.viperfish.minijava.ebnf;

import java.util.Collection;
import java.util.Map;

public class WildcardPredictSet extends BasePredictSet {

    private boolean LL1;

    public WildcardPredictSet(Symbol srcRule, Map<String, Collection<ParsableSymbol>> predictSymbols, int predictPoint, boolean LL1) {
        super(srcRule, predictSymbols, predictPoint);
        this.LL1 = LL1;
    }

    @Override
    public boolean isLL1() {
        return this.LL1;
    }
}
