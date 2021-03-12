package net.viperfish.minijava.scanner;

import java.util.Objects;

public class SourcePosition {

    private int absPosition;
    private int lineNumber;

    public SourcePosition(int absPosition, int lineNumber) {
        this.absPosition = absPosition;
        this.lineNumber = lineNumber;
    }

    public int getAbsPosition() {
        return absPosition;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourcePosition that = (SourcePosition) o;
        return absPosition == that.absPosition &&
                lineNumber == that.lineNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(absPosition, lineNumber);
    }
}
