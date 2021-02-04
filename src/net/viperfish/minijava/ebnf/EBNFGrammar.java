package net.viperfish.minijava.ebnf;

import java.util.*;

public class EBNFGrammar {

    public static final ParsableSymbol EMPTY_STRING = new EmptyStringSymbol();

    private Map<String, Symbol> symbols;

    private Collection<String> startSymbols;
    private Collection<String> terminalSymbols;
    private Collection<String> nonTerminalSymbols;
    private Collection<String> unnamedSymbols;

    public EBNFGrammar() {
        symbols = new HashMap<>();
        startSymbols = new HashSet<>();
        terminalSymbols = new HashSet<>();
        nonTerminalSymbols = new HashSet<>();
        unnamedSymbols = new HashSet<>();
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
        return new LookUpSymbol(symbolName);
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
        Collection<String> currentNullable = new HashSet<>();
        Collection<String> prevNullable;

        do {
            System.out.println("Iteration Nullable: " + currentNullable);
            prevNullable = currentNullable;
            currentNullable = new HashSet<>();
            for (String s : symbols.keySet()) {
                Symbol symbol = symbols.get(s);
                if (symbol == EMPTY_STRING) {
                    currentNullable.add(s);
                } else {
                    if (terminalSymbols.contains(s)) {
                        currentNullable.remove(s);
                    } else if (symbol.isWildcard()) {
                        currentNullable.add(s);
                    } else if (symbol.isDecision()) {
                        for (Symbol c : symbol.getExpression()) {
                            if (prevNullable.contains(c.getName())) {
                                currentNullable.add(s);
                                break;
                            }
                        }
                    } else {
                        currentNullable.add(s);
                        for (Symbol c : symbol.getExpression()) {
                            if (!prevNullable.contains(c.getName())) {
                                currentNullable.remove(s);
                                break;
                            }
                        }
                    }
                }
            }
        } while (!currentNullable.equals(prevNullable));
        return currentNullable;
    }

    public Map<String, Collection<ParsableSymbol>> starterSets() {
        Map<String, Collection<ParsableSymbol>> currentStarter = new HashMap<>();
        Map<String, Collection<ParsableSymbol>> prevStarter = null;
        Collection<String> nullable = getNullableSymbols();
        for (String s : symbols.keySet()) {
            if (nullable.contains(s)) {
                currentStarter.put(s, new HashSet<>(Collections.singletonList(EMPTY_STRING)));
            } else {
                currentStarter.put(s, new HashSet<>());
            }
        }
        do {
            System.out.println("Iteration Starter: " + currentStarter);
            prevStarter = currentStarter;
            currentStarter = new HashMap<>();

            for (String s : symbols.keySet()) {
                Symbol symbol = symbols.get(s);
                Set<ParsableSymbol> newStarters = starters(prevStarter, symbol);
                currentStarter.put(s, newStarters);
            }
        } while (!currentStarter.equals(prevStarter));

        return currentStarter;
    }

    public Map<String, Collection<ParsableSymbol>> followers() {
        Collection<String> nullable = getNullableSymbols();
        Map<String, Collection<ParsableSymbol>> starters = starterSets();

        GetBaseFollowerSymbols getFollowerSymbols = new GetBaseFollowerSymbols(starters);
        Set<String> walkedSet = new HashSet<>();
        for (Symbol s : symbols.values()) {
            walkSymbol(s, getFollowerSymbols, walkedSet);
        }
        walkedSet.clear();

        Map<String, Collection<ParsableSymbol>> currentFollower = getFollowerSymbols.getFollowing();
        GetDecisionNodeFollowers decisionNodeFollowers = new GetDecisionNodeFollowers(currentFollower);

        for (Symbol s : symbols.values()) {
            walkSymbol(s, decisionNodeFollowers, walkedSet);
        }
        walkedSet.clear();

        for (Collection<ParsableSymbol> c : currentFollower.values()) {
            c.remove(EMPTY_STRING);
        }

        GetRuleFollowingNullable getRules = new GetRuleFollowingNullable(nullable);
        for (Symbol s : symbols.values()) {
            walkSymbol(s, getRules, walkedSet);
        }
        Map<String, Collection<Symbol>> rules = getRules.getRules();
        Map<String, Collection<ParsableSymbol>> prevFollower = null;

        System.out.println("Rules: " + rules);

        do {
            System.out.println("Iteration Follower: " + currentFollower);
            prevFollower = currentFollower;
            currentFollower = new HashMap<>();

            for (String s : symbols.keySet()) {
                Collection<ParsableSymbol> prevSet = prevFollower.getOrDefault(s, new HashSet<>());
                Set<ParsableSymbol> newSet = new HashSet<>(prevSet);
                for (Symbol p : rules.getOrDefault(s, new HashSet<>())) {
                    Collection<ParsableSymbol> prev = prevFollower.get(p.getName());
                    if (prev == null) {
                        prev = new HashSet<>();
                    }
                    newSet.addAll(prev);
                }
                currentFollower.put(s, newSet);
            }
        } while (!currentFollower.equals(prevFollower));

        for (String s : startSymbols) {
            currentFollower.get(s).add(EMPTY_STRING);
        }

        return currentFollower;
    }

