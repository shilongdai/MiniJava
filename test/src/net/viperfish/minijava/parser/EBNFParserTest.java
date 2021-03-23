package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.ASTDisplay;
import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.TokenScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;
import java.util.Scanner;

public class EBNFParserTest {

    private static final String[] TEST_SUCCESS = new String[] {"testBasicDeclaration", "testFullClassDec", "testExpression", "testStatement", "testComprehensive", "testAST", "testNull"};
    private static final String[] TEST_FAIL = new String[] {};

    @Test
    public void testParserSuccess() throws IOException, ParsingException, GrammarException {
        for (String resources : TEST_SUCCESS) {
            System.out.println("\n\nTesting: " + resources);
            try (FileInputStream inputStream = new FileInputStream(Paths.get("resources", "parser",  resources).toFile())) {
                TokenScanner scanner = new TokenScanner(inputStream);
                RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
                parser.init();
                Package ast = parser.parse();
                ast.visit(new ASTDisplay(), "");
            }
        }
    }

    @Test
    public void runProvidedGrammarTests() throws IOException {
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
                        Package ast = parser.parse();
                        ast.visit(new ASTDisplay(), "");
                    } catch (Throwable e) {
                        System.out.println("Failed: " + x.getName());
                        Assertions.fail(e);
                    }
                } else {
                    try {
                        parser.init();
                        parser.parse();
                    } catch (ParsingException | GrammarException e) {
                        System.out.println("successfully failed: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Test
    public void runProvidedASTTests() throws IOException {
        String projDir = System.getProperty("user.dir");
        System.out.println("Run pa1_tests on miniJava compiler in " + projDir);

        // test directory present ?
        File testDir = new File(projDir + "/../../tests/pa2_tests");
        for (File x : testDir.listFiles()) {
            if (x.getName().endsWith("out") || x.getName().startsWith(".")) {
                continue;
            }
            System.out.println("Testing: " + x.getName());
            try {
                String ast = runASTTest(x);
                String actualAST = getAST(new FileInputStream(x.getPath() + ".out"));
                Assertions.assertEquals(actualAST, ast);
            } catch (ParsingException | GrammarException e) {
                if(x.getName().contains("fail")) {
                    continue;
                } else {
                    Assertions.fail(e);
                }
            }
        }
    }

    public static String getAST(InputStream stream) {
        Scanner scan = new Scanner(stream);
        String ast = null;
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (line.equals("======= AST Display =========================")) {
                line = scan.nextLine();
                while(scan.hasNext() && !line.equals("=============================================")) {
                    ast += line + "\n";
                    line = scan.nextLine();
                }
            }
            if (line.startsWith("*** "))
                System.out.println(line);
            if (line.startsWith("ERROR")) {
                System.out.println(line);
                while(scan.hasNext())
                    System.out.println(scan.next());
            }
        }
        scan.close();
        return ast;
    }

    private static String runASTTest(File x) throws ParsingException, GrammarException, IOException {
        try (FileInputStream inputStream = new FileInputStream(x)) {
            TokenScanner scanner = new TokenScanner(inputStream);
            RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
            PrintStream oldOut = System.out;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream newOut = new PrintStream(out);
            System.setOut(newOut);
            ASTDisplay.showPosition = false;

            try {
                parser.init();
                Package ast = parser.parse();
                ASTDisplay display = new ASTDisplay();
                display.showTree(ast);
            } finally {
                System.setOut(oldOut);
                ASTDisplay.showPosition = true;
            }

            return getAST(new ByteArrayInputStream(out.toByteArray()));
        }
    }

}
