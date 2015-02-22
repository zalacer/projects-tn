package old.code;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReducerRSJ extends
		Reducer<CompositeKeyWritableRSJ, Text, NullWritable, Text> {

	StringBuilder reduceValueBuilder = new StringBuilder("");
	NullWritable nullWritableKey = NullWritable.get();
	Text reduceOutputValue = new Text("");
	String strSeparator = ",";
	private MapFile.Reader deptMapReader = null;
	Text txtMapFileLookupKey = new Text("");
	Text txtMapFileLookupValue = new Text("");
	FileSystem fs = null;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {

	Path[] cacheFilesLocal = DistributedCache.getLocalCacheFiles(context.getConfiguration());
		
		MapFile.Reader reader = null;
		try {
//			fs = FileSystem.get(context.getConfiguration());
			
			fs = FileSystem.getLocal(context.getConfiguration());
			
			if (cacheFilesLocal.length == 0) {
				throw new FileNotFoundException("No distributed cache file found.");
			}
			
			if (cacheFilesLocal[0].getName().toString().trim().equals("nomapdata")) {
				String dirname = cacheFilesLocal[0].getName().toString().trim();
				reader = new MapFile.Reader(fs, dirname,context.getConfiguration());
				initializeDepartmentsMap(reader);
			} else {
				throw new FileNotFoundException("Distributed cache file nomapdata not found.");
			}
			
//			for (Path eachPath : cacheFilesLocal) {
//
//				if (eachPath.getName().toString().trim().equals("nomapdata")) {
//
//					String dirname = eachPath.getName().toString().trim();
//					reader = new MapFile.Reader(fs, dirname,context.getConfiguration());
//
//					initializeDepartmentsMap(reader);
//				}
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initializeDepartmentsMap(MapFile.Reader reader)
			throws IOException {

		try {
			deptMapReader = reader;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private StringBuilder buildOutputValue(CompositeKeyWritableRSJ key,
			StringBuilder reduceValueBuilder, Text value) {

		if (key.getsourceIndex() == 1) {
			
			reduceValueBuilder.append(key.getjoinKey()).append(strSeparator);

			String arrEmpAttributes[] = value.toString().split(",");
			txtMapFileLookupKey.set(arrEmpAttributes[3].toString());
			try {
				deptMapReader.get(txtMapFileLookupKey, txtMapFileLookupValue);
				
			} catch (Exception e) {
				txtMapFileLookupValue.set("");

			} finally {
				txtMapFileLookupValue
						.set((txtMapFileLookupValue.equals(null) || txtMapFileLookupValue
								.equals("")) ? "NOT-FOUND"
								: txtMapFileLookupValue.toString());
			}
			
			reduceValueBuilder.append(value.toString()).append(strSeparator)
					.append(txtMapFileLookupValue.toString())
					.append(strSeparator);
			

		} else if (key.getsourceIndex() == 2) {
			// Current recent salary data (1..1 on join key)
			// Salary data; Just append the salary, drop the effective-to-date
			String arrSalAttributes[] = value.toString().split(",");
			reduceValueBuilder.append(arrSalAttributes[0].toString()).append(
					strSeparator);
		} else // key.getsourceIndex() == 3; Historical salary data
		{
			// {{
			// Get the salary data but extract only current salary
			// (to_date='9999-01-01')
			String arrSalAttributes[] = value.toString().split(",");
			if (arrSalAttributes[1].toString().equals("9999-01-01")) {
				// Salary data; Just append
				reduceValueBuilder.append(arrSalAttributes[0].toString())
						.append(strSeparator);
			}
			// }}

		}

		// {{
		// Reset
		txtMapFileLookupKey.set("");
		txtMapFileLookupValue.set("");
		// }}

		return reduceValueBuilder;
	}

	@Override
	public void reduce(CompositeKeyWritableRSJ key, Iterable<Text> values,
			Context context) throws IOException, InterruptedException {

		// Iterate through values; First set is csv of employee data
		// second set is salary data; The data is already ordered
		// by virtue of secondary sort; Append each value;
		for (Text value : values) {
			buildOutputValue(key, reduceValueBuilder, value);
		}

		// Drop last comma, set value, and emit output
		if (reduceValueBuilder.length() > 1) {

			reduceValueBuilder.setLength(reduceValueBuilder.length() - 1);
			// Emit output
			reduceOutputValue.set(reduceValueBuilder.toString());
			context.write(nullWritableKey, reduceOutputValue);
		} else {
			System.out.println("Key=" + key.getjoinKey() + "src="
					+ key.getsourceIndex());

		}

		// Reset variables
		reduceValueBuilder.setLength(0);
		reduceOutputValue.set("");

	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		// deptMapReader.close();
	}
}
