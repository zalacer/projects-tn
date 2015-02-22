package tn;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import tn.TextPair;

public class CopyOfReduceSideJoinMapper extends Mapper<LongWritable, Text, TextPair, Text> {
	
	TextPair tp = new TextPair();
	Text first = new Text("");
	Text second = new Text("");
	Text newVal = new Text("");
	Text done1 = new Text("");
	Text done2 = new Text("");

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		if (value.toString().length() > 0) {
			String val[] = value.toString().split(",");
			first.set(val[0]);
			if (((val.length == 7) && (done1 != first)) || ((val.length == 4) && (done2 != first))) {
				if (val.length == 7) {
					second.set("0");
					newVal.set(val[2]+","+val[3]+","+val[4]+","+val[6]);
					// firstName,lastName,gender,deptID only from part-e
					done1 = first;
				} else if (val.length == 4) {
					second.set("1");
					newVal.set(val[1]); // salary only from part-sc
					done2 = first;
				}
				tp.set(first,second);
				context.write(tp, newVal);
			}

		}

	}
	
}
	
	



//		ckwKey.setjoinKey(arrEntityAttributes[0].toString());
//		ckwKey.setsourceIndex(intSrcIndex);
//		txtValue.set(buildMapValue(arrEntityAttributes));

//		context.write(textPair, text);
//		context.write(ckwKey, txtValue);

