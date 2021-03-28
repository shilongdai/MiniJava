package net.viperfish.minijava.context;

import java.util.ArrayList;
import java.util.List;

public class ContextAnalysisErrorException extends Exception {

    private List<ContextualErrors> errors;

    public ContextAnalysisErrorException(List<ContextualErrors> errors) {
        super("Identification error occurred");
        this.errors = new ArrayList<>(errors);
    }

    public List<ContextualErrors> getErrors() {
        return errors;
    }
}
