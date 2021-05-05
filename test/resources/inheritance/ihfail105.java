/**
 * static access protection. error on line 14.
 */

class B {

    private static int hidden;

}

class A extends B {

    public void test() {
        int c = hidden + 1;
    }

}