    public Collection<PredictionSet> predictSets() {
        Map<String, Collection<ParsableSymbol>> starters = starterSets();
        Map<String, Collection<ParsableSymbol>> followers = followers();

        Collection<Symbol> choices = getChoicePoints();
        Collection<Symbol> reps = getRepetition();
        Collection<PredictionSet> result = new HashSet<>();


        for (Symbol s : choices) {
            Map<String, Collection<ParsableSymbol>> predictSets = new HashMap<>();
            if (s.isDecision()) {
                for (Symbol a : s.getExpression()) {
                    Collection<ParsableSymbol> starter = starters(starters, a);
                    Collection<ParsableSymbol> follower = followers.get(s.getName());
                    Collection<ParsableSymbol> predictSet = unionIfEmpty(starter, follower);
                    predictSets.put(a.getName(), predictSet);
                }
            } else {
                for (int i = 0; i < s.getExpression().size(); ++i) {
                    Symbol current = s.getExpression().get(i);
                    if (current.isDecision()) {
                        for (Symbol a : current.getExpression()) {
                            List<Symbol> symbolList = new ArrayList<>(s.getExpression().subList(i + 1, s.getExpression().size()));
                            symbolList.add(0, a);
                            Collection<ParsableSymbol> starter = starters(starters, new CompositeSymbol(symbolList));
                            Collection<ParsableSymbol> follower = followers.get(s.getName());
                            Collection<ParsableSymbol> predictSet = unionIfEmpty(starter, follower);
                            predictSets.put(a.getName(), predictSet);
                        }
                    }
                }
            }
            result.add(new DecisionPredictSet(s, predictSets));
        }

        for (Symbol s : reps) {
            for (int i = 0; i < s.getExpression().size(); ++i) {
                Symbol current = s.getExpression().get(i);
                if (current.isWildcard()) {
                    Map<String, Collection<ParsableSymbol>> predictSets = new HashMap<>();
                    Collection<ParsableSymbol> predictCurrent = new HashSet<>(starters(starters, current.getExpression().get(0)));
                    Collection<ParsableSymbol> predictLater = null;

                    List<Symbol> symbolList = s.getExpression().subList(i + 1, s.getExpression().size());
                    Symbol composite = new CompositeSymbol(symbolList);
                    Collection<ParsableSymbol> starter = starters(starters, new CompositeSymbol(symbolList));
                    Collection<ParsableSymbol> follower = followers.get(s.getName());
                    predictLater = unionIfEmpty(starter, follower);

                    boolean isLL1 = true;
                    if (predictCurrent.contains(EMPTY_STRING)) {
                        isLL1 = false;
                    }
                    if (!Collections.disjoint(predictCurrent, predictLater)) {
                        isLL1 = false;
                    }

                    predictSets.put(current.getExpression().get(0).getName(), predictCurrent);
                    predictSets.put(composite.getName(), predictLater);
                    result.add(new WildcardPredictSet(s, predictSets, isLL1));
                }
            }
        }
        return result;
    }

    private Collection<Symbol> getChoicePoints() {
        Collection<Symbol> result = new HashSet<>();
        for (Symbol s : this.symbols.values()) {
            if (s.isDecision()) {
                result.add(s);
            } else {
                for (Symbol c : s.getExpression()) {
                    if (c.isDecision() && unnamedSymbols.contains(c.getName())) {
                        result.add(s);
                        break;
                    }
                }
            }
        }
        return result;
    }

