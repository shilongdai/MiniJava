/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenType;

abstract public class Terminal extends AST {

    public TokenType kind;
    public String spelling;

    public Terminal(Token t) {
        super(t.getPosition());
        spelling = t.getSpelling();
        kind = t.getTokenType();
    }
}
