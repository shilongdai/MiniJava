package net.viperfish.minijava.codegen;

import net.viperfish.minijava.ast.Declaration;

class PatchInfo {

    public Declaration decl;
    public int instruction;

    public PatchInfo(Declaration decl, int instruction) {
        this.decl = decl;
        this.instruction = instruction;
    }
}
