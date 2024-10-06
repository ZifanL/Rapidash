// Hamming distance metric class

package kdrange;

class HammingDistance extends DistanceMetric {
    
    protected int distance(int [] a, int [] b)  {

	int dist = 0;

	for (int i=0; i<a.length; ++i) {
	    int diff = (a[i] - b[i]);
	    dist += Math.abs(diff);
	}

	return dist;
    }     
}
