package utils;

// for Ch0607Pair
//7. Implement a class Pair<E> that stores a pair of elements of type E. Provide
//accessors to get the first and second element.

// References used for paper size data:
// https://en.wikipedia.org/wiki/ISO_216
// https://en.wikipedia.org/wiki/Paper_size
// http://www.cl.cam.ac.uk/~mgk25/iso-paper.html

public class Pair<E> {
    
    private E first;
    private E second;
    
    public Pair(){};
    
    public Pair(E first, E second) {
        this.first = first;
        this.second = second;
    }

    public E getFirst() {
        return first;
    }

    public void setFirst(E first) {
        this.first = first;
    }

    public E getSecond() {
        return second;
    }

  public void setSecond(E second) {
        this.second = second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Pair(first=");
        builder.append(first);
        builder.append(", second=");
        builder.append(second);
        builder.append(")");
        return builder.toString();
    }
    
    

}
