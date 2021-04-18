package net.viperfish.minijava.ast;

import java.util.Objects;

public class Address {

    private int offset;

    public Address(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return offset == address.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset);
    }
}
