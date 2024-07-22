package trees;

import java.util.HashMap;
import java.util.Random;

public class TestingTrees {

    private static final int SIZE = 1000;
    private static final int RANDOM_MAX = 100;

    public static void main(String[] args) {
        Random random = new Random(System.currentTimeMillis());
        Integer[] randomInts = new Integer[SIZE];
        HashMap<Integer, Integer> count = new HashMap<>();
        HashMap<Integer, Integer> countLessThan = new HashMap<>();
        HashMap<Integer, Integer> countLessThanOrEq = new HashMap<>();
        HashMap<Integer, Integer> countGreaterThan = new HashMap<>();
        HashMap<Integer, Integer> countGreaterThanOrEq = new HashMap<>();

        for (int k = 0; k < RANDOM_MAX; k++) {
            countLessThan.put(k, 0);
            countLessThanOrEq.put(k, 0);
            countGreaterThan.put(k, 0);
            countGreaterThanOrEq.put(k, 0);
            count.put(k, 0);
        }

        for (int i = 0; i < SIZE; i++) {
            randomInts[i] = random.nextInt(RANDOM_MAX);
            count.put(randomInts[i], count.get(randomInts[i]) + 1);
            for (int k = 0; k < randomInts[i]; k++) {
                countGreaterThan.put(k, countGreaterThan.get(k) + 1);
            }
            for (int k = 0; k <= randomInts[i]; k++) {
                countGreaterThanOrEq.put(k, countGreaterThanOrEq.get(k) + 1);
            }
            for (int k = RANDOM_MAX - 1; k > randomInts[i]; k--) {
                countLessThan.put(k, countLessThan.get(k) + 1);
            }
            for (int k = RANDOM_MAX - 1; k >= randomInts[i]; k--) {
                countLessThanOrEq.put(k, countLessThanOrEq.get(k) + 1);
            }
        }

        AVLTree tree = new AVLTree();
        for (int i = 0; i < SIZE; i++) {
            System.out.println("=========== " + randomInts[i] + " ===========");
            tree.insert(randomInts[i]);
//            tree.printTree();
        }
//        for (int k = 0; k < RANDOM_MAX; k++) {
//        	System.out.println(k + ":" + count.get(k));
//        }

        // tree.printTree();

        for (int k = 0; k < RANDOM_MAX; k++) {
            if (tree.count(k, "<") != countLessThan.get(k)) {
                System.out.println("Not correct for k = " + k + " and <");
                System.out.println(tree.count(k, "<") + " vs " + countLessThan.get(k));
            }
            if (tree.count(k, "<=") != countLessThanOrEq.get(k)) {
                System.out.println("Not correct for k = " + k + " and <=");
                System.out.println(tree.count(k, "<=") + " vs " + countLessThanOrEq.get(k));
            }
            if (tree.count(k, ">") != countGreaterThan.get(k)) {
                System.out.println("Not correct for k = " + k + " and >");
                System.out.println(tree.count(k, ">") + " vs " + countGreaterThan.get(k));
            }
            if (tree.count(k, ">=") != countGreaterThanOrEq.get(k)) {
                System.out.println("Not correct for k = " + k + " and >=");
                System.out.println(tree.count(k, ">=") + " vs " + countGreaterThanOrEq.get(k));
            }
        }
    }

}
