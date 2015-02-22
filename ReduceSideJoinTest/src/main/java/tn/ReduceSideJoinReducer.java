package tn;


import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReduceSideJoinReducer extends Reducer<TextPair, Text, NullWritable, Text> {
	
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
