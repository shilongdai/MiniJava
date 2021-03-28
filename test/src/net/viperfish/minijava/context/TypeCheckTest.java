package net.viperfish.minijava.context;

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

public class TypeCheckTest {

    private static String[] Type_SUCCESS = new String[] {"basicId", "complexId", "scopeRuleSuccess", "staticSuccess"};

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

}
