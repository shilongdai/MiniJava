package net.viperfish.minijava.codegen;

import net.viperfish.minijava.ast.ClassDecl;
import net.viperfish.minijava.ast.MethodDecl;

class MainInfo {

    public ClassDecl mainClass;
    public MethodDecl mainMethod;

    public MainInfo(ClassDecl mainClass, MethodDecl mainMethod) {
        this.mainClass = mainClass;
        this.mainMethod = mainMethod;
    }
}