package net.viperfish.minijava.ident;

public interface LeveledIdentificationTable extends IdentificationTable {

    public void openScope();

    public void closeScope();

    public int currentLevel();

    public LeveledDecl getLevelDeclaration(String id);

    public boolean contains(String id, int minLevel);
}
