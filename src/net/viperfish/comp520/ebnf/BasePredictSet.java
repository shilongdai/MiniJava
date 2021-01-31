package net.viperfish.comp520.ebnf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BasePredictSet implements PredictionSet {

    private Symbol srcRule;
    private Map<String, Collection<ParsableSymbol>> predictSymbols;


    public BasePredictSet(Symbol srcRule, Map<String, Collection<ParsableSymbol>> predictSymbols) {
        this.srcRule = srcRule;
        this.predictSymbols = new HashMap<>(predictSymbols);
    }

    @Override
    public Symbol getSrcRule() {
        return srcRule;
    }

    @Override
    public Map<String, Collection<ParsableSymbol>> getPredictSets() {
        return new HashMap<>(this.predictSymbols);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasePredictSet that = (BasePredictSet) o;
        return Objects.equals(srcRule, that.srcRule) &&
                Objects.equals(predictSymbols, that.predictSymbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcRule, predictSymbols);
    }
}
