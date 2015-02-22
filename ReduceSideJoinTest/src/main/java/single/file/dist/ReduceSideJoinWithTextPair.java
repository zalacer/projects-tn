package single.file.dist;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import tn.TextPair;

public class ReduceSideJoinWithTextPair extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		
		Configuration configuration = new Configuration();
		int exitCode = ToolRunner.run(configuration, new ReduceSideJoinWithTextPair(), args);
		System.exit(exitCode);
	}
	
	@Override
	public int run(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println(" Usage: "+this.getClass().getSimpleName()+" <input path> <output path>");
			System.exit(-1);
		}

		Job job = new Job();
		Configuration conf = job.getConfiguration();
		job.setJarByClass(this.getClass());
		job.setJobName(this.getClass().getSimpleName());
		
		// Add departments map to distributed cache
		DistributedCache.addCacheFile(new URI("/home/training/workspace/ReduceSideJoinAa/depmap"),conf);
		
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
	
	public static class ReduceSideJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {
		
		TextPair textPair = new TextPair();
		Text first = new Text("");
		Text second = new Text("");
		Text newVal = new Text("");

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			if (value.toString().length() > 0) {
				String val[] = value.toString().split(",");
				first.set(val[0]);
				if ((val.length == 7) || (val.length == 4)) {
					if (val.length == 7) {
						second.set("0");
						newVal.set(val[2]+","+val[3]+","+val[4]+","+val[6]);
						// firstName,lastName,gender,deptID from part-e
					} else if (val.length == 4) {
						second.set("1");
						newVal.set(val[1]); // salary from part-sc
					}
					textPair.set(first,second);
					context.write(textPair, newVal);
				}

			}

		}
		
	}
	
	public static class ReduceSideJoinReducer extends Reducer<TextPair, Text, NullWritable, Text> {
		
		private MapFile.Reader mapReader = null;
		private NullWritable nullWritableKey = NullWritable.get();
		private Text reduceOutputValue = new Text("");
		private Text first = new Text("");
		private Text second = new Text("");
		private Text zero = new Text("0");
		private Text one = new Text("1");
		private Text mapKey = new Text("");
		private Text mapVal = new Text("");
		private boolean done1 = false;
		private boolean done2 = false;

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {

			Path[] cacheFilesLocal = DistributedCache.getLocalCacheFiles(context.getConfiguration());
			
			try {
				if (cacheFilesLocal.length == 0) {
					throw new FileNotFoundException("No distributed cache file found.");
				}
				FileSystem fs = FileSystem.getLocal(context.getConfiguration());
				String dirname = cacheFilesLocal[0].getName().toString().trim();
				mapReader = new MapFile.Reader(fs, dirname,context.getConfiguration());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
			
		@Override
		public void reduce(TextPair key, Iterable<Text> values,	Context context) 
				throws IOException, InterruptedException {
			
			first = key.getFirst();
			second = key.getSecond();
			
			String[] result = new String[7];
			result[0] = first.toString();
			done1 = false; done2 = false;
			for (Text value : values) {
				if ((done1 == false) && (second.equals(zero))) {
					String val[] = value.toString().split(",");
					if (val.length == 4) {
						result[1] = val[0];
						result[2] = val[1];
						result[3] = val[2];
						result[4] = val[3];
						done1 = true;
					}
				} else if ((done2 == false) && (second.equals(one))) {
					result[6] = value.toString();
					done2 = true;
				} else continue;			
			}
			
			if (result[4] == null) {
				result[5] = "unknown";
			} else {
				mapKey.set(result[4]);
				mapReader.get(mapKey, mapVal);
				if (mapVal.equals(null)) {
					result[5] = "unknown";
				} else {
					result[5] = mapVal.toString();
				}		
			}
		
			if (done1 && done2) {
				reduceOutputValue.set(result[0]+","+result[1]+","+result[2]+","+result[3]
						+","+result[4]+","+result[5]+","+result[6]);
				context.write(nullWritableKey, reduceOutputValue);
			}
		}
		
	}

	public static class ReduceSideJoinPartitioner extends Partitioner<TextPair, Text> {

		@Override
		public int getPartition(TextPair key, Text value, int numReduceTasks) {
			// Partitions on EmployeeID)
			return (key.getFirst().hashCode() % numReduceTasks);
		}
	}
	
}
