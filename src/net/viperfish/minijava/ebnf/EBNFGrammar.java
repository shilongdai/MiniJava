package net.viperfish.minijava.ebnf;

import java.util.*;

public class EBNFGrammar {

    public static final ParsableSymbol EMPTY_STRING = new EmptyStringSymbol();

    private Map<String, Symbol> symbols;
    private Map<String, LookUpSymbol> placeholders;

    private Collection<String> startSymbols;
    private Collection<String> terminalSymbols;
    private Collection<String> nonTerminalSymbols;
    private Collection<String> unnamedSymbols;

    private Map<String, Collection<ParsableSymbol>> starterCache;
    private Map<String, Collection<ParsableSymbol>> followerCache;
    private Collection<String> nullableCache;

    public static Collection<ParsableSymbol> unionIfEmpty(Collection<? extends Collection<ParsableSymbol>> chainedUnionIfEmpty) {
        List<? extends Collection<ParsableSymbol>> ogList = new ArrayList<>(chainedUnionIfEmpty);
        if (ogList.size() == 1) {
            return new HashSet<>(ogList.get(0));
        }
        return unionIfEmpty(ogList.get(0), unionIfEmpty(ogList.subList(1, ogList.size())));
    }

    public static Collection<ParsableSymbol> unionIfEmpty(Collection<ParsableSymbol> a, Collection<ParsableSymbol> b) {
        if (!a.contains(EMPTY_STRING)) {
            return new HashSet<>(a);
        } else {
            Set<ParsableSymbol> result = new HashSet<>();
            a = new HashSet<>(a);
            a.remove(EMPTY_STRING);
            result.addAll(a);
            result.addAll(b);
            return result;
        }
    }

    public EBNFGrammar() {
        symbols = new HashMap<>();
        startSymbols = new HashSet<>();
        terminalSymbols = new HashSet<>();
        nonTerminalSymbols = new HashSet<>();
        unnamedSymbols = new HashSet<>();
        placeholders = new HashMap<>();

        registerTerminalSymbol(EMPTY_STRING);
    }

    public void registerStartSymbol(Symbol start) {
        startSymbols.add(start.getName());
        unnamedSymbols.remove(start.getName());
        if (symbols.containsKey(start.getName())) {
            return;
        }
        walkSymbol(start, new DetectSymbols(), new HashSet<>(this.symbols.keySet()));
    }

    public void registerTerminalSymbol(ParsableSymbol terminal) {
        terminalSymbols.add(terminal.getName());
        unnamedSymbols.remove(terminal.getName());
        if (symbols.containsKey(terminal.getName())) {
            return;
        }
        symbols.put(terminal.getName(), terminal);
    }

    public void registerNonTerminalSymbol(Symbol nonTerminal) {
        nonTerminalSymbols.add(nonTerminal.getName());
        unnamedSymbols.remove(nonTerminal.getName());
        if (symbols.containsKey(nonTerminal.getName())) {
            return;
        }
        walkSymbol(nonTerminal, new DetectSymbols(), new HashSet<>(this.symbols.keySet()));
    }

    public Symbol placeholderName(String symbolName) {
        if(!placeholders.containsKey(symbolName)) {
            LookUpSymbol placeholder = new LookUpSymbol(symbolName);
            placeholders.put(symbolName, placeholder);
        }
        return placeholders.get(symbolName);
    }

    public Collection<String> getStartSymbols() {
        return new HashSet<>(startSymbols);
    }

    public Collection<String> getTerminalSymbols() {
        return new HashSet<>(terminalSymbols);
    }

    public Collection<String> getNonTerminalSymbols() {
        return new HashSet<>(nonTerminalSymbols);
    }

    public Collection<String> getUnnamedSymbols() {
        return new HashSet<>(unnamedSymbols);
    }

