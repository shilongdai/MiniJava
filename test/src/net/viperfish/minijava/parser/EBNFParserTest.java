package net.viperfish.minijava.parser;

import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.TokenScanner;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class EBNFParserTest {

    private static final String[] TEST_SUCCESS = new String[] {"testBasicDeclaration", "testFullClassDec", "testExpression", "testStatement", "testComprehensive"};
    private static final String[] TEST_FAIL = new String[] {};

    @Test
    public void testParserSuccess() throws IOException, ParsingException, GrammarException {
        for (String resources : TEST_SUCCESS) {
            System.out.println("Testing: " + resources + "\n\n");
            try (FileInputStream inputStream = new FileInputStream(Paths.get("resources", "parser",  resources).toFile())) {
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                parser.init();
                parser.parse();
            }
        }
    }
}
