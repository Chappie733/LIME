package math.representation.operators;

import math.representation.FunctionOperator;

public class ExpOperator extends FunctionOperator {

	@Override
	public double apply(double... vals) {
		return Math.exp(vals[0]);
	}
	
}
