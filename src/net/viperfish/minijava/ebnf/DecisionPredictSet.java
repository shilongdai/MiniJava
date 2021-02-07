package net.viperfish.minijava.ebnf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DecisionPredictSet extends BasePredictSet {

    public DecisionPredictSet(Symbol srcRule, Map<String, Collection<ParsableSymbol>> predictSymbols, int predictPoint) {
        super(srcRule, predictSymbols, predictPoint);
    }

    @Override
    public boolean isLL1() {
        Set<ParsableSymbol> symbols = new HashSet<>();
        for (Collection<ParsableSymbol> c : getPredictSets().values()) {
            for (ParsableSymbol p : c) {
                if (symbols.contains(p)) {
                    return false;
                }
                symbols.add(p);
            }
        }
        return true;
    }
}
