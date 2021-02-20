/**
 * miniJava Abstract Syntax Tree classes
 *
 * @author prins
 * @version COMP 520 (v2.2)
 */
package net.viperfish.minijava.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FieldDeclList implements Iterable<FieldDecl> {
    private List<FieldDecl> fieldDeclList;

    public FieldDeclList() {
        fieldDeclList = new ArrayList<FieldDecl>();
    }

    public void add(FieldDecl cd) {
        fieldDeclList.add(cd);
    }

    public FieldDecl get(int i) {
        return fieldDeclList.get(i);
    }

    public int size() {
        return fieldDeclList.size();
    }

    public Iterator<FieldDecl> iterator() {
        return fieldDeclList.iterator();
    }
}

