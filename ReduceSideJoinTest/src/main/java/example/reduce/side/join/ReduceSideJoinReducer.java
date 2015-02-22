package example.reduce.side.join;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReduceSideJoinReducer extends
        Reducer<TextPair, TextQuintet, NullWritable, TextSeptet> {

    private MapFile.Reader mapReader = null;
    private TextSeptet textSeptet = new TextSeptet();
    private NullWritable nullWritableKey = NullWritable.get();
    private Text first = new Text();
    private Text second = new Text();
    private Text zero = new Text("0");
    private Text one = new Text("1");
    private Text result0 = new Text();
    private Text result1 = new Text();
    private Text result2 = new Text();
    private Text result3 = new Text();
    private Text result4 = new Text();
    private Text result5 = new Text();
    private Text result6 = new Text();
    private Text mapKey = new Text();
    private Text mapVal = new Text();
    private Text unknown = new Text("unknown");
    private boolean done1 = false;
    private boolean done2 = false;

    @Override
    protected void setup(Context context) throws IOException,
            InterruptedException {

        Path[] cacheFilesLocal = DistributedCache.getLocalCacheFiles(context
                .getConfiguration());

        try {
            if ((cacheFilesLocal == null) || (cacheFilesLocal.length == 0)) {
                throw new FileNotFoundException(
                        "No distributed cache file found.");
            }
            FileSystem fs = FileSystem.getLocal(context.getConfiguration());
            String dirname = cacheFilesLocal[0].getName().toString().trim();
            mapReader = new MapFile.Reader(fs, dirname,
                    context.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reduce(TextPair key, Iterable<TextQuintet> values,
            Context context) throws IOException, InterruptedException {

        first = key.getFirst();
        result0.set(first);
        second = key.getSecond();
        done1 = false;
        done2 = false;

        for (TextQuintet textQuintet : values) {
            if ((done1 == false) && (second.equals(zero))) {
                result1.set(textQuintet.getFirst());
                result2.set(textQuintet.getSecond());
                result3.set(textQuintet.getThird());
                result4.set(textQuintet.getFourth());
                done1 = true;
            } else if ((done2 == false) && (second.equals(one))) {
                result6.set(textQuintet.getFifth());
                done2 = true;
            } else
                continue;
        }

        if (result4.equals(null)) {
            result5 = unknown;
            ;
        } else {
            mapKey.set(result4);
            mapReader.get(mapKey, mapVal);
            if (mapVal.equals(null)) {
                result5 = unknown;
            } else {
                result5.set(mapVal);
            }
        }

        if (done1 && done2) {
            textSeptet.set(result0, result1, result2, result3, result4,
                    result5, result6);
            context.write(nullWritableKey, textSeptet);

        }
    }

}
