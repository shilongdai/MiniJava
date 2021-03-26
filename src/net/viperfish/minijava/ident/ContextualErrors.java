package net.viperfish.minijava.ident;

import net.viperfish.minijava.scanner.SourcePosition;

public class ContextualErrors {

    private SourcePosition position;
    private String msg;

    public ContextualErrors(SourcePosition position, String msg) {
        this.position = position;
        this.msg = msg;
    }

    public SourcePosition getPosition() {
        return position;
    }

    public String getMsg() {
        return msg;
    }

}
