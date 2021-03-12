/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class IdRef extends BaseRef {

    public Identifier id;

    public IdRef(Identifier id, SourcePosition posn) {
        super(posn);
        this.id = id;
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitIdRef(this, o);
    }
}
