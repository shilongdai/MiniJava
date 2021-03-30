package net.viperfish.minijava;

import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.context.ContextAnalysisErrorException;
import net.viperfish.minijava.context.ContextAnalyzer;
import net.viperfish.minijava.context.ContextualErrors;
import net.viperfish.minijava.parser.GrammarException;
import net.viperfish.minijava.parser.MiniJavaEBNFGrammarParser;
import net.viperfish.minijava.parser.RecursiveParser;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenScanner;
import net.viperfish.minijava.scanner.TokenType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Compiler {

    public static void main(String[] argv) {
        if(argv.length != 1) {
            System.err.println("Please specify a file to compile");
            System.exit(1);
        }
        try {
            FileInputStream inputStream = new FileInputStream(new File(argv[0]));
            TokenScanner scanner = new TokenScanner(inputStream);
            RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
            parser.init();
            Package ast = parser.parse();

            addPredefinedClasses(ast);
            ContextAnalyzer analyzer = new ContextAnalyzer();
            ast = analyzer.analyze(ast);
        } catch (FileNotFoundException e) {
            System.out.println(String.format("The file %s is not found", argv[0]));
            System.exit(1);
        } catch (IOException e) {
            System.out.println(String.format("Failed to read the file: %s, %s", argv[0], e.getMessage()));
            System.exit(1);
        } catch (ParsingException e) {
            System.out.println(String.format("*** line %d: %s", e.getPosition().getLineNumber(), e.getMessage()));
            System.exit(4);
        } catch (GrammarException e) {
            System.out.println(String.format("*** line %d: %s", e.getActual().getPosition().getLineNumber(), e.getMessage()));
            System.exit(4);
        } catch (ContextAnalysisErrorException e) {
            for(ContextualErrors error : e.getErrors()) {
                System.out.println(String.format("*** line %d: %s", error.getPosition().getLineNumber(), error.getMsg()));
            }
            System.exit(4);
        }
    }

    public static void addPredefinedClasses(Package ast) {
        ClassDecl str = new ClassDecl("String", new FieldDeclList(), new MethodDeclList(), null);
        str.type = new BaseType(TypeKind.UNSUPPORTED, null);
        ParameterDeclList params = new ParameterDeclList();
        params.add(new ParameterDecl(new BaseType(TypeKind.INT, null), "n", null));
        MethodDecl println = new MethodDecl(new FieldDecl(false, false, new BaseType(TypeKind.VOID, null), "println", null), params, new StatementList(), null);
        MethodDeclList methodList = new MethodDeclList();
        methodList.add(println);
        ClassDecl printStream = new ClassDecl("_PrintStream", new FieldDeclList(), methodList, null);
        FieldDeclList fieldList = new FieldDeclList();
        ClassType printStreamType = new ClassType(new Identifier(new Token(TokenType.ID, "_PrintStream", null)), null);
        fieldList.add(new FieldDecl(false, true, printStreamType, "out", null));
        ClassDecl system = new ClassDecl("System", fieldList, new MethodDeclList(), null);

        ast.classDeclList.add(str, 0);
        ast.classDeclList.add(printStream, 0);
        ast.classDeclList.add(system, 0);

    }

}
