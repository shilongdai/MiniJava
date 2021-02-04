package net.viperfish.minijava.parser;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TokenScannerTest {

    private static final String[] TEST_FILES = new String[]{"testNextTokenBasic", "testNextTokenFullDeclare", "testNextTokenOperators", "testLineComment"};

    @Test
    public void testNextToken() throws IOException, ParsingException {
        for (String resources : TEST_FILES) {
            try (FileInputStream inputStream = new FileInputStream(Paths.get("resources", resources).toFile())) {
                TokenScanner scanner = new TokenScanner(inputStream);
                List<Token> tokens = new ArrayList<>();
                while (scanner.hasNext()) {
                    tokens.add(scanner.nextToken());
                }

                StringBuilder parsed = new StringBuilder();
                for (Token t : tokens) {
                    parsed.append(t.getTokenType().toString()).append(" ");
                }
                String result = parsed.toString().trim();
                String expected = new String(Files.readAllBytes(Paths.get("resources", resources + "Expected")));
                Assert.assertEquals(expected, result);
            }
        }
    }

    @Test(expected = ParsingException.class)
    public void testBlockCommentFail() throws IOException, ParsingException {
        try (FileInputStream in = new FileInputStream(Paths.get("resources", "badBlockComment").toFile())) {
            TokenScanner scanner = new TokenScanner(in);
            scanner.nextToken();
        }
    }

    @Test(expected = ParsingException.class)
    public void testUnicodeChars() throws IOException, ParsingException {
        try (FileInputStream in = new FileInputStream(Paths.get("resources", "badEncoding").toFile())) {
            TokenScanner scanner = new TokenScanner(in);
            scanner.nextToken();
        }
    }

    @Test(expected = ParsingException.class)
    public void testBadOperator() throws IOException, ParsingException {
        try (FileInputStream in = new FileInputStream(Paths.get("resources", "badOperators").toFile())) {
            TokenScanner scanner = new TokenScanner(in);
            while (scanner.hasNext()) {
                scanner.nextToken();
            }
        }
    }

    @Test
    public void testBadDeclaration() throws IOException, ParsingException {
        try (FileInputStream in = new FileInputStream(Paths.get("resources", "badDeclaration").toFile())) {
            TokenScanner scanner = new TokenScanner(in);
            while (scanner.hasNext()) {
                scanner.nextToken();
            }
        }
    }
}