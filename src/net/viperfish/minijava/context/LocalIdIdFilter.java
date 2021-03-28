package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.ClassDecl;
import net.viperfish.minijava.ast.FieldDecl;
import net.viperfish.minijava.ast.LocalDecl;

public class LocalIdIdFilter implements IdentificationFilter {
    @Override
    public boolean filter(LeveledIdentificationTable table, String id, LeveledDecl value) {
        return value.getDec() instanceof LocalDecl || value.getDec() instanceof FieldDecl || value.getDec() instanceof ClassDecl;
    }
}
