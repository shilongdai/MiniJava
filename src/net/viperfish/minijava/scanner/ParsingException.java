package net.viperfish.minijava.scanner;

public class ParsingException extends Exception {

    private int position;

    public ParsingException(String message, int position) {
        super(message);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
