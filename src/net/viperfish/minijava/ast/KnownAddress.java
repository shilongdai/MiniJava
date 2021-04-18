package net.viperfish.minijava.ast;

import java.util.Objects;

public class KnownAddress extends RuntimeEntity {

    private RuntimeAddress address;
    private boolean stored;

    public KnownAddress(int size, RuntimeAddress address, boolean stored) {
        super(size);
        this.address = address;
        this.stored = stored;
    }

    public RuntimeAddress getAddress() {
        return address;
    }

    public boolean isStored() {
        return stored;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnownAddress that = (KnownAddress) o;
        return stored == that.stored &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, stored);
    }
}
