package utils;

// for ch06.Ch0614closeAll

public class Arrays2 {

    public static <T> T[] swap(int i, int j, @SuppressWarnings("unchecked") T... values) {
        // Type safety: Potential heap pollution via varargs parameter values
        T temp = values[i];
        values[i] = values[j];
        values[j] = temp;
        return values;
    }

    public static <T> T[] swap2(int i, int j, T[] values) {
        // Type safety: Potential heap pollution via varargs parameter values
        T temp = values[i];
        values[i] = values[j];
        values[j] = temp;
        return values;
    }

    // public static <T> T[] swap3(int i, int j, Object...values) {
    // for (Object o : values) {
    // if (! (o instanceof T))
    // //Cannot perform instanceof check against type parameter T.
    // // Use its erasure Object instead since further generic type
    // // information will be erased at runtime
    // throw new IllegalArgumentException();
    // }
    // T temp = (T) values[i];
    // values[i] = values[j];
    // values[j] = temp;
    // return values;
    // }

    public static Double[] swap4(int i, int j, Number... values) {
        Double[] d = new Double[values.length];
        for (int k = 0; k < values.length; k++)
            d[k] = values[k].doubleValue();
        Double temp = d[i];
        d[i] = d[j];
        d[j] = temp;
        return d;
    }

}
