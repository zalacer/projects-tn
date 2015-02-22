package tn;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
//import org.apache.hadoop.mapreduce.Reducer.Context;

public class BasicReducer extends Reducer<TextPair, Text, TextPair, Text> {
	
	@Override
	public void reduce(TextPair key, Iterable<Text> values,	Context context) 
			throws IOException, InterruptedException {
	
		for (Text value : values) {
			context.write(key, value);
		}
		
	}
	
}
