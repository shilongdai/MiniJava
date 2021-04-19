

class A {

    public static void main(String[] argv) {
        B = 1;
        System.out.println(B);

        B = A.B + 1;
        System.out.println(A.B);

        alpha = new A();
        A.alpha.test3(A.alpha.B, B - 1);
        System.out.println(B);

        beta.alpha.beta.alpha.beta.a = B + 1;
        System.out.println(C.a);

        C.incrementA();
        System.out.println(C.a);

        C.delta = new D();
        C.delta.initArr();

        D.arr = null;
        if(D.arr == null) {
            C.delta.initArr();
            if(D.arr != null) {
                printArr();
            }
        }
    }

    public static int return1() {
        return 1;
    }

    public void test3(int a1, int a2) {
        this.B = this.B * a1 - a2;
    }

    public static void printArr() {
        int i = 0;
        while(i < D.arr.length) {
            System.out.println(D.arr[i]);
            i = i + 1;
        }
    }

    public static A alpha;
    public static C beta;
    private static int B;

}

class C {
    public static int a;
    public static A alpha;
    private static int d;
    public static D delta;

    public static void incrementA(){
        a = a + return2() * alpha.return1();
        a = a - A.return1();
    }

    private static int return2() {
        return 2;
    }
}

class D {

    public static int[] arr;

    public void initArr() {
        arr = new int[100];
        int i = 0;
        while(i < 100) {
            arr[i] = i;
            i = i + 1;
        }
        A.printArr();
    }

}