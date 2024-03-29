package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.MethodDecl;

public class MethodIdFilter implements IdentificationFilter {

    @Override
    public boolean filter(LeveledIdentificationTable table, String id, LeveledDecl value) {
        return value.getDec() instanceof MethodDecl;
    }

}
