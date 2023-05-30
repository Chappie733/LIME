package math.representation.operators;

import math.representation.FunctionOperator;

public class CeilOperator extends FunctionOperator {

	@Override
	public double apply(double... args) {
		return Math.ceil(args[0]);
	}

}
