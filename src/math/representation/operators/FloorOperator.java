package math.representation.operators;

import math.representation.FunctionOperator;

public class FloorOperator extends FunctionOperator {

	@Override
	public double apply(double... args) {
		return Math.floor(args[0]);
	}

	
	
}
