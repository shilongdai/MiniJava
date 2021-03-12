package net.viperfish.minijava.scanner;

public class ParsingException extends Exception {

    private SourcePosition position;

    public ParsingException(String message, SourcePosition position) {
        super(message);
        this.position = position;
    }

    public SourcePosition getPosition() {
        return position;
    }
}
