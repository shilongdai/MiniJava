package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.scanner.SourcePosition;

import java.util.*;

public class TypeCheckVisitor implements Visitor<Object, TypeDenoter> {

    private static final TypeDenoter REFERENCE_INT;
    private static final TypeDenoter REFERENCE_BOOL;
    private static final Collection<String> VAL_MATCH_OPS;
    private static final Collection<String> BOOL_OPS;
    private static final Collection<String> ARITH_OPS;
    private static final Collection<String> VAL_MAG_COMP_OPS;

    private List<ContextualErrors> errors;

    public TypeCheckVisitor() {
        this.errors = new ArrayList<>();
    }

    static {
        REFERENCE_INT = new BaseType(TypeKind.INT, null);
        REFERENCE_BOOL = new BaseType(TypeKind.BOOLEAN, null);
        VAL_MATCH_OPS = new HashSet<>(Arrays.asList("==", "!="));
        BOOL_OPS = new HashSet<>(Arrays.asList("&&", "||", "!"));
        ARITH_OPS = new HashSet<>(Arrays.asList("+", "-", "*", "/"));
        VAL_MAG_COMP_OPS = new HashSet<>(Arrays.asList("<", ">", "<=", ">="));
    }

    public List<ContextualErrors> getErrors() {
        return new ArrayList<>(errors);
    }

    @Override
    public TypeDenoter visitPackage(Package prog, Object arg) {
        for(ClassDecl c : prog.classDeclList) {
            this.visitClassDecl(c, null);
        }
        return null;
    }

    @Override
    public TypeDenoter visitClassDecl(ClassDecl cd, Object arg) {
        cd.type = new BaseType(TypeKind.META, cd.posn);
        for(FieldDecl f : cd.fieldDeclList) {
            this.visitFieldDecl(f, null);
        }
        for(MethodDecl d : cd.methodDeclList) {
            this.visitMethodDecl(d, null);
        }
        return null;
    }

    @Override
    public TypeDenoter visitFieldDecl(FieldDecl fd, Object arg) {
        TypeDenoter t = VisitorUtils.visitCorrectType(this, null, fd.type);
        fd.type = t;
        return null;
    }

    @Override
    public TypeDenoter visitMethodDecl(MethodDecl md, Object arg) {
        TypeDenoter returnType = VisitorUtils.visitCorrectType(this, null, md.type);
        md.type = returnType;

        if(returnType.typeKind != TypeKind.ERROR) {
            for(ParameterDecl p : md.parameterDeclList) {
                this.visitParameterDecl(p, null);
            }
        }

        TypeDenoter stmtType = null;
        for(Statement s : md.statementList) {
            TypeDenoter t = VisitorUtils.visitCorrectStmt(this, returnType, s);
            if(stmtType == null && t != null) {
                stmtType = t;
            }
        }

        if(stmtType == null && returnType.typeKind != TypeKind.ERROR && returnType.typeKind != TypeKind.VOID) {
            missingReturnStmt(md);
        }
        return null;
    }

    @Override
    public TypeDenoter visitParameterDecl(ParameterDecl pd, Object arg) {
        TypeDenoter type = VisitorUtils.visitCorrectType(this, null, pd.type);
        pd.type = type;
        return type;
    }

    @Override
    public TypeDenoter visitVarDecl(VarDecl decl, Object arg) {
        TypeDenoter type = VisitorUtils.visitCorrectType(this, null, decl.type);
        EnumSet<TypeKind> acceptableEnums = EnumSet.of(TypeKind.ARRAY, TypeKind.BOOLEAN, TypeKind.CLASS, TypeKind.INT, TypeKind.ERROR, TypeKind.UNSUPPORTED);
        if(!acceptableEnums.contains(type.typeKind)) {
            expectedActualType(type);
            return new BaseType(TypeKind.ERROR, type.posn);
        }
        return type;
    }

    @Override
    public TypeDenoter visitBaseType(BaseType type, Object arg) {
        return type;
    }

    @Override
    public TypeDenoter visitClassType(ClassType type, Object arg) {
        TypeDenoter idType = this.visitIdentifier(type.className, arg);
        if(idType.typeKind != TypeKind.META) {
            expectedActualType(type);
            return new BaseType(TypeKind.ERROR, type.posn);
        }
        if(type.className.dominantDecl instanceof MethodDecl) {
            expectedActualType(type);
            return new BaseType(TypeKind.ERROR, type.posn);
        }
        return new ClassType(type.className, type.posn);
    }

