package net.viperfish.minijava.ast;

import java.util.Objects;

public class StaticCode extends RuntimeEntity {

    private Address address;

    public StaticCode(Address address) {
        super(0);
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticCode that = (StaticCode) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
