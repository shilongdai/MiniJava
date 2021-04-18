package net.viperfish.minijava.ast;

import net.viperfish.minijava.mJAM.Machine;

import java.util.Objects;

public class RuntimeAddress extends Address {

    private Machine.Reg register;

    public RuntimeAddress(Machine.Reg register, short offset) {
        super(offset);
        this.register = register;
    }

    public Machine.Reg getRegister() {
        return register;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RuntimeAddress that = (RuntimeAddress) o;
        return register == that.register;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), register);
    }
}
