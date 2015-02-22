package old.code;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class SortingComparatorRSJ extends WritableComparator {

	protected SortingComparatorRSJ() {
		super(CompositeKeyWritableRSJ.class, true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		// Sort on all attributes of composite key
		CompositeKeyWritableRSJ key1 = (CompositeKeyWritableRSJ) w1;
		CompositeKeyWritableRSJ key2 = (CompositeKeyWritableRSJ) w2;

		int cmpResult = key1.getjoinKey().compareTo(key2.getjoinKey());
		if (cmpResult == 0)// same joinKey
		{
//			return Double.compare(key1.getsourceIndex(), key2.getsourceIndex());
			return (key1.getsourceIndex() - key2.getsourceIndex());
		}
		return cmpResult;
	}
}