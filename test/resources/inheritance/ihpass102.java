/**
 * Should pass. Test variable hidding.
 * Output: 1 2 1 1 2
 */


class A {
    int id;

    public int getId() {
        return this.id;
    }
}

class B extends A {
    int id;

    public int getId() {
        return id;
    }

}

class C {

    public static void main(String[] argv) {
        B b = new B();
        A a = b;

        b.id = 1;
        System.out.println(b.getId());
        a.id = 2;
        System.out.println(a.id);
        System.out.println(b.getId());
        System.out.println(a.getId());
        System.out.println(a.id);
    }

}