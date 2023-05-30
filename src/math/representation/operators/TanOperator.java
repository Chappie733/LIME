package math.representation.operators;

import math.representation.FunctionOperator;

public class TanOperator extends FunctionOperator {

	@Override
	public double apply(double... args) {
		return Math.tan(args[0]);
	}

}
