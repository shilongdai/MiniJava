package net.viperfish.minijava.parser;

import net.viperfish.minijava.CompilerGlobal;
import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ebnf.*;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenScanner;
import net.viperfish.minijava.scanner.TokenType;

import java.io.IOException;
import java.util.*;

public class MiniJavaEBNFGrammarParser extends EBNFGrammarBackedParser {

    private static final EBNFGrammar GRAMMAR;

    static {
        GRAMMAR = new EBNFGrammar();

        // terminals
        ParsableSymbol id = new TokenTypeParsibleSymbol(TokenType.ID);
        GRAMMAR.registerTerminalSymbol(id);
        ParsableSymbol tHis = new TokenTypeParsibleSymbol(TokenType.THIS);
        GRAMMAR.registerTerminalSymbol(tHis);
        ParsableSymbol dot = new TokenTypeParsibleSymbol(TokenType.DOT);
        GRAMMAR.registerTerminalSymbol(dot);
        ParsableSymbol lsqb = new TokenTypeParsibleSymbol(TokenType.LEFT_SQ_BRACKET);
        GRAMMAR.registerTerminalSymbol(lsqb);
        ParsableSymbol rsqb = new TokenTypeParsibleSymbol(TokenType.RIGHT_SQ_BRACKET);
        GRAMMAR.registerTerminalSymbol(rsqb);
        ParsableSymbol lpb = new TokenTypeParsibleSymbol(TokenType.LEFT_PARANTHESIS);
        GRAMMAR.registerTerminalSymbol(lpb);
        ParsableSymbol rpb = new TokenTypeParsibleSymbol(TokenType.RIGHT_PARANTHESIS);
        GRAMMAR.registerTerminalSymbol(rpb);
        ParsableSymbol unop = new TokenTypeWSpellingParsibleSymbol(TokenType.OPERATOR, Arrays.asList("!", "-"));
        GRAMMAR.registerTerminalSymbol(unop);
        ParsableSymbol multop = new TokenTypeWSpellingParsibleSymbol(TokenType.OPERATOR, Arrays.asList("*", "/"));
        GRAMMAR.registerTerminalSymbol(multop);
        ParsableSymbol addop = new TokenTypeWSpellingParsibleSymbol(TokenType.OPERATOR, Arrays.asList("+", "-"));
        GRAMMAR.registerTerminalSymbol(addop);
        ParsableSymbol relop = new TokenTypeWSpellingParsibleSymbol(TokenType.OPERATOR, Arrays.asList("<", "<=", ">", ">="));
        GRAMMAR.registerTerminalSymbol(relop);
        ParsableSymbol eqop = new TokenTypeWSpellingParsibleSymbol(TokenType.OPERATOR, Arrays.asList("==", "!="));
        GRAMMAR.registerTerminalSymbol(eqop);
        ParsableSymbol cjop = new TokenTypeWSpellingParsibleSymbol(TokenType.OPERATOR, Collections.singletonList("&&"));
        GRAMMAR.registerTerminalSymbol(cjop);
        ParsableSymbol djop = new TokenTypeWSpellingParsibleSymbol(TokenType.OPERATOR, Collections.singletonList("||"));
        GRAMMAR.registerTerminalSymbol(djop);
        ParsableSymbol num = new TokenTypeParsibleSymbol(TokenType.NUM);
        GRAMMAR.registerTerminalSymbol(num);
        ParsableSymbol True = new TokenTypeParsibleSymbol(TokenType.TRUE);
        GRAMMAR.registerTerminalSymbol(True);
        ParsableSymbol False = new TokenTypeParsibleSymbol(TokenType.FALSE);
        GRAMMAR.registerTerminalSymbol(False);
        ParsableSymbol New = new TokenTypeParsibleSymbol(TokenType.NEW);
        GRAMMAR.registerTerminalSymbol(New);
        ParsableSymbol Int = new TokenTypeParsibleSymbol(TokenType.INT);
        GRAMMAR.registerTerminalSymbol(Int);
        ParsableSymbol llb = new TokenTypeParsibleSymbol(TokenType.LEFT_LARGE_BRACKET);
        GRAMMAR.registerTerminalSymbol(llb);
        ParsableSymbol rlb = new TokenTypeParsibleSymbol(TokenType.RIGHT_LARGE_BRACKET);
        GRAMMAR.registerTerminalSymbol(rlb);
        ParsableSymbol Boolean = new TokenTypeParsibleSymbol(TokenType.BOOLEAN);
        GRAMMAR.registerTerminalSymbol(Boolean);
        ParsableSymbol semi = new TokenTypeParsibleSymbol(TokenType.SEMI_COLON);
        GRAMMAR.registerTerminalSymbol(semi);
        ParsableSymbol eq = new TokenTypeParsibleSymbol(TokenType.EQ);
        GRAMMAR.registerTerminalSymbol(eq);
        ParsableSymbol comma = new TokenTypeParsibleSymbol(TokenType.COMMA);
        GRAMMAR.registerTerminalSymbol(comma);
        ParsableSymbol reTurn = new TokenTypeParsibleSymbol(TokenType.RETURN);
        GRAMMAR.registerTerminalSymbol(reTurn);
        ParsableSymbol If = new TokenTypeParsibleSymbol(TokenType.IF);
        GRAMMAR.registerTerminalSymbol(If);
        ParsableSymbol Else = new TokenTypeParsibleSymbol(TokenType.ELSE);
        GRAMMAR.registerTerminalSymbol(Else);
        ParsableSymbol While = new TokenTypeParsibleSymbol(TokenType.WHILE);
        GRAMMAR.registerTerminalSymbol(While);
        ParsableSymbol Static = new TokenTypeParsibleSymbol(TokenType.STATIC);
        GRAMMAR.registerTerminalSymbol(Static);
        ParsableSymbol Public = new TokenTypeParsibleSymbol(TokenType.PUBLIC);
        GRAMMAR.registerTerminalSymbol(Public);
        ParsableSymbol Private = new TokenTypeParsibleSymbol(TokenType.PRIVATE);
        GRAMMAR.registerTerminalSymbol(Private);
        ParsableSymbol Void = new TokenTypeParsibleSymbol(TokenType.VOID);
        GRAMMAR.registerTerminalSymbol(Void);
        ParsableSymbol Class = new TokenTypeParsibleSymbol(TokenType.CLASS);
        GRAMMAR.registerTerminalSymbol(Class);
        ParsableSymbol terminalSymbol = new TokenTypeParsibleSymbol(TokenType.EOT);
        GRAMMAR.registerTerminalSymbol(terminalSymbol);

        // non terminals

        // Reference ::= id | this | Reference . id
        List<Symbol> referenceList = new ArrayList<>();
        // id | this | Reference . id -> (id | this) (. id)*
        referenceList.add(new DecisionPointSymbol(Arrays.asList(tHis, id)));
        Symbol dotIdContinue = new WildCardSymbol(new CompositeSymbol(Arrays.asList(dot, id)));
        referenceList.add(dotIdContinue);

        Symbol reference = new CompositeSymbol("Reference", referenceList);
        GRAMMAR.registerNonTerminalSymbol(reference);

        // Type ::= int | boolean | id | ( int | id ) []
        // -> int | boolean | id | int [ ] | id [ ] -> boolean | int (ε | [ ] ) | id ( ε | [ ] )
        List<Symbol> typeList = new ArrayList<>();
        List<Symbol> typeArrayDec = new ArrayList<>();
        typeArrayDec.add(lsqb);
        typeArrayDec.add(rsqb);
        Symbol emptyOrSqb = new DecisionPointSymbol(Arrays.asList(EBNFGrammar.EMPTY_STRING, new CompositeSymbol(typeArrayDec)));
        typeList.add(Boolean);
        Symbol intType = new CompositeSymbol(Arrays.asList(Int, emptyOrSqb));
        Symbol idType = new CompositeSymbol(Arrays.asList(id, emptyOrSqb));
        typeList.add(intType);
        typeList.add(idType);

        Symbol type = new DecisionPointSymbol("Type", typeList);
        GRAMMAR.registerNonTerminalSymbol(type);

        /*
        Expression ::=
                    Reference
                  | Reference [ Expression ]
                  | Reference ( ArgumentList? )
                  | unop Expression
                  | Expression binop Expression
                  | ( Expression )
                  | num | true | false
                  | new ( id () | int [ Expression ] | id [ Expression ] )
         */

        /*
        BExp :=
            Reference ( ε | [ Exp ] | (ArgList?) ) |
            num | true | false |
            new ( id() | int[ Exp ] | id[ Exp ] )
         */
        List<Symbol> bExpList = new ArrayList<>();
        // apply left factorization Reference ( [ Expression ] | ( ArgumentList? ) | ε )
        List<Symbol> factorizedExpressionList = new ArrayList<>();
        Symbol argListOrEmpty = new DecisionPointSymbol(Arrays.asList(GRAMMAR.placeholderName("ArgumentList"), EBNFGrammar.EMPTY_STRING));
        factorizedExpressionList.add(new CompositeSymbol(Arrays.asList(lsqb, GRAMMAR.placeholderName("Expression"), rsqb)));
        factorizedExpressionList.add(new CompositeSymbol(Arrays.asList(lpb, argListOrEmpty, rpb)));
        factorizedExpressionList.add(EBNFGrammar.EMPTY_STRING);
        bExpList.add(new CompositeSymbol(Arrays.asList(reference, new DecisionPointSymbol(factorizedExpressionList))));
        bExpList.add(num);
        bExpList.add(True);
        bExpList.add(False);
        // new ( id () | int [ Expression ] | id [ Expression ] ) -> new ( id (() | [ Expression ]) | int [ Expression ])
        List<Symbol> rhsNewList = new ArrayList<>();
        List<Symbol> rhsFactoredList = new ArrayList<>();
        rhsFactoredList.add(new CompositeSymbol(Arrays.asList(lpb, rpb)));
        rhsFactoredList.add(new CompositeSymbol(Arrays.asList(lsqb, GRAMMAR.placeholderName("Expression"), rsqb)));
        rhsNewList.add(new CompositeSymbol(Arrays.asList(id, new DecisionPointSymbol(rhsFactoredList))));
        rhsNewList.add(new CompositeSymbol(Arrays.asList(Int, lsqb, GRAMMAR.placeholderName("Expression"), rsqb)));
        Symbol rhsNew = new DecisionPointSymbol(rhsNewList);
        Symbol newDec = new CompositeSymbol(Arrays.asList(New, rhsNew));
        bExpList.add(newDec);
        Symbol bExp = new DecisionPointSymbol("BExp", bExpList);

        /*
        PExp :=
            (Exp) | BExp
         */
        List<Symbol> pExpList = new ArrayList<>();
        pExpList.add(new CompositeSymbol(Arrays.asList(lpb, GRAMMAR.placeholderName("Expression"), rpb)));
        pExpList.add(bExp);
        Symbol pExp = new DecisionPointSymbol("PExp", pExpList);

        /*
        UExp :=
            unop Exp | PExp
         */
        List<Symbol> uExpList = new ArrayList<>();
        uExpList.add(pExp);
        uExpList.add(new CompositeSymbol(Arrays.asList(unop, GRAMMAR.placeholderName("Expression"))));
        Symbol uExp = new DecisionPointSymbol("UExp", uExpList);

        /*
        MExp :=
            UExp ( multop UExp )*
         */
        List<Symbol> mExpList = new ArrayList<>();
        mExpList.add(uExp);
        mExpList.add(new WildCardSymbol("MExpRep", new CompositeSymbol(Arrays.asList(multop, uExp))));
        Symbol mExp = new CompositeSymbol("MExp", mExpList);

        /*
        AExp :=
            MExp ( addop MExp )*
         */
        List<Symbol> aExpList = new ArrayList<>();
        aExpList.add(mExp);
        aExpList.add(new WildCardSymbol("AExpRep", new CompositeSymbol(Arrays.asList(addop, mExp))));
        Symbol aExp = new CompositeSymbol("AExp", aExpList);

        /*
        RExp :=
            AExp ( relop AExp )*
         */
        List<Symbol> rExpList = new ArrayList<>();
        rExpList.add(aExp);
        rExpList.add(new WildCardSymbol("RExpRep", new CompositeSymbol(Arrays.asList(relop, aExp))));
        Symbol rExp = new CompositeSymbol("RExp", rExpList);

        /*
        EqExp :=
            RExp ( eqop RExp )*
         */
        List<Symbol> eqExpList = new ArrayList<>();
        eqExpList.add(rExp);
        eqExpList.add(new WildCardSymbol("EqExpRep", new CompositeSymbol(Arrays.asList(eqop, rExp))));
        Symbol eqExp = new CompositeSymbol("EqExp", eqExpList);

        /*
        CExp :=
            EqExp ( cjop EqExp )*
         */
        List<Symbol> cExpList = new ArrayList<>();
        cExpList.add(eqExp);
        cExpList.add(new WildCardSymbol("CExpRep", new CompositeSymbol(Arrays.asList(cjop, eqExp))));
        Symbol cExp = new CompositeSymbol("CExp", cExpList);

        /*
        Exp :=
            CExp ( djop CExp )*
         */
        List<Symbol> expressionList = new ArrayList<>();
        expressionList.add(cExp);
        expressionList.add(new WildCardSymbol("ExpRep", new CompositeSymbol(Arrays.asList(djop, cExp))));
        Symbol expression = new CompositeSymbol("Expression", expressionList);
        GRAMMAR.registerNonTerminalSymbol(expression);

        // ArgumentList ::= Expression ( , Expression )*
        List<Symbol> argList = new ArrayList<>();
        argList.add(expression);
        argList.add(new WildCardSymbol(new CompositeSymbol(Arrays.asList(comma, expression))));

        Symbol argumentList = new CompositeSymbol("ArgumentList", argList);
        GRAMMAR.registerNonTerminalSymbol(argumentList);

        /*
        Statement ::=
            { Statement* }
          | Type id = Expression ;
          | Reference = Expression ;
          | Reference [ Expression ] = Expression ;
          | Reference ( ArgumentList? ) ;
          | return Expression? ;
          | if ( Expression ) Statement (else Statement)?
          | while ( Expression ) Statement
         */
        List<Symbol> statementList = new ArrayList<>();
        List<Symbol> stmtWildCard = new ArrayList<>();
        stmtWildCard.add(llb);
        stmtWildCard.add(new WildCardSymbol(GRAMMAR.placeholderName("Statement")));
        stmtWildCard.add(rlb);
        statementList.add(new CompositeSymbol(stmtWildCard));
        // apply left factorization Reference ( = Expression | [ Expression ] = Expression | ( ArgumentList? ) ) ;
        // then eliminates reference and type to ensure LL1
        List<Symbol> stmtLFList = new ArrayList<>();
        stmtLFList.add(new CompositeSymbol(Arrays.asList(eq, expression)));
        stmtLFList.add(new CompositeSymbol(Arrays.asList(lsqb, expression, rsqb, eq, expression)));
        stmtLFList.add(new CompositeSymbol(Arrays.asList(lpb, argListOrEmpty, rpb)));
        Symbol stmtLF = new DecisionPointSymbol(stmtLFList);
        statementList.add(new CompositeSymbol(Arrays.asList(tHis, dotIdContinue, stmtLF, semi)));
        statementList.add(new CompositeSymbol(Arrays.asList(new DecisionPointSymbol(Arrays.asList(Boolean, intType)), id, eq, expression, semi)));
        // ( e | [exp] )
        List<Symbol> emptyOrBExpList = new ArrayList<>();
        emptyOrBExpList.add(EBNFGrammar.EMPTY_STRING);
        emptyOrBExpList.add(new CompositeSymbol(Arrays.asList(lsqb, expression, rsqb)));
        Symbol emptyOrBExp = new DecisionPointSymbol(emptyOrBExpList);
        // (e | [exp]) = exp;
        List<Symbol> referenceEqExpList = new ArrayList<>();
        referenceEqExpList.add(emptyOrBExp);
        referenceEqExpList.addAll(Arrays.asList(eq, expression, semi));
        Symbol referenceEqExpSymbol = new CompositeSymbol(referenceEqExpList);
        // (e | [exp]) = exp; | ( Args? );
        List<Symbol> idLfFactoredList = new ArrayList<>();
        idLfFactoredList.add(referenceEqExpSymbol);
        idLfFactoredList.add(new CompositeSymbol(Arrays.asList(lpb, argListOrEmpty, rpb, semi)));
        Symbol idLfFactoredDecisionSymbol = new DecisionPointSymbol(idLfFactoredList);
        Symbol idLfFactoredSymbol = new CompositeSymbol("RefStmt", Arrays.asList(dotIdContinue, idLfFactoredDecisionSymbol));
        // ( e | []) id = exp;
        List<Symbol> idRfFactoredList = new ArrayList<>();
        idRfFactoredList.add(emptyOrSqb);
        idRfFactoredList.addAll(Arrays.asList(id, eq, expression, semi));
        Symbol idRfFactoredSymbol = new CompositeSymbol("TypeStmt", idRfFactoredList);
        Symbol idFactoredDecision = new DecisionPointSymbol("RefOrType", Arrays.asList(idLfFactoredSymbol, idRfFactoredSymbol));
        statementList.add(new CompositeSymbol(Arrays.asList(id, idFactoredDecision)));
        statementList.add(new CompositeSymbol(Arrays.asList(reTurn, new DecisionPointSymbol(Arrays.asList(expression, EBNFGrammar.EMPTY_STRING)), semi)));
        List<Symbol> ifStmtList = new ArrayList<>();
        ifStmtList.add(If);
        ifStmtList.add(lpb);
        ifStmtList.add(expression);
        ifStmtList.add(rpb);
        ifStmtList.add(GRAMMAR.placeholderName("Statement"));
        ifStmtList.add(new DecisionPointSymbol("TrailingElse", Arrays.asList(new CompositeSymbol("ElseStmt", Arrays.asList(Else, GRAMMAR.placeholderName("Statement"))), EBNFGrammar.EMPTY_STRING)));
        statementList.add(new CompositeSymbol(ifStmtList));
        statementList.add(new CompositeSymbol(Arrays.asList(While, lpb, expression, rpb, GRAMMAR.placeholderName("Statement"))));

        Symbol statement = new DecisionPointSymbol("Statement", statementList);
        GRAMMAR.registerNonTerminalSymbol(statement);

        // ParameterList ::= Type id ( , Type id )*
        List<Symbol> paramList = new ArrayList<>();
        paramList.add(type);
        paramList.add(id);
        paramList.add(new WildCardSymbol(new CompositeSymbol(Arrays.asList(comma, type, id))));

        Symbol parameterList = new CompositeSymbol("ParameterList", paramList);
        GRAMMAR.registerNonTerminalSymbol(parameterList);

        // Access ::= static ?
        Symbol access = new DecisionPointSymbol("Access", Arrays.asList(Static, EBNFGrammar.EMPTY_STRING));
        GRAMMAR.registerNonTerminalSymbol(access);

        // Visibility ::= ( public | private )?
        List<Symbol> visibilityList = new ArrayList<>();
        visibilityList.add(new DecisionPointSymbol(Arrays.asList(Public, Private)));
        visibilityList.add(EBNFGrammar.EMPTY_STRING);

        Symbol visibility = new DecisionPointSymbol("Visibility", visibilityList);
        GRAMMAR.registerNonTerminalSymbol(visibility);

        // MethodDeclaration ::= Visibility Access ( Type | void ) id ( ParameterList? ) {Statement*}
        List<Symbol> methodDeclarationList = new ArrayList<>();
        List<Symbol> methodDistinguished = new ArrayList<>();
        methodDeclarationList.add(visibility);
        methodDeclarationList.add(access);
        methodDeclarationList.add(new DecisionPointSymbol(Arrays.asList(type, Void)));
        methodDeclarationList.add(id);
        methodDistinguished.add(lpb);
        methodDistinguished.add(new DecisionPointSymbol(Arrays.asList(parameterList, EBNFGrammar.EMPTY_STRING)));
        methodDistinguished.add(rpb);
        methodDistinguished.add(llb);
        methodDistinguished.add(new WildCardSymbol(statement));
        methodDistinguished.add(rlb);
        methodDeclarationList.addAll(methodDistinguished);

        Symbol methodDeclaration = new CompositeSymbol("MethodDeclaration", methodDeclarationList);
        GRAMMAR.registerNonTerminalSymbol(methodDeclaration);

        // FieldDeclaration ::= Visibility Access Type id ;
        List<Symbol> fieldDecList = new ArrayList<>();
        fieldDecList.add(visibility);
        fieldDecList.add(access);
        fieldDecList.add(type);
        fieldDecList.add(id);
        fieldDecList.add(semi);

        Symbol fieldDeclaration = new CompositeSymbol("FieldDeclaration", fieldDecList);
        GRAMMAR.registerNonTerminalSymbol(fieldDeclaration);

        // ClassDeclaration ::= class id { ( FieldDeclaration | MethodDeclaration )* }
        List<Symbol> classDecList = new ArrayList<>();
        classDecList.add(Class);
        classDecList.add(id);
        classDecList.add(llb);
        // decompose the declarations for LL1
        List<Symbol> typedDeclaration = new ArrayList<>();
        typedDeclaration.add(type);
        typedDeclaration.add(id);
        typedDeclaration.add(new DecisionPointSymbol(Arrays.asList(semi, new CompositeSymbol(methodDistinguished))));
        List<Symbol> voidDeclaration = new ArrayList<>();
        voidDeclaration.add(Void);
        voidDeclaration.add(id);
        voidDeclaration.addAll(methodDistinguished);
        Symbol decomposedDecs = new CompositeSymbol(Arrays.asList(visibility, access, new DecisionPointSymbol(Arrays.asList(new CompositeSymbol(typedDeclaration), new CompositeSymbol(voidDeclaration)))));
        classDecList.add(new WildCardSymbol(decomposedDecs));
        classDecList.add(rlb);

        Symbol classDeclaration = new CompositeSymbol("ClassDeclaration", classDecList);
        GRAMMAR.registerNonTerminalSymbol(classDeclaration);

        Symbol program = new CompositeSymbol("Program", Arrays.asList(new WildCardSymbol(classDeclaration), terminalSymbol));
        GRAMMAR.registerStartSymbol(program);
    }

