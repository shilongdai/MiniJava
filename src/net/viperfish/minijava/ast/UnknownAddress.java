package net.viperfish.minijava.ast;

import java.util.Objects;

public class UnknownAddress extends RuntimeEntity {

    private RuntimeEntity ref;
    private int offset;
    private int targetSize;
    private RefType refType;

    public UnknownAddress(RuntimeEntity ref, int offset, int targetSize, RefType refType) {
        super(1);
        this.ref = ref;
        this.offset = offset;
        this.targetSize = targetSize;
        this.refType = refType;
    }

    public UnknownAddress(RuntimeEntity ref, int offset, int targetSize) {
        this(ref, offset, targetSize, RefType.PLAIN);
    }

    public UnknownAddress(RuntimeEntity ref) {
        this(ref, 0, 1, RefType.PLAIN);
    }

    public RuntimeEntity getRef() {
        return ref;
    }

    public int getOffset() {
        return offset;
    }

    public int getTargetSize() {
        return targetSize;
    }

    public RefType getRefType() {
        return refType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnknownAddress that = (UnknownAddress) o;
        return offset == that.offset &&
                targetSize == that.targetSize &&
                Objects.equals(ref, that.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ref, offset, targetSize);
    }
}
