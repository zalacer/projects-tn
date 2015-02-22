package example.reduce.side.join;

import org.apache.hadoop.mapreduce.Partitioner;

public class ReduceSideJoinPartitioner extends Partitioner<TextPair, TextQuintet> {

		@Override
		public int getPartition(TextPair key, TextQuintet value, int numReduceTasks) {
			// Partitions on EmployeeID)
			return (key.getFirst().hashCode() % numReduceTasks);
		}
}
