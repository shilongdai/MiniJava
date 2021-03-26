package net.viperfish.minijava.ident;

public interface IdentificationFilter {

    public boolean filter(LeveledIdentificationTable table, String id, LeveledDecl value);

}
