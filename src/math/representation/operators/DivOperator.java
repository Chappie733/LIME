package math.representation.operators;

import math.representation.Operator;
import math.representation.OperatorType;

public class DivOperator extends Operator {

	public DivOperator() {
		super(OperatorType.DIV);
	}

	@Override
	public double apply(double... vals) {
		if (vals[1] == 0)
			return (vals[0] > 0)?Double.POSITIVE_INFINITY:Double.NEGATIVE_INFINITY;
		
		return vals[0]/vals[1];
	}

}
