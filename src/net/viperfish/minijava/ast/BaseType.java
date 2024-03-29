/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import net.viperfish.minijava.scanner.SourcePosition;

public class BaseType extends TypeDenoter {
    public BaseType(TypeKind t, SourcePosition posn) {
        super(t, posn);
    }

    public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitBaseType(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
