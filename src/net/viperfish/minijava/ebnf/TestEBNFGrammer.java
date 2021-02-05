package net.viperfish.minijava.ebnf;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
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
        ParsableSymbol unop = new StandardTerminalSymbol("Unop");
        grammer.registerTerminalSymbol(unop);
        ParsableSymbol binop = new StandardTerminalSymbol("Binop");
        grammer.registerTerminalSymbol(binop);
        ParsableSymbol num = new StandardTerminalSymbol("Num");
        grammer.registerTerminalSymbol(num);
        ParsableSymbol True = new StandardTerminalSymbol("True");
        grammer.registerTerminalSymbol(True);
        ParsableSymbol False = new StandardTerminalSymbol("False");
        grammer.registerTerminalSymbol(False);
        ParsableSymbol New = new StandardTerminalSymbol("new");
        grammer.registerTerminalSymbol(New);
        ParsableSymbol Int = new StandardTerminalSymbol("int");
        grammer.registerTerminalSymbol(Int);
        ParsableSymbol llb = new StandardTerminalSymbol("{");
        grammer.registerTerminalSymbol(llb);
        ParsableSymbol rlb = new StandardTerminalSymbol("}");
        grammer.registerTerminalSymbol(rlb);
        ParsableSymbol Boolean = new StandardTerminalSymbol("Boolean");
        grammer.registerTerminalSymbol(Boolean);
        ParsableSymbol semi = new StandardTerminalSymbol(";");
        grammer.registerTerminalSymbol(semi);
        ParsableSymbol eq = new StandardTerminalSymbol("=");
        grammer.registerTerminalSymbol(eq);
        ParsableSymbol comma = new StandardTerminalSymbol(",");
        grammer.registerTerminalSymbol(comma);
        ParsableSymbol reTurn = new StandardTerminalSymbol("Return");
        grammer.registerTerminalSymbol(reTurn);
        ParsableSymbol If = new StandardTerminalSymbol("If");
        grammer.registerTerminalSymbol(If);
        ParsableSymbol Else = new StandardTerminalSymbol("Else");
        grammer.registerTerminalSymbol(Else);
        ParsableSymbol While = new StandardTerminalSymbol("While");
        grammer.registerTerminalSymbol(While);
        ParsableSymbol Static = new StandardTerminalSymbol("Static");
        grammer.registerTerminalSymbol(Static);
        ParsableSymbol Public = new StandardTerminalSymbol("Public");
        grammer.registerTerminalSymbol(Public);
        ParsableSymbol Private = new StandardTerminalSymbol("Private");
        grammer.registerTerminalSymbol(Private);
        ParsableSymbol Void = new StandardTerminalSymbol("Void");
        grammer.registerTerminalSymbol(Void);
        ParsableSymbol Class = new StandardTerminalSymbol("Class");
        grammer.registerTerminalSymbol(Class);
        ParsableSymbol terminalSymbol = new StandardTerminalSymbol("$");
        grammer.registerTerminalSymbol(terminalSymbol);

        // non terminals

        // Reference ::= id | this | Reference . id
        List<Symbol> referenceList = new ArrayList<>();
        referenceList.add(id);
        referenceList.add(tHis);
        referenceList.add(new CompositeSymbol(Arrays.asList(grammer.placeholderName("Reference"), dot, id)));

        Symbol reference = new DecisionPointSymbol("Reference", referenceList);
        grammer.registerNonTerminalSymbol(reference);

        // Type ::= int | boolean | id | ( int | id ) []
        List<Symbol> typeList = new ArrayList<>();
        typeList.add(Int);
        typeList.add(Boolean);
        typeList.add(id);
        List<Symbol> typeArrayDec = new ArrayList<>();
        typeArrayDec.add(new DecisionPointSymbol(Arrays.asList(Int, id)));
        typeArrayDec.add(lsqb);
        typeArrayDec.add(rsqb);
        typeList.add(new CompositeSymbol(typeArrayDec));

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
        List<Symbol> expressionList = new ArrayList<>();
        expressionList.add(reference);
        expressionList.add(new CompositeSymbol(Arrays.asList(reference, lsqb, grammer.placeholderName("Expression"), rsqb)));
        Symbol argListOrEmpty = new DecisionPointSymbol(Arrays.asList(grammer.placeholderName("ArgumentList"), EBNFGrammar.EMPTY_STRING));
        expressionList.add(new CompositeSymbol(Arrays.asList(reference, lpb, argListOrEmpty, rpb)));
        expressionList.add(new CompositeSymbol(Arrays.asList(unop, grammer.placeholderName("Expression"))));
        expressionList.add(new CompositeSymbol(Arrays.asList(grammer.placeholderName("Expression"), binop, grammer.placeholderName("Expression"))));
        expressionList.add(new CompositeSymbol(Arrays.asList(lpb, grammer.placeholderName("Expression"), rpb)));
        expressionList.add(num); expressionList.add(True); expressionList.add(False);
        List<Symbol> rhsNewList = new ArrayList<>();
        rhsNewList.add(new CompositeSymbol(Arrays.asList(id, lpb, rpb)));
        rhsNewList.add(new CompositeSymbol(Arrays.asList(Int, lsqb, grammer.placeholderName("Expression"), rsqb)));
        rhsNewList.add(new CompositeSymbol(Arrays.asList(id, lsqb, grammer.placeholderName("Expression"), rsqb)));
        Symbol rhsNew = new DecisionPointSymbol(rhsNewList);
        Symbol newDec = new CompositeSymbol(Arrays.asList(New, rhsNew));
        expressionList.add(newDec);

        Symbol expression = new DecisionPointSymbol("Expression", expressionList);
        grammer.registerNonTerminalSymbol(expression);

        // ArgumentList ::= Expression ( , Expression )*
        List<Symbol> argList = new ArrayList<>();
        argList.add(expression);
        argList.add(new WildCardSymbol(new CompositeSymbol(Arrays.asList(comma, expression))));

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
        stmtWildCard.add(new WildCardSymbol(grammer.placeholderName("Statement")));
        stmtWildCard.add(rlb);
        statementList.add(new CompositeSymbol(stmtWildCard));
        statementList.add(new CompositeSymbol(Arrays.asList(type, id, eq, expression, semi)));
        statementList.add(new CompositeSymbol(Arrays.asList(reference, eq, expression, semi)));
        statementList.add(new CompositeSymbol(Arrays.asList(reference, lsqb, expression, rsqb, eq, expression, semi)));
        statementList.add(new CompositeSymbol(Arrays.asList(reference, lpb, new DecisionPointSymbol(Arrays.asList(argumentList, EBNFGrammar.EMPTY_STRING)), rpb, semi)));
        statementList.add(new CompositeSymbol(Arrays.asList(reTurn, new DecisionPointSymbol(Arrays.asList(expression, EBNFGrammar.EMPTY_STRING)), semi)));
        List<Symbol> ifStmtList = new ArrayList<>();
        ifStmtList.add(If);
        ifStmtList.add(lpb);
        ifStmtList.add(expression);
        ifStmtList.add(rpb);
        ifStmtList.add(grammer.placeholderName("Statement"));
        ifStmtList.add(new DecisionPointSymbol(Arrays.asList(new CompositeSymbol(Arrays.asList(Else, grammer.placeholderName("Statement"))), EBNFGrammar.EMPTY_STRING)));
        statementList.add(new CompositeSymbol(ifStmtList));
        statementList.add(new CompositeSymbol(Arrays.asList(While, lpb, expression, rpb, grammer.placeholderName("Statement"))));

        Symbol statement = new DecisionPointSymbol("Statement", statementList);
        grammer.registerNonTerminalSymbol(statement);

        // ParameterList ::= Type id ( , Type id )*
        List<Symbol> paramList = new ArrayList<>();
        paramList.add(type);
        paramList.add(id);
        paramList.add(lpb);
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
        methodDeclarationList.add(visibility);
        methodDeclarationList.add(access);
        methodDeclarationList.add(new DecisionPointSymbol(Arrays.asList(type, Void)));
        methodDeclarationList.add(id);
        methodDeclarationList.add(lpb);
        methodDeclarationList.add(new DecisionPointSymbol(Arrays.asList(parameterList, EBNFGrammar.EMPTY_STRING)));
        methodDeclarationList.add(rpb);
        methodDeclarationList.add(llb);
        methodDeclarationList.add(new WildCardSymbol(statement));
        methodDeclarationList.add(rlb);

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
        classDecList.add(new WildCardSymbol(new DecisionPointSymbol(Arrays.asList(fieldDeclaration, methodDeclaration))));
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
        nullable.retainAll(nonterminals);
        starters.keySet().retainAll(nonterminals);
        followers.keySet().retainAll(nonterminals);

        System.out.println("\n\n-----------------------------------------");
        System.out.println("Nullable: " + nullable);
        System.out.println("Starters: " + starters);
        System.out.println("Followers:" + followers);

        System.out.println("-----Predict Sets LL1------");
        for (PredictionSet set : predictSets) {
            if(grammar.getUnnamedSymbols().contains(set.getSrcRule().getName())) {
                continue;
            }
            if(set.isLL1()) {
                System.out.println(String.format("Rule: %s, Sets: %s, LL1: %b", set.getSrcRule().getName(), set.getPredictSets(), set.isLL1()));
            }
        }

        System.out.println("-----Predict Sets Not LL1------");
        for (PredictionSet set : predictSets) {
            if(!set.isLL1()) {
                System.out.println(String.format("Rule: %s, Sets: %s, LL1: %b", set.getSrcRule().getName(), set.getPredictSets(), set.isLL1()));
            }
        }
    }
}
