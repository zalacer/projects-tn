package tn;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;

// based on https://github.com/tomwhite/hadoop-book/blob/master/ch04/src/main/java/TextPair.java

public class TextSeptet implements WritableComparable<TextSeptet> {

	private Text first;
	private Text second;
	private Text third;
	private Text fourth;
	private Text fifth;
	private Text sixth;
	private Text seventh;
	
	public TextSeptet() {
		set(new Text(),new Text(),new Text(),new Text(),new Text(),new Text(),new Text());
	}

	public TextSeptet(String first,String second,String third,String fourth,
			String fifth,String sixth,String seventh) {
		set(new Text(first),new Text(second),new Text(third),new Text(fourth),
				new Text(fifth),new Text(sixth),new Text(seventh));
	}

	public TextSeptet(Text first,Text second,Text third,Text fourth,
			Text fifth,Text sixth,Text seventh) {
		set(first,second,third,fourth,fifth,sixth,seventh);
	}

	public void set(Text first,Text second,Text third,Text fourth,
			Text fifth,Text sixth,Text seventh) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
		this.seventh = seventh;
	}

	public Text getFirst() {
		return first;
	}

	public void setFirst(Text first) {
		this.first = first;
	}

	public Text getSecond() {
		return second;
	}

	public void setSecond(Text second) {
		this.second = second;
	}

	public Text getThird() {
		return third;
	}

	public void setThird(Text third) {
		this.third = third;
	}

	public Text getFourth() {
		return fourth;
	}

	public void setFourth(Text fourth) {
		this.fourth = fourth;
	}

	public Text getFifth() {
		return fifth;
	}

	public void setFifth(Text fifth) {
		this.fifth = fifth;
	}

	public Text getSixth() {
		return sixth;
	}

	public void setSixth(Text sixth) {
		this.sixth = sixth;
	}

	public Text getSeventh() {
		return seventh;
	}

	public void setSeventh(Text seventh) {
		this.seventh = seventh;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		first.write(out);
		second.write(out);
		third.write(out);
		fourth.write(out);
		fifth.write(out);
		sixth.write(out);
		seventh.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		first.readFields(in);
		second.readFields(in);
		third.readFields(in);
		fourth.readFields(in);
		fifth.readFields(in);
		sixth.readFields(in);
		seventh.readFields(in);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fifth == null) ? 0 : fifth.hashCode());
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((fourth == null) ? 0 : fourth.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		result = prime * result + ((seventh == null) ? 0 : seventh.hashCode());
		result = prime * result + ((sixth == null) ? 0 : sixth.hashCode());
		result = prime * result + ((third == null) ? 0 : third.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextSeptet other = (TextSeptet) obj;
		if (fifth == null) {
			if (other.fifth != null)
				return false;
		} else if (!fifth.equals(other.fifth))
			return false;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (fourth == null) {
			if (other.fourth != null)
				return false;
		} else if (!fourth.equals(other.fourth))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		if (seventh == null) {
			if (other.seventh != null)
				return false;
		} else if (!seventh.equals(other.seventh))
			return false;
		if (sixth == null) {
			if (other.sixth != null)
				return false;
		} else if (!sixth.equals(other.sixth))
			return false;
		if (third == null) {
			if (other.third != null)
				return false;
		} else if (!third.equals(other.third))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return first + "\t" + second + "\t" + third + "\t" + fourth
				 + "\t" + fifth + "\t" + sixth + "\t" + seventh;
	}

	@Override
	public int compareTo(TextSeptet ts) {
		int cmp = first.compareTo(ts.first);
		if (cmp != 0) {
			return cmp;
		}
		cmp = second.compareTo(ts.second);
		if (cmp != 0) {
			return cmp;
		}		
		cmp = third.compareTo(ts.third);
		if (cmp != 0) {
			return cmp;
		}
		cmp = fourth.compareTo(ts.fourth);
		if (cmp != 0) {
			return cmp;
		}
		cmp = fifth.compareTo(ts.fifth);
		if (cmp != 0) {
			return cmp;
		}
		cmp = sixth.compareTo(ts.sixth);
		if (cmp != 0) {
			return cmp;
		}
		return seventh.compareTo(ts.seventh);
	}

	public static class TextPairComparator extends WritableComparator {
//		A RawComparator for comparing TextSeptet byte representations
		private static final Text.Comparator TEXT_COMPARATOR = new Text.Comparator();

		public TextPairComparator() {
			super(TextSeptet.class);
		}

		@Override
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {

			try {
				// first
				int len1 = WritableUtils.decodeVIntSize(b1[s1]) + readVInt(b1, s1);
				int len2 = WritableUtils.decodeVIntSize(b2[s2]) + readVInt(b2, s2);
				int cmp = TEXT_COMPARATOR.compare(b1, s1, len1, b2, s2, len2);
				if (cmp != 0) return cmp;

				// second
				int totLen1 = len1;
				int totLen2 = len2;
				int start1 = s1+len1;
				int start2 = s2+len2;
				len1 = WritableUtils.decodeVIntSize(b1[start1]) + readVInt(b1, start1);
				len2 = WritableUtils.decodeVIntSize(b2[start2]) + readVInt(b2, start2);
				cmp = TEXT_COMPARATOR.compare(b1, start1, len1, b2, start2, len2);
				if (cmp != 0) return cmp;

				// third
				totLen1 += len1;
				totLen2 += len2;
				start1 += len1;
				start2 += len2;
				len1 = WritableUtils.decodeVIntSize(b1[start1]) + readVInt(b1, start1);
				len2 = WritableUtils.decodeVIntSize(b2[start2]) + readVInt(b2, start2);
				cmp = TEXT_COMPARATOR.compare(b1, start1, len1, b2, start2, len2);
				if (cmp != 0) return cmp;

				// fourth
				totLen1 += len1;
				totLen2 += len2;
				start1 += len1;
				start2 += len2;
				len1 = WritableUtils.decodeVIntSize(b1[start1]) + readVInt(b1, start1);
				len2 = WritableUtils.decodeVIntSize(b2[start2]) + readVInt(b2, start2);
				cmp = TEXT_COMPARATOR.compare(b1, start1, len1, b2, start2, len2);
				if (cmp != 0) return cmp;

				// fifth
				totLen1 += len1;
				totLen2 += len2;
				start1 += len1;
				start2 += len2;
				len1 = WritableUtils.decodeVIntSize(b1[start1]) + readVInt(b1, start1);
				len2 = WritableUtils.decodeVIntSize(b2[start2]) + readVInt(b2, start2);
				cmp = TEXT_COMPARATOR.compare(b1, start1, len1, b2, start2, len2);
				if (cmp != 0) return cmp;

				// sixth
				totLen1 += len1;
				totLen2 += len2;
				start1 += len1;
				start2 += len2;
				len1 = WritableUtils.decodeVIntSize(b1[start1]) + readVInt(b1, start1);
				len2 = WritableUtils.decodeVIntSize(b2[start2]) + readVInt(b2, start2);
				cmp = TEXT_COMPARATOR.compare(b1, start1, len1, b2, start2, len2);
				if (cmp != 0) return cmp;

				// seventh
				totLen1 += len1;
				totLen2 += len2;
				start1 += len1;
				start2 += len2;
				return TEXT_COMPARATOR.compare(b1, start1, l1 - totLen1, b2, start2, l2 - totLen2);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

//	static {
//		Registers TextPairComparator as default comparator of TextPair.class
//		WritableComparator.define(TextPair.class, new TextPairComparator());
//	}


	public static class TextPairFirstComparator extends WritableComparator {

		private static final Text.Comparator TEXT_COMPARATOR = new Text.Comparator();

		public TextPairFirstComparator() {
			super(TextSeptet.class);
		}

		@Override
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {

			try {
				int firstL1 = WritableUtils.decodeVIntSize(b1[s1]) + readVInt(b1, s1);
				int firstL2 = WritableUtils.decodeVIntSize(b2[s2]) + readVInt(b2, s2);
				return TEXT_COMPARATOR .compare(b1, s1, firstL1, b2, s2, firstL2);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable a, WritableComparable b) {
			if (a instanceof TextSeptet && b instanceof TextSeptet) {
				return ((TextSeptet) a).first.compareTo(((TextSeptet) b).first);
			}
			return super.compare(a, b);
		}
	}
}
