package net.viperfish.minijava.parser;

import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EBNFParserTest {

    private static final String[] TEST_SUCCESS = new String[] {"testBasicDeclaration", "testFullClassDec", "testExpression", "testStatement", "testComprehensive"};
    private static final String[] TEST_FAIL = new String[] {};

    @Test
    public void testParserSuccess() throws IOException, ParsingException, GrammarException {
        for (String resources : TEST_SUCCESS) {
            try (FileInputStream inputStream = new FileInputStream(Paths.get("resources", "parser",  resources).toFile())) {
                System.out.println("\n\n");
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                parser.init();
                parser.parse();
            }
        }
    }
}
