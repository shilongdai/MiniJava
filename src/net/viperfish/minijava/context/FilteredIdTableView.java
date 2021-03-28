package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.Declaration;

import java.util.ArrayList;
import java.util.List;

public class FilteredIdTableView implements FilterableIdentificationTable {

    private IdentificationFilter filter;
    private LeveledIdentificationTable table;

    public FilteredIdTableView(IdentificationFilter filter, LeveledIdentificationTable table) {
        this.filter = filter;
        this.table = table;
    }

    @Override
    public FilterableIdentificationTable filterTable(IdentificationFilter filter) {
        return new FilteredIdTableView(filter, table);
    }

    @Override
    public void openScope() {
        this.table.openScope();
    }

    @Override
    public void closeScope() {
        this.table.closeScope();
    }

    @Override
    public int currentLevel() {
        return this.table.currentLevel();
    }

    @Override
    public LeveledDecl getLevelDeclaration(String id) {
        List<LeveledDecl> raw = table.getAllLeveledDeclarations(id);
        for(LeveledDecl l : raw) {
            if(this.filter.filter(this.table, id, l)) {
                return l;
            }
        }
        return null;
    }

    @Override
    public List<LeveledDecl> getAllLeveledDeclarations(String id) {
        List<LeveledDecl> result = new ArrayList<>();
        List<LeveledDecl> raw = table.getAllLeveledDeclarations(id);
        for(LeveledDecl l : raw) {
            if(this.filter.filter(this.table, id, l)) {
                result.add(l);
            }
        }
        return result;
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
        LeveledDecl decl = getLevelDeclaration(id);
        if(decl != null) {
            return decl.getDec();
        }
        return null;
    }

    @Override
    public Declaration registerDeclaration(String id, Declaration decl) {
        return table.registerDeclaration(id, decl);
    }
}
