/**
 * Should fail. Cannot down cast
 * Error on line 58
 */

class Store {

    public void feed() {
        System.out.println(-1);
    }

    public void seed() {
        System.out.println(-2);
    }

}

class Chuck extends Store {

    public static Chuck INSTANCE;

    public void feed() {
        System.out.println(1);
    }

    public Store replacedBy() {
        return Sneed.INSTANCE;
    }

    public void seed() {
        System.out.println(2);
    }


}

class Sneed extends Chuck {

    public static Sneed INSTANCE;

    public void feed() {
        System.out.println(3);
    }

    public void seed() {
        System.out.println(4);
    }

    public Store formerly() {
        return Chuck.INSTANCE;
    }
}

class Main {

    public static void main(String[] argv) {
        Chuck c = new Chuck();
        Sneed s = c;
    }

}