    private Collection<Symbol> getRepetition() {
        Collection<Symbol> result = new HashSet<>();
        for (Symbol s : this.symbols.values()) {
            for (Symbol c : s.getExpression()) {
                if (c.isWildcard()) {
                    result.add(s);
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
        Set<ParsableSymbol> newSet = (Set<ParsableSymbol>) unionIfEmpty(prevSets);
        return newSet;
    }

    private <T> Collection<T> unionIfEmpty(Collection<? extends Collection<T>> chainedUnionIfEmpty) {
        List<? extends Collection<T>> ogList = new ArrayList<>(chainedUnionIfEmpty);
        if (ogList.size() == 1) {
            return new HashSet<>(ogList.get(0));
        }
        return unionIfEmpty(ogList.get(0), unionIfEmpty(ogList.subList(1, ogList.size())));
    }

    private <T> Collection<T> unionIfEmpty(Collection<T> a, Collection<T> b) {
        if (!a.contains(EMPTY_STRING)) {
            return new HashSet<>(a);
        } else {
            Set<T> result = new HashSet<>();
            result.addAll(a);
            result.addAll(b);
            result.remove(EMPTY_STRING);
            return result;
        }
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
        for (Symbol next : s.getExpression()) {
            walkSymbol(next, walker, walked);
        }
    }

    private static interface SymbolWalker {

        public void process(Symbol current);

    }

    private class DetectSymbols implements SymbolWalker {

        @Override
        public void process(Symbol c) {
            if (!EBNFGrammar.this.symbols.containsKey(c.getName())) {
                EBNFGrammar.this.symbols.put(c.getName(), c);
                if (!(startSymbols.contains(c.getName()) || terminalSymbols.contains(c.getName()) || nonTerminalSymbols.contains(c.getName()))) {
                    EBNFGrammar.this.unnamedSymbols.add(c.getName());
                }
            }
        }
    }

    // TODO: Address where if there are several item in wildcard, the final item, also fix decision items
    private class GetBaseFollowerSymbols implements SymbolWalker {

        private Map<String, Collection<ParsableSymbol>> following;
        private Map<String, Collection<ParsableSymbol>> starters;

        public GetBaseFollowerSymbols(Map<String, Collection<ParsableSymbol>> starters) {
            following = new HashMap<>();
            this.starters = starters;
        }

        public Map<String, Collection<ParsableSymbol>> getFollowing() {
            return following;
        }

        @Override
        public void process(Symbol current) {
            if (current.isDecision()) {
                return;
            }
            if (current.isWildcard() && current.getExpression().size() == 1) {
                Symbol embedded = current.getExpression().get(0);
                if (!following.containsKey(embedded.getName())) {
                    following.put(embedded.getName(), new HashSet<>());
                }
                following.get(embedded.getName()).addAll(starters.get(embedded.getName()));
            } else {
                for (int i = 1; i < current.getExpression().size(); ++i) {
                    Symbol followed = current.getExpression().get(i - 1);
                    if (!following.containsKey(followed.getName())) {
                        following.put(followed.getName(), new HashSet<>());
                    }
                    List<Symbol> followers = current.getExpression().subList(i, current.getExpression().size());
                    Collection<ParsableSymbol> result = starters(this.starters, new CompositeSymbol(followers));
                    following.get(followed.getName()).addAll(result);
                }
            }
        }
    }

    private class GetDecisionNodeFollowers implements SymbolWalker {

        private Map<String, Collection<ParsableSymbol>> following;

        public GetDecisionNodeFollowers(Map<String, Collection<ParsableSymbol>> following) {
            this.following = following;
        }

        @Override
        public void process(Symbol current) {
            if (!current.isDecision()) {
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

    private class GetRuleFollowingNullable implements SymbolWalker {

        private Map<String, Collection<Symbol>> rules;
        private Collection<String> nullable;

        public GetRuleFollowingNullable(Collection<String> nullable) {
            rules = new HashMap<>();
            this.nullable = nullable;
        }

        public Map<String, Collection<Symbol>> getRules() {
            return rules;
        }

        @Override
        public void process(Symbol current) {
            if (current.isDecision()) {
                return;
            }
            if (current.isWildcard() && current.getExpression().size() == 1) {
                Symbol embedded = current.getExpression().get(0);
                if (nullable.contains(embedded.getName())) {
                    if (!rules.containsKey(embedded.getName())) {
                        rules.put(embedded.getName(), new HashSet<>());
                    }
                    rules.get(embedded.getName()).add(embedded);
                }
            } else {
                for (int i = 0; i < current.getExpression().size() - 1; ++i) {
                    Symbol checking = current.getExpression().get(i);
                    if (!rules.containsKey(checking.getName())) {
                        rules.put(checking.getName(), new HashSet<>());
                    }
                    rules.get(checking.getName()).add(current);
                    for (int j = i + 1; j < current.getExpression().size(); ++j) {
                        Symbol toCheck = current.getExpression().get(j);
                        if (!nullable.contains(toCheck.getName())) {
                            rules.get(checking.getName()).remove(current);
                        }
                    }
                }
                if (!current.getExpression().isEmpty()) {
                    Symbol finalSymbol = current.getExpression().get(current.getExpression().size() - 1);
                    if (!rules.containsKey(finalSymbol.getName())) {
                        rules.put(finalSymbol.getName(), new HashSet<>());
                    }
                    rules.get(finalSymbol.getName()).add(current);
                }
            }
        }
    }

    private class LookUpSymbol implements Symbol {

        private String name;

        public LookUpSymbol(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Symbol> getExpression() {
            return symbols.get(name).getExpression();
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
    }

}
