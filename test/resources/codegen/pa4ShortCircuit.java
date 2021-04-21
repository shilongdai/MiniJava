
/*************
 *  mainclass
 */
class PA4Test
{
    public static void main(String[] args)
    {
        A nullPtr = null;
        if(1 == 1 || nullPtr.n == 2 && 2 == 2) {
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

}