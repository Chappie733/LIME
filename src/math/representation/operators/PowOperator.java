package math.representation.operators;

import math.representation.Operator;
import math.representation.OperatorType;

public class PowOperator extends Operator {

	public PowOperator() {
		super(OperatorType.POW);
	}

	@Override
	public double apply(double... vals) {
		return Math.pow(vals[0], vals[1]);
	}

}