    public Collection<String> getNullableSymbols() {
        checkPlaceholders();
        if(nullableCache != null) {
            return new HashSet<>(nullableCache);
        }
        Collection<String> currentNullable = new HashSet<>();
        Collection<String> prevNullable;

        do {
            prevNullable = currentNullable;
            currentNullable = new HashSet<>();
            for (String s : symbols.keySet()) {
                Symbol symbol = symbols.get(s);
                if(nullable(prevNullable, symbol)) {
                    currentNullable.add(s);
                } else {
                    currentNullable.remove(s);
                }
            }
        } while (!currentNullable.equals(prevNullable));
        nullableCache = currentNullable;
        return currentNullable;
    }

    public Map<String, Collection<ParsableSymbol>> starterSets() {
        checkPlaceholders();
        if(starterCache != null) {
            return new HashMap<>(starterCache);
        }

        Map<String, Collection<ParsableSymbol>> currentStarter = new HashMap<>();
        Map<String, Collection<ParsableSymbol>> prevStarter = null;
        Collection<String> nullable = getNullableSymbols();
        for (String s : symbols.keySet()) {
            Symbol symbol = symbols.get(s);
            if (nullable(nullable, symbol)) {
                currentStarter.put(s, new HashSet<>(Collections.singletonList(EMPTY_STRING)));
            } else {
                currentStarter.put(s, new HashSet<>());
            }
        }
        do {
            prevStarter = currentStarter;
            currentStarter = new HashMap<>();

            for (String s : symbols.keySet()) {
                Symbol symbol = symbols.get(s);
                Set<ParsableSymbol> newStarters = starters(prevStarter, symbol);
                currentStarter.put(s, newStarters);
            }
        } while (!currentStarter.equals(prevStarter));

        starterCache = currentStarter;
        return currentStarter;
    }

    public Collection<ParsableSymbol> startersFor(List<Symbol> symbols) {
        Symbol symbol = null;
        if (symbols.isEmpty()) {
            symbol = EMPTY_STRING;
        } else if (symbols.size() == 1) {
            symbol = symbols.get(0);
        } else {
            symbol = new CompositeSymbol(symbols);
        }
        return starters(starterSets(), symbol);
    }

    public Map<String, Collection<ParsableSymbol>> followers() {
        checkPlaceholders();
        if(followerCache != null) {
            return followerCache;
        }

        Collection<String> nullable = getNullableSymbols();
        Map<String, Collection<ParsableSymbol>> currentFollower = initFollowers();
        Map<String, Collection<ParsableSymbol>> prevFollowers;

        for(Collection<ParsableSymbol> p : currentFollower.values()) {
            p.remove(EMPTY_STRING);
        }
        Set<String> unUsedSymbols = new HashSet<>(symbols.keySet());
        unUsedSymbols.removeAll(currentFollower.keySet());for(String u : unUsedSymbols) {
            currentFollower.put(u, new HashSet<>());
        }

        do {
            prevFollowers = currentFollower;
            currentFollower = new HashMap<>();
            for(String u : prevFollowers.keySet()) {
                currentFollower.put(u, new HashSet<>(prevFollowers.get(u)));
            }

            for(Symbol symbol : this.symbols.values()) {
                if (symbol.isDecision()) {
                    continue;
                } else if (symbol.isWildcard()) {
                    continue;
                } else {
                    for (int i = 0; i < symbol.getExpression().size(); ++i) {
                        List<Symbol> followingSymbols = symbol.getExpression().subList(i + 1, symbol.getExpression().size());
                        Symbol following = null;
                        if (followingSymbols.isEmpty()) {
                            following = EMPTY_STRING;
                        } else if (followingSymbols.size() == 1) {
                            following = followingSymbols.get(0);
                        } else {
                            following = new CompositeSymbol(followingSymbols);
                        }
                        Symbol followed = symbol.getExpression().get(i);
                        boolean followingNullable = nullable(nullable, following);
                        if (followingNullable) {
                            currentFollower.get(followed.getName()).addAll(prevFollowers.get(symbol.getName()));
                            if(followed.isDecision() || followed.isWildcard()) {
                                walkSymbol(followed, new PropagateFollowers(currentFollower));
                            }
                        }
                    }
                }
            }
        } while (!prevFollowers.equals(currentFollower));

        for(String u : unUsedSymbols) {
            currentFollower.put(u, Collections.singletonList(EMPTY_STRING));
        }

        followerCache = currentFollower;
        return currentFollower;
    }

