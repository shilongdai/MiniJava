package net.viperfish.minijava.codegen;

import net.viperfish.minijava.CompilerGlobal;
import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.mJAM.Machine;

import java.util.ArrayList;
import java.util.Collection;

public class CodeGenerator implements Visitor<Object, Object> {

    private Collection<PatchInfo> patchAddress;
    private Collection<PatchRaw> patchQueue;

    private short staticOffset;

    public CodeGenerator() {
        patchAddress = new ArrayList<>();
        patchQueue = new ArrayList<>();
        staticOffset = 0;
    }

    public int genCode(Package program) {
        return (int) program.visit(this, null);
    }

    @Override
    public Object visitPackage(Package prog, Object arg) {
        MainInfo mainClass = findMainClass(prog.classDeclList);
        if(mainClass == null) {
            return -1;
        }

        for(ClassDecl cd : prog.classDeclList) {
            int fieldSize = 0;
            for(FieldDecl f : cd.fieldDeclList) {
                int size = (int) f.visit(this, fieldSize);
                if(!f.isStatic) {
                    fieldSize += size;
                }

                if(fieldSize == Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Longer than shorts");
                }
            }

            cd.runtimeEntity = new RuntimeEntity(fieldSize);
        }

        Machine.emit(Machine.Op.LOADL, 0, 0, 0);
        Machine.emit(Machine.Prim.newarr);
        int mainCallAddr = Machine.nextInstrAddr();
        Machine.emit(Machine.Op.CALL, 0);
        patchAddress.add(new PatchInfo(mainClass.mainMethod, mainCallAddr));
        Machine.emit(Machine.Op.HALT, 0);

        for(ClassDecl c : prog.classDeclList) {
            c.visit(this, arg);
        }

        patchAddress();
        patchAll();
        return 0;
    }

    @Override
    public Object visitClassDecl(ClassDecl cd, Object arg) {
        for(MethodDecl m : cd.methodDeclList) {
            m.visit(this, arg);
        }

        return null;
    }

    @Override
    public Integer visitFieldDecl(FieldDecl fd, Object arg) {
        int size = (int) fd.type.visit(this, arg);
        if(!fd.isStatic) {
            int offset = (int) arg;
            KnownAddress relative = new KnownAddress(0, new RuntimeAddress(Machine.Reg.OB, (short) 0), false);
            fd.runtimeEntity = new UnknownAddress(relative, offset, size, RefType.FIELD);
        } else {
            Machine.emit(Machine.Op.PUSH, size);
            fd.runtimeEntity = new KnownAddress(size, new RuntimeAddress(Machine.Reg.SB, staticOffset), true);
            staticOffset += size;
        }
        return size;
    }

    @Override
    public Object visitMethodDecl(MethodDecl md, Object arg) {
        short paramOffset = (short) (-1 * md.parameterDeclList.size());
        for(ParameterDecl p : md.parameterDeclList) {
            paramOffset += (int) p.visit(this, paramOffset);
        }

        if(md.type.typeKind == TypeKind.VOID) {
            if(md.statementList.size() == 0) {
                md.statementList.add(new ReturnStmt(null, null));
            } else {
                Statement finalStmt = md.statementList.get(md.statementList.size() - 1);
                if(!(finalStmt instanceof ReturnStmt)) {
                    md.statementList.add(new ReturnStmt(null, null));
                }
            }
        }

        int methodStart = Machine.nextInstrAddr();
        md.runtimeEntity = new StaticCode(new Address(methodStart));
        ActivationFrame frame = new ActivationFrame(3, md.parameterDeclList.size());
        for(Statement s : md.statementList) {
            s.visit(this, frame);
        }
        return null;
    }

    @Override
    public Integer visitParameterDecl(ParameterDecl pd, Object arg) {
        pd.runtimeEntity = new KnownAddress(1, new RuntimeAddress(Machine.Reg.LB, (Short) arg), true);
        return (Integer) pd.type.visit(this, null);
    }

    @Override
    public Object visitVarDecl(VarDecl decl, Object arg) {
        ActivationFrame next = (ActivationFrame) arg;
        int size = (int) decl.type.visit(this, arg);
        decl.runtimeEntity = new KnownAddress(size, new RuntimeAddress(Machine.Reg.LB, (short) next.getAllocatedVarAndIncrement(1)), true);
        return null;
    }

