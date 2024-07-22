// Hamming distance metric class

package kdrange;

class EuclideanDistance extends DistanceMetric {
    
    protected double distance(double [] a, double [] b)  {
	
	return Math.sqrt(sqrdist(a, b));
	
    }
    
    protected static double sqrdist(double [] a, double [] b) {

	int dist = 0;

	for (int i=0; i<a.length; ++i) {
		double diff = (a[i] - b[i]);
	    dist += diff*diff;
	}

	return dist;
    }     
}
