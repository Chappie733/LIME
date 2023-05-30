package math.representation.operators;

import math.representation.FunctionOperator;

public class LnOperator extends FunctionOperator {

	@Override
	public double apply(double... args) {
		return Math.log(args[0]);
	}

	
	
}
