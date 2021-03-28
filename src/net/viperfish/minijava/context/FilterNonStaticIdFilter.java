package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.Declaration;
import net.viperfish.minijava.ast.MemberDecl;

public class FilterNonStaticIdFilter implements IdentificationFilter {
    @Override
    public boolean filter(LeveledIdentificationTable table, String id, LeveledDecl value) {
        Declaration dec = value.getDec();
        if(dec instanceof MemberDecl) {
            MemberDecl memberDecl = (MemberDecl) dec;
            if(!memberDecl.isStatic) {
                return false;
            }
        }
        return true;
    }
}
