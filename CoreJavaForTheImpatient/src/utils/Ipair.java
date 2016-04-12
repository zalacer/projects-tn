package utils;

public class Ipair {

	private Integer _1 = 0;
	private Integer _2 = 0;

	public Ipair() {}
	
	public Ipair(Integer _1, Integer _2) {
		this._1 = _1;
		this._2 = _2;
	}

	public Integer get1() {
		return _1;
	}

	public void set1(Integer _1) {
		this._1 = _1;
	}

	public Integer get2() {
		return _2;
	}

	public void set2(Integer _2) {
		this._2 = _2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
		result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
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
		Ipair other = (Ipair) obj;
		if (_1 == null) {
			if (other._1 != null)
				return false;
		} else if (!_1.equals(other._1))
			return false;
		if (_2 == null) {
			if (other._2 != null)
				return false;
		} else if (!_2.equals(other._2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Ipair(" + _1 + "," + _2 + ")";
	}

	

	
}
