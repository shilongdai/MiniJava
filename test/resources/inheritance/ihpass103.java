/**
 * Should pass. Test polymorphism.
 * Output: 1 2 3 4 5 6
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

class Customer {

    public void visit(Store store) {
        store.feed();
        store.seed();
    }

}

class Main {

    public static void main(String[] argv) {
        Customer c = new Customer();
        Chuck chuck = new Chuck();
        chuck.INSTANCE = chuck;
        Sneed sneed = new Sneed();
        Sneed.INSTANCE = sneed;

        c.visit(chuck);
        c.visit(sneed);

        if(chuck.replacedBy() != sneed.formerly()) {
            System.out.println(5);
        }
        Store chuckStore = chuck;
        if(sneed.formerly() == chuckStore) {
            System.out.println(6);
        }
    }

}