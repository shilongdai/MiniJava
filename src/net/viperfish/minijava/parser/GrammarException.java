package net.viperfish.minijava.parser;

import net.viperfish.minijava.ebnf.ParsableSymbol;
import net.viperfish.minijava.scanner.Token;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class GrammarException extends Exception {

    private Collection<ParsableSymbol> expected;
    private Token actual;

    public GrammarException(ParsableSymbol expected, Token actual) {
        super(String.format("Expected: %s, got: %s, %s, line: %d", expected.getName(), actual.getTokenType(), actual.getSpelling(), actual.getPosition().getLineNumber()));
        this.expected = Arrays.asList(expected);
        this.actual = actual;
    }

    public GrammarException(Collection<? extends ParsableSymbol> expected, Token actual) {
        super(String.format("Expected: %s, got: %s, %s, line: %d", expected, actual.getTokenType(), actual.getSpelling(), actual.getPosition().getLineNumber()));
        this.expected = new HashSet<>(expected);
        this.actual = actual;
    }
}
