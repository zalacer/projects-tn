package com.tn;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Java8ForLoopTest {

	static NullOutputStream nout = new NullOutputStream();
	static long start = 0;
	static long end = 0;
	static long elapsed = 0;
	static double e = 0.0;
    static double[] t = new double[100];
    static DescriptiveStatistics d = new DescriptiveStatistics();
    static double min = 0;
    static double max = 0;
    static double mean = 0;
    static double stddev = 0;
    
	public static void runtest(Boolean x) {
		start = System.nanoTime();
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			nout.write(i);
		}
		end = System.nanoTime();
		elapsed = end - start;
		e = elapsed;
		if (x ==  true) {
			System.out.println("\nStart : " + start + "\nEnd: " + end + "\nElapsed: " + (elapsed) + " " +(e));
		}	
	}
	
	public static void main(String[] args) {
		boolean p = false;
		if (args.length > 0) {
			p = true;
		}
		
		System.out.println("Java8 For Loop Test");
		
		for (int j = 0; j<3; j++) { 
			runtest(false);
		}

		for (int j = 0; j<100; j++) { 
			runtest(p);
			t[j] = e;
		}
		
		for (int j = 0; j<100; j++) {
			d.addValue(t[j]);
		}
		
		min = d.getMin();
		max = d.getMax();
		mean = d.getMean();
		stddev = d.getStandardDeviation();

		System.out.println();
		System.out.printf("min    = %.2f ns\n", min);
		System.out.printf("max    = %.2f ns\n", max);
		System.out.printf("mean   = %.2f ns\n", mean);
		System.out.printf("stddev = %.2f ns\n", stddev);
		
	}
}

