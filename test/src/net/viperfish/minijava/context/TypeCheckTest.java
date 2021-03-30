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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class TypeCheckTest {

    private static String[] Type_SUCCESS = new String[] {"idBuiltin", "basicId", "complexId", "scopeRuleSuccess", "staticSuccess"};

    private static Map<String, Set<Integer>> TYPE_FAILS;

    static {
        TYPE_FAILS = new HashMap<>();
        TYPE_FAILS.put("basicTypeFails", new HashSet<>(Arrays.asList(4, 5, 6, 10, 11, 12, 13)));
        TYPE_FAILS.put("objTypeFails", new HashSet<>(Arrays.asList(19, 31, 32, 34, 35, 38, 39, 41, 43, 46, 47, 51, 52, 57, 58, 65, 68, 69, 71, 74, 75)));
        TYPE_FAILS.put("arrayTypeFail", new HashSet<>(Arrays.asList(5, 8, 9, 11, 14, 15, 18, 19, 20, 21, 51, 55, 57, 61, 63, 65, 67)));
        TYPE_FAILS.put("functionSignatureFails", new HashSet<>(Arrays.asList(5, 7, 11, 18, 51, 52, 53, 54)));
        TYPE_FAILS.put("failBuiltIn", new HashSet<>(Arrays.asList(4, 8, 9, 14, 18)));
    }

    @Test
    public void testTypeCheckSuccess() throws IOException, ParsingException, GrammarException {
        for (String resources : Type_SUCCESS) {
            System.out.println("\n\nTesting: " + resources);
            try (FileInputStream inputStream = new FileInputStream(Paths.get("resources", "id",  resources).toFile())) {
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                ContextAnalyzer analyzer = new ContextAnalyzer();
                parser.init();
                Package ast = parser.parse();
                Compiler.addPredefinedClasses(ast);
                ast = analyzer.identification(ast);
                ast = analyzer.typeChecking(ast);
            } catch (ContextAnalysisErrorException e) {
                for(ContextualErrors errors : e.getErrors()) {
                    System.out.println(String.format("*** line %d: %s", errors.getPosition().getLineNumber(), errors.getMsg()));
                }
                Assertions.fail(e);
            }
        }
    }

    @Test
    public void testTypeCheckFails() throws IOException, ParsingException, GrammarException {
        for (String resources : TYPE_FAILS.keySet()) {
            System.out.println("\n\nTesting: " + resources);
            try (FileInputStream inputStream = new FileInputStream(Paths.get("resources", "id",  resources).toFile())) {
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                ContextAnalyzer analyzer = new ContextAnalyzer();
                parser.init();
                Package ast = parser.parse();
                Compiler.addPredefinedClasses(ast);
                ast = analyzer.identification(ast);
                ast = analyzer.typeChecking(ast);
                Assertions.fail("Failed to detect issue");
            } catch (ContextAnalysisErrorException e) {
                Set<Integer> expectedErrors = TYPE_FAILS.get(resources);
                List<ContextualErrors> error = e.getErrors();

                for(ContextualErrors r : e.getErrors()) {
                    System.out.println(String.format("*** line %d: %s", r.getPosition().getLineNumber(), r.getMsg()));
                }

                if(error.size() != expectedErrors.size()) {
                    Assertions.fail(String.format("Expected %d errors, got %d", expectedErrors.size(), error.size()));
                }
                for(ContextualErrors r : error) {
                    Assertions.assertTrue(expectedErrors.contains(r.getPosition().getLineNumber()));
                }
            }
        }
    }

}
