package net.viperfish.minijava.context;

public interface IdentificationFilter {

    public boolean filter(LeveledIdentificationTable table, String id, LeveledDecl value);

}