    public Collection<PredictionSet> predictSets() {
        checkPlaceholders();
        Map<String, Collection<ParsableSymbol>> starters = starterSets();
        Map<String, Collection<ParsableSymbol>> followers = followers();
        Collection<PredictionSet> sets = new HashSet<>();

        sets.addAll(getPredictionSetsForDecisions(starters, followers));
        sets.addAll(getPredictionSetForWildcards(starters, followers));

        return sets;
    }

    public Map<String, Symbol> symbols() {
        return new HashMap<>(this.symbols);
    }

    private Map<String, Collection<ParsableSymbol>> initFollowers() {
        Map<String, Collection<ParsableSymbol>> starters = starterSets();
        Map<String, Collection<ParsableSymbol>> currentFollower = new HashMap<>();
        for(Symbol symbol : this.symbols.values()) {
            if(symbol.isDecision()) {
                continue;
            } else if(symbol.isWildcard()) {
                continue;
            } else {
                for (int i = 0; i < symbol.getExpression().size(); ++i) {
                    List<Symbol> followingSymbols = symbol.getExpression().subList(i + 1, symbol.getExpression().size());
                    Symbol following = null;
                    if (followingSymbols.isEmpty()) {
                        following = EMPTY_STRING;
                    } else if (followingSymbols.size() == 1) {
                        following = followingSymbols.get(0);
                    } else {
                        following = new CompositeSymbol(followingSymbols);
                    }
                    Symbol followed = symbol.getExpression().get(i);
                    Collection<ParsableSymbol> followingStarters = starters(starters, following);
                    if (!currentFollower.containsKey(followed.getName())) {
                        currentFollower.put(followed.getName(), new HashSet<>());
                    }
                    currentFollower.get(followed.getName()).addAll(followingStarters);

                    if (followed.isWildcard()) {
                        Symbol repeated = followed.getExpression().get(0);
                        Collection<ParsableSymbol> repeatedStarters = new HashSet<>(starters(starters, repeated));
                        if (!currentFollower.containsKey(repeated.getName())) {
                            currentFollower.put(repeated.getName(), new HashSet<>());
                        }
                        currentFollower.get(repeated.getName()).addAll(repeatedStarters);
                        walkSymbol(followed, new PropagateFollowers(currentFollower));
                    }
                    if (followed.isDecision()) {
                        walkSymbol(followed, new PropagateFollowers(currentFollower));
                    }
                }
            }
        }
        return currentFollower;
    }

    private Collection<PredictionSet> getPredictionSetsForDecisions(Map<String, Collection<ParsableSymbol>> starters, Map<String, Collection<ParsableSymbol>> followers) {
        Collection<ChoicePoint> choices = getChoicePoints();
        Collection<PredictionSet> result = new HashSet<>();

        for (ChoicePoint s : choices) {
            Map<String, Collection<ParsableSymbol>> predictSets = new HashMap<>();
            if (s.getParentSymbol().isDecision()) {
                for (Symbol a : s.getParentSymbol().getExpression()) {
                    Collection<ParsableSymbol> starter = starters(starters, a);
                    Collection<ParsableSymbol> follower = followers.get(s.getParentSymbol().getName());
                    Collection<ParsableSymbol> predictSet = unionIfEmpty(starter, follower);
                    predictSets.put(a.getName(), predictSet);
                }
            } else {
                Symbol parentSymbol = s.getParentSymbol();
                Symbol current = parentSymbol.getExpression().get(s.getSymbolPosition());
                for (Symbol a : current.getExpression()) {
                    List<Symbol> symbolList = new ArrayList<>(parentSymbol.getExpression().subList(s.getSymbolPosition() + 1, parentSymbol.getExpression().size()));
                    symbolList.add(0, a);
                    Collection<ParsableSymbol> starter = starters(starters, new CompositeSymbol(symbolList));
                    Collection<ParsableSymbol> follower = followers.get(parentSymbol.getName());
                    Collection<ParsableSymbol> predictSet = unionIfEmpty(starter, follower);
                    predictSets.put(a.getName(), predictSet);
                }
            }
            result.add(new DecisionPredictSet(s.getParentSymbol(), predictSets, s.getSymbolPosition()));
        }

        return result;
    }

