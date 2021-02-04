package net.viperfish.minijava.ebnf;

import java.util.*;

public class TestEBNFGrammer {

    public static void main(String argv[]) {
        testGrammar1();

        System.out.println("\n\n");

        testGrammar2();

        System.out.println("\n\n");

        testGrammar3();
    }

    private static void testGrammar1() {
        EBNFGrammar grammar = new EBNFGrammar();
        ParsableSymbol c = new StandardTerminalSymbol("c");
        ParsableSymbol b = new StandardTerminalSymbol("b");
        ParsableSymbol a = new StandardTerminalSymbol("a");
        Symbol B = new DecisionPointSymbol("B", Arrays.asList(b, EBNFGrammar.EMPTY_STRING));
        Symbol A = new DecisionPointSymbol("A", Arrays.asList(new CompositeSymbol(Arrays.asList(a, B)), EBNFGrammar.EMPTY_STRING));
        Symbol S = new CompositeSymbol("S", Arrays.asList(A, B, c));
        grammar.registerStartSymbol(S);
        grammar.registerTerminalSymbol(a);
        grammar.registerTerminalSymbol(b);
        grammar.registerTerminalSymbol(c);
        grammar.registerNonTerminalSymbol(A);
        grammar.registerNonTerminalSymbol(B);
        grammar.registerStartSymbol(S);
        printGrammarProperties(grammar);
    }

    private static void testGrammar2() {
        EBNFGrammar grammar = new EBNFGrammar();
        ParsableSymbol end = new StandardTerminalSymbol("$");
        ParsableSymbol b = new StandardTerminalSymbol("b");
        ParsableSymbol a = new StandardTerminalSymbol("a");
        ParsableSymbol d = new StandardTerminalSymbol("d");
        ParsableSymbol f = new StandardTerminalSymbol("f");
        ParsableSymbol x = new StandardTerminalSymbol("x");
        Symbol D = new DecisionPointSymbol("D", Arrays.asList(new CompositeSymbol(Arrays.asList(d, grammar.placeholderName("D"), x)), f));
        Symbol B = new DecisionPointSymbol("B", Arrays.asList(new CompositeSymbol(Arrays.asList(b, grammar.placeholderName("B"), x)), D));
        Symbol A = new DecisionPointSymbol("A", Arrays.asList(new CompositeSymbol(Arrays.asList(a, grammar.placeholderName("A"), x)), B));
        Symbol S = new CompositeSymbol("S", Arrays.asList(A, end));
        grammar.registerNonTerminalSymbol(D);
        grammar.registerNonTerminalSymbol(B);
        grammar.registerNonTerminalSymbol(A);
        grammar.registerStartSymbol(S);
        grammar.registerTerminalSymbol(end);
        grammar.registerTerminalSymbol(b);
        grammar.registerTerminalSymbol(a);
        grammar.registerTerminalSymbol(d);
        grammar.registerTerminalSymbol(f);
        grammar.registerTerminalSymbol(x);
        printGrammarProperties(grammar);
    }

    private static void testGrammar3() {
        EBNFGrammar grammar = new EBNFGrammar();
        ParsableSymbol b = new StandardTerminalSymbol("b");
        ParsableSymbol a = new StandardTerminalSymbol("a");
        ParsableSymbol c = new StandardTerminalSymbol("c");
        Symbol A = new DecisionPointSymbol("A", Arrays.asList(new CompositeSymbol(Arrays.asList(a, new WildCardSymbol(grammar.placeholderName("A")), b)), b));
        Symbol S = new CompositeSymbol("S", Arrays.asList(A, c));
        grammar.registerTerminalSymbol(b);
        grammar.registerTerminalSymbol(a);
        grammar.registerTerminalSymbol(c);
        grammar.registerNonTerminalSymbol(A);
        grammar.registerStartSymbol(S);
        printGrammarProperties(grammar);
    }

    private static void printGrammarProperties(EBNFGrammar grammar) {
        Collection<String> nullable = grammar.getNullableSymbols();
        Map<String, Collection<ParsableSymbol>> starters = grammar.starterSets();
        Map<String, Collection<ParsableSymbol>> followers = grammar.followers();
        Collection<PredictionSet> predictSets = grammar.predictSets();
        Set<String> nonterminals = new HashSet<>();
        nonterminals.addAll(grammar.getStartSymbols());
        nonterminals.addAll(grammar.getNonTerminalSymbols());
        nullable.retainAll(nonterminals);
        starters.keySet().retainAll(nonterminals);
        followers.keySet().retainAll(nonterminals);
        System.out.println(nullable);
        System.out.println(starters);
        System.out.println(followers);

        System.out.println("-----Predict Sets------");
        for (PredictionSet set : predictSets) {
            System.out.println(String.format("Rule: %s, Sets: %s, LL1: %b", set.getSrcRule().getName(), set.getPredictSets(), set.isLL1()));
        }
    }
}
