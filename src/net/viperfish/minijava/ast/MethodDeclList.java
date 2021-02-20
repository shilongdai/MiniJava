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

public class MethodDeclList implements Iterable<MethodDecl> {
    private List<MethodDecl> methodDeclList;

    public MethodDeclList() {
        methodDeclList = new ArrayList<MethodDecl>();
    }

    public void add(MethodDecl cd) {
        methodDeclList.add(cd);
    }

    public MethodDecl get(int i) {
        return methodDeclList.get(i);
    }

    public int size() {
        return methodDeclList.size();
    }

    public Iterator<MethodDecl> iterator() {
        return methodDeclList.iterator();
    }
}

