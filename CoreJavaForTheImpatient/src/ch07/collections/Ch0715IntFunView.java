package ch07.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 15. Generalize the preceding exercise to an arbitrary IntFunction. Note that the
// result is an infinite collection, so certain methods (such as size and toArray)
// should throw an UnsupportedOperationException.

public class Ch0715IntFunView {

    public static <T> Collection<T> getView(int n, IntFunction<Collection<T>> fun) {
        return fun.apply(n);
    }
    
    public static <T> Collection<T> getView(int n, DoubleFunction<Collection<T>> fun) {
        return fun.apply(n);
    }

    public static IntFunction<Collection<Integer>> intFun() {
        return x -> Collections.unmodifiableCollection(Stream
                .iterate((ToIntFunction<IntBinaryOperator>) f -> f.applyAsInt(1, 2),
                        prev -> f -> prev.applyAsInt((a, b) -> f.applyAsInt(b, a * b)))
                .limit(x).map(pair -> pair.applyAsInt((a, b) -> a))
                .collect(Collectors.toList()));
    }
    
    public static DoubleFunction<Collection<Double>> dubFun() {
        return x -> Collections.unmodifiableCollection(Stream
                .iterate((ToDoubleFunction<DoubleBinaryOperator>) f -> f.applyAsDouble(1, 2),
                        prev -> f -> prev.applyAsDouble((a, b) -> f.applyAsDouble(b, a * b)))
                .limit((long) x).map(pair -> pair.applyAsDouble((a, b) -> a))
                .collect(Collectors.toList()));
    }

    public static void main(String[] args) {

        Collection<Integer> y = getView(9, intFun());
        System.out.println(y);
        // [1, 2, 2, 4, 8, 32, 256, 8192, 2097152]
        
        Collection<Double> z = getView(9, dubFun());
        System.out.println(z);
        // [1.0, 2.0, 2.0, 4.0, 8.0, 32.0, 256.0, 8192.0, 2097152.0]
    }

}
