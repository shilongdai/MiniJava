/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public abstract class Reference extends AST {

    public Declaration dominantDecl;

    public Reference(SourcePosition posn) {
        super(posn);
    }

}