    @Override
    public TypeDenoter visitArrayType(ArrayType type, Object arg) {
        TypeDenoter elt = VisitorUtils.visitCorrectType(this, null, type.eltType);
        type.eltType = elt;
        return type;
    }

    @Override
    public TypeDenoter visitBlockStmt(BlockStmt stmt, Object arg) {
        TypeDenoter result = null;
        for(Statement s : stmt.sl) {
            TypeDenoter returnType = VisitorUtils.visitCorrectStmt(this, arg, s);
            if(result == null && returnType != null) {
                result = returnType;
            }
        }
        return result;
    }

    @Override
    public TypeDenoter visitVardeclStmt(VarDeclStmt stmt, Object arg) {
        TypeDenoter declType = this.visitVarDecl(stmt.varDecl, arg);
        TypeDenoter initType = VisitorUtils.visitCorrectExp(this, null, stmt.initExp);

        if(!declType.equals(initType)) {
            expectMatchingTypeError(stmt.initExp);
        }

        return null;
    }

    @Override
    public TypeDenoter visitAssignStmt(AssignStmt stmt, Object arg) {
        TypeDenoter refType = VisitorUtils.visitCorrectRef(this, null, stmt.ref);
        TypeDenoter assignedType = VisitorUtils.visitCorrectExp(this, null, stmt.val);

        if(!refType.equals(assignedType)) {
            expectMatchingTypeError(stmt.val);
        }
        return null;
    }

    @Override
    public TypeDenoter visitIxAssignStmt(IxAssignStmt stmt, Object arg) {
        TypeDenoter refType = VisitorUtils.visitCorrectRef(this, null, stmt.ref);

        if(refType.typeKind != TypeKind.ARRAY) {
            expectedArrayError(stmt.ref);
            return null;
        }

        TypeDenoter ixType = VisitorUtils.visitCorrectExp(this, null, stmt.ix);
        if(!ixType.equals(REFERENCE_INT)) {
            expectedIntType(stmt.ix);
            return null;
        }

        TypeDenoter assignedType = VisitorUtils.visitCorrectExp(this, null, stmt.exp);
        ArrayType arrType = (ArrayType) refType;
        if(!assignedType.equals(arrType.eltType)) {
            expectMatchingTypeError(stmt.exp);
        }
        return null;
    }

    @Override
    public TypeDenoter visitCallStmt(CallStmt stmt, Object arg) {
        VisitorUtils.visitCorrectRef(this, null, stmt.methodRef);
        for(Expression exp : stmt.argList) {
            VisitorUtils.visitCorrectExp(this, null, exp);
        }

        TypeDenoter type = validMethodInvocation(stmt.methodRef, stmt.argList);
        return null;
    }

    @Override
    public TypeDenoter visitReturnStmt(ReturnStmt stmt, Object arg) {
        TypeDenoter returnType = (TypeDenoter) arg;
        if(stmt.returnExpr != null) {
            TypeDenoter actualReturnType = VisitorUtils.visitCorrectExp(this, null, stmt.returnExpr);
            if(arg == null) {
                expectedVoid(stmt.returnExpr);
                return null;
            } else {
                if(actualReturnType.equals(arg)) {
                    return returnType;
                } else {
                    wrongReturnType(stmt.returnExpr);
                    return new BaseType(TypeKind.ERROR, stmt.returnExpr.posn);
                }
            }
        } else {
            if(arg == null) {
                return null;
            } else {
                missingReturnType(stmt.posn);
                return new BaseType(TypeKind.ERROR, stmt.posn);
            }
        }

    }

    @Override
    public TypeDenoter visitIfStmt(IfStmt stmt, Object arg) {
        TypeDenoter conditionalType = VisitorUtils.visitCorrectExp(this, null, stmt.cond);
        if(!conditionalType.equals(REFERENCE_BOOL)) {
            expectedBoolType(stmt.cond);
        }

        TypeDenoter thenType = VisitorUtils.visitCorrectStmt(this, arg, stmt.thenStmt);
        if(stmt.elseStmt != null) {
            TypeDenoter elseType = VisitorUtils.visitCorrectStmt(this, arg, stmt.elseStmt);

            if(thenType == null && elseType == null) {
                return null;
            } else if(thenType != null && elseType != null) {
                return thenType;
            } else {
                return null;
            }

        } else {
            return thenType;
        }

    }

