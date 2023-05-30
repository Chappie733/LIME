package math.representation.operators;

import math.representation.Operator;
import math.representation.OperatorType;

public class SumOperator extends Operator {

	public SumOperator() {
		super(OperatorType.SUM);
	}

	public double apply(double... vals) {
		return vals[0] + vals[1];
	}
	

}
