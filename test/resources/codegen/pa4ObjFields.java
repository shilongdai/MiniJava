class Test {

    public static void main(String[] argv) {
        /* 1: simple literal */
        int x = 1;
        System.out.println(x);

        /* 2: simple expression */
        x = 2 * x + x - 1;
        System.out.println(x);

        /* 3: System.out.println */
        System.out.println(3);

        /* 4: conditional statement */
        if (x != -1)
            System.out.println(4);
        else
            System.out.println(-1);

        /* 5: repetitive statement */
        int i = 0;
        while (i < 5) {
            i = i + 1;
            x = i;
        }
        System.out.println(x);

        /* 6: object creation */
        A a = new A();
        if (a != null)
            System.out.println(6);

        /* 7: field reference */
        x = 7 + a.n;
        System.out.println(x);

        /* 8: qualified reference and update */
        a.b = new B();
        a.b.n = 8;
        System.out.println(a.b.n);
    }

}

class A {

    int n;
    B b;

}

/**********************************************************************
 *
 *  class B
 */
class B
{
    int n;
    A a;

    public int fact(int nn){
        int r = 1;
        if (nn > 1)
            r = nn * fact(nn -1);
        return r;
    }
}