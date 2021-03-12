/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class Package extends AST {

    public ClassDeclList classDeclList;

    public Package(ClassDeclList cdl, SourcePosition posn) {
        super(posn);
        classDeclList = cdl;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitPackage(this, o);
    }
}