    @Override
    public Object visitBaseType(BaseType type, Object arg) {
        return 1;
    }

    @Override
    public Object visitClassType(ClassType type, Object arg) {
        return 1;
    }

    @Override
    public Object visitArrayType(ArrayType type, Object arg) {
        return 1;
    }

    @Override
    public Object visitBlockStmt(BlockStmt stmt, Object arg) {
        for(Statement s : stmt.sl) {
            s.visit(this, arg);
        }
        return null;
    }

    @Override
    public Object visitVardeclStmt(VarDeclStmt stmt, Object arg) {
        stmt.varDecl.visit(this, arg);
        RuntimeEntity entity = stmt.varDecl.runtimeEntity;
        Machine.emit(Machine.Op.PUSH, entity.getSize());
        stmt.initExp.visit(this, null);

        if(entity instanceof KnownAddress) {
            KnownAddress addr = (KnownAddress) entity;
            Machine.emit(Machine.Op.STORE, addr.getSize(), addr.getAddress().getRegister(), addr.getAddress().getOffset());
        } else {
            throw new IllegalArgumentException("Illegal AST");
        }
        return null;
    }

    @Override
    public Object visitAssignStmt(AssignStmt stmt, Object arg) {
        RuntimeEntity entity = (RuntimeEntity) stmt.ref.visit(this, null);
        if(entity instanceof KnownAddress) {
            stmt.val.visit(this, null);
            KnownAddress addr = (KnownAddress) entity;
            Machine.emit(Machine.Op.STORE, addr.getSize(), addr.getAddress().getRegister(), addr.getAddress().getOffset());
        } else if(entity instanceof UnknownAddress) {
            UnknownAddress addr = (UnknownAddress) entity;
            if(addr.getRefType() == RefType.FIELD) {
                resolveUnknownAddress(addr);
                Machine.emit(Machine.Op.LOADL, addr.getOffset());
                stmt.val.visit(this, null);
                Machine.emit(Machine.Prim.fieldupd);
            } else {
                stmt.val.visit(this, null);
                resolveUnknownAddress(addr);
                Machine.emit(Machine.Op.LOADL, addr.getOffset());
                Machine.emit(Machine.Prim.add);
                Machine.emit(Machine.Op.STOREI, addr.getTargetSize(), 0, 0);
            }
        }
        return null;
    }

