package net.viperfish.minijava.context;

import net.viperfish.minijava.ast.Package;

public class ContextAnalyzer {

    public Package identification(Package program) throws ContextAnalysisErrorException {
        IdentificationVisitor visitor = new IdentificationVisitor();
        FilterableIdentificationTable identificationTable = new ListLeveldIdentificationTable();
        visitor.visitPackage(program, identificationTable);
        if(!visitor.getErrors().isEmpty()) {
            throw new ContextAnalysisErrorException(visitor.getErrors());
        }
        return program;
    }

    public Package typeChecking(Package program) throws ContextAnalysisErrorException {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        typeCheckVisitor.visitPackage(program, null);
        if(!typeCheckVisitor.getErrors().isEmpty()) {
            throw new ContextAnalysisErrorException(typeCheckVisitor.getErrors());
        }
        return program;
    }

    public Package analyze(Package program) throws ContextAnalysisErrorException {
        program = identification(program);
        program = typeChecking(program);
        return program;
    }

}
