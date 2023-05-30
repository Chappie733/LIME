package math.representation.operators;

import math.representation.FunctionOperator;

public class CotOperator extends FunctionOperator {

	@Override
	public double apply(double... args) {
		return 1/Math.tan(args[0]);
	}

	
	
}
