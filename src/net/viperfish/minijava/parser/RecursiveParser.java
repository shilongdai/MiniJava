package net.viperfish.minijava.parser;

import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.ParsingException;

import java.io.IOException;
import java.util.List;

public interface RecursiveParser {

    public void parse(List<Symbol> symbols) throws IOException, ParsingException, GrammarException;

    public void parse() throws IOException, ParsingException, GrammarException;

    public void init() throws IOException, ParsingException;

}
