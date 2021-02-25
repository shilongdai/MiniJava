package net.viperfish.minijava.ebnf;

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
        EBNFGrammar grammer = new EBNFGrammar();

        // terminals
        ParsableSymbol id = new StandardTerminalSymbol("id");
        grammer.registerTerminalSymbol(id);
        ParsableSymbol tHis = new StandardTerminalSymbol("this");
        grammer.registerTerminalSymbol(tHis);
        ParsableSymbol dot = new StandardTerminalSymbol(".");
        grammer.registerTerminalSymbol(dot);
        ParsableSymbol lsqb = new StandardTerminalSymbol("[");
        grammer.registerTerminalSymbol(lsqb);
        ParsableSymbol rsqb = new StandardTerminalSymbol("]");
        grammer.registerTerminalSymbol(rsqb);
        ParsableSymbol lpb = new StandardTerminalSymbol("(");
        grammer.registerTerminalSymbol(lpb);
        ParsableSymbol rpb = new StandardTerminalSymbol(")");
        grammer.registerTerminalSymbol(rpb);
        ParsableSymbol unop = new StandardTerminalSymbol("unop");
        grammer.registerTerminalSymbol(unop);
//        ParsableSymbol binop = new StandardTerminalSymbol("binop");
//        grammer.registerTerminalSymbol(binop);
        ParsableSymbol multop = new StandardTerminalSymbol("multop");
        grammer.registerTerminalSymbol(multop);
        ParsableSymbol addop = new StandardTerminalSymbol("addop");
        grammer.registerTerminalSymbol(addop);
        ParsableSymbol relop = new StandardTerminalSymbol("relop");
        grammer.registerTerminalSymbol(relop);
        ParsableSymbol eqop = new StandardTerminalSymbol("eqop");
        grammer.registerTerminalSymbol(eqop);
        ParsableSymbol cjop = new StandardTerminalSymbol("cjop");
        grammer.registerTerminalSymbol(cjop);
        ParsableSymbol djop = new StandardTerminalSymbol("djop");
        grammer.registerTerminalSymbol(djop);
        ParsableSymbol num = new StandardTerminalSymbol("num");
        grammer.registerTerminalSymbol(num);
        ParsableSymbol True = new StandardTerminalSymbol("true");
        grammer.registerTerminalSymbol(True);
        ParsableSymbol False = new StandardTerminalSymbol("false");
        grammer.registerTerminalSymbol(False);
        ParsableSymbol New = new StandardTerminalSymbol("new");
        grammer.registerTerminalSymbol(New);
        ParsableSymbol Int = new StandardTerminalSymbol("int");
        grammer.registerTerminalSymbol(Int);
        ParsableSymbol llb = new StandardTerminalSymbol("{");
        grammer.registerTerminalSymbol(llb);
        ParsableSymbol rlb = new StandardTerminalSymbol("}");
        grammer.registerTerminalSymbol(rlb);
        ParsableSymbol Boolean = new StandardTerminalSymbol("boolean");
        grammer.registerTerminalSymbol(Boolean);
        ParsableSymbol semi = new StandardTerminalSymbol(";");
        grammer.registerTerminalSymbol(semi);
        ParsableSymbol eq = new StandardTerminalSymbol("=");
        grammer.registerTerminalSymbol(eq);
        ParsableSymbol comma = new StandardTerminalSymbol(",");
        grammer.registerTerminalSymbol(comma);
        ParsableSymbol reTurn = new StandardTerminalSymbol("return");
        grammer.registerTerminalSymbol(reTurn);
        ParsableSymbol If = new StandardTerminalSymbol("if");
        grammer.registerTerminalSymbol(If);
        ParsableSymbol Else = new StandardTerminalSymbol("else");
        grammer.registerTerminalSymbol(Else);
        ParsableSymbol While = new StandardTerminalSymbol("while");
        grammer.registerTerminalSymbol(While);
        ParsableSymbol Static = new StandardTerminalSymbol("static");
        grammer.registerTerminalSymbol(Static);
        ParsableSymbol Public = new StandardTerminalSymbol("public");
        grammer.registerTerminalSymbol(Public);
        ParsableSymbol Private = new StandardTerminalSymbol("private");
        grammer.registerTerminalSymbol(Private);
        ParsableSymbol Void = new StandardTerminalSymbol("void");
        grammer.registerTerminalSymbol(Void);
        ParsableSymbol Class = new StandardTerminalSymbol("class");
        grammer.registerTerminalSymbol(Class);
        ParsableSymbol terminalSymbol = new StandardTerminalSymbol("$");
        grammer.registerTerminalSymbol(terminalSymbol);

        // non terminals

        // Reference ::= id | this | Reference . id
        List<Symbol> referenceList = new ArrayList<>();
        // id | this | Reference . id -> (id | this) (. id)*
        referenceList.add(new DecisionPointSymbol("thisOrId", Arrays.asList(tHis, id)));
        Symbol dotIdContinue = new WildCardSymbol("dotIdChain", new CompositeSymbol("dotId", Arrays.asList(dot, id)));
        referenceList.add(dotIdContinue);

        Symbol reference = new CompositeSymbol("Reference", referenceList);
        grammer.registerNonTerminalSymbol(reference);

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
        grammer.registerNonTerminalSymbol(type);

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
        Symbol argListOrEmpty = new DecisionPointSymbol("argListEmpty", Arrays.asList(grammer.placeholderName("ArgumentList"), EBNFGrammar.EMPTY_STRING));
        factorizedExpressionList.add(new CompositeSymbol("ExpBracketed", Arrays.asList(lsqb, grammer.placeholderName("Expression"), rsqb)));
        factorizedExpressionList.add(new CompositeSymbol("CallArguments", Arrays.asList(lpb, argListOrEmpty, rpb)));
        factorizedExpressionList.add(EBNFGrammar.EMPTY_STRING);
        bExpList.add(new CompositeSymbol("RefExtendedExp", Arrays.asList(reference, new DecisionPointSymbol(factorizedExpressionList))));
        bExpList.add(num);
        bExpList.add(True);
        bExpList.add(False);
        // new ( id () | int [ Expression ] | id [ Expression ] ) -> new ( id (() | [ Expression ]) | int [ Expression ])
        List<Symbol> rhsNewList = new ArrayList<>();
        List<Symbol> rhsFactoredList = new ArrayList<>();
        rhsFactoredList.add(new CompositeSymbol(Arrays.asList(lpb, rpb)));
        rhsFactoredList.add(grammer.placeholderName("ExpBracketed"));
        rhsNewList.add(new CompositeSymbol("NewIdRelated", Arrays.asList(id, new DecisionPointSymbol(rhsFactoredList))));
        rhsNewList.add(new CompositeSymbol("NewIntArray", Arrays.asList(Int, grammer.placeholderName("ExpBracketed"))));
        Symbol rhsNew = new DecisionPointSymbol("NewRHS", rhsNewList);
        Symbol newDec = new CompositeSymbol("NewExpression", Arrays.asList(New, rhsNew));
        bExpList.add(newDec);
        Symbol bExp = new DecisionPointSymbol("BExp", bExpList);

        /*
        PExp :=
            (Exp) | BExp
         */
        List<Symbol> pExpList = new ArrayList<>();
        pExpList.add(new CompositeSymbol("PExpEnclosed", Arrays.asList(lpb, grammer.placeholderName("Expression"), rpb)));
        pExpList.add(bExp);
        Symbol pExp = new DecisionPointSymbol("PExp", pExpList);

        /*
        UExp :=
            unop Exp | PExp
         */
        List<Symbol> uExpList = new ArrayList<>();
        uExpList.add(pExp);
        uExpList.add(new CompositeSymbol("UExpEnclosed", Arrays.asList(unop, grammer.placeholderName("UExp"))));
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
        grammer.registerNonTerminalSymbol(expression);

        // ArgumentList ::= Expression ( , Expression )*
        List<Symbol> argList = new ArrayList<>();
        argList.add(expression);
        argList.add(new WildCardSymbol("FollowingExpArgs", new CompositeSymbol("CommaExpArg", Arrays.asList(comma, expression))));

        Symbol argumentList = new CompositeSymbol("ArgumentList", argList);
        grammer.registerNonTerminalSymbol(argumentList);

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
        stmtWildCard.add(new WildCardSymbol("StatementList", grammer.placeholderName("Statement")));
        stmtWildCard.add(rlb);
        statementList.add(new CompositeSymbol("StatementsBlock", stmtWildCard));
        statementList.add(new CompositeSymbol("TypeInitAssign", Arrays.asList(type, id, eq, expression, semi)));

        // left factorization Reference ( = Exp | [Exp] = Exp | (ArgList?) );
        List<Symbol> stmtFactorized = new ArrayList<>();
        stmtFactorized.add(new CompositeSymbol(Arrays.asList(eq, expression)));
        stmtFactorized.add(new CompositeSymbol(Arrays.asList(grammer.placeholderName("ExpBracketed"), eq, expression)));
        stmtFactorized.add(grammer.placeholderName("CallArguments"));
        Symbol factorizedStatement = new CompositeSymbol("RefFactoredStmt", Arrays.asList(reference, new DecisionPointSymbol("RefFactoredStmtChoice", stmtFactorized), semi));
        statementList.add(factorizedStatement);

        statementList.add(new CompositeSymbol("returnStmt", Arrays.asList(reTurn, new DecisionPointSymbol("expOrEmpty", Arrays.asList(expression, EBNFGrammar.EMPTY_STRING)), semi)));
        List<Symbol> ifStmtList = new ArrayList<>();
        ifStmtList.add(If);
        ifStmtList.add(lpb);
        ifStmtList.add(expression);
        ifStmtList.add(rpb);
        ifStmtList.add(grammer.placeholderName("Statement"));
        ifStmtList.add(new DecisionPointSymbol("TrailingElse", Arrays.asList(new CompositeSymbol("ElseStmt", Arrays.asList(Else, grammer.placeholderName("Statement"))), EBNFGrammar.EMPTY_STRING)));
        statementList.add(new CompositeSymbol("IfStmt", ifStmtList));
        statementList.add(new CompositeSymbol("WhileStmt", Arrays.asList(While, lpb, expression, rpb, grammer.placeholderName("Statement"))));

        Symbol statement = new DecisionPointSymbol("Statement", statementList);
        grammer.registerNonTerminalSymbol(statement);

        // ParameterList ::= Type id ( , Type id )*
        List<Symbol> paramList = new ArrayList<>();
        paramList.add(type);
        paramList.add(id);
        paramList.add(new WildCardSymbol(new CompositeSymbol(Arrays.asList(comma, type, id))));

        Symbol parameterList = new CompositeSymbol("ParameterList", paramList);
        grammer.registerNonTerminalSymbol(parameterList);

        // Access ::= static ?
        Symbol access = new DecisionPointSymbol("Access", Arrays.asList(Static, EBNFGrammar.EMPTY_STRING));
        grammer.registerNonTerminalSymbol(access);

        // Visibility ::= ( public | private )?
        List<Symbol> visibilityList = new ArrayList<>();
        visibilityList.add(new DecisionPointSymbol(Arrays.asList(Public, Private)));
        visibilityList.add(EBNFGrammar.EMPTY_STRING);

        Symbol visibility = new DecisionPointSymbol("Visibility", visibilityList);
        grammer.registerNonTerminalSymbol(visibility);

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
        grammer.registerNonTerminalSymbol(methodDeclaration);

        // FieldDeclaration ::= Visibility Access Type id ;
        List<Symbol> fieldDecList = new ArrayList<>();
        fieldDecList.add(visibility);
        fieldDecList.add(access);
        fieldDecList.add(type);
        fieldDecList.add(id);
        fieldDecList.add(semi);

        Symbol fieldDeclaration = new CompositeSymbol("FieldDeclaration", fieldDecList);
        grammer.registerNonTerminalSymbol(fieldDeclaration);

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
        grammer.registerNonTerminalSymbol(classDeclaration);

        Symbol program = new CompositeSymbol("Program", Arrays.asList(new WildCardSymbol(classDeclaration), terminalSymbol));
        grammer.registerStartSymbol(program);

        printGrammarProperties(grammer);
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
