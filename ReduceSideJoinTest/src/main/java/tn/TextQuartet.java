package tn;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;

// based on https://github.com/tomwhite/hadoop-book/blob/master/ch04/src/main/java/TextPair.java

public class TextQuartet implements WritableComparable<TextQuartet> {

	private Text first;
	private Text second;
	private Text third;
	private Text fourth;
	
	public TextQuartet() {
		set(new Text(),new Text(),new Text(),new Text());
	}

	public TextQuartet(String first,String second,String third,String fourth) {
		set(new Text(first),new Text(second),new Text(third),new Text(fourth));
	}

	public TextQuartet(Text first,Text second,Text third,Text fourth) {
		set(first,second,third,fourth);
	}

	public void set(Text first,Text second,Text third,Text fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}
	
	public void set(String first,String second,String third,String fourth) {
		this.first = new Text(first);
		this.second = new Text(second);
		this.third = new Text(third);
		this.fourth = new Text(fourth);
	}

	public Text getFirst() {
		return first;
	}

	public void setFirst(Text first) {
		this.first = first;
	}
	
	public void setFirst(String first) {
		this.first = new Text(first);
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

	@Override
	public void write(DataOutput out) throws IOException {
		first.write(out);
		second.write(out);
		third.write(out);
		fourth.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		first.readFields(in);
		second.readFields(in);
		third.readFields(in);
		fourth.readFields(in);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((fourth == null) ? 0 : fourth.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
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
		TextQuartet other = (TextQuartet) obj;
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
		if (third == null) {
			if (other.third != null)
				return false;
		} else if (!third.equals(other.third))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return first + "\t" + second + "\t" + third + "\t" + fourth ;
	}

	@Override
	public int compareTo(TextQuartet ts) {
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
		return fourth.compareTo(ts.fourth);
	}

	public static class TextQuartetComparator extends WritableComparator {
//		A RawComparator for comparing TextQuartet byte representations
		private static final Text.Comparator TEXT_COMPARATOR = new Text.Comparator();

		public TextQuartetComparator() {
			super(TextQuartet.class);
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
				return TEXT_COMPARATOR.compare(b1, start1, l1 - totLen1, b2, start2, l2 - totLen2);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

//	static {
////	Registers TextQuartetComparator as default comparator of TextQuartet.class
//		WritableComparator.define(TextQuartet.class, new TextQuartetComparator());
//	}


	public static class TextQuartetFirstComparator extends WritableComparator {

		private static final Text.Comparator TEXT_COMPARATOR = new Text.Comparator();

		public TextQuartetFirstComparator() {
			super(TextQuartet.class);
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
			if (a instanceof TextQuartet && b instanceof TextQuartet) {
				return ((TextQuartet) a).first.compareTo(((TextQuartet) b).first);
			}
			return super.compare(a, b);
		}
	}
}
