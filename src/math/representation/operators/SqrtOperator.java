package math.representation.operators;

import math.representation.FunctionOperator;

public class SqrtOperator extends FunctionOperator {

	@Override
	public double apply(double... args) {
		return Math.sqrt(args[0]);
	}
	
	
	
}
