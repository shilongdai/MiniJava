package net.viperfish.minijava.ast;

import java.util.Objects;

public class KnownValue extends RuntimeEntity {

    private int word;

    public KnownValue(int size, int word) {
        super(size);
        this.word = word;
    }

    public int getWord() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnownValue that = (KnownValue) o;
        return word == that.word;
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }
}

