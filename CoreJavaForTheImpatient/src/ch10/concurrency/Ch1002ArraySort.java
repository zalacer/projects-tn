package ch10.concurrency;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

// 2. How large does an array have to be for Arrays.parallelSort to be faster
// than Arrays.sort on your computer?

// In one set of tests the lowest length I found for which Arrays.parallelSort 
// completed faster than Arrays.sort was 7600 and the average length at which 
// this occurred over 10 trials was 9280. In all trials a pseudo-random array
// of doubles was used, array length started at 100 and was incremented by 100 
// 1000 times and the parallel sorts were done prior to the serial sorts in the
// same process. I found that when running the  parallel sorts after the serial
// sorts the parallel ones ran fastest for all array lengths starting at 1.
// Using another method which attempts to isolate the effects of running serial 
// and parallel sorts in the same  process, the lowest length was just 200 for 
// which parallel sort outperformed serial sort. In this method, serial and 
// parallel test data was gathered in separate processes and then serialized to
// save it for later analysis. An attempt was made to get minimum runtimes by 
// running each sort 5 times for every length and taking the lowest runtime.

public class Ch1002ArraySort {

  public static final Random r = new Random(7349);

  public static void main(String[] args) throws IOException, ClassNotFoundException {

    long start, stop, elapsed, least; 
    double[] d;
    //        int inc = 2;
    long length;
    //        int count = 0;

    // section 1 parallel data 
    TreeMap<Long,Long> parallel = new TreeMap<>();        
    for (int i = 1; i <= 1000; i++) {
      length = i * 100;
      d = r.doubles().limit(length).toArray();
      start = System.nanoTime();
      Arrays.parallelSort(d);
      stop = System.nanoTime();
      elapsed = stop - start;
      parallel.put(length, elapsed);
    }      

    // section 2 serial data 
    TreeMap<Long,Long> serial = new TreeMap<>();        
    for (int i = 1; i <= 1000; i++) {
      length = i * 100;
      d = r.doubles().limit(length).toArray();
      start = System.nanoTime();
      Arrays.sort(d);
      stop = System.nanoTime();
      elapsed = stop - start;
      serial.put(length, elapsed);
    }      

    //  section 3 analysis
    boolean [] found = new boolean[]{false};
    parallel.forEach((k,v) -> {     
      if (!found[0] && parallel.get(k) < serial.get(k)) {
        System.out.println("length:"+k+",parT:"+parallel.get(k)+",serT:"+serial.get(k));
        found[0] = true;
      }
    });

    // experimental results for lowest length at which parallel time < serial time
    // length:9400,parT:596589,serT:640764
    // length:8800,parT:533673,serT:584095
    // length:9700,parT:626485,serT:655935
    // length:7600,parT:544828,serT:555983
    // length:9400,parT:620238,serT:668430
    // length:9500,parT:642996,serT:671999
    // length:9400,parT:589003,serT:673338
    // length:10300,parT:768381,serT:793816
    // length:9700,parT:626039,serT:662629
    // length:9000,parT:630055,serT:634071

    double avg = (9400+8800+9700+7600+9400+9500+9400+10300+9700+9000)/10.;
    System.out.println(avg); // 9280

    // another approach with the purpose of isolating cross-effects by running 
    // serial and parallel sorts in separate processes, serializing the data 
    // and in a final process deserializing and analyzing it

    // section 4 parallel data and serialization
    TreeMap<Long,Long> parallel2 = new TreeMap<>();        
    for (int i = 1; i <= 1000; i++) {
      length = i * 100;
      d = r.doubles().limit(length).toArray();
      least = 0;
      for (int j = 0; j < 5; j++) {
        start = System.nanoTime();
        Arrays.parallelSort(d);
        stop = System.nanoTime();
        elapsed = stop - start;
        if (j == 0) {
          least = elapsed;
        } else if (elapsed < least) {
          least = elapsed;
        }
      }
      parallel2.put(length, least);
    }      
    Path outpar = Paths.get("Ch1002ArraySort-parallel.ser");
    OutputStream os = Files.newOutputStream(outpar);
    ObjectOutputStream oos = new ObjectOutputStream(os);
    oos.writeObject(parallel2);

    // section 5 serial data and serialization
    TreeMap<Long,Long> serial2 = new TreeMap<>();        
    for (int i = 1; i <= 1000; i++) {
      length = i * 100;
      d = r.doubles().limit(length).toArray();
      least = 0;
      for (int j = 0; j < 5; j++) {
        start = System.nanoTime();
        Arrays.sort(d);
        stop = System.nanoTime();
        elapsed = stop - start;
        if (j == 0) {
          least = elapsed;
        } else if (elapsed < least) {
          least = elapsed;
        }
      }
      serial2.put(length, least);
    }      
    Path outser = Paths.get("Ch1002ArraySort-serial.ser");
    OutputStream os2 = Files.newOutputStream(outser);
    ObjectOutputStream oos2 = new ObjectOutputStream(os2);
    oos2.writeObject(serial2);

    // section 6 deserialize the maps 
    Path datapar = Paths.get("Ch1002ArraySort-parallel.ser");
    InputStream is = Files.newInputStream(datapar);
    ObjectInputStream ois = new ObjectInputStream(is);
    @SuppressWarnings("unchecked")
    TreeMap<Long,Long> par = (TreeMap<Long,Long>) ois.readObject();

    Path dataser = Paths.get("Ch1002ArraySort-serial.ser");
    InputStream is2 = Files.newInputStream(dataser);
    ObjectInputStream ois2 = new ObjectInputStream(is2);
    @SuppressWarnings("unchecked")
    TreeMap<Long,Long> ser = (TreeMap<Long,Long>) ois2.readObject();

    // section 7 analysis
    boolean [] found2 = new boolean[]{false};
    par.forEach((k,v) -> {     
      if (!found2[0] && par.get(k) < ser.get(k)) {
        System.out.println("length:"+k+",parT:"+par.get(k)+",serT:"+ser.get(k));
        found2[0] = true;
      }
    });
    // result: length:200,parT:23203,serT:33912

  }

}
