package math.representation.operators;

import math.representation.Operator;
import math.representation.OperatorType;

public class SubOperator extends Operator {

	public SubOperator() {
		super(OperatorType.SUB);
	}
	
	public double apply(double... args) {
		return args[0] - args[1];
	}

}
