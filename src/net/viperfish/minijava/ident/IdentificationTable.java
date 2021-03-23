package net.viperfish.minijava.ident;

import net.viperfish.minijava.ast.Declaration;

public interface IdentificationTable {

    public Declaration getDeclaration(String id);

    public Declaration registerDeclaration(String id, Declaration decl);

    public boolean contains(String id);

}
