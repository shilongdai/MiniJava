package net.viperfish.minijava.parser;

import net.viperfish.minijava.CompilerGlobal;
import net.viperfish.minijava.ebnf.ParsableSymbol;
import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenScanner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class BaseRecursiveParser implements RecursiveParser {

    private TokenScanner scanner;
    private Token currentToken;
    private Token peeked;

    public BaseRecursiveParser(TokenScanner scanner) {
        this.scanner = scanner;
        this.peeked = null;
    }

    @Override
    public void init() throws IOException, ParsingException {
        currentToken = scanner.nextToken();
    }

    @Override
    public void parse(List<Symbol> symbols) throws IOException, ParsingException, GrammarException {
        if (symbols.isEmpty()) {
            return;
        }
        Symbol next = symbols.get(0);
        if (CompilerGlobal.DEBUG_3) {
            System.out.println("Looking At Symbol: " + next.getName());
        }
        if (next instanceof ParsableSymbol) {
            if (CompilerGlobal.DEBUG_2) {
                System.out.println("Accepting: " + next.getName());
            }
            accept((ParsableSymbol) next);
            parse(symbols.subList(1, symbols.size()));
        } else if (next.isDecision()) {
            if (CompilerGlobal.DEBUG_3) {
                System.out.println("Deciding:");
            }
            handleDecisionPoint(symbols);
            parse(symbols.subList(1, symbols.size()));
        } else if (next.isWildcard()) {
            if (CompilerGlobal.DEBUG_3) {
                System.out.println("Handling Repeat");
            }
            handleRepeatingPoint(symbols);
            parse(symbols.subList(1, symbols.size()));
        } else {
            if (CompilerGlobal.DEBUG_3) {
                System.out.println("Analysing sub-components");
            }
            parse(next.getExpression());
            parse(symbols.subList(1, symbols.size()));
        }
        if (CompilerGlobal.DEBUG_3) {
            System.out.println("Finished with: " + next.getName());
        }
    }

    protected abstract Collection<ParsableSymbol> getFollowers(Symbol symbol);

    protected abstract Collection<ParsableSymbol> getStarters(List<Symbol> symbols);

    protected abstract void handleDecisionPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException;

    protected abstract void handleRepeatingPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException;

    protected Token getToken() {
        return this.currentToken;
    }

    protected Token peek() throws IOException, ParsingException {
        if (peeked == null) {
            peeked = scanner.nextToken();
        }
        return peeked;
    }

    protected boolean accept(ParsableSymbol symbol) throws GrammarException, IOException, ParsingException {
        if (CompilerGlobal.DEBUG_2) {
            System.out.println(String.format("Comparing: %s vs %s", symbol.getName(), currentToken.getSpelling()));
        }
        if (symbol.isInstance(currentToken)) {
            if (peeked != null) {
                currentToken = peeked;
                peeked = null;
            } else {
                currentToken = scanner.nextToken();
            }
            return true;
        } else {
            throw new GrammarException(symbol, currentToken);
        }
    }

}
