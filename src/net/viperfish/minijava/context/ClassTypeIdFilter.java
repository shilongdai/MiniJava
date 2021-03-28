package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.ClassDecl;

public class ClassTypeIdFilter implements IdentificationFilter {
    @Override
    public boolean filter(LeveledIdentificationTable table, String id, LeveledDecl value) {
        return value.getDec() instanceof ClassDecl;
    }
}
