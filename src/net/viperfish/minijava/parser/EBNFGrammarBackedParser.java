package net.viperfish.minijava.parser;

import net.viperfish.minijava.CompilerGlobal;
import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.DefaultAST;
import net.viperfish.minijava.ast.Terminal;
import net.viperfish.minijava.ebnf.EBNFGrammar;
import net.viperfish.minijava.ebnf.ParsableSymbol;
import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.TokenScanner;

import java.io.IOException;
import java.util.*;

public class EBNFGrammarBackedParser extends BaseRecursiveParser {

    private EBNFGrammar grammar;
    private Map<String, ASTConstructor> constructors;

    public EBNFGrammarBackedParser(TokenScanner scanner, EBNFGrammar grammar, Map<String, ASTConstructor> constructors) {
        super(scanner);
        this.grammar = grammar;
        this.constructors = constructors;
    }

    @Override
    public AST parse() throws IOException, ParsingException, GrammarException {
        String startingSymbolName = grammar.getStartSymbols().iterator().next();
        Symbol startingSymbol = grammar.symbols().get(startingSymbolName);
        ASTConstructor startConstructor = getASTConstructor(startingSymbol);
        List<AST> tempList = parse(Collections.singletonList(startingSymbol));
        return startConstructor.buildTree(startingSymbol, tempList);
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
    protected AST handleDecisionPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException {
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
                    List<AST> childASTs = parse(Collections.singletonList(child));
                    ASTConstructor childCtor = getASTConstructor(child);
                    return childCtor.buildTree(child, childASTs);
                }
            }
        }
        throw new GrammarException(possibleSymbols, getToken());
    }

    @Override
    protected AST handleRepeatingPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException {
        Symbol rep = symbols.get(0);
        Symbol gamma = rep.getExpression().get(0);
        ASTConstructor wildCardCtor = getASTConstructor(rep);
        ASTConstructor gammaCtor = getASTConstructor(gamma);
        List<AST> repASTs = new ArrayList<>();

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
            List<AST> childs = parse(Collections.singletonList(gamma));
            AST childAST = gammaCtor.buildTree(gamma, childs);
            repASTs.add(childAST);

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

        return wildCardCtor.buildTree(rep, repASTs);
    }

    @Override
    protected ASTConstructor getASTConstructor(Symbol currentSymbol) {
        if(constructors.containsKey(currentSymbol.getName())) {
            return constructors.get(currentSymbol.getName());
        } else {
            return new DefaultASTConstructor();
        }
    }

    @Override
    protected Terminal accept(ParsableSymbol symbol) throws GrammarException, IOException, ParsingException {
        if(symbol == EBNFGrammar.EMPTY_STRING) {
            return null;
        }
        return super.accept(symbol);
    }

    private static class DefaultASTConstructor implements ASTConstructor {

        @Override
        public AST buildTree(Symbol current, List<AST> parsed) {
            return new DefaultAST(null, current, parsed);
        }
    }
}
