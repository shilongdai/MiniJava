class Mutator {

    public int mutate(int val) {
        return val;
    }

}

class List {

    public int getSize() {
        return 0;
    }

    public int get(int index) {
        return -1;
    }

    public void append(int val) {
        return;
    }

    public boolean set(int index, int val) {
        return false;
    }

    public void apply(Mutator m) {
        int i = 0;
        while(i < getSize()) {
            set(i, m.mutate(get(i)));
            i = i + 1;
        }
    }
}

class ArrayList extends List {
    private int[] arr;
    private int size;

    public void init(int initSize) {
        arr = new int[initSize];
    }

    public int getSize() {
        return size;
    }

    public int get(int index) {
        return arr[index];
    }

    public void append(int val) {
        if(arr.length == size) {
            resize();
        }
        arr[size] = val;
        size = size + 1;
    }

    public boolean set(int index, int val) {
        if(index == getSize()) {
            append(val);
            return true;
        }
        if(index > size) {
            return false;
        }

        arr[index] = val;
        return true;
    }

    private void resize() {
        int[] old = arr;
        arr = new int[old.length * 2];
        int i = 0;
        while(i < size) {
            arr[i] = old[i];
            i = i + 1;
        }
    }

}

class Node {

    int val;
    Node next;

}

class LinkedList extends List {

    private Node head;
    private int size;

    public int getSize() {
        return size;
    }

    public int get(int index) {
        Node n = traverse(index);
        return n.val;
    }

    public void append(int val) {
        if(head == null) {
            head = new Node();
            head.val = val;
        } else {
            Node last = traverse(size - 1);
            Node newNode = new Node();
            newNode.val = val;
            last.next = newNode;
        }
        size = size + 1;
    }

    public boolean set(int index, int val) {
        if(index == getSize()) {
            append(val);
            return true;
        }
        Node n = traverse(index);
        n.val = val;
        return true;
    }

    private Node traverse(int index) {
        int i = 0;
        Node current = head;
        while (i < index) {
            current = current.next;
            i = i + 1;
        }
        return current;
    }
}

class IncrementMutator extends Mutator {

    public int toAdd;

    public int mutate(int val) {
        return val + toAdd;
    }

}


class Main {

    public static void main(String[] argv) {

        ArrayList l1 = new ArrayList();
        l1.init(2);
        LinkedList l2 = new LinkedList();

        int i = 1;
        while(i <= 5) {
            l1.append(i);
            l2.append(i + 5);
            i = i + 1;
        }

        if(!listEqual(l1, l2)) {
            System.out.println(1);
        } else {
            System.out.println(-1);
        }

        IncrementMutator add = new IncrementMutator();
        add.toAdd = 5;

        l1.apply(add);

        if(listEqual(l1, l2)) {
            System.out.println(2);
        } else {
            System.out.println(-1);
        }

        add.toAdd = -3;
        l2.apply(add);

        i = 0;
        while(i < l2.getSize()) {
            System.out.println(l2.get(i));
            i = i + 1;
        }
    }

    private static boolean listEqual(List l1, List l2) {
        if(l1.getSize() != l2.getSize()) {
            return false;
        }

        int i = 0;
        while(i < l1.getSize()) {
            if(l1.get(i) != l2.get(i)) {
                return false;
            }
            i = i + 1;
        }
        return true;
    }
}