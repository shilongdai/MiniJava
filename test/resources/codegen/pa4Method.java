
/*************
 *  mainclass
 */
class PA4Test
{
    public static void main(String[] args)
    {
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

        /* 9: array creation and length */
        int [] aa = new int [4];
        x = aa.length;
        System.out.println(2*x + 1);

        /* 10: array reference and update */
        aa[0] = 0;
        i = 1;
        while (i < aa.length) {
            aa[i] = aa[i-1] + i;
            i = i + 1;
        }
        x = aa[3] + 4;
        System.out.println(x);

        /* 11: simple method invocation */
        a.start();

        A nullPtr = null;
        if(1 == 1 || nullPtr.n == 2) {
            System.out.println(16);
        } else {
            System.out.println(-1);
        }

        if(1 == 2 && nullPtr.n == 2) {
            System.out.println(-2);
        } else {
            System.out.println(17);
        }

        int j = 18;
        while(j < 9999) {
            int p = j;
            j = j + 1;
            System.out.println(p);
        }

        /* end of test */
        int y = 9998 + 1;
        int z = y * 2 / 2 + 1;
        System.out.println(y);
        System.out.println(z);
    }
}

/**********************************************************************
 *
 *  class A
 */
class A
{

    int n;
    B b;

    public void start(){
        int x = 11;
        System.out.println(x);

        /* 12: field ref */
        b.a = this;
        n = 12;
        x = b.a.n;
        System.out.println(x);

        /* 13: complex method invocation */
        n = 4;
        x = 2 + foo(3,4);
        System.out.println(x);

        /* 14: recursion */
        System.out.println(8 + b.fact(3));

        /* 15: object-values */
        this.n = 4;
        b.n = 5;
        System.out.println(2 + this.goo(this,this.b));
    }

    public int foo(int x, int y) {
        return (n + x + y);
    }

    public int goo(A a, B bb) {
        return (a.n + bb.n + this.n);
    }

}


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