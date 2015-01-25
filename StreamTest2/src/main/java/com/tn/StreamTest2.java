package com.tn;

//import java.io.IOException;
import java.util.stream.IntStream;

import org.apache.commons.io.output.NullOutputStream;

public class StreamTest2 {
	
	static NullOutputStream nout = new NullOutputStream();
	static long start = 0;
	static long end = 0;
	static long elapsed = 0;
	static double e = 0.0;
	
	public static void runtest(Boolean x) {
		start = System.nanoTime();
		IntStream.range(1, Integer.MAX_VALUE).forEach(nout::write);
		end = System.nanoTime();
		elapsed = end - start;
		e = elapsed;
		if (x == true) {
			System.out.println("\nStart : " + start + "\nEnd: " + end + "\nElapsed: " + (elapsed) + " " +(e));
		}
	}

	
	public static void main(String[] args) {
		System.out.println("StreamTest2.StreamTest2");
		
		for (int j = 1; j<4; j++) { 
			runtest(false);
		}

		for (int j = 1; j<34; j++) { 
			runtest(true);
		}
		
	
		
//		try {
//			nout.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