    private boolean nullable(Collection<String> prevNullable, Symbol symbol) {
        if (symbol == EMPTY_STRING) {
            return true;
        } else {
            if (terminalSymbols.contains(symbol.getName())) {
                return false;
            } else if (symbol.isWildcard()) {
                return true;
            } else if (symbol.isDecision()) {
                for (Symbol c : symbol.getExpression()) {
                    if (prevNullable.contains(c.getName())) {
                        return true;
                    }
                }
                return false;
            } else {
                boolean result = true;
                for (Symbol c : symbol.getExpression()) {
                    if (!prevNullable.contains(c.getName())) {
                        result = false;
                        break;
                    }
                }
                return result;
            }
        }
    }

    private Collection<PredictionSet> getPredictionSetForWildcards(Map<String, Collection<ParsableSymbol>> starters, Map<String, Collection<ParsableSymbol>> followers) {
        Collection<PredictionSet> result = new HashSet<>();
        Collection<ChoicePoint> reps = getRepetition();

        for (ChoicePoint choicePoint : reps) {
            Symbol current = choicePoint.getParentSymbol().getExpression().get(choicePoint.getSymbolPosition());

            Map<String, Collection<ParsableSymbol>> predictSets = new HashMap<>();
            Collection<ParsableSymbol> predictCurrent = new HashSet<>(starters(starters, current.getExpression().get(0)));
            Collection<ParsableSymbol> predictLater = null;

            List<Symbol> symbolList = choicePoint.getParentSymbol().getExpression().subList(choicePoint.getSymbolPosition() + 1, choicePoint.getParentSymbol().getExpression().size());
            Symbol beta;
            if (symbolList.isEmpty()) {
                beta = EMPTY_STRING;
            } else if (symbolList.size() > 1) {
                beta = new CompositeSymbol(symbolList);
            } else {
                beta = symbolList.get(0);
            }
            Collection<ParsableSymbol> starter = starters(starters, beta);
            Collection<ParsableSymbol> follower = followers.get(choicePoint.getParentSymbol().getName());
            predictLater = unionIfEmpty(starter, follower);

            boolean isLL1 = true;
            if (predictCurrent.contains(EMPTY_STRING)) {
                isLL1 = false;
            }
            if (!Collections.disjoint(predictCurrent, predictLater)) {
                isLL1 = false;
            }

            predictSets.put(current.getExpression().get(0).getName(), predictCurrent);
            predictSets.put(beta.getName(), predictLater);
            result.add(new WildcardPredictSet(choicePoint.getParentSymbol(), predictSets, choicePoint.getSymbolPosition(), isLL1));
        }
        return result;
    }

    private Collection<ChoicePoint> getChoicePoints() {
        Collection<ChoicePoint> result = new HashSet<>();
        Collection<String> registered = new HashSet<>();
        for (Symbol s : this.symbols.values()) {
            for (int i = 0; i < s.getExpression().size(); ++i) {
                Symbol c = s.getExpression().get(i);
                if (c.isDecision() && unnamedSymbols.contains(c.getName())) {
                    result.add(new ChoicePoint(s, i));
                    registered.add(c.getName());
                    break;
                }
            }
        }
        for(Symbol s : this.symbols.values()) {
            if(s.isDecision() && !registered.contains(s.getName())) {
                result.add(new ChoicePoint(s, -1));
            }
        }
        return result;
    }

    private Collection<ChoicePoint> getRepetition() {
        Collection<ChoicePoint> result = new HashSet<>();
        for (Symbol s : this.symbols.values()) {
            for (int i = 0; i < s.getExpression().size(); ++i) {
                Symbol c = s.getExpression().get(i);
                if (c.isWildcard()) {
                    result.add(new ChoicePoint(s, i));
                    break;
                }
            }
        }
        return result;
    }

