package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.Package;
import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenType;

import java.util.*;

public class IdentificationVisitor implements Visitor<FilterableIdentificationTable, Object> {

    private List<ContextualErrors> errors;
    private static final int MAX_LEVEL_OVERRIDE = 3;

    private static final String CURRENT_CLASS = "-this.class";
    private static final String THIS_FIELD = "-this";

    public IdentificationVisitor() {
        this.errors = new ArrayList<>();
    }

    public List<ContextualErrors> getErrors() {
        return new ArrayList<>(errors);
    }

    @Override
    public Object visitPackage(Package prog, FilterableIdentificationTable arg) {
        arg.openScope();
        for (ClassDecl c : prog.classDeclList) {
            Declaration conflict = checkCurrentConflict(c.name, arg);
            if (conflict != null) {
                duplicateError(c, conflict);
            }
            arg.registerDeclaration(c.name, c);
        }
        for(ClassDecl c : prog.classDeclList) {
            for(FieldDecl fieldDecl : c.fieldDeclList) {
                this.visitFieldDecl(fieldDecl, arg);
            }
        }

        for (ClassDecl c : prog.classDeclList) {
            this.visitClassDecl(c, arg);
        }
        arg.closeScope();
        return null;
    }

    @Override
    public Object visitClassDecl(ClassDecl cd, FilterableIdentificationTable arg) {
        visitCorrectType(cd.type, arg);
        arg.openScope();
        for (MemberDecl decl : cd.fieldDeclList) {
            Declaration conflict = checkCurrentConflict(decl.name, arg);
            if (conflict != null) {
                duplicateError(decl, conflict);
                return null;
            }
            arg.registerDeclaration(decl.name, decl);
        }
        for (MemberDecl decl : cd.methodDeclList) {
            Declaration conflict = checkCurrentConflict(decl.name, arg);
            if (conflict != null) {
                duplicateError(decl, conflict);
                return null;
            }
            arg.registerDeclaration(decl.name, decl);
        }
        arg.registerDeclaration(THIS_FIELD, generateThis(cd));
        arg.registerDeclaration(CURRENT_CLASS, cd);

        for (MethodDecl decl : cd.methodDeclList) {
            this.visitMethodDecl(decl, arg);
        }
        arg.closeScope();
        return null;
    }

    @Override
    public Object visitFieldDecl(FieldDecl fd, FilterableIdentificationTable arg) {
        TypeDenoter type = fd.type;
        visitCorrectType(type, arg);
        return null;
    }

    @Override
    public Object visitMethodDecl(MethodDecl md, FilterableIdentificationTable arg) {
        visitCorrectType(md.type, arg);
        arg.openScope();
        for (ParameterDecl param : md.parameterDeclList) {
            this.visitParameterDecl(param, arg);
        }
        arg.openScope();
        if(md.isStatic) {
            arg = arg.filterTable(new FilterNonStaticIdFilter());
        }
        for (Statement stmt : md.statementList) {
            this.visitCorrectStmt(stmt, arg);
        }
        arg.closeScope();
        arg.closeScope();
        return null;
    }

    @Override
    public Object visitParameterDecl(ParameterDecl pd, FilterableIdentificationTable arg) {
        visitCorrectType(pd.type, arg);
        Declaration conflict = checkCurrentConflict(pd.name, arg);
        if (conflict != null) {
            duplicateError(pd, conflict);
            return null;
        }
        arg.registerDeclaration(pd.name, pd);
        return null;
    }

    @Override
    public Object visitVarDecl(VarDecl decl, FilterableIdentificationTable arg) {
        visitCorrectType(decl.type, arg);
        Declaration conflict = checkConflictLeveled(decl.name, MAX_LEVEL_OVERRIDE, arg);
        if (conflict != null) {
            duplicateError(decl, conflict);
            return null;
        }
        arg.registerDeclaration(decl.name, decl);
        return null;
    }

