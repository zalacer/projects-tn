package old.code;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class GroupingComparatorRSJ extends WritableComparator {
	protected GroupingComparatorRSJ() {
		super(CompositeKeyWritableRSJ.class, true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		// The grouping comparator is the joinKey (Employee ID)
		CompositeKeyWritableRSJ key1 = (CompositeKeyWritableRSJ) w1;
		CompositeKeyWritableRSJ key2 = (CompositeKeyWritableRSJ) w2;
		return key1.getjoinKey().compareTo(key2.getjoinKey());
	}
}
