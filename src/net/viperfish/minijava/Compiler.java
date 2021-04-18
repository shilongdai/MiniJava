package net.viperfish.minijava;

import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.codegen.CodeGenerator;
import net.viperfish.minijava.context.ContextAnalysisErrorException;
import net.viperfish.minijava.context.ContextAnalyzer;
import net.viperfish.minijava.context.ContextualErrors;
import net.viperfish.minijava.mJAM.Interpreter;
import net.viperfish.minijava.mJAM.ObjectFile;
import net.viperfish.minijava.parser.GrammarException;
import net.viperfish.minijava.parser.MiniJavaEBNFGrammarParser;
import net.viperfish.minijava.parser.RecursiveParser;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.TokenScanner;

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

            CodeGenerator codeGenerator = new CodeGenerator();
            int status = codeGenerator.genCode(ast);
            if(status == -1) {
                System.out.println("Did not find main class");
                System.exit(4);
            }

            ObjectFile objectFile = new ObjectFile("obj.mJAM");
            objectFile.write();
            Interpreter.main(new String[] {});
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
        ast.classDeclList.add(CompilerGlobal.sysString, 0);
        ast.classDeclList.add(CompilerGlobal.sysPrintStream, 0);
        ast.classDeclList.add(CompilerGlobal.sysSystem, 0);
    }

}
