package net.viperfish.minijava.codegen;

class ActivationFrame {

    private int allocatedVar;
    private int argCount;

    public ActivationFrame(int allocatedVar, int argCount) {
        this.allocatedVar = allocatedVar;
        this.argCount = argCount;
    }

    public int getAllocatedVarAndIncrement(int amount) {
        int old = allocatedVar;
        allocatedVar += amount;
        return old;
    }

    public int getArgCount() {
        return argCount;
    }

    public int getAllocatedVar() {
        return this.allocatedVar;
    }
}