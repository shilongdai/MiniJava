package net.viperfish.minijava.parser;

import net.viperfish.minijava.Compiler;
import net.viperfish.minijava.CompilerGlobal;
import net.viperfish.minijava.ebnf.EBNFGrammar;
import net.viperfish.minijava.ebnf.ParsableSymbol;
import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.TokenScanner;

import java.io.IOException;
import java.util.*;

public class EBNFGrammarBackedParser extends BaseRecursiveParser {

    private EBNFGrammar grammar;

    public EBNFGrammarBackedParser(TokenScanner scanner, EBNFGrammar grammar) {
        super(scanner);
        this.grammar = grammar;
    }

    @Override
    public void parse() throws IOException, ParsingException, GrammarException {
        String startingSymbolName = grammar.getStartSymbols().iterator().next();
        Symbol startingSymbol = grammar.symbols().get(startingSymbolName);
        parse(Collections.singletonList(startingSymbol));
    }

    @Override
    protected Collection<ParsableSymbol> getFollowers(Symbol symbol) {
        return grammar.followers().get(symbol.getName());
    }

    @Override
    protected Collection<ParsableSymbol> getStarters(List<Symbol> symbols) {
        return grammar.startersFor(symbols);
    }

    @Override
    protected void handleDecisionPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException {
        Symbol decision = symbols.get(0);
        if(CompilerGlobal.DEBUG_3) {
            System.out.println("Choices: " + decision.getExpression());
        }
        Collection<ParsableSymbol> possibleSymbols = new HashSet<>();
        for (Symbol child : decision.getExpression()) {
            List<Symbol> symbolsPartition = new ArrayList<>();
            symbolsPartition.add(child);
            symbolsPartition.addAll(symbols.subList(1, symbols.size()));
            Collection<ParsableSymbol> symToCheck = EBNFGrammar.unionIfEmpty(grammar.startersFor(symbolsPartition), getFollowers(decision));
            if(CompilerGlobal.DEBUG_3) {
                System.out.println("Checking if symbols falls into: " + child);
            }
            for (ParsableSymbol p : symToCheck) {
                possibleSymbols.add(p);
                if (p.isInstance(getToken())) {
                    if(CompilerGlobal.DEBUG_3) {
                        System.out.println(String.format("Choosing %s because of %s", child, p.getName()));
                    }
                    parse(Collections.singletonList(child));
                    return;
                }
            }
        }
        throw new GrammarException(possibleSymbols, getToken());
    }

    @Override
    protected void handleRepeatingPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException {
        Symbol rep = symbols.get(0);
        Symbol gamma = rep.getExpression().get(0);
        if(CompilerGlobal.DEBUG_3) {
            System.out.println("Handling Repeating: " + gamma.getName());
        }
        Collection<ParsableSymbol> starters = getStarters(Collections.singletonList(gamma));
        if(CompilerGlobal.DEBUG_3) {
            System.out.println("Indicator for repeat: " + starters);
        }
        boolean keepGoing = false;
        for (ParsableSymbol p : starters) {
            if (p.isInstance(getToken())) {
                keepGoing = true;
                if(CompilerGlobal.DEBUG_3) {
                    System.out.println("Keep going because of: " + p.getName());
                }
                break;
            }
        }
        if(CompilerGlobal.DEBUG_3) {
            if (!keepGoing) {
                System.out.println("Wildcard -> Empty String");
            }
        }
        while (keepGoing) {
            parse(Collections.singletonList(gamma));
            keepGoing = false;
            for (ParsableSymbol p : starters) {
                if (p.isInstance(getToken())) {
                    keepGoing = true;
                    if(CompilerGlobal.DEBUG_3) {
                        System.out.println("Keep going because of: " + p.getName());
                    }
                    break;
                }
            }
            if(CompilerGlobal.DEBUG_3) {
                if (!keepGoing) {
                    System.out.println(String.format("Stopped Processing wildcard %s, current token %s", gamma.getName(), getToken().getSpelling()));
                }
            }
        }
    }

    @Override
    protected boolean accept(ParsableSymbol symbol) throws GrammarException, IOException, ParsingException {
        if(symbol == EBNFGrammar.EMPTY_STRING) {
            return true;
        }
        return super.accept(symbol);
    }
}
