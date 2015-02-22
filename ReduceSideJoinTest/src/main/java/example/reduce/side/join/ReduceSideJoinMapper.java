package example.reduce.side.join;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ReduceSideJoinMapper extends
        Mapper<LongWritable, Text, TextPair, TextQuintet> {

    private TextPair textPair = new TextPair();
    private TextQuintet textQuintet = new TextQuintet();
    private Text first = new Text();
    private Text second = new Text();
    private Text val1 = new Text();
    private Text val2 = new Text();
    private Text val3 = new Text();
    private Text val4 = new Text();
    private Text val6 = new Text();

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        if (value.toString().length() > 0) {
            String val[] = value.toString().split(",");
            first.set(val[0]);
            if ((val.length == 7) || (val.length == 4)) {
                if (val.length == 7) {
                    second.set("0");
                    val2.set(val[2]);
                    val3.set(val[3]);
                    val4.set(val[4]);
                    val6.set(val[6]);
                    textQuintet.setFirst(val2); // firstName from part-e
                    textQuintet.setSecond(val3); // lastName from part-e
                    textQuintet.setThird(val4); // gender from part-e
                    textQuintet.setFourth(val6); // deptID from part-e
                } else if (val.length == 4) {
                    second.set("1");
                    val1.set(val[1]);
                    textQuintet.setFifth(val1); // salary from part-sc
                }
                textPair.set(first, second);
                context.write(textPair, textQuintet);
            }

        }

    }

}
