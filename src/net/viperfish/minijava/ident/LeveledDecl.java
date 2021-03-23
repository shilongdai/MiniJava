package net.viperfish.minijava.ident;

import net.viperfish.minijava.ast.Declaration;

public class LeveledDecl {

    private Declaration dec;
    private int level;

    public LeveledDecl(Declaration dec, int level) {
        this.dec = dec;
        this.level = level;
    }

    public Declaration getDec() {
        return dec;
    }

    public int getLevel() {
        return level;
    }

}
