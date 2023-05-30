package math.representation.operators;

import math.representation.FunctionOperator;

public class CosOperator extends FunctionOperator {

	@Override
	public double apply(double... args) {
		return Math.cos(args[0]);
	}

}