    private Set<ParsableSymbol> starters(Map<String, Collection<ParsableSymbol>> prevStarter, Symbol symbol) {
        if (symbol == EMPTY_STRING) {
            return new HashSet<>(Collections.singletonList(EMPTY_STRING));
        } else {
            if (terminalSymbols.contains(symbol.getName())) {
                ParsableSymbol terminal = (ParsableSymbol) symbol;
                return new HashSet<>(Collections.singletonList(terminal));
            } else if (symbol.isWildcard()) {
                Set<ParsableSymbol> newSet = collectiveUnion(prevStarter, symbol);
                newSet.add(EMPTY_STRING);
                return newSet;
            } else if (symbol.isDecision()) {
                Set<ParsableSymbol> newStarter = new HashSet<>();
                for (Symbol c : symbol.getExpression()) {
                    newStarter.addAll(prevStarter.get(c.getName()));
                }
                return newStarter;
            } else {
                Set<ParsableSymbol> newStarter = collectiveUnion(prevStarter, symbol);
                return newStarter;
            }
        }
    }

    private Set<ParsableSymbol> collectiveUnion(Map<String, Collection<ParsableSymbol>> prevStarter, Symbol symbol) {
        Collection<Collection<ParsableSymbol>> prevSets = new ArrayList<>();
        for (Symbol p : symbol.getExpression()) {
            prevSets.add(prevStarter.get(p.getName()));
        }
        return (Set<ParsableSymbol>) unionIfEmpty(prevSets);
    }

    private void walkSymbol(Symbol s, SymbolWalker walker) {
        Set<String> walked = new HashSet<>();
        walkSymbol(s, walker, walked);
    }

    private void walkSymbol(Symbol s, SymbolWalker walker, Collection<String> walked) {
        if (walked.contains(s.getName())) {
            return;
        }
        walker.process(s);
        walked.add(s.getName());
        List<Symbol> next2Scan = s.getExpression();
        for (Symbol next : next2Scan) {
            walkSymbol(next, walker, walked);
        }
    }

    private void checkPlaceholders() {
        if(!symbols.keySet().containsAll(placeholders.keySet())) {
            Set<String> badPlaceholders = new HashSet<>();
            badPlaceholders.addAll(placeholders.keySet());
            badPlaceholders.removeAll(symbols.keySet());
            throw new IllegalStateException("Uninstantiated Placeholders: " + badPlaceholders);
        }
    }

    private static interface SymbolWalker {

        public void process(Symbol current);

    }

    private class DetectSymbols implements SymbolWalker {

        @Override
        public void process(Symbol c) {
            if(c instanceof LookUpSymbol) {
                return;
            }
            if (!EBNFGrammar.this.symbols.containsKey(c.getName())) {
                EBNFGrammar.this.symbols.put(c.getName(), c);
                if (!(startSymbols.contains(c.getName()) || terminalSymbols.contains(c.getName()) || nonTerminalSymbols.contains(c.getName()))) {
                    EBNFGrammar.this.unnamedSymbols.add(c.getName());
                }
            }
        }
    }

    private static class PropagateFollowers implements SymbolWalker {

        private Map<String, Collection<ParsableSymbol>> following;

        PropagateFollowers(Map<String, Collection<ParsableSymbol>> following) {
            this.following = following;
        }

        @Override
        public void process(Symbol current) {
            if (!current.isDecision() && !current.isWildcard()) {
                return;
            }

            Collection<ParsableSymbol> parentFollowing = following.getOrDefault(current.getName(), new HashSet<>());
            for (Symbol s : current.getExpression()) {
                if (!following.containsKey(s.getName())) {
                    following.put(s.getName(), new HashSet<>());
                }
                following.get(s.getName()).addAll(parentFollowing);
            }
        }
    }

    private class LookUpSymbol implements Symbol {

        private String name;

        LookUpSymbol(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Symbol> getExpression() {
            if(symbols.get(name) != null) {
                return symbols.get(name).getExpression();
            }
            return new ArrayList<>();
        }

        @Override
        public boolean isDecision() {
            return symbols.get(name).isDecision();
        }

        @Override
        public boolean isWildcard() {
            return symbols.get(name).isWildcard();
        }

        @Override
        public boolean equals(Object o) {
            return Objects.equals(symbols.get(name), o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
