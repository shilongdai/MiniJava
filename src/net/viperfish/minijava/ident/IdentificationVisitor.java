package net.viperfish.minijava.ident;

import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.ast.*;

public class IdentificationVisitor implements Visitor<LeveledIdentificationTable, Object> {
    @Override
    public Object visitPackage(Package prog, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitClassDecl(ClassDecl cd, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitFieldDecl(FieldDecl fd, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitMethodDecl(MethodDecl md, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitParameterDecl(ParameterDecl pd, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitVarDecl(VarDecl decl, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitBaseType(BaseType type, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitClassType(ClassType type, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitArrayType(ArrayType type, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitBlockStmt(BlockStmt stmt, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitVardeclStmt(VarDeclStmt stmt, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitAssignStmt(AssignStmt stmt, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitIxAssignStmt(IxAssignStmt stmt, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitCallStmt(CallStmt stmt, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt stmt, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt stmt, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt stmt, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitRefExpr(RefExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitIxExpr(IxExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitCallExpr(CallExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitNewObjectExpr(NewObjectExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitNewArrayExpr(NewArrayExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitNullExpr(NullExpr expr, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitThisRef(ThisRef ref, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitIdRef(IdRef ref, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitQRef(QualRef ref, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitIdentifier(Identifier id, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitOperator(Operator op, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitIntLiteral(IntLiteral num, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitBooleanLiteral(BooleanLiteral bool, LeveledIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitNullLiteral(NullLiteral bool, LeveledIdentificationTable arg) {
        return null;
    }
}
