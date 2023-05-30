package math.representation.operators;

import math.representation.FunctionOperator;

public class SinOperator extends FunctionOperator {

	@Override
	public double apply(double... args) {
		return Math.sin(args[0]);
	}

}
