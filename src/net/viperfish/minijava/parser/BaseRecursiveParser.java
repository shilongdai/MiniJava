package net.viperfish.minijava.parser;

import net.viperfish.minijava.CompilerGlobal;
import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.ParsableSymbol;
import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenScanner;
import net.viperfish.minijava.scanner.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseRecursiveParser implements RecursiveParser {

    private TokenScanner scanner;
    private Token currentToken;
    private List<Token> peeked;

    public BaseRecursiveParser(TokenScanner scanner) {
        this.scanner = scanner;
        this.peeked = new ArrayList<>();
    }

    @Override
    public void init() throws IOException, ParsingException {
        currentToken = scanner.nextToken();
    }

    @Override
    public List<AST> parse(List<Symbol> symbols) throws IOException, ParsingException, GrammarException {
        if (symbols.isEmpty()) {
            return new ArrayList<>();
        }
        Symbol next = symbols.get(0);
        List<AST> result = new ArrayList<>();
        if (CompilerGlobal.DEBUG_3) {
            System.out.println("Looking At Symbol: " + next.getName());
        }
        if (next instanceof ParsableSymbol) {
            if (CompilerGlobal.DEBUG_2) {
                System.out.println("Accepting: " + next.getName());
            }
            AST t = accept((ParsableSymbol) next);
            List<AST> other = parse(symbols.subList(1, symbols.size()));
            if(t != null) {
                result.add(t);
            }
            result.addAll(other);
        } else if (next.isDecision()) {
            if (CompilerGlobal.DEBUG_3) {
                System.out.println("Deciding:");
            }
            AST decisionAST = handleDecisionPoint(symbols);
            List<AST> others = parse(symbols.subList(1, symbols.size()));
            if(decisionAST != null) {
                result.add(decisionAST);
            }
            result.addAll(others);
        } else if (next.isWildcard()) {
            if (CompilerGlobal.DEBUG_3) {
                System.out.println("Handling Repeat");
            }
            AST repeatAST = handleRepeatingPoint(symbols);
            List<AST> others = parse(symbols.subList(1, symbols.size()));
            if(repeatAST != null) {
                result.add(repeatAST);
            }
            result.addAll(others);
        } else {
            if (CompilerGlobal.DEBUG_3) {
                System.out.println("Analysing sub-components");
            }
            List<AST> childASTs = parse(next.getExpression());
            ASTConstructor constructor = getASTConstructor(next);
            AST nextAST = constructor.buildTree(next, childASTs);
            List<AST> others = parse(symbols.subList(1, symbols.size()));
            if(nextAST != null) {
                result.add(nextAST);
            }
            result.addAll(others);
        }
        if (CompilerGlobal.DEBUG_3) {
            System.out.println("Finished with: " + next.getName());
        }
        return result;
    }

    protected abstract Collection<ParsableSymbol> getFollowers(Symbol symbol);

    protected abstract Collection<ParsableSymbol> getStarters(List<Symbol> symbols);

    protected abstract AST handleDecisionPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException;

    protected abstract AST handleRepeatingPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException;

    protected abstract ASTConstructor getASTConstructor(Symbol currentSymbol);

    protected Token getToken() {
        return this.currentToken;
    }

    protected Token peek() throws IOException, ParsingException {
        return peek(1).get(0);
    }

    protected List<Token> peek(int amount) throws IOException, ParsingException {
        if(peeked.size() < amount) {
            while(peeked.size() != amount) {
                peeked.add(scanner.nextToken());
            }
        }
        return new ArrayList<>(peeked.subList(0, amount));
    }

    protected AST accept(ParsableSymbol symbol) throws GrammarException, IOException, ParsingException {
        if (CompilerGlobal.DEBUG_2) {
            System.out.println(String.format("Comparing: %s vs %s", symbol.getName(), currentToken.getSpelling()));
        }
        if (symbol.isInstance(currentToken)) {
            Token toConvert = currentToken;
            if (!peeked.isEmpty()) {
                currentToken = peeked.remove(0);
            } else {
                currentToken = scanner.nextToken();
            }

            if (toConvert.getTokenType().equals(TokenType.TRUE) || toConvert.getTokenType().equals(TokenType.FALSE)) {
                return new BooleanLiteral(toConvert);
            } else if (toConvert.getTokenType().equals(TokenType.ID)) {
                return new Identifier(toConvert);
            } else if (toConvert.getTokenType().equals(TokenType.NUM)) {
                return new IntLiteral(toConvert);
            } else if (toConvert.getTokenType().equals(TokenType.OPERATOR)) {
                return new Operator(toConvert);
            } else if (toConvert.getTokenType().equals(TokenType.THIS)) {
                return new ThisRef(toConvert.getPosition());
            } else if (toConvert.getTokenType().equals(TokenType.INT)) {
                return new BaseType(TypeKind.INT, toConvert.getPosition());
            } else if(toConvert.getTokenType().equals(TokenType.BOOLEAN)) {
                return new BaseType(TypeKind.BOOLEAN, toConvert.getPosition());
            } else if(toConvert.getTokenType().equals(TokenType.VOID)) {
                return new BaseType(TypeKind.VOID, toConvert.getPosition());
            } else if(toConvert.getTokenType().equals(TokenType.PUBLIC) || toConvert.getTokenType().equals(TokenType.PRIVATE)) {
                return new DefaultAST(toConvert.getPosition(), symbol, new ArrayList<>());
            } else if(toConvert.getTokenType().equals(TokenType.STATIC)) {
                return new DefaultAST(toConvert.getPosition(), symbol, new ArrayList<>());
            } else {
                return null;
            }
        } else {
            throw new GrammarException(symbol, currentToken);
        }
    }

}
