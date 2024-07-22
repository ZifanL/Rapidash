package trees;

public class NewTest {

    public static void main(String[] args) {
        Integer[] randomInts = new Integer[]{1,1,2,2,3,4,4,5,5,5,6};
        AVLTree tree = new AVLTree();
        for (int i = 0; i < randomInts.length; i++) {
            System.out.println("=========== " + randomInts[i] + " ===========");
            tree.insert(randomInts[i]);
//            tree.printTree();
        }
        System.out.println(tree.count(2, "<"));
    }

}
