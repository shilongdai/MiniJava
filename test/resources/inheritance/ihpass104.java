/**
 * Should pass. test static method hiding.
 * Output: 1 2 3 4
 */

class A {

    public static int val;

    public static void print2() {
        System.out.println(val);
    }

}

class B extends A {

    public static void print2() {
        System.out.println(val + 1);
    }

}

class Main {

    public static void main(String[] argv) {
        A a = new A();
        B b = new B();

        A.val = 1;
        a.print2();
        B.print2();

        b.val = 3;
        a = b;
        a.print2();
        b.print2();
    }

}