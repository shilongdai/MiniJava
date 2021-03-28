package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.*;

public final class VisitorUtils {

    private VisitorUtils() {}

    public static <A,R> R visitCorrectType(Visitor<A, R> v, A arg, TypeDenoter type) {
        if (type instanceof ClassType) {
            return v.visitClassType((ClassType) type, arg);
        }
        if (type instanceof ArrayType) {
            return v.visitArrayType((ArrayType) type, arg);
        }
        if (type instanceof BaseType) {
            return v.visitBaseType((BaseType) type, arg);
        }
        return null;
    }

    public static <A,R> R visitCorrectStmt(Visitor<A, R> v, A arg, Statement stmt) {
        if (stmt instanceof IxAssignStmt) {
            return v.visitIxAssignStmt((IxAssignStmt) stmt, arg);
        }
        if (stmt instanceof BlockStmt) {
            return v.visitBlockStmt((BlockStmt) stmt, arg);
        }
        if (stmt instanceof IfStmt) {
            return v.visitIfStmt((IfStmt) stmt, arg);
        }
        if (stmt instanceof CallStmt) {
            return v.visitCallStmt((CallStmt) stmt, arg);
        }
        if (stmt instanceof AssignStmt) {
            return v.visitAssignStmt((AssignStmt) stmt, arg);
        }
        if (stmt instanceof VarDeclStmt) {
            return v.visitVardeclStmt((VarDeclStmt) stmt, arg);
        }
        if (stmt instanceof ReturnStmt) {
            return v.visitReturnStmt((ReturnStmt) stmt, arg);
        }
        if (stmt instanceof WhileStmt) {
            return v.visitWhileStmt((WhileStmt) stmt, arg);
        }
        return null;
    }

    public static <A,R> R visitCorrectExp(Visitor<A, R> v, A arg, Expression exp) {
        if (exp instanceof NullExpr) {
            return v.visitNullExpr((NullExpr) exp, arg);
        }
        if (exp instanceof NewArrayExpr) {
            return v.visitNewArrayExpr((NewArrayExpr) exp, arg);
        }
        if (exp instanceof NewObjectExpr) {
            return v.visitNewObjectExpr((NewObjectExpr) exp, arg);
        }
        if (exp instanceof IxExpr) {
            return v.visitIxExpr((IxExpr) exp, arg);
        }
        if (exp instanceof UnaryExpr) {
            return v.visitUnaryExpr((UnaryExpr) exp, arg);
        }
        if (exp instanceof BinaryExpr) {
            return v.visitBinaryExpr((BinaryExpr) exp, arg);
        }
        if (exp instanceof RefExpr) {
            return v.visitRefExpr((RefExpr) exp, arg);
        }
        if (exp instanceof LiteralExpr) {
            return v.visitLiteralExpr((LiteralExpr) exp, arg);
        }
        if (exp instanceof CallExpr) {
            return v.visitCallExpr((CallExpr) exp, arg);
        }
        return null;
    }

    public static <A,R> R visitCorrectRef(Visitor<A, R> v, A arg, Reference ref) {
        if (ref instanceof QualRef) {
            return v.visitQRef((QualRef) ref, arg);
        }
        if (ref instanceof ThisRef) {
            return v.visitThisRef((ThisRef) ref, arg);
        }
        if (ref instanceof IdRef) {
            return v.visitIdRef((IdRef) ref, arg);
        }
        return null;
    }

    public static <A,R> R visitCorrectTerminal(Visitor<A, R> v, A arg, Terminal terminal) {
        if (terminal instanceof NullLiteral) {
            return v.visitNullLiteral((NullLiteral) terminal, arg);
        }
        if (terminal instanceof BooleanLiteral) {
            return v.visitBooleanLiteral((BooleanLiteral) terminal, arg);
        }
        if (terminal instanceof Identifier) {
            throw new IllegalArgumentException("Identifier should be treated separately");
        }
        if (terminal instanceof ReturnTerminal) {
            throw new IllegalArgumentException("Return Terminal are not legal");
        }
        if (terminal instanceof IntLiteral) {
            return v.visitIntLiteral((IntLiteral) terminal, arg);
        }
        if (terminal instanceof Operator) {
            return v.visitOperator((Operator) terminal, arg);
        }
        return null;
    }


}