    @Override
    public TypeDenoter visitWhileStmt(WhileStmt stmt, Object arg) {
        TypeDenoter conditionalType = VisitorUtils.visitCorrectExp(this, null, stmt.cond);
        if(!conditionalType.equals(REFERENCE_BOOL)) {
            expectedBoolType(stmt.cond);
        }

        return VisitorUtils.visitCorrectStmt(this, arg, stmt.body);
    }

    @Override
    public TypeDenoter visitUnaryExpr(UnaryExpr expr, Object arg) {
        TypeDenoter exprType = VisitorUtils.visitCorrectExp(this, null, expr.expr);
        this.visitOperator(expr.operator, null);

        TypeDenoter result = null;
        if(BOOL_OPS.contains(expr.operator.spelling)) {
            if(!exprType.equals(REFERENCE_BOOL)) {
                result = new BaseType(TypeKind.ERROR, expr.expr.posn);
                expectedBoolType(expr.expr);
            } else {
                result = new BaseType(TypeKind.BOOLEAN, expr.operator.posn);
            }
        } else if(ARITH_OPS.contains(expr.operator.spelling)) {
            if(!exprType.equals(REFERENCE_INT)) {
                result = new BaseType(TypeKind.ERROR, expr.expr.posn);
                expectedIntType(expr.expr);
            } else {
                result = new BaseType(TypeKind.INT, expr.operator.posn);
            }
        }

        expr.dominantType = result;
        return result;
    }

    @Override
    public TypeDenoter visitBinaryExpr(BinaryExpr expr, Object arg) {
        TypeDenoter lhsType = VisitorUtils.visitCorrectExp(this, null, expr.left);
        TypeDenoter rhsType = VisitorUtils.visitCorrectExp(this, null, expr.right);
        this.visitOperator(expr.operator, arg);

        TypeDenoter result = null;
        if(VAL_MATCH_OPS.contains(expr.operator.spelling)) {
            result = handleValMatchOps(expr.left, lhsType, rhsType);
        } else if(BOOL_OPS.contains(expr.operator.spelling)) {
            result = handleBooleanOps(expr.left, lhsType, rhsType);
        } else if(ARITH_OPS.contains(expr.operator.spelling)) {
            result = handleArithOps(expr.left, lhsType, rhsType);
        } else if(VAL_MAG_COMP_OPS.contains(expr.operator.spelling)) {
            result = handleMagCompOps(expr.left, lhsType, rhsType);
        } else {
            throw new IllegalArgumentException("Did not find operator: " + expr.operator.spelling);
        }
        expr.dominantType = result;
        return result;
    }

    @Override
    public TypeDenoter visitRefExpr(RefExpr expr, Object arg) {
        TypeDenoter refType = VisitorUtils.visitCorrectRef(this, null, expr.ref);
        EnumSet<TypeKind> acceptableEnums = EnumSet.of(TypeKind.ARRAY, TypeKind.BOOLEAN, TypeKind.CLASS, TypeKind.INT, TypeKind.ERROR, TypeKind.UNSUPPORTED);
        if(acceptableEnums.contains(refType.typeKind)) {
            expr.dominantType = refType;
            return refType;
        } else {
            TypeDenoter error = new BaseType(TypeKind.ERROR, expr.ref.posn);
            expr.dominantType = error;
            expectValReferenceError(expr.ref);
            return error;
        }
    }

