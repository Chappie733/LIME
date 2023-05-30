package math.primitives;

import java.util.HashMap;

import math.representation.Numeric;

public class Interval {

	private Numeric min, max;

	public Interval(Numeric min, Numeric max) {
		this.min = min;
		this.max = max;
	}
	
	public Interval() {
		min = null;
		max = null;
	}
	
	public double getMin(HashMap<String, Double> values) {
		return min.getValue(values);
	}

	public double getMax(HashMap<String, Double> values) {
		return max.getValue(values);
	}
	
	
	
}
