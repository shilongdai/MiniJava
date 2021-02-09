package net.viperfish.minijava;

import net.viperfish.minijava.parser.GrammarException;
import net.viperfish.minijava.parser.MiniJavaEBNFGrammarParser;
import net.viperfish.minijava.parser.RecursiveParser;
import net.viperfish.minijava.scanner.ParsingException;
import net.viperfish.minijava.scanner.TokenScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Compiler {

    public static void main(String[] argv) {
        if(argv.length != 1) {
            System.err.println("Please specify a file to compile");
            System.exit(1);
        }
        try {
            FileInputStream inputStream = new FileInputStream(new File(argv[0]));
            TokenScanner scanner = new TokenScanner(inputStream);
            RecursiveParser parser = new MiniJavaEBNFGrammarParser(scanner);
            parser.init();
            parser.parse();
        } catch (FileNotFoundException e) {
            System.out.println(String.format("The file %s is not found", argv[0]));
            System.exit(1);
        } catch (IOException e) {
            System.err.println(String.format("Failed to read the file: %s, %s", argv[0], e.getMessage()));
            System.exit(1);
        } catch (ParsingException | GrammarException e) {
            System.err.println(e.getMessage());
            System.exit(4);
        }
    }

}
