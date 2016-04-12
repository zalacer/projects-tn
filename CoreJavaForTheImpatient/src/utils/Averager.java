package utils;

public class Averager {

    final long count;
    final Double value;

    public Averager() {
        this.count = 0;
        this.value = 0.0;
    }

    Averager(long count, Double value) {
        this.count = count;
        this.value = value;
    }

    public Averager accept(Double value) {
        return new Averager(this.count + 1, this.value + value);
    }

    public Averager combine(Averager averager) {
        return new Averager(this.count + averager.count, this.value + averager.value);
    }

    public double average() {
        return value / count;
    }

}
