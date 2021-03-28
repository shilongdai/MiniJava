package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.ASTDisplay;
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
import java.util.HashMap;
import java.util.Map;

public class IdentificationTest {

    private static String[] ID_SUCCESS = new String[] {"basicId", "complexId", "scopeRuleSuccess", "staticSuccess"};
    private static Map<String, Integer> ID_FAILS;

    static {
        ID_FAILS = new HashMap<>();
        ID_FAILS.put("undeclaredBasic", 4);
        ID_FAILS.put("conflictInScope", 4);
        ID_FAILS.put("ConflictAcrossScope", 5);
        ID_FAILS.put("MoreConflictAcrossScope", 7);
        ID_FAILS.put("AccessPrivate", 11);
        ID_FAILS.put("AccessStatic", 6);
        ID_FAILS.put("StaticThis", 6);
        ID_FAILS.put("MultilevelAccessFail", 15);
    }

    @Test
    public void testIdentificationSuccess() throws IOException, ParsingException, GrammarException {
        for (String resources : ID_SUCCESS) {
            System.out.println("\n\nTesting: " + resources);
            try (FileInputStream inputStream = new FileInputStream(Paths.get("resources", "id",  resources).toFile())) {
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                ContextAnalyzer analyzer = new ContextAnalyzer();
                parser.init();
                Package ast = parser.parse();
                ast = analyzer.identification(ast);
                new ASTDisplay().showTree(ast);
            } catch (ContextAnalysisErrorException e) {
                for(ContextualErrors errors : e.getErrors()) {
                    System.out.println(String.format("*** line %d: %s", errors.getPosition().getLineNumber(), errors.getMsg()));
                }
                Assertions.fail(e);
            }
        }
    }

    @Test
    public void testIdentificationFail() throws IOException, ParsingException, GrammarException {
        for (String resources : ID_FAILS.keySet()) {
            System.out.println("\n\nTesting: " + resources);
            try (FileInputStream inputStream = new FileInputStream(Paths.get("resources", "id",  resources).toFile())) {
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                ContextAnalyzer analyzer = new ContextAnalyzer();
                parser.init();
                Package ast = parser.parse();
                ast = analyzer.identification(ast);
                Assertions.fail("Did not detect failure in: " + resources);
            } catch (ContextAnalysisErrorException e) {
                ContextualErrors error = e.getErrors().get(0);
                Assertions.assertEquals(ID_FAILS.get(resources), error.getPosition().getLineNumber());
            }
        }
    }

}