    public MiniJavaEBNFGrammarParser(TokenScanner scanner) {
        super(scanner, GRAMMAR, new HashMap<>());
    }

    @Override
    protected AST handleDecisionPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException {
        Symbol decidingPoint = symbols.get(0);
        ASTConstructor ctor = getASTConstructor(decidingPoint);
        if(decidingPoint.getName().equals("TrailingElse")) {
            if(CompilerGlobal.DEBUG_3) {
                System.out.println("Non LL1 deciding trailing else");
            }
            if(getToken().getTokenType() == TokenType.ELSE) {
                for(Symbol s : decidingPoint.getExpression()) {
                    if(s.getName().equals("ElseStmt")) {
                        if(CompilerGlobal.DEBUG_3) {
                            System.out.println("Encountered Else, greedily proceeding");
                        }
                        return ctor.buildTree(s, parse(Collections.singletonList(s)));
                    }
                }
                throw new UnsupportedOperationException("Unsupported MiniJava Grammar");
            }
        } else if(decidingPoint.getName().equals("RefOrType")) {
            if(getToken().getTokenType() != TokenType.LEFT_SQ_BRACKET) {
                if(CompilerGlobal.DEBUG_3) {
                    System.out.println("No ambiguity, proceeding normally");
                }
                return super.handleDecisionPoint(symbols);
            } else {
                Token peeked = peek();
                if(peeked.getTokenType() == TokenType.RIGHT_SQ_BRACKET) {
                    for(Symbol s : decidingPoint.getExpression()) {
                        if(s.getName().equals("TypeStmt")) {
                            if(CompilerGlobal.DEBUG_3) {
                                System.out.println("Peeked ], handling as []");
                            }
                            return ctor.buildTree(s, parse(Collections.singletonList(s)));
                        }
                    }
                    throw new UnsupportedOperationException("Unsupported MiniJava Grammar");
                } else {
                    for(Symbol s : decidingPoint.getExpression()) {
                        if(s.getName().equals("RefStmt")) {
                            if(CompilerGlobal.DEBUG_3) {
                                System.out.println("Did not peek ], handling as ref");
                            }
                            return ctor.buildTree(s, parse(Collections.singletonList(s)));
                        }
                    }
                    throw new UnsupportedOperationException("Unsupported MiniJava Grammar");
                }
            }
        } else {
            return super.handleDecisionPoint(symbols);
        }
        return null;
    }