    @Override
    public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg) {
        getArrAddress(stmt.ref);
        stmt.ix.visit(this, null);
        stmt.exp.visit(this, null);
        Machine.emit(Machine.Prim.arrayupd);
        return null;
    }

    @Override
    public Object visitCallStmt(CallStmt stmt, Object arg) {
        for(Expression e : stmt.argList) {
            e.visit(this, null);
        }
        if(stmt.methodRef.dominantDecl == CompilerGlobal.sysOutPrintln) {
            Machine.emit(Machine.Prim.putintnl);
        } else {
            MethodDecl decl = (MethodDecl) stmt.methodRef.dominantDecl;
            RuntimeEntity entity = (RuntimeEntity) stmt.methodRef.visit(this, null);
            callMethod(decl, entity);
            if(decl.type.typeKind != TypeKind.VOID) {
                Machine.emit(Machine.Op.POP, 0, 0, (Integer) decl.type.visit(this, null));
            }
        }
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt stmt, Object arg) {
        ActivationFrame f = (ActivationFrame) arg;
        if(stmt.returnExpr == null) {
            Machine.emit(Machine.Op.RETURN, 0, 0, f.getArgCount());
        } else {
            int size = (int) stmt.returnExpr.visit(this, null);
            Machine.emit(Machine.Op.RETURN, size, 0, f.getArgCount());
        }
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt stmt, Object arg) {
        int jAddr = Machine.nextInstrAddr();
        Machine.emit(Machine.Op.JUMP, Machine.Reg.CP, 0);
        int stmtStart = Machine.nextInstrAddr();
        stmt.thenStmt.visit(this, arg);
        int endThenAddr = Machine.nextInstrAddr();
        Machine.emit(Machine.Op.JUMP, Machine.Reg.CP, 0);

        int condStart = Machine.nextInstrAddr();
        stmt.cond.visit(this, null);
        int jumpStart = Machine.nextInstrAddr();
        Machine.emit(Machine.Op.JUMPIF, 1, Machine.Reg.CP, stmtStart - jumpStart);
        Machine.patch(endThenAddr, jumpStart - endThenAddr + 1);
        Machine.patch(jAddr, condStart - jAddr);

        if(stmt.elseStmt != null) {
            stmt.elseStmt.visit(this, arg);
            int elseEnd = Machine.nextInstrAddr();
            Machine.patch(endThenAddr, elseEnd - endThenAddr);
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt stmt, Object arg) {
        int jAddr = Machine.nextInstrAddr();
        Machine.emit(Machine.Op.JUMP, Machine.Reg.CP, 0);
        int stmtStart = Machine.nextInstrAddr();
        stmt.body.visit(this, arg);

        int condStart = Machine.nextInstrAddr();
        stmt.cond.visit(this, null);
        int jumpStart = Machine.nextInstrAddr();
        Machine.emit(Machine.Op.JUMPIF, 1, Machine.Reg.CP, stmtStart - jumpStart);
        Machine.patch(jAddr, condStart - jAddr);
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr expr, Object arg) {
        int size = (int) expr.expr.visit(this, arg);
        return expr.operator.visit(this, new OpsInfo(size, true));
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr expr, Object arg) {
        int size = (int) expr.left.visit(this, arg);
        expr.right.visit(this, arg);
        return expr.operator.visit(this, new OpsInfo(size, false));
    }

    @Override
    public Object visitRefExpr(RefExpr expr, Object arg) {
        RuntimeEntity entity = (RuntimeEntity) expr.ref.visit(this, null);
        if(entity instanceof KnownAddress) {
            KnownAddress addr = (KnownAddress) entity;
            RuntimeAddress runtimeAddress = addr.getAddress();
            Machine.emit(Machine.Op.LOAD, entity.getSize(), runtimeAddress.getRegister(), runtimeAddress.getOffset());
        } else if(entity instanceof UnknownAddress) {
            UnknownAddress addr = (UnknownAddress) entity;
            resolveUnknownAddress(addr);
            getUnknownAddrVal(addr);
        } else {
            throw new IllegalArgumentException("Invalid ref");
        }
        return entity.getSize();
    }

    @Override
    public Object visitIxExpr(IxExpr expr, Object arg) {
        getArrAddress(expr.ref);
        expr.ixExpr.visit(this, null);
        Machine.emit(Machine.Prim.arrayref);
        return expr.dominantType.visit(this, null);
    }

    @Override
    public Object visitCallExpr(CallExpr expr, Object arg) {
        for(Expression e : expr.argList) {
            e.visit(this, null);
        }

        MethodDecl decl = (MethodDecl) expr.functionRef.dominantDecl;
        RuntimeEntity entity = (RuntimeEntity) expr.functionRef.visit(this, null);
        callMethod(decl, entity);

        return decl.type.visit(this, null);
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr expr, Object arg) {
        KnownValue val = (KnownValue) expr.lit.visit(this, arg);
        Machine.emit(Machine.Op.LOADL, val.getWord());
        return val.getSize();
    }

    @Override
    public Object visitNewObjectExpr(NewObjectExpr expr, Object arg) {
        Machine.emit(Machine.Op.LOADL, -1);
        Machine.emit(Machine.Op.LOADL, expr.classtype.className.dominantDecl.runtimeEntity.getSize());
        Machine.emit(Machine.Prim.newobj);

        return expr.classtype.visit(this, null);
    }

    @Override
    public Object visitNewArrayExpr(NewArrayExpr expr, Object arg) {
        expr.sizeExpr.visit(this, arg);
        Machine.emit(Machine.Prim.newarr);
        return 1;
    }

    @Override
    public Object visitNullExpr(NullExpr expr, Object arg) {
        KnownValue val = (KnownValue) expr.nullLiteral.visit(this, arg);
        Machine.emit(Machine.Op.LOADL, val.getWord());
        return 1;
    }

    @Override
    public Object visitThisRef(ThisRef ref, Object arg) {
        KnownAddress address = new KnownAddress(1, new RuntimeAddress(Machine.Reg.OB, (short) 0), false);
        return new UnknownAddress(address, 0, 0, RefType.ADDR);
    }

    @Override
    public Object visitIdRef(IdRef ref, Object arg) {
        return ref.dominantDecl.runtimeEntity;
    }

    @Override
    public Object visitQRef(QualRef ref, Object arg) {
        // TODO: Support static and array.length and function
        RuntimeEntity entity = (RuntimeEntity) ref.ref.visit(this, arg);
        if(ref.ref.dominantType.typeKind == TypeKind.ARRAY && ref.id.spelling.equals("length")) {
            return new UnknownAddress(ref.ref.dominantDecl.runtimeEntity, -1, 1, RefType.ARRLEN);
        } else if(ref.dominantDecl instanceof FieldDecl) {
            FieldDecl field = (FieldDecl) ref.dominantDecl;
            if(field.isStatic) {
                return field.runtimeEntity;
            } else {
                UnknownAddress fieldAddr = (UnknownAddress) field.runtimeEntity;
                return new UnknownAddress(entity, fieldAddr.getOffset(), fieldAddr.getTargetSize(), RefType.FIELD);
            }
        } else if(ref.dominantDecl instanceof MethodDecl) {
            MethodDecl decl = (MethodDecl) ref.dominantDecl;
            if(decl.isStatic) {
                return null;
            } else {
                return new UnknownAddress(entity, 0, 0, null);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported");
        }
    }

    @Override
    public Object visitIdentifier(Identifier id, Object arg) {
        return null;
    }

    @Override
    public Object visitOperator(Operator op, Object arg) {
        // TODO: Support short circuit

        OpsInfo info = (OpsInfo) arg;
        switch (op.spelling) {
            case ">":
                Machine.emit(Machine.Prim.gt);
                return 1;
            case "<":
                Machine.emit(Machine.Prim.lt);
                return 1;
            case "==":
                Machine.emit(Machine.Prim.eq);
                return 1;
            case "<=":
                Machine.emit(Machine.Prim.le);
                return 1;
            case ">=":
                Machine.emit(Machine.Prim.ge);
                return 1;
            case "!=":
                Machine.emit(Machine.Prim.ne);
                return 1;
            case "&&":
                Machine.emit(Machine.Prim.and);
                return 1;
            case "||":
                Machine.emit(Machine.Prim.or);
                return 1;
            case "!":
                Machine.emit(Machine.Prim.not);
                return 1;
            case "+":
                Machine.emit(Machine.Prim.add);
                return info.size;
            case "-":
                if(info.unary) {
                    Machine.emit(Machine.Prim.neg);
                    return 1;
                } else {
                    Machine.emit(Machine.Prim.sub);
                    return info.size;
                }
            case "*":
                Machine.emit(Machine.Prim.mult);
                return info.size;
            case "/":
                Machine.emit(Machine.Prim.div);
                return info.size;
            default:
                throw new IllegalArgumentException("Unsupported Operator");
        }
    }

    @Override
    public Object visitIntLiteral(IntLiteral num, Object arg) {
        return new KnownValue(1, Integer.parseInt(num.spelling));
    }

    @Override
    public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) {
        boolean b = Boolean.parseBoolean(bool.spelling);
        if(b) {
            return new KnownValue(1, 1);
        } else {
            return new KnownValue(1, 0);
        }
    }

    @Override
    public Object visitNullLiteral(NullLiteral bool, Object arg) {
        return new KnownValue(1, 0);
    }

    private void patchAddress() {
        for(PatchInfo p : this.patchAddress) {
            RuntimeEntity entity = p.decl.runtimeEntity;
            if(entity instanceof KnownAddress) {
                patchQueue.add(new PatchRaw(p.instruction, ((KnownAddress) entity).getAddress().getOffset()));
            } else if(entity instanceof StaticCode) {
                patchQueue.add(new PatchRaw(p.instruction, ((StaticCode) entity).getAddress().getOffset()));
            }
        }
    }

    private void patchAll() {
        for(PatchRaw p : this.patchQueue) {
            Machine.patch(p.instruction, p.number);
        }
    }


    private MainInfo findMainClass(ClassDeclList classes) {
        // TODO: detect 2 main class
        for(ClassDecl c : classes) {
            for(MethodDecl m : c.methodDeclList) {
                if(m.type.typeKind == TypeKind.VOID && m.isStatic && !m.isPrivate && m.name.equals("main")) {
                    if(m.parameterDeclList.size() == 1 && m.parameterDeclList.get(0).type.typeKind == TypeKind.ARRAY) {
                        ArrayType arr = (ArrayType) m.parameterDeclList.get(0).type;
                        if(arr.eltType.typeKind == TypeKind.UNSUPPORTED) {
                            return new MainInfo(c, m);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void resolveUnknownAddress(UnknownAddress addr) {
        if(addr.getRef() instanceof KnownAddress) {
            KnownAddress startingPoint = (KnownAddress) addr.getRef();
            if(startingPoint.isStored()) {
                Machine.emit(Machine.Op.LOAD, startingPoint.getSize(), startingPoint.getAddress().getRegister(), startingPoint.getAddress().getOffset());
            } else {
                Machine.emit(Machine.Op.LOADA, startingPoint.getAddress().getRegister(), startingPoint.getAddress().getOffset());
            }
        } else if(addr.getRef() instanceof UnknownAddress) {
            resolveUnknownAddress((UnknownAddress) addr.getRef());
            getUnknownAddrVal((UnknownAddress) addr.getRef());
        } else {
            throw new IllegalArgumentException("Invalid Unknown Address");
        }
    }

    private void getUnknownAddrVal(UnknownAddress addr) {
        if(addr.getRefType() == RefType.ARRLEN) {
            Machine.emit(Machine.Prim.arraylen);
            return;
        }
        if(addr.getRefType() == RefType.FIELD) {
            Machine.emit(Machine.Op.LOADL, addr.getOffset());
            Machine.emit(Machine.Prim.fieldref);
        } else if(addr.getRefType() == RefType.ARRAY) {
            Machine.emit(Machine.Op.LOADL, addr.getOffset());
            Machine.emit(Machine.Prim.arrayref);
        } else if(addr.getRefType() == RefType.ADDR) {
            return;
        } else {
            Machine.emit(Machine.Op.LOADL, addr.getOffset());
            Machine.emit(Machine.Prim.add);
            Machine.emit(Machine.Op.LOADI, addr.getTargetSize(), 0, 0);
        }
    }

    private void callMethod(MethodDecl decl, RuntimeEntity entity) {
        int callAddr;
        if(entity instanceof UnknownAddress) {
            UnknownAddress unknownAddress = (UnknownAddress) entity;
            resolveUnknownAddress(unknownAddress);
            callAddr = Machine.nextInstrAddr();
            Machine.emit(Machine.Op.CALLI, 0);
        } else {
            if(decl.isStatic) {
                callAddr = Machine.nextInstrAddr();
                Machine.emit(Machine.Op.CALL, 0);
            } else {
                Machine.emit(Machine.Op.LOADA, Machine.Reg.OB, 0);
                callAddr = Machine.nextInstrAddr();
                Machine.emit(Machine.Op.CALLI, 0);
            }
        }
        patchAddress.add(new PatchInfo(decl, callAddr));
    }

    private void getArrAddress(Reference ref) {
        RuntimeEntity entity = (RuntimeEntity) ref.visit(this, null);
        if(entity instanceof KnownAddress) {
            KnownAddress kAddr = (KnownAddress) entity;
            Machine.emit(Machine.Op.LOAD, entity.getSize(), kAddr.getAddress().getRegister(), kAddr.getAddress().getOffset());
        } else if(entity instanceof UnknownAddress) {
            UnknownAddress uaddr = (UnknownAddress) entity;
            resolveUnknownAddress(uaddr);
        }
    }

    private static class MainInfo {

        public ClassDecl mainClass;
        public MethodDecl mainMethod;

        public MainInfo(ClassDecl mainClass, MethodDecl mainMethod) {
            this.mainClass = mainClass;
            this.mainMethod = mainMethod;
        }
    }

    private static class OpsInfo {
        public int size;
        public boolean unary;

        public OpsInfo(int size, boolean unary) {
            this.size = size;
            this.unary = unary;
        }
    }

    private static class PatchInfo {

        public Declaration decl;
        public int instruction;

        public PatchInfo(Declaration decl, int instruction) {
            this.decl = decl;
            this.instruction = instruction;
        }
    }

    private static class PatchRaw {
        public int instruction;
        public int number;

        public PatchRaw(int instruction, int number) {
            this.instruction = instruction;
            this.number = number;
        }
    }

    private static class ActivationFrame {

        private int allocatedVar;
        private int argCount;

        public ActivationFrame(int allocatedVar, int argCount) {
            this.allocatedVar = allocatedVar;
            this.argCount = argCount;
        }

        public int getAllocatedVarAndIncrement(int amount) {
            int old = allocatedVar;
            allocatedVar += amount;
            return old;
        }

        public int getArgCount() {
            return argCount;
        }
    }

}