    @Override
    public Object visitBaseType(BaseType type, FilterableIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitClassType(ClassType type, FilterableIdentificationTable arg) {
        arg = arg.filterTable(new ClassTypeIdFilter());
        this.visitIdentifier(type.className, arg);
        return null;
    }

    @Override
    public Object visitArrayType(ArrayType type, FilterableIdentificationTable arg) {
        visitCorrectType(type.eltType, arg);
        return null;
    }

    @Override
    public Object visitBlockStmt(BlockStmt stmt, FilterableIdentificationTable arg) {
        arg.openScope();
        for (Statement s : stmt.sl) {
            this.visitCorrectStmt(s, arg);
        }
        arg.closeScope();
        return null;
    }

    @Override
    public Object visitVardeclStmt(VarDeclStmt stmt, FilterableIdentificationTable arg) {
        // ensures that can't use the declaration in the initExp
        this.visitVarDecl(stmt.varDecl, arg);
        Collection<Declaration> decls = this.visitCorrectExp(stmt.initExp, arg);
        if(decls.contains(stmt.varDecl)) {
            referenceDeclaringVariable(stmt.varDecl);
        }
        return null;
    }

    @Override
    public Object visitAssignStmt(AssignStmt stmt, FilterableIdentificationTable arg) {
        this.visitCorrectRef(stmt.ref, arg);
        this.visitCorrectExp(stmt.val, arg);
        return null;
    }

    @Override
    public Object visitIxAssignStmt(IxAssignStmt stmt, FilterableIdentificationTable arg) {
        this.visitCorrectRef(stmt.ref, arg);
        this.visitCorrectExp(stmt.ix, arg);
        this.visitCorrectExp(stmt.exp, arg);
        return null;
    }

    @Override
    public Object visitCallStmt(CallStmt stmt, FilterableIdentificationTable arg) {
        this.visitCorrectRef(stmt.methodRef, arg);
        for (Expression expr : stmt.argList) {
            this.visitCorrectExp(expr, arg);
        }
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt stmt, FilterableIdentificationTable arg) {
        if (stmt.returnExpr != null) {
            this.visitCorrectExp(stmt.returnExpr, arg);
        }
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt stmt, FilterableIdentificationTable arg) {
        this.visitCorrectExp(stmt.cond, arg);
        if (checkIfSoleDecl(stmt.thenStmt)) {
            soleDeclareError(stmt.thenStmt);
            return null;
        }
        if (checkIfSoleDecl(stmt.elseStmt)) {
            soleDeclareError(stmt.elseStmt);
            return null;
        }

        this.visitCorrectStmt(stmt.thenStmt, arg);
        if (stmt.elseStmt != null) {
            this.visitCorrectStmt(stmt.elseStmt, arg);
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt stmt, FilterableIdentificationTable arg) {
        this.visitCorrectExp(stmt.cond, arg);
        if (checkIfSoleDecl(stmt.body)) {
            soleDeclareError(stmt.body);
            return null;
        }
        this.visitCorrectStmt(stmt.body, arg);
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr expr, FilterableIdentificationTable arg) {
        this.visitOperator(expr.operator, arg);
        return this.visitCorrectExp(expr.expr, arg);
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr expr, FilterableIdentificationTable arg) {
        Collection<Declaration> declarations = this.visitCorrectExp(expr.left, arg);
        this.visitOperator(expr.operator, arg);
        declarations.addAll(this.visitCorrectExp(expr.right, arg));
        return declarations;
    }

    @Override
    public Object visitRefExpr(RefExpr expr, FilterableIdentificationTable arg) {
        this.visitCorrectRef(expr.ref, arg);
        if(expr.ref.dominantDecl != null) {
            return new HashSet<>(Collections.singletonList(expr.ref.dominantDecl));
        } else {
            return new HashSet<>();
        }
    }

    @Override
    public Object visitIxExpr(IxExpr expr, FilterableIdentificationTable arg) {
        Collection<Declaration> collection = this.visitCorrectExp(expr.ixExpr, arg);
        this.visitCorrectRef(expr.ref, arg);
        if(expr.ref.dominantDecl != null) {
            collection.add(expr.ref.dominantDecl);
        }
        return collection;
    }

    @Override
    public Object visitCallExpr(CallExpr expr, FilterableIdentificationTable arg) {
        Set<Declaration> referencedDecl = new HashSet<>();

        this.visitCorrectRef(expr.functionRef, arg);
        for (Expression exp : expr.argList) {
            referencedDecl.addAll(this.visitCorrectExp(exp, arg));
        }
        return referencedDecl;
    }

    @Override
    public Collection<Declaration> visitLiteralExpr(LiteralExpr expr, FilterableIdentificationTable arg) {
        this.visitCorrectTerminal(expr.lit, arg);
        return new ArrayList<>();
    }

    @Override
    public Object visitNewObjectExpr(NewObjectExpr expr, FilterableIdentificationTable arg) {
        this.visitCorrectType(expr.classtype, arg);
        return new HashSet<>();
    }

    @Override
    public Object visitNewArrayExpr(NewArrayExpr expr, FilterableIdentificationTable arg) {
        this.visitCorrectType(expr.eltType, arg);
        return this.visitCorrectExp(expr.sizeExpr, arg);
    }

    @Override
    public Object visitNullExpr(NullExpr expr, FilterableIdentificationTable arg) {
        return new HashSet<>();
    }

    @Override
    public Object visitThisRef(ThisRef ref, FilterableIdentificationTable arg) {
        ref.dominantDecl = arg.getDeclaration(THIS_FIELD);
        if(ref.dominantDecl == null) {
            this.undefinedSymbolError(new Identifier(new Token(TokenType.THIS, "this", ref.posn)));
        }
        return null;
    }

    @Override
    public Object visitIdRef(IdRef ref, FilterableIdentificationTable arg) {
        this.visitIdentifier(ref.id, arg);
        ref.dominantDecl = ref.id.dominantDecl;
        return null;
    }

    @Override
    public Object visitQRef(QualRef ref, FilterableIdentificationTable arg) {
        this.visitCorrectRef(ref.ref, arg);
        if(ref.ref.dominantDecl == null) {
            return null;
        }
        if(ref.ref.dominantDecl.type != null && ref.ref.dominantDecl.type.typeKind == TypeKind.ARRAY) {
            return handleArrayLength(ref, arg);
        } else {
            return handleRegularQRef(ref, arg);
        }
    }

    @Override
    public Object visitIdentifier(Identifier id, FilterableIdentificationTable arg) {
        Declaration decl = arg.getDeclaration(id.spelling);
        if (decl == null) {
            undefinedSymbolError(id);
        }
        id.dominantDecl = decl;
        return null;
    }

    @Override
    public Object visitOperator(Operator op, FilterableIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitIntLiteral(IntLiteral num, FilterableIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitBooleanLiteral(BooleanLiteral bool, FilterableIdentificationTable arg) {
        return null;
    }

    @Override
    public Object visitNullLiteral(NullLiteral bool, FilterableIdentificationTable arg) {
        return null;
    }

    private void duplicateError(Declaration newDec, Declaration duplicate) {
        String msg = null;
        if(duplicate.posn != null) {
            msg = String.format("Duplicate declaration. Attempting to declare %s, but %s already declared at line %d", newDec.name, newDec.name, duplicate.posn.getLineNumber());
        } else {
            msg = String.format("Duplicate declaration. Attempting to declare %s, but %s already declared at as built-in", newDec.name, newDec.name);
        }
        ContextualErrors error = new ContextualErrors(newDec.posn, msg);
        this.errors.add(error);
    }

    private void soleDeclareError(Statement stmt) {
        this.errors.add(new ContextualErrors(stmt.posn, "Declaration not allowed here"));
    }

    private void undefinedSymbolError(Identifier id) {
        this.errors.add(new ContextualErrors(id.posn, String.format("Unknown symbol %s", id.spelling)));
    }

    private void notQualifiedError(Reference ref) {
        this.errors.add(new ContextualErrors(ref.posn, "not a qualified reference"));
    }

    private void referenceDeclaringVariable(VarDecl decl) {
        this.errors.add(new ContextualErrors(decl.posn, "Cannot reference the variable currently being declared"));
    }

    private Declaration checkCurrentConflict(String id, FilterableIdentificationTable table) {
        LeveledDecl decl = table.getLevelDeclaration(id);
        if (decl != null) {
            int level = decl.getLevel();
            if (level == table.currentLevel()) {
                return decl.getDec();
            }
            return null;
        } else {
            return null;
        }
    }

    private Declaration checkConflictLeveled(String id, int minLevel, FilterableIdentificationTable table) {
        LeveledDecl decl = table.getLevelDeclaration(id);
        if (decl != null) {
            int level = decl.getLevel();
            if (level >= minLevel) {
                return decl.getDec();
            }
            return null;
        } else {
            return null;
        }
    }

    private Object visitCorrectType(TypeDenoter type, FilterableIdentificationTable arg) {
        return VisitorUtils.visitCorrectType(this, arg, type);
    }

    private Object visitCorrectStmt(Statement stmt, FilterableIdentificationTable arg) {
        return VisitorUtils.visitCorrectStmt(this, arg, stmt);
    }

    private Collection<Declaration> visitCorrectExp(Expression exp, FilterableIdentificationTable arg) {
        return (Collection<Declaration>) VisitorUtils.visitCorrectExp(this, arg, exp);
    }

    private Object visitCorrectRef(Reference ref, FilterableIdentificationTable arg) {
        return VisitorUtils.visitCorrectRef(this, arg, ref);
    }

    private Object visitCorrectTerminal(Terminal terminal, FilterableIdentificationTable arg) {
        return VisitorUtils.visitCorrectTerminal(this, arg, terminal);
    }

    private boolean checkIfSoleDecl(Statement stmt) {
        if (stmt == null) {
            return false;
        }
        return stmt instanceof VarDeclStmt;
    }

    private boolean accessible(MemberDecl d, Reference srcRef,  ClassDecl declClass, LeveledIdentificationTable arg) {
        ClassDecl currentClass = (ClassDecl) arg.getDeclaration(CURRENT_CLASS);

        boolean pass = true;

        if (!currentClass.name.equals(declClass.name)) {
            pass = !d.isPrivate;
        }

        if(srcRef.dominantDecl instanceof ClassDecl) {
            pass = pass && d.isStatic;
        }

        return pass;
    }

    private Object handleArrayLength(QualRef ref, FilterableIdentificationTable arg) {
        if(!ref.id.spelling.equals("length")) {
            undefinedSymbolError(ref.id);
            return null;
        }

        FieldDecl lengthDecl = new FieldDecl(false, false, new BaseType(TypeKind.INT, null), "length", null);
        ref.id.dominantDecl = lengthDecl;
        ref.dominantDecl = lengthDecl;
        return null;
    }

    private Object handleRegularQRef(QualRef ref, FilterableIdentificationTable arg) {
        Declaration qualRefDecl = ref.ref.dominantDecl;
        ClassDecl qualClass = getOriginalClass(qualRefDecl);
        if (qualClass != null) {
            Declaration target = null;
            for (MemberDecl d : qualClass.methodDeclList) {
                if (accessible(d, ref.ref, qualClass, arg) && d.name.equals(ref.id.spelling)) {
                    target = d;
                    break;
                }
            }
            if (target == null) {
                for (MemberDecl d : qualClass.fieldDeclList) {
                    if (accessible(d, ref.ref, qualClass, arg) && d.name.equals(ref.id.spelling)) {
                        target = d;
                        break;
                    }
                }
            }
            FilterableIdentificationTable table = new ListLeveldIdentificationTable();
            table.registerDeclaration(ref.id.spelling, target);
            this.visitIdentifier(ref.id, table);
            ref.dominantDecl = ref.id.dominantDecl;
        } else {
            notQualifiedError(ref.ref);
        }
        return null;
    }

    private ClassDecl getOriginalClass(Declaration dec) {
        if(dec == null) {
            return null;
        }
        if (dec instanceof ClassDecl) {
            return (ClassDecl) dec;
        }
        if (dec instanceof ParticularDecl) {
            return ((ParticularDecl) dec).getClassDecl();
        }
        return null;
    }

    private Declaration generateThis(ClassDecl current) {
        Identifier thisId = new Identifier(new Token(TokenType.THIS, "this", null));
        thisId.dominantDecl = current;
        TypeDenoter thisType = new ClassType(thisId, null);
        FieldDecl thisField = new FieldDecl(true, false, thisType, "this", null);
        return thisField;
    }

}
