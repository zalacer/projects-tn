package com.tn;

import org.apache.commons.io.output.NullOutputStream;
//import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ForLoopTest {

	static NullOutputStream nout = new NullOutputStream();
	static long start = 0;
	static long end = 0;
	static long elapsed = 0;
	static double e = 0.0;
	
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
		System.out.println("StreamTest2.ForLoopTest");
		
		for (int j = 1; j<4; j++) { 
			runtest(false);
		}

		for (int j = 1; j<34; j++) { 
			runtest(true);
		}
		
	}
}

