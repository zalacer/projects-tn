package tn;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class ReduceSideJoinPartitioner extends Partitioner<TextPair, Text> {

		@Override
		public int getPartition(TextPair key, Text value, int numReduceTasks) {
			// Partitions on EmployeeID)
			return (key.getFirst().hashCode() % numReduceTasks);
		}
}