    @Override
    protected AST handleRepeatingPoint(List<Symbol> symbols) throws IOException, ParsingException, GrammarException {
        Symbol wildCard = symbols.get(0);
        if (Arrays.asList("AExpRep", "MExpRep", "ExpRep", "EqExpRep", "CExpRep", "RExpRep").contains(wildCard.getName())) {
            ASTConstructor ctor = getASTConstructor(wildCard);
            List<AST> childASTs = handleRepeatedExpressions(wildCard.getExpression().get(0));
            return ctor.buildTree(wildCard, childASTs);
        } else {
            return super.handleRepeatingPoint(symbols);
        }
    }

    private List<AST> handleRepeatedExpressions(Symbol repeated) throws ParsingException, GrammarException, IOException {
        ParsableSymbol operator = (ParsableSymbol) repeated.getExpression().get(0);
        ASTConstructor ctor = getASTConstructor(repeated);
        List<AST> result = new ArrayList<>();
        while (operator.isInstance(getToken())) {
            if (CompilerGlobal.DEBUG_3) {
                System.out.println("Found following operator, proceeding");
            }
            List<AST> childs = parse(Collections.singletonList(repeated));
            AST childAST = ctor.buildTree(repeated, childs);
            result.add(childAST);
        }
        return result;
    }

}
