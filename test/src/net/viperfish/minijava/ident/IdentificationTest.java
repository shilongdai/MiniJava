package net.viperfish.minijava.ident;

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

public class IdentificationTest {

    private static String[] ID_SUCCESS = new String[] {"basicId", "complexId", "scopeRuleSuccess", "staticSuccess"};

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
                ast = analyzer.analyze(ast);
                new ASTDisplay().showTree(ast);
            } catch (IdentificationErrorException e) {
                for(ContextualErrors errors : e.getErrors()) {
                    System.out.println(String.format("*** line %d: %s", errors.getPosition().getLineNumber(), errors.getMsg()));
                }
                Assertions.fail(e);
            }
        }
    }

}