    @Override
    public TypeDenoter visitIxExpr(IxExpr expr, Object arg) {
        TypeDenoter refType = VisitorUtils.visitCorrectRef(this, null, expr.ref);
        if(refType instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) refType;
            TypeDenoter exprType = VisitorUtils.visitCorrectExp(this, null, expr.ixExpr);
            if(!exprType.equals(REFERENCE_INT)) {
                arraySizeIntError(expr.ixExpr);
                TypeDenoter err = new BaseType(TypeKind.ERROR, expr.ixExpr.posn);
                expr.dominantType = err;
                return err;
            }
            expr.dominantType = arrayType.eltType;
            return arrayType.eltType;
        } else {
            expectedArrayError(expr.ref);
            TypeDenoter err =  new BaseType(TypeKind.ERROR, expr.ref.posn);
            expr.dominantType = err;
            return err;
        }
    }

    @Override
    public TypeDenoter visitCallExpr(CallExpr expr, Object arg) {
        VisitorUtils.visitCorrectRef(this, null, expr.functionRef);
        for(Expression exp : expr.argList) {
            VisitorUtils.visitCorrectExp(this, null, exp);
        }

        TypeDenoter type = validMethodInvocation(expr.functionRef, expr.argList);
        expr.dominantType = type;
        return type;
    }

    @Override
    public TypeDenoter visitLiteralExpr(LiteralExpr expr, Object arg) {
        Terminal terminal = expr.lit;
        TypeDenoter type = VisitorUtils.visitCorrectTerminal(this, arg, terminal);
        expr.dominantType = type;
        return type;
    }

    @Override
    public TypeDenoter visitNewObjectExpr(NewObjectExpr expr, Object arg) {
        TypeDenoter classType = this.visitClassType(expr.classtype, arg);
        expr.dominantType = classType;
        return classType;
    }

    @Override
    public TypeDenoter visitNewArrayExpr(NewArrayExpr expr, Object arg) {
        TypeDenoter sizeType = VisitorUtils.visitCorrectExp(this, null, expr.sizeExpr);
        if(!sizeType.equals(REFERENCE_INT)) {
            arraySizeIntError(expr.sizeExpr);
            TypeDenoter error = new BaseType(TypeKind.ERROR, expr.posn);
            expr.dominantType = error;
            return error;
        }
        TypeDenoter elementType = VisitorUtils.visitCorrectType(this, null, expr.eltType);
        expr.eltType = elementType;

        ArrayType arrType = new ArrayType(elementType, expr.posn);
        expr.dominantType = arrType;
        return arrType;
    }

    @Override
    public TypeDenoter visitNullExpr(NullExpr expr, Object arg) {
        TypeDenoter t = new BaseType(TypeKind.NULL, expr.posn);
        expr.dominantType = t;
        return t;
    }

    @Override
    public TypeDenoter visitThisRef(ThisRef ref, Object arg) {
        FieldDecl thisDecl = (FieldDecl) ref.dominantDecl;
        ref.dominantType = thisDecl.type;
        return thisDecl.type;
    }

    @Override
    public TypeDenoter visitIdRef(IdRef ref, Object arg) {
        TypeDenoter type = this.visitIdentifier(ref.id, arg);
        ref.dominantType = type;
        return type;
    }

    @Override
    public TypeDenoter visitQRef(QualRef ref, Object arg) {
        VisitorUtils.visitCorrectRef(this, arg, ref.ref);
        TypeDenoter type = this.visitIdentifier(ref.id, arg);
        ref.dominantType = type;
        return type;
    }

    @Override
    public TypeDenoter visitIdentifier(Identifier id, Object arg) {
        Declaration dec = id.dominantDecl;
        if(dec instanceof ClassDecl) {
            return new BaseType(TypeKind.META, id.posn);
        }
        if(dec instanceof MethodDecl) {
            return new BaseType(TypeKind.META, id.posn);
        }

        return dec.type;
    }

    @Override
    public TypeDenoter visitOperator(Operator op, Object arg) {
        return new BaseType(TypeKind.META, op.posn);
    }

    @Override
    public TypeDenoter visitIntLiteral(IntLiteral num, Object arg) {
        return new BaseType(TypeKind.INT, num.posn);
    }

    @Override
    public TypeDenoter visitBooleanLiteral(BooleanLiteral bool, Object arg) {
        return new BaseType(TypeKind.BOOLEAN, bool.posn);
    }

    @Override
    public TypeDenoter visitNullLiteral(NullLiteral bool, Object arg) {
        return new BaseType(TypeKind.NULL, bool.posn);
    }


    private void arraySizeIntError(Expression exp) {
        String msg = "Expected int expression";
        errors.add(new ContextualErrors(exp.posn, msg));
    }

    private void notFunctionError(Reference ref) {
        String msg = String.format("The reference %s is not a method", ref.dominantDecl.name);
        errors.add(new ContextualErrors(ref.posn, msg));
    }

    private void wrongParametersError(Reference ref, MethodDecl method, ExprList exprs) {
        String msg = String.format("Parameters for %s has incorrect types", method.name);
        errors.add(new ContextualErrors(ref.posn, msg));
    }

    private void expectedArrayError(Reference ref) {
        errors.add(new ContextualErrors(ref.posn, "Expected array"));
    }

    private void expectValReferenceError(Reference ref) {
        errors.add(new ContextualErrors(ref.posn, "Expected reference to value"));
    }

    private void expectMatchingTypeError(Expression lhs) {
        errors.add(new ContextualErrors(lhs.posn, "Expected matching types"));
    }

    private void expectedBoolType(Expression lhs) {
        errors.add(new ContextualErrors(lhs.posn, "Expected boolean types"));
    }

    private void expectedIntType(Expression lhs) {
        errors.add(new ContextualErrors(lhs.posn, "Expected int types"));
    }

    private void expectedVoid(Expression exp) {
        errors.add(new ContextualErrors(exp.posn, "Expected void return"));
    }

    private void wrongReturnType(Expression exp) {
        errors.add(new ContextualErrors(exp.posn, "Wrong return type"));
    }

    private void missingReturnType(SourcePosition posn) {
        errors.add(new ContextualErrors(posn, "Missing return expression"));
    }

    private void expectedActualType(TypeDenoter t) {
        errors.add(new ContextualErrors(t.posn, "Expected array declaration, class, int, or bool"));
    }

    private void missingReturnStmt(MethodDecl decl) {
        errors.add(new ContextualErrors(decl.posn, "Missing return statement"));
    }

    private TypeDenoter validMethodInvocation(Reference ref, ExprList parameters) {
        Declaration decl = ref.dominantDecl;
        if(decl instanceof MethodDecl) {
            MethodDecl method = (MethodDecl) decl;
            ParameterDeclList paramList = method.parameterDeclList;
            if(paramList.size() != parameters.size()) {
                wrongParametersError(ref, method, parameters);
                return new BaseType(TypeKind.ERROR, ref.posn);
            }
            for(int i = 0; i < paramList.size(); ++i) {
                TypeDenoter declType = paramList.get(i).type;
                TypeDenoter paramType = parameters.get(i).dominantType;

                if(!declType.equals(paramType)) {
                    wrongParametersError(ref, method, parameters);
                    return new BaseType(TypeKind.ERROR, ref.posn);
                }
            }
            return method.type;
        } else {
            notFunctionError(ref);
            return new BaseType(TypeKind.ERROR, ref.posn);
        }
    }

    private TypeDenoter handleValMatchOps(Expression lhsExp, TypeDenoter lhs, TypeDenoter rhs) {
        TypeDenoter result = null;
        if(!lhs.equals(rhs)) {
            result = new BaseType(TypeKind.ERROR, lhs.posn);
            expectMatchingTypeError(lhsExp);
        } else {
            result = new BaseType(TypeKind.BOOLEAN, lhs.posn);
        }
        return result;
    }

    private TypeDenoter handleBooleanOps(Expression lhsExp, TypeDenoter lhs, TypeDenoter rhs) {
        TypeDenoter result = null;
        if(!(lhs.equals(REFERENCE_BOOL) && rhs.equals(REFERENCE_BOOL))) {
            result = new BaseType(TypeKind.ERROR, lhs.posn);
            expectedBoolType(lhsExp);
        } else {
            result = new BaseType(TypeKind.BOOLEAN, lhs.posn);
        }
        return result;
    }

    private TypeDenoter handleArithOps(Expression lhsExp, TypeDenoter lhs, TypeDenoter rhs) {
        TypeDenoter result = null;
        if(!(lhs.equals(REFERENCE_INT) && rhs.equals(REFERENCE_INT))) {
            result = new BaseType(TypeKind.ERROR, lhs.posn);
            expectedIntType(lhsExp);
        } else {
            result = new BaseType(TypeKind.INT, lhs.posn);
        }
        return result;
    }

    private TypeDenoter handleMagCompOps(Expression lhsExp, TypeDenoter lhs, TypeDenoter rhs) {
        TypeDenoter result = null;
        if(!(lhs.equals(REFERENCE_INT) && rhs.equals(REFERENCE_INT))) {
            result = new BaseType(TypeKind.ERROR, lhs.posn);
            expectedIntType(lhsExp);
        } else {
            result = new BaseType(TypeKind.BOOLEAN, lhs.posn);
        }
        return result;
    }
}
