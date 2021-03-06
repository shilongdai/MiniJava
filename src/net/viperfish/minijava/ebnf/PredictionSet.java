package net.viperfish.minijava.ebnf;

import java.util.Collection;
import java.util.Map;

public interface PredictionSet {

    public Symbol getSrcRule();

    public boolean isLL1();

    public Map<String, Collection<ParsableSymbol>> getPredictSets();

    public int getPredictPoint();

}

