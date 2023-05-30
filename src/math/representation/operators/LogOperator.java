package math.representation.operators;

import math.representation.FunctionOperator;

public class LogOperator extends FunctionOperator {

	@Override
	public double apply(double... vals) {
		return Math.log10(vals[0]);
	}

}
