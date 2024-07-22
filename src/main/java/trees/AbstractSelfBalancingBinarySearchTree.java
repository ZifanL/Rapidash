package trees;

/**
 * Abstract class for self balancing binary search trees. Contains some methods
 * that is used for self balancing trees.
 * 
 * @author Ignas Lelys
 * @created Jul 24, 2011
 * 
 */
public abstract class AbstractSelfBalancingBinarySearchTree extends AbstractBinarySearchTree {

    /**
     * Rotate to the left.
     * 
     * @param node Node on which to rotate.
     * @return Node that is in place of provided node after rotation.
     */
    protected Node rotateLeft(Node node) {
//    	System.out.println("Rotate left: " + node.value);
        Node temp = node.right;
        temp.parent = node.parent;
        int nodeCount = node.count;

        node.right = temp.left;
        node.count -= temp.count;
        if (temp.left != null) {
        	node.count += temp.left.count;
        }
        temp.count = nodeCount;
        
        if (node.right != null) {
            node.right.parent = node;
        }

        temp.left = node;
        node.parent = temp;

        // temp took over node's place so now its parent should point to temp
        if (temp.parent != null) {
            if (node == temp.parent.left) {
                temp.parent.left = temp;
            } else {
                temp.parent.right = temp;
            }
        } else {
            root = temp;
        }
        
        return temp;
    }

    /**
     * Rotate to the right.
     * 
     * @param node Node on which to rotate.
     * @return Node that is in place of provided node after rotation.
     */
    protected Node rotateRight(Node node) {
//    	System.out.println("Rotate right: " + node.value);
        Node temp = node.left;
        temp.parent = node.parent;
        int nodeCount = node.count;

        node.left = temp.right;
        node.count -= temp.count;
        if (temp.right != null) {
        	node.count += temp.right.count;
        }
        temp.count = nodeCount;
        
        if (node.left != null) {
            node.left.parent = node;
        }

        temp.right = node;
        node.parent = temp;

        // temp took over node's place so now its parent should point to temp
        if (temp.parent != null) {
            if (node == temp.parent.left) {
                temp.parent.left = temp;
            } else {
                temp.parent.right = temp;
            }
        } else {
            root = temp;
        }
        
        return temp;
    }

}
