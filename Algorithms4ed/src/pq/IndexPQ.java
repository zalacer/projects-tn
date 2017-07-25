package pq;

/*********************************************************************
 *  ftp://ftp.cs.princeton.edu/pub/cs226/map/IndexPQ.java
 *  Indirect priority queue.
 *
 *  The priority queue maintains its own copy of the priorities,
 *  unlike the one in Algorithms in Java.
 *
 *  This code is from "Algorithms in Java, Third Edition,
 *  by Robert Sedgewick, Addison-Wesley, 2003.

 *********************************************************************/

public class IndexPQ {
    private int N;              // number of elements on PQ
    private int[] pq;           // binary heap
    private int[] qp;           //
    private double[] priority;  // priority values

    public IndexPQ(int NMAX) {
        pq = new int[NMAX + 1];
        qp = new int[NMAX + 1];
        priority = new double[NMAX + 1]; 
        N = 0;
    }

    public boolean isEmpty() { return N == 0; }

    // insert element k with given priority
    public void insert(int k, double value) {
        N++;
        qp[k] = N;
        pq[N] = k;
        priority[k] = value;
        fixUp(pq, N);
    }

    // delete and return the minimum element
    public int delMin() { 
        exch(pq[1], pq[N]); 
        fixDown(pq, 1, --N); 
        return pq[N+1]; 
    }

    // change the priority of element k to specified value
    public void change(int k, double value) {
        priority[k] = value;
        fixUp(pq, qp[k]);
        fixDown(pq, qp[k], N);
    }


   /**************************************************************
    * General helper functions
    **************************************************************/
    private boolean greater(int i, int j) {
        return priority[i] > priority[j];
    }

    private void exch(int i, int j) {
        int t = qp[i]; qp[i] = qp[j]; qp[j] = t;
        pq[qp[i]] = i; pq[qp[j]] = j;
    }


   /**************************************************************
    * Heap helper functions
    **************************************************************/
    private void fixUp(int[] a, int k)  {
        while (k > 1 && greater(a[k/2], a[k])) {
            exch(a[k], a[k/2]);
            k = k/2;
        }
    }

    private void fixDown(int[] a, int k, int N) {
        int j;
        while (2*k <= N) {
            j = 2*k;
            if (j < N && greater(a[j], a[j+1])) j++;
            if (!greater(a[k], a[j])) break;
            exch(a[k], a[j]);
            k = j;
        }
    }

}

