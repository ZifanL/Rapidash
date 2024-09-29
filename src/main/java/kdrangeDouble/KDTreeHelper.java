package kdrangeDouble;

import java.util.LinkedList;
import java.util.List;

/**
* KDTree is a class supporting KD-tree insertion, deletion, equality
* search, range search, and nearest neighbor(s) using int-precision
* floating-point keys.  Splitting dimension is chosen naively, by
* depth modulo K.  Semantics are as follows:
*
* <UL>
* <LI> Two different keys containing identical numbers should retrieve the 
*      same value from a given KD-tree.  Therefore keys are cloned when a 
*      node is inserted.
* <BR><BR>
* <LI> As with Hashtables, values inserted into a KD-tree are <I>not</I>
*      cloned.  Modifying a value between insertion and retrieval will
*      therefore modify the value stored in the tree.
*</UL>
*
* Implements the Nearest Neighbor algorithm (Table 6.4) of
*
* <PRE>
* &#064;techreport{AndrewMooreNearestNeighbor,
    *   author  = {Andrew Moore},
    *   title   = {An introductory tutorial on kd-trees},
    *   institution = {Robotics Institute, Carnegie Mellon University},
    *   year    = {1991},
    *   number  = {Technical Report No. 209, Computer Laboratory, 
    *              University of Cambridge},
    *   address = {Pittsburgh, PA}
* }
* </PRE>
*  
*
* @author      Simon Levy, Bjoern Heckel
* @version     %I%, %G%
* @since JDK1.2 
*/
public class KDTreeHelper<T> {
    // number of milliseconds
    final long m_timeout;
    
    // K = number of dimensions
    final private int m_K;
    
    // root of KD-tree
    private KDNode<T> m_root;
    
    // count of nodes
    private int m_count;
    
    /**
    * Creates a KD-tree with specified number of dimensions.
    *
    * @param k number of dimensions
    */
    public KDTreeHelper(int k) {
        this(k, 0);
    }
    public KDTreeHelper(int k, long timeout) {
        this.m_timeout = timeout;
        m_K = k;
        m_root = null;
    }
    
    /** 
    * Insert a node in a KD-tree.  Uses algorithm translated from 352.ins.c of
    *
    *   <PRE>
    *   &#064;Book{GonnetBaezaYates1991,                                   
	*     author =    {G.H. Gonnet and R. Baeza-Yates},
	*     title =     {Handbook of Algorithms and Data Structures},
	*     publisher = {Addison-Wesley},
	*     year =      {1991}
    *   }
    *   </PRE>
    *
    * @param key key for KD-tree node
    * @param value value at that key
    *
    * @throws KeySizeException if key.length mismatches K
    * @throws KeyDuplicateException if key already in tree
    */
    public void insert(double [] key, T value)
    throws KeySizeException, KeyDuplicateException {
        this.edit(key, new Editor.Inserter<T>(value));
    }
    
    /** 
    * Edit a node in a KD-tree
    *
    * @param key key for KD-tree node
    * @param editor object to edit the value at that key
    *
    * @throws KeySizeException if key.length mismatches K
    * @throws KeyDuplicateException if key already in tree
    */
    
    public void edit(double [] key, Editor<T> editor)
    throws KeySizeException, KeyDuplicateException {
	
	if (key.length != m_K) {
	    throw new KeySizeException();
	}
	
        synchronized (this) {
            // the first insert has to be synchronized
            if (null == m_root) {
                m_root = KDNode.create(new HPoint(key), editor);
                m_count = m_root.deleted ? 0 : 1;
                return;
            }
	}
	
        m_count += KDNode.edit(new HPoint(key), editor, m_root, 0, m_K);
    }
    
    /** 
    * Find  KD-tree node whose key is identical to key.  Uses algorithm 
    * translated from 352.srch.c of Gonnet & Baeza-Yates.
    *
    * @param key key for KD-tree node
    *
    * @return object at key, or null if not found
    *
    * @throws KeySizeException if key.length mismatches K
    */
    public T search(double [] key) throws KeySizeException {
	
	if (key.length != m_K) {
	    throw new KeySizeException();
	}
	
	KDNode<T> kd = KDNode.srch(new HPoint(key), m_root, m_K);
	
	return (kd == null ? null : kd.v);
    }
    
    
    public void delete(double [] key)
    throws KeySizeException, KeyMissingException {
        delete(key, false);
    }
    /** 
    * Delete a node from a KD-tree.  Instead of actually deleting node and
    * rebuilding tree, marks node as deleted.  Hence, it is up to the caller
    * to rebuild the tree as needed for efficiency.
    *
    * @param key key for KD-tree node
    * @param optional  if false and node not found, throw an exception
    *
    * @throws KeySizeException if key.length mismatches K
    * @throws KeyMissingException if no node in tree has key
    */
    public void delete(double [] key, boolean optional)
    throws KeySizeException, KeyMissingException {
	
	if (key.length != m_K) {
	    throw new KeySizeException();
	}
        KDNode<T> t = KDNode.srch(new HPoint(key), m_root, m_K);
        if (t == null) {
            if (optional == false) {
                throw new KeyMissingException();
            }
        }
        else {
            if (KDNode.del(t)) {
                m_count--;
            }
        }
    }


    /** 
    * Range search in a KD-tree.  Uses algorithm translated from
    * 352.range.c of Gonnet & Baeza-Yates.
    *
    * @param lowk lower-bounds for key
    * @param uppk upper-bounds for key
    *
    * @return array of Objects whose keys fall in range [lowk,uppk]
    *
    * @throws KeySizeException on mismatch among lowk.length, uppk.length, or K
    */
    public List<T> range(double [] lowk, double [] uppk)
    throws KeySizeException {
	
	if (lowk.length != uppk.length) {
	    throw new KeySizeException();
	}
	
	else if (lowk.length != m_K) {
	    throw new KeySizeException();
	}
	
	else {
	    List<KDNode<T>> found = new LinkedList<KDNode<T>>();
	    KDNode.rsearch(new HPoint(lowk), new HPoint(uppk), 
	    m_root, 0, m_K, found);
            List<T> o = new LinkedList<T>();
            for (KDNode<T> node : found) {
                o.add(node.v);
	    }
	    return o;
	}
    }
    
    public int rangeCount(double [] lowk, double [] uppk)
    throws KeySizeException {
	
	if (lowk.length != uppk.length) {
	    throw new KeySizeException();
	}
	
	else if (lowk.length != m_K) {
	    throw new KeySizeException();
	}
	
	else {
	    return KDNode.rcount(new HPoint(lowk), new HPoint(uppk), m_root, 0, m_K);
	}
    }
    
    public int size() { /* added by MSL */
        return m_count;
    }
    
    public String toString() {
	return m_root.toString(0);
    }


}

