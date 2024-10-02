package rangetree;

import java.util.ArrayList;

public class RangeTreeCount {
	private Node root;
	
	public RangeTreeCount() {
		root = null;
	}
	
	public void insert(Point p) {
		if (root == null) {
			root = new Node(p, p.dimension() - 1);
		}
		else {
			root.insert(p);
		}
	}
	
	public long query(Point from, Point to) {
		if (root == null) {
			return 0;
		}
		else {
			return root.query(from, to);
		}
	}
	
	private class Node {
		private final int dimension;
		private int value; // for non-leaf nodes, this value is the minimum value of its right subtree
		
		private int min;
		private int max;
		private Node left;
		private Node right;
		private final Node inner;
		private long count;
		
		public Node(Point p, int dimension) {
			if (dimension == 0) {
				this.inner = null;
				this.count = 1;
			} else {
				this.inner = new Node(p, dimension - 1);
				this.count = 0;
			}
			
			this.dimension = dimension;
			this.value = p.get(dimension);
			this.left = null;
			this.right = null;
			this.min = this.value;
			this.max = this.value;
		}
		
		public Node(Node node) {
			if (node.getInner() == null) {
				this.inner = null;
				this.count = node.getCount();
			} else {
				this.inner = new Node(node.getInner());
			}
			
			if (node.getLeft() == null) {
				this.left = null;
			} else {
				this.left = new Node(node.getLeft());
			}
			
			if (node.getRight() == null) {
				this.right = null;
			} else {
				this.right = new Node(node.getRight());
			}
			this.dimension = node.getDimension();
			this.value = node.getValue();
			this.min = node.getMin();
			this.max = node.getMax();
		}
		
		public long getCount() {
			return count;
		}
		
		public Node getInner() {
			return inner;
		}
		
		public Node getLeft() {
			return left;
		}
		
		public Node getRight() {
			return right;
		}
		
		public int getValue() {
			return value;
		}
		
		public int getMin() {
			return min;
		}
		
		public int getMax() {
			return max;
		}
		
		public int getDimension() {
			return dimension;
		}
		
		public void printNode() {
			printNode(this, "");
		}
		
		private void printNode(Node node, String prefix) {
	        if (node == null) {
	            return;
	        }
	        if (node.count == 0) {
	        	System.out.println(prefix + " + " + node.value);
	        } else {
	        	System.out.print(prefix + " + " + node.value + " : [");
	        	System.out.print(node.count);
	        	System.out.println("]");
	        }
	        printNode(node.left, prefix + " ");
	        printNode(node.right, prefix + " ");
		}
		
		public void insert(Point p) {
			if (left != null) {
				// When the current node is not a leaf node
				if (p.get(dimension) < value) {
					left.insert(p);
					min = left.min;
				} else {
					right.insert(p);
					max = right.max;
				}
			} else {
				// When the current node is a leaf node
				if (p.get(dimension) > value) {
					left = new Node(this);
					right = new Node(p, dimension);
					value = p.get(dimension);
					max = p.get(dimension);
				} else if (p.get(dimension) < value) {
					right = new Node(this);
					left = new Node(p, dimension);
					min = p.get(dimension);
				}
			}
			if (inner != null) {
				inner.insert(p);
			} else {
				count += 1;
			}
		}
		
		public long query(Point from, Point to) {
			
			if (to.get(dimension) < min || from.get(dimension) > max) {
				return 0;
			}
			if (to.get(dimension) == min && !to.getInclusive(dimension)) {
				return 0;
			}
			if (from.get(dimension) == max && !from.getInclusive(dimension)) {
				return 0;
			}
			if ((from.get(dimension) < min || from.get(dimension) == min && from.getInclusive(dimension)) && 
					(to.get(dimension) > max || to.get(dimension) == max && to.getInclusive(dimension))) {
				if (inner == null) {
					return count;
				}
				return inner.query(from, to);
			} else {
				return left.query(from, to) + right.query(from, to);
			}
		}
	}
}
