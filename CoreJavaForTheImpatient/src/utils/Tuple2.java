package utils;

import java.util.stream.Stream;

public class Tuple2<T1, T2> {
    public T1 _1;
    public T2 _2;
    
    public Tuple2(){
        super();
    }
    
    public Tuple2(T1 _1, T2 _2) {
        super();
        this._1 = _1;
        this._2 = _2;
    }
    
    public Stream<Object> toStream() {
        return Stream.of(_1, _2);
    }
            
            
            

    @Override
    public int hashCode() {
        final int prime = 97;
 //       final int prime = 31;
        int result = 1;
        result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
        result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
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
        Tuple2 other = (Tuple2) obj;
        if (_1 == null) {
            if (other._1 != null)
                return false;
        } else if (!_1.equals(other._1))
            return false;
        if (_2 == null) {
            if (other._2 != null)
                return false;
        } else if (!_2.equals(other._2))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return("("+_1+","+_2+")");
    }
    
    public String asString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tuple2 [_1=");
        builder.append(_1);
        builder.append(", _2=");
        builder.append(_2);
        builder.append("]");
        return builder.toString();
    }

}
