package st;

// based on http://algs4.cs.princeton.edu/35applications/SparseVector.java

public class SparseVectorintdouble {
    private int d;                   // dimension
    private STintdouble st;  // the vector, represented by index-double pairs

    public SparseVectorintdouble(int d) {
        this.d  = d;
        this.st = new STintdouble();
    }

    public void put(int i, double value) {
        if (i < 0 || i >= d) throw new IndexOutOfBoundsException("Illegal index");
        if (value == 0.0) st.delete(i);
        else              st.put(i, value);
    }

    public double get(int i) {
        if (i < 0 || i >= d) throw new IndexOutOfBoundsException("Illegal index");
        if (st.contains(i)) return st.get(i);
        else                return 0.0;
    }

    public int nnz() { return st.size(); }

    public int size() { return d; }

    public int dimension() { return d; }

    public double dot(SparseVectorintdouble that) {
        if (this.d != that.d) 
          throw new IllegalArgumentException("Vector lengths disagree");
        double sum = 0.0;

        // iterate over the vector with the fewest nonzeros
        if (this.st.size() <= that.st.size()) {
            for (int i : this.st.keys())
                if (that.st.contains(i)) sum += this.get(i) * that.get(i);
        }
        else {
            for (int i : that.st.keys())
                if (this.st.contains(i)) sum += this.get(i) * that.get(i);
        }
        return sum;
    }

    public double dot(double[] that) {
        double sum = 0.0;
        for (int i : st.keys())
            sum += that[i] * this.get(i);
        return sum;
    }

    public double magnitude() { return Math.sqrt(this.dot(this)); }

    public double norm() { return Math.sqrt(this.dot(this)); }

    public SparseVectorintdouble scale(double alpha) {
        SparseVectorintdouble c = new SparseVectorintdouble(d);
        for (int i : this.st.keys()) c.put(i, alpha * this.get(i));
        return c;
    }

    public SparseVectorintdouble plus(SparseVectorintdouble that) {
        if (this.d != that.d) throw new IllegalArgumentException("Vector lengths disagree");
        SparseVectorintdouble c = new SparseVectorintdouble(d);
        for (int i : this.st.keys()) c.put(i, this.get(i));                // c = this
        for (int i : that.st.keys()) c.put(i, that.get(i) + c.get(i));     // c = c + that
        return c;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i : st.keys()) {
            s.append("(" + i + ", " + st.get(i) + ") ");
        }
        return s.toString();
    }

    public static void main(String[] args) {
        SparseVectorintdouble a = new SparseVectorintdouble(10);
        SparseVectorintdouble b = new SparseVectorintdouble(10);
        a.put(3, 0.50);
        a.put(9, 0.75);
        a.put(6, 0.11);
        a.put(6, 0.00);
        b.put(3, 0.60);
        b.put(4, 0.90);
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("a dot b = " + a.dot(b));
        System.out.println("a + b   = " + a.plus(b));
    }

}
