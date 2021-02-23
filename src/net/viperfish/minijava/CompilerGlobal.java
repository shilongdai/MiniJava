package net.viperfish.minijava;

public final class CompilerGlobal {

    public static final int DEBUG_LEVEL = 2;

    public static final boolean DEBUG_1;

    public static final boolean DEBUG_2;

    public static final boolean DEBUG_3;

    static {
        DEBUG_1 = DEBUG_LEVEL >= 1;
        DEBUG_2 = DEBUG_LEVEL >= 2;
        DEBUG_3 = DEBUG_LEVEL >= 3;
    }

}
