package net.viperfish.minijava.context;

import net.viperfish.minijava.Compiler;
import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.parser.GrammarException;
import net.viperfish.minijava.parser.MiniJavaEBNFGrammarParser;
import net.viperfish.minijava.parser.RecursiveParser;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.TokenScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class CheckpointTest {

    @Test
    public void runProvidedGrammarTests() throws IOException {
        String projDir = System.getProperty("user.dir");
        System.out.println("Run pa1_tests on miniJava compiler in " + projDir);

        // test directory present ?
        File testDir = new File(projDir + "/../../tests/pa3-tests");

        for (File x : testDir.listFiles()) {
            System.out.println("Testing: " + x.getName());
            try (FileInputStream inputStream = new FileInputStream(x)) {
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                if(x.getName().contains("pass")) {
                    try {
                        ContextAnalyzer analyzer = new ContextAnalyzer();
                        parser.init();
                        Package ast = parser.parse();
                        Compiler.addPredefinedClasses(ast);
                        ast = analyzer.analyze(ast);
                    } catch(ContextAnalysisErrorException e) {
                        List<ContextualErrors> error = e.getErrors();

                        for(ContextualErrors r : e.getErrors()) {
                            System.out.println(String.format("*** line %d: %s", r.getPosition().getLineNumber(), r.getMsg()));
                        }

                        Assertions.fail(e);
                    } catch (Throwable e) {
                        System.out.println("Failed: " + x.getName());
                        e.printStackTrace();
                        Assertions.fail(e);
                    }
                } else {
                    try {
                        ContextAnalyzer analyzer = new ContextAnalyzer();
                        parser.init();
                        Package ast = parser.parse();
                        Compiler.addPredefinedClasses(ast);
                        ast = analyzer.analyze(ast);
                        Assertions.fail("Did not fail successfully");
                    } catch (ParsingException | GrammarException e) {
                        System.out.println("successfully failed: " + e.getMessage());
                    } catch (ContextAnalysisErrorException e) {
                        List<ContextualErrors> error = e.getErrors();

                        for(ContextualErrors r : e.getErrors()) {
                            System.out.println(String.format("*** line %d: %s", r.getPosition().getLineNumber(), r.getMsg()));
                        }

                    }
                }
            }
        }
    }

}
