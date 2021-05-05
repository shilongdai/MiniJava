/**
 * Should fail. It checks whether private vars are hidden.
 * Output: unknown symbol error on line 23
 *
 */


class A {

    public int id;

}

class B extends A {

    private int c;

}

class C extends B {

    public void setC(int val) {
        c = val;
    }

}
