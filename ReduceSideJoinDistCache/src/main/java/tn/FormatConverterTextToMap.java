package tn;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.fs.FSDataInputStream;

public class FormatConverterTextToMap {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(conf);
			Path inputFile = new Path(args[0]);
			Path outputFile = new Path(args[1]);

			Text txtKey = new Text();
			Text txtValue = new Text();

			String strLineInInputFile = "";
			String lstKeyValuePair[] = null;
			MapFile.Writer writer = null;
			FSDataInputStream inputStream = fs.open(inputFile);

			try {
				writer = new MapFile.Writer(conf, fs, outputFile.toString(),
						txtKey.getClass(), txtKey.getClass());
				writer.setIndexInterval(1);// Need this as the default is 128,
											// and my data is just 9 records
				while (inputStream.available() > 0) {
					strLineInInputFile = inputStream.readLine();
					lstKeyValuePair = strLineInInputFile.split(",");
					txtKey.set(lstKeyValuePair[0]);
					txtValue.set(lstKeyValuePair[1]);
					writer.append(txtKey, txtValue);
				}
			} finally {
				IOUtils.closeStream(writer);
				System.out.println("Map file created successfully!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
