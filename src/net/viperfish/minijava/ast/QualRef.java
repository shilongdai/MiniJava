/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

public class QualRef extends Reference {

    public Reference ref;
    public Identifier id;

    public QualRef(Reference ref, Identifier id, SourcePosition posn) {
        super(posn);
        this.ref = ref;
        this.id = id;
    }

    @Override
    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitQRef(this, o);
    }
}
