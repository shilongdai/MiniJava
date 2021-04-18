package net.viperfish.minijava;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenType;

public final class CompilerGlobal {

    public static final int DEBUG_LEVEL = 0;

    public static final boolean DEBUG_1;

    public static final boolean DEBUG_2;

    public static final boolean DEBUG_3;

    public static final ClassDecl sysString;
    public static final ClassDecl sysPrintStream;
    public static final ClassDecl sysSystem;

    public static final MethodDecl sysOutPrintln;

    static {
        DEBUG_1 = DEBUG_LEVEL >= 1;
        DEBUG_2 = DEBUG_LEVEL >= 2;
        DEBUG_3 = DEBUG_LEVEL >= 3;

        sysString = new ClassDecl("String", new FieldDeclList(), new MethodDeclList(), null);
        sysString.type = new BaseType(TypeKind.UNSUPPORTED, null);
        ParameterDeclList params = new ParameterDeclList();
        params.add(new ParameterDecl(new BaseType(TypeKind.INT, null), "n", null));
        sysOutPrintln = new MethodDecl(new FieldDecl(false, false, new BaseType(TypeKind.VOID, null), "println", null), params, new StatementList(), null);
        MethodDeclList methodList = new MethodDeclList();
        methodList.add(sysOutPrintln);
        sysPrintStream = new ClassDecl("_PrintStream", new FieldDeclList(), methodList, null);
        FieldDeclList fieldList = new FieldDeclList();
        ClassType printStreamType = new ClassType(new Identifier(new Token(TokenType.ID, "_PrintStream", null)), null);
        fieldList.add(new FieldDecl(false, true, printStreamType, "out", null));
        sysSystem = new ClassDecl("System", fieldList, new MethodDeclList(), null);
    }

}
