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

        if(x < -1) {
            System.out.println(-1);
        } else {
            System.out.println(4);
        }

        /* 5: repetitive statement */
        int i = 0;
        while (i < 5) {
            i = i + 1;
            x = i;
        }
        System.out.println(x);
    }

}