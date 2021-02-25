package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.AST;
import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.ebnf.Symbol;
import net.viperfish.minijava.scanner.ParsingException;

import java.io.IOException;
import java.util.List;

public interface RecursiveParser {

    public List<AST> parse(List<Symbol> symbols) throws IOException, ParsingException, GrammarException;

    public Package parse() throws IOException, ParsingException, GrammarException;

    public void init() throws IOException, ParsingException;

}
