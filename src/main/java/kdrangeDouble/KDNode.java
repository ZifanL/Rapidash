package kdrangeDouble;

import java.util.List;

// K-D Tree node class

class KDNode<T> {

    // these are seen by KDTree
    protected HPoint k;
    T v;
    protected KDNode<T> left, right;
    protected boolean deleted;
    
    protected int count;
    // exclusive
    protected double[] min;
    // inclusive
    protected double[] max;

    // Method ins translated from 352.ins.c of Gonnet & Baeza-Yates
    protected static <T> int edit(HPoint key, Editor<T> editor, KDNode<T> t, int lev, int K)
 	throws KeyDuplicateException {
        KDNode<T> next_node = null;
        int next_lev = (lev+1) % K;
        t.count += 1;
            if (key.equals(t.k)) {
                boolean was_deleted = t.deleted;
                //t.v = editor.edit(t.deleted ? null : t.v );
                t.deleted = (t.v == null);

                if (t.deleted == was_deleted) {
                    return 0;
                } else if (was_deleted) {
                    return -1;
                }
                return 1;
            } else if (key.coord[lev] > t.k.coord[lev]) {
                next_node = t.right;
                if (next_node == null) {
                    t.right = create(key, editor);
                    for (int l = 0; l < K; l++) {
                    	t.right.min[l] = t.min[l];
                    	t.right.max[l] = t.max[l];
                    }
                    
                    t.right.min[lev] = t.k.coord[lev];
                    
                    return t.right.deleted ? 0 : 1;
                }                
            }
            else {
                next_node = t.left;
                if (next_node == null) {
                    t.left = create(key, editor);
                    for (int l = 0; l < K; l++) {
                       	t.left.min[l] = t.min[l];
                    	t.left.max[l] = t.max[l];
                    }
                    t.left.max[lev] = t.k.coord[lev];
                    return t.left.deleted ? 0 : 1;
                }                
            }


        return edit(key, editor, next_node, next_lev, K);
    }

    protected static <T> KDNode<T> create(HPoint key, Editor<T> editor)
        throws KeyDuplicateException {
        KDNode<T> t = new KDNode<T>(key, editor.edit(null));
        if (t.v == null) {
            t.deleted = true;
        }
        return t;            
    }

    protected static <T> boolean del(KDNode<T> t) {
        synchronized (t) {
            if (!t.deleted) {
                t.deleted = true;
                return true;
            }
        }
        return false;
    }

    // Method srch translated from 352.srch.c of Gonnet & Baeza-Yates
    protected static <T> KDNode<T> srch(HPoint key, KDNode<T> t, int K) {

	for (int lev=0; t!=null; lev=(lev+1)%K) {

	    if (!t.deleted && key.equals(t.k)) {
		return t;
	    }
	    else if (key.coord[lev] > t.k.coord[lev]) {
		t = t.right;
	    }
	    else {
		t = t.left;
	    }
	}

	return null;
    }

    // Method rsearch translated from 352.range.c of Gonnet & Baeza-Yates
    protected static <T> void rsearch(HPoint lowk, HPoint uppk, KDNode<T> t, int lev,
				  int K, List<KDNode<T>> v) {

	if (t == null) return;
	if (lowk.coord[lev] <= t.k.coord[lev]) {
	    rsearch(lowk, uppk, t.left, (lev+1)%K, K, v);
	}
        if (!t.deleted) {
            int j = 0;
            while (j<K && lowk.coord[j]<=t.k.coord[j] && 
                   uppk.coord[j]>=t.k.coord[j]) {
                j++;
            }
            if (j==K) v.add(t);
        }
	if (uppk.coord[lev] > t.k.coord[lev]) {
	    rsearch(lowk, uppk, t.right, (lev+1)%K, K, v);
	}
    }
    
    // Method rsearch translated from 352.range.c of Gonnet & Baeza-Yates
    protected static <T> int rcount(HPoint lowk, HPoint uppk, KDNode<T> t, int lev, int K) {
    	if (t == null) return 0;
    	for (int l = 0; l < K; l++) {
    		if (lowk.coord[l] > t.max[l] || uppk.coord[l] <= t.min[l]) return 0;
    	}
    	
    	if (!t.deleted) {
	        int j = 0;
	        while (j<K && lowk.coord[j] <= t.min[j] && 
	               uppk.coord[j] >= t.max[j]) {
	            j++;
	        }
	        if (j==K) return t.count;
    	}
    	
    	int countInRange = 0;
        if (!t.deleted) {
            int j = 0;
            while (j<K && lowk.coord[j]<=t.k.coord[j] && 
                   uppk.coord[j]>=t.k.coord[j]) {
                j++;
            }
            if (j==K) countInRange += 1;
        }
        
        return countInRange + rcount(lowk, uppk, t.left, (lev+1)%K, K) + rcount(
        		lowk, uppk, t.right, (lev+1)%K, K);
    }


    // constructor is used only by class; other methods are static
    private KDNode(HPoint key, T val) {	
		k = key;
		v = val;
		left = null;
		right = null;
		deleted = false;
		count = 1;
		min = new double[key.coord.length];
		max = new double[key.coord.length];
		for (int l = 0; l < key.coord.length; l++) {
			min[l] = Integer.MIN_VALUE;
			max[l] = Integer.MAX_VALUE;
		}
    }

    protected String toString(int depth) {
	String s = k + "  " + v + (deleted ? "*" : "");
	if (left != null) {
	    s = s + "\n" + pad(depth) + "L " + left.toString(depth+1);
	}
	if (right != null) {
	    s = s + "\n" + pad(depth) + "R " + right.toString(depth+1);
	}
	return s;
    }

    private static String pad(int n) {
	String s = "";
	for (int i=0; i<n; ++i) {
	    s += " ";
	}
	return s;
    }

    private static void hrcopy(HRect hr_src, HRect hr_dst) {
	hpcopy(hr_src.min, hr_dst.min);
	hpcopy(hr_src.max, hr_dst.max);
    }

    private static void hpcopy(HPoint hp_src, HPoint hp_dst) {
	for (int i=0; i<hp_dst.coord.length; ++i) {
	    hp_dst.coord[i] = hp_src.coord[i];
	}
    }
}
