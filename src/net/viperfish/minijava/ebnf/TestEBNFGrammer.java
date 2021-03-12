package net.viperfish.minijava.ebnf;

import net.viperfish.minijava.parser.TokenTypeParsibleSymbol;
import net.viperfish.minijava.parser.TokenTypeWSpellingParsibleSymbol;
import net.viperfish.minijava.scanner.TokenType;

import java.util.*;

public class TestEBNFGrammer {

    public static void main(String argv[]) {
        testMiniJava();
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

    private static void testMiniJava() {
        EBNFGrammar GRAMMAR = new EBNFGrammar();

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
        ParsableSymbol Null = new TokenTypeParsibleSymbol(TokenType.NULL);
        GRAMMAR.registerTerminalSymbol(Null);
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
        referenceList.add(new DecisionPointSymbol("thisOrId", Arrays.asList(tHis, id)));
        Symbol dotIdContinue = new WildCardSymbol("dotIdChain", new CompositeSymbol("dotId", Arrays.asList(dot, id)));
        referenceList.add(dotIdContinue);

        Symbol reference = new CompositeSymbol("Reference", referenceList);
        GRAMMAR.registerNonTerminalSymbol(reference);

        // Type ::= int | boolean | id | ( int | id ) []
        // -> int | boolean | id | int [ ] | id [ ] -> boolean | int (ε | [ ] ) | id ( ε | [ ] )
        List<Symbol> typeList = new ArrayList<>();
        List<Symbol> typeArrayDec = new ArrayList<>();
        typeArrayDec.add(lsqb);
        typeArrayDec.add(rsqb);
        Symbol emptyOrSqb = new DecisionPointSymbol("emptyOrSqBrackets", Arrays.asList(EBNFGrammar.EMPTY_STRING, new CompositeSymbol("sqBrackets", typeArrayDec)));
        typeList.add(Boolean);
        Symbol intType = new CompositeSymbol("IntRelatedType", Arrays.asList(Int, emptyOrSqb));
        Symbol idType = new CompositeSymbol("UserRelatedType", Arrays.asList(id, emptyOrSqb));
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
        Symbol argListOrEmpty = new DecisionPointSymbol("argListEmpty", Arrays.asList(GRAMMAR.placeholderName("ArgumentList"), EBNFGrammar.EMPTY_STRING));
        factorizedExpressionList.add(new CompositeSymbol("ExpBracketed", Arrays.asList(lsqb, GRAMMAR.placeholderName("Expression"), rsqb)));
        factorizedExpressionList.add(new CompositeSymbol("CallArguments", Arrays.asList(lpb, argListOrEmpty, rpb)));
        factorizedExpressionList.add(EBNFGrammar.EMPTY_STRING);
        bExpList.add(new CompositeSymbol("RefExtendedExp", Arrays.asList(reference, new DecisionPointSymbol("ChooseRefExpType", factorizedExpressionList))));
        bExpList.add(num);
        bExpList.add(True);
        bExpList.add(False);
        // new ( id () | int [ Expression ] | id [ Expression ] ) -> new ( id (() | [ Expression ]) | int [ Expression ])
        List<Symbol> rhsNewList = new ArrayList<>();
        List<Symbol> rhsFactoredList = new ArrayList<>();
        rhsFactoredList.add(new CompositeSymbol(Arrays.asList(lpb, rpb)));
        rhsFactoredList.add(GRAMMAR.placeholderName("ExpBracketed"));
        rhsNewList.add(new CompositeSymbol("NewIdRelated", Arrays.asList(id, new DecisionPointSymbol("NewIdDecide", rhsFactoredList))));
        rhsNewList.add(new CompositeSymbol("NewIntArray", Arrays.asList(Int, GRAMMAR.placeholderName("ExpBracketed"))));
        Symbol rhsNew = new DecisionPointSymbol("NewRHS", rhsNewList);
        Symbol newDec = new CompositeSymbol("NewExpression", Arrays.asList(New, rhsNew));
        bExpList.add(newDec);
        Symbol bExp = new DecisionPointSymbol("BExp", bExpList);

        /*
        PExp :=
            (Exp) | BExp
         */
        List<Symbol> pExpList = new ArrayList<>();
        pExpList.add(new CompositeSymbol("PExpEnclosed", Arrays.asList(lpb, GRAMMAR.placeholderName("Expression"), rpb)));
        pExpList.add(bExp);
        Symbol pExp = new DecisionPointSymbol("PExp", pExpList);

        /*
        UExp :=
            unop Exp | PExp
         */
        List<Symbol> uExpList = new ArrayList<>();
        uExpList.add(pExp);
        uExpList.add(new CompositeSymbol("UExpEnclosed", Arrays.asList(unop, GRAMMAR.placeholderName("UExp"))));
        Symbol uExp = new DecisionPointSymbol("UExp", uExpList);

        /*
        MExp :=
            UExp ( multop UExp )*
         */
        List<Symbol> mExpList = new ArrayList<>();
        mExpList.add(uExp);
        mExpList.add(new WildCardSymbol("MExpRep", new CompositeSymbol("MExpEnclosed", Arrays.asList(multop, uExp))));
        Symbol mExp = new CompositeSymbol("MExp", mExpList);

        /*
        AExp :=
            MExp ( addop MExp )*
         */
        List<Symbol> aExpList = new ArrayList<>();
        aExpList.add(mExp);
        aExpList.add(new WildCardSymbol("AExpRep", new CompositeSymbol("AExpEnclosed", Arrays.asList(addop, mExp))));
        Symbol aExp = new CompositeSymbol("AExp", aExpList);

        /*
        RExp :=
            AExp ( relop AExp )*
         */
        List<Symbol> rExpList = new ArrayList<>();
        rExpList.add(aExp);
        rExpList.add(new WildCardSymbol("RExpRep", new CompositeSymbol("RExpEnclosed", Arrays.asList(relop, aExp))));
        Symbol rExp = new CompositeSymbol("RExp", rExpList);

        /*
        EqExp :=
            RExp ( eqop RExp )*
         */
        List<Symbol> eqExpList = new ArrayList<>();
        Symbol rExpOrNull = new DecisionPointSymbol("rExpOrNull", Arrays.asList(Null, rExp));
        Symbol nullEqComposite = new CompositeSymbol("nullEqComposite", Arrays.asList(Null, eqop, rExpOrNull));
        Symbol eqExpBaseDec = new DecisionPointSymbol("eqExpBaseDecision", Arrays.asList(rExp, nullEqComposite));
        eqExpList.add(eqExpBaseDec);
        eqExpList.add(new WildCardSymbol("EqExpRep", new CompositeSymbol("EqExpEnclosed", Arrays.asList(eqop, rExpOrNull))));
        Symbol eqExp = new CompositeSymbol("EqExp", eqExpList);

        /*
        CExp :=
            EqExp ( cjop EqExp )*
         */
        List<Symbol> cExpList = new ArrayList<>();
        cExpList.add(eqExp);
        cExpList.add(new WildCardSymbol("CExpRep", new CompositeSymbol("CExpEnclosed", Arrays.asList(cjop, eqExp))));
        Symbol cExp = new CompositeSymbol("CExp", cExpList);

        /*
        Exp :=
            CExp ( djop CExp )*
         */
        List<Symbol> expressionList = new ArrayList<>();
        expressionList.add(cExp);
        expressionList.add(new WildCardSymbol("ExpRep", new CompositeSymbol("ExpEnclosed", Arrays.asList(djop, cExp))));
        Symbol expression = new CompositeSymbol("Expression", expressionList);
        GRAMMAR.registerNonTerminalSymbol(expression);

        // ArgumentList ::= Expression ( , Expression )*
        List<Symbol> argList = new ArrayList<>();
        argList.add(expression);
        argList.add(new WildCardSymbol(new CompositeSymbol("ArgListEnclosed", Arrays.asList(comma, expression))));

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
        Symbol expOrNull = new DecisionPointSymbol("expOrNull", Arrays.asList(expression, Null));
        stmtWildCard.add(llb);
        stmtWildCard.add(new WildCardSymbol("StatementList", GRAMMAR.placeholderName("Statement")));
        stmtWildCard.add(rlb);
        statementList.add(new CompositeSymbol("StatementsBlock", stmtWildCard));
        statementList.add(new CompositeSymbol("TypeInitAssign", Arrays.asList(type, id, eq, expOrNull, semi)));

        // left factorization Reference ( = Exp | [Exp] = Exp | (ArgList?) );
        List<Symbol> stmtFactorized = new ArrayList<>();
        stmtFactorized.add(new CompositeSymbol("RefEqExpStmt", Arrays.asList(eq, expOrNull)));
        stmtFactorized.add(new CompositeSymbol("RefIdxEqExpStmt", Arrays.asList(GRAMMAR.placeholderName("ExpBracketed"), eq, expOrNull)));
        stmtFactorized.add(GRAMMAR.placeholderName("CallArguments"));
        Symbol factorizedStatement = new CompositeSymbol("RefFactoredStmt", Arrays.asList(reference, new DecisionPointSymbol("RefFactoredStmtChoice", stmtFactorized), semi));
        statementList.add(factorizedStatement);

        statementList.add(new CompositeSymbol("returnStmt", Arrays.asList(reTurn, new DecisionPointSymbol("expOrEmpty", Arrays.asList(expression, EBNFGrammar.EMPTY_STRING)), semi)));
        List<Symbol> ifStmtList = new ArrayList<>();
        ifStmtList.add(If);
        ifStmtList.add(lpb);
        ifStmtList.add(expression);
        ifStmtList.add(rpb);
        ifStmtList.add(GRAMMAR.placeholderName("Statement"));
        ifStmtList.add(new DecisionPointSymbol("TrailingElse", Arrays.asList(new CompositeSymbol("ElseStmt", Arrays.asList(Else, GRAMMAR.placeholderName("Statement"))), EBNFGrammar.EMPTY_STRING)));
        statementList.add(new CompositeSymbol("IfStmt", ifStmtList));
        statementList.add(new CompositeSymbol("WhileStmt", Arrays.asList(While, lpb, expression, rpb, GRAMMAR.placeholderName("Statement"))));

        Symbol statement = new DecisionPointSymbol("Statement", statementList);
        GRAMMAR.registerNonTerminalSymbol(statement);

        // ParameterList ::= Type id ( , Type id )*
        List<Symbol> paramList = new ArrayList<>();
        Symbol paramDecl = new CompositeSymbol("ParamDecl", Arrays.asList(type, id));
        paramList.add(paramDecl);
        paramList.add(new WildCardSymbol(new CompositeSymbol("ParameterListEnclosed", Arrays.asList(comma, paramDecl))));

        Symbol parameterList = new CompositeSymbol("ParameterList", paramList);
        GRAMMAR.registerNonTerminalSymbol(parameterList);

        // Access ::= static ?
        Symbol access = new DecisionPointSymbol("Access", Arrays.asList(Static, EBNFGrammar.EMPTY_STRING));
        GRAMMAR.registerNonTerminalSymbol(access);

        // Visibility ::= ( public | private )?
        List<Symbol> visibilityList = new ArrayList<>();
        visibilityList.add(new DecisionPointSymbol("PublicPrivate", Arrays.asList(Public, Private)));
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
        methodDistinguished.add(new DecisionPointSymbol("ParamOrEmpty", Arrays.asList(parameterList, EBNFGrammar.EMPTY_STRING)));
        methodDistinguished.add(rpb);
        methodDistinguished.add(GRAMMAR.placeholderName("StatementsBlock"));
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
        typedDeclaration.add(new DecisionPointSymbol("FieldOrTypeMethodDecl", Arrays.asList(semi, new CompositeSymbol("MethodDeclExtension", methodDistinguished))));
        List<Symbol> voidDeclaration = new ArrayList<>();
        voidDeclaration.add(Void);
        voidDeclaration.add(id);
        voidDeclaration.addAll(methodDistinguished);
        Symbol decomposedDecs = new CompositeSymbol("ClassMemberDecl", Arrays.asList(visibility, access, new DecisionPointSymbol("VoidOrTypedDecl", Arrays.asList(new CompositeSymbol("TypedDecl", typedDeclaration), new CompositeSymbol("VoidDecl", voidDeclaration)))));
        classDecList.add(new WildCardSymbol(decomposedDecs));
        classDecList.add(rlb);

        Symbol classDeclaration = new CompositeSymbol("ClassDeclaration", classDecList);
        GRAMMAR.registerNonTerminalSymbol(classDeclaration);

        Symbol program = new CompositeSymbol("Program", Arrays.asList(new WildCardSymbol(classDeclaration), terminalSymbol));
        GRAMMAR.registerStartSymbol(program);

        printGrammarProperties(GRAMMAR);
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
        nonterminals.add(EBNFGrammar.EMPTY_STRING.getName());
        nullable.retainAll(nonterminals);
        starters.keySet().retainAll(nonterminals);
        followers.keySet().retainAll(nonterminals);

        System.out.println("\n\n-----------------------------------------");
        System.out.println("Symbols:");
        Map<String, Symbol> symbols = grammar.symbols();
        for(String nonterminal : nonterminals) {
            Symbol symbol = symbols.get(nonterminal);
            Symbol toString = null;
            if(symbol.isDecision()) {
                toString = new DecisionPointSymbol(symbol.getExpression());
            } else if(symbol != EBNFGrammar.EMPTY_STRING) {
                toString = new CompositeSymbol(symbol.getExpression());
            } else {
                toString = EBNFGrammar.EMPTY_STRING;
            }
            System.out.println(String.format("Symbol %s -> %s", symbol.getName(), toString.toString()));
        }
        System.out.println("Nullable: " + nullable);
        System.out.println("Starters: ");
        printTerminalResults(starters);
        System.out.println("---------------------------------------------------");
        System.out.println("Followers:");
        printTerminalResults(followers);

        System.out.println("-----Predict Sets LL1------");
        for (PredictionSet set : predictSets) {
            if(set.isLL1()) {
                System.out.println(String.format("Rule: %s, Sets: %s, LL1: %b", set.getSrcRule().getName(), set.getPredictSets(), set.isLL1()));
            }
        }

        System.out.println("-----Predict Sets Not LL1------");
        for (PredictionSet set : predictSets) {
            if(!set.isLL1()) {
                if(set.getPredictPoint() != -1) {
                    System.out.println(String.format("Rule: %s, Specific: %s, Sets: %s, LL1: %b", set.getSrcRule().getName(), set.getSrcRule().getExpression().get(set.getPredictPoint()), set.getPredictSets(), set.isLL1()));
                } else {
                    System.out.println(String.format("Rule: %s, Sets: %s, LL1: %b", set.getSrcRule().getName(), set.getPredictSets(), set.isLL1()));
                }
            }
        }
    }

    private static void printTerminalResults(Map<String, Collection<ParsableSymbol>> symbolMap) {
        for(Map.Entry<String, Collection<ParsableSymbol>> e : symbolMap.entrySet()) {
            System.out.println(String.format("Symbol %s: %s", e.getKey(), e.getValue()));
        }
    }
}
