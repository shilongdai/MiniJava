package net.viperfish.minijava.ident;

import java.util.ArrayList;
import java.util.List;

public class IdentificationErrorException extends Exception {

    private List<ContextualErrors> errors;

    public IdentificationErrorException(List<ContextualErrors> errors) {
        super("Identification error occurred");
        this.errors = new ArrayList<>(errors);
    }

    public List<ContextualErrors> getErrors() {
        return errors;
    }
}
