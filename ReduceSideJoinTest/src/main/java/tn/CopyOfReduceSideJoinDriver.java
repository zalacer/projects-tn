package tn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.NullWritable;
//import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
//import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
//import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class CopyOfReduceSideJoinDriver extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		
		Configuration configuration = new Configuration();
		int exitCode = ToolRunner.run(configuration, new CopyOfReduceSideJoinDriver(), args);
		System.exit(exitCode);
	}
	
	@Override
	public int run(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println(" Usage: "+this.getClass().getSimpleName()+" <input path> <output path>");
			System.exit(-1);
		}

		Job job = new Job();
		job.setJarByClass(this.getClass());
		job.setJobName(this.getClass().getSimpleName());
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		Path output = new Path(args[1]);
		FileOutputFormat.setOutputPath(job, output);
		FileSystem.get(getConf()).delete(output,true);
				
		job.setMapperClass(ReduceSideJoinMapper.class);
		job.setMapOutputKeyClass(TextPair.class);
	    job.setMapOutputValueClass(Text.class);
	    
		job.setPartitionerClass(ReduceSideJoinPartitioner.class);
		job.setSortComparatorClass(TextPair.TextPairComparator.class);
		job.setGroupingComparatorClass(TextPair.TextPairFirstComparator.class);

		job.setReducerClass(ReduceSideJoinReducer.class);
		job.setNumReduceTasks(1);

		job.setOutputKeyClass(TextPair.class);
		job.setOutputValueClass(Text.class);
		
		boolean outcome = job.waitForCompletion(true);
		return(outcome ? 0 : 1);
	}
}
