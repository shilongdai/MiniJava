/**
 * Should pass. It checks whether the variables can be inherited successfully.
 * Output: integer values 0, 1, 2, 3, 4, 5
 *
 */


class A {

    public int id;

}

class B extends A {

    int c;

}

class C extends B {

    public void setC(int val) {
        c = val;
    }

    public int getC() {
        return this.c;
    }

}

class D {

    public static void main(String[] argv) {
        C c = new C();
        B b = new B();

        c.id = 0;
        c.c = 1;
        b.id = c.c * 2;
        b.c = b.id + 1;

        System.out.println(c.id);
        System.out.println(c.c);
        System.out.println(b.id);
        System.out.println(b.c);

        A a = c;
        a.id = 4;
        System.out.println(a.id);

        c.setC(a.id + 1);
        System.out.println(c.getC());
    }

}