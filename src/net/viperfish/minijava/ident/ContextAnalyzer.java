package net.viperfish.minijava.ident;

import net.viperfish.minijava.ast.Package;

public class ContextAnalyzer {

    public Package analyze(Package program) throws IdentificationErrorException {
        IdentificationVisitor visitor = new IdentificationVisitor();
        FilterableIdentificationTable identificationTable = new ListLeveldIdentificationTable();
        visitor.visitPackage(program, identificationTable);
        if(!visitor.getErrors().isEmpty()) {
            throw new IdentificationErrorException(visitor.getErrors());
        }
        return program;
    }

}
