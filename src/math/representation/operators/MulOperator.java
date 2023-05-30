package math.representation.operators;

import math.representation.Operator;
import math.representation.OperatorType;

public class MulOperator extends Operator {

	public MulOperator() {
		super(OperatorType.MUL);
	}

	@Override
	public double apply(double... vals) {
		return vals[0]*vals[1];
	}

}
