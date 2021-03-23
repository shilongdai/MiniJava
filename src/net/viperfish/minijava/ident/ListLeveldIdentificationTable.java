package net.viperfish.minijava.ident;

import net.viperfish.minijava.ast.Declaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListLeveldIdentificationTable implements LeveledIdentificationTable {

    private int currentLevel;
    private List<Map<String, Declaration>> decls;

    public ListLeveldIdentificationTable() {
        this.currentLevel = 0;
        this.decls = new ArrayList<>();
        this.decls.add(new HashMap<>());
    }

    @Override
    public void openScope() {
        this.currentLevel += 1;
        this.decls.add(new HashMap<>());
    }

    @Override
    public void closeScope() {
        this.currentLevel -= 1;
        this.decls.remove(this.decls.size() - 1);
    }

    @Override
    public int currentLevel() {
        return this.currentLevel;
    }

    @Override
    public LeveledDecl getLevelDeclaration(String id) {
        for(int i = this.currentLevel; i >= 0; --i) {
            Map<String, Declaration> current = this.decls.get(i);
            if(current.containsKey(id)) {
                return new LeveledDecl(current.get(id), i);
            }
        }
        return null;
    }

    @Override
    public boolean contains(String id, int minLevel) {
        LeveledDecl l = getLevelDeclaration(id);
        if(l == null) {
            return false;
        }
        return l.getLevel() >= minLevel;
    }

    @Override
    public boolean contains(String id) {
        return contains(id, 0);
    }

    @Override
    public Declaration getDeclaration(String id) {
        return getLevelDeclaration(id).getDec();
    }

    @Override
    public Declaration registerDeclaration(String id, Declaration decl) {
        return decls.get(currentLevel).put(id, decl);
    }
}
