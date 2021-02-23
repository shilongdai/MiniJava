package net.viperfish.minijava.parser;

import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.TokenScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
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

    @Test
    public void runProvidedTests() throws IOException {
        String projDir = System.getProperty("user.dir");
        System.out.println("Run pa1_tests on miniJava compiler in " + projDir);

        // test directory present ?
        File testDir = new File(projDir + "/../../tests/pa1_tests");

        for (File x : testDir.listFiles()) {
            try (FileInputStream inputStream = new FileInputStream(x)) {
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                if(x.getName().contains("pass")) {
                    try {
                        parser.init();
                        parser.parse();
                    } catch (ParsingException | GrammarException e) {
                        System.out.println("Failed: " + x.getName());
                        Assertions.fail(e);
                    }
                } else {
                    try {
                        parser.init();
                        parser.parse();
                    } catch (ParsingException | GrammarException e) {
                        System.out.println("successfully failed");
                    }
                }
            }
        }
    }
}
