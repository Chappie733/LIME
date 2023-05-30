package math.representation;

import math.representation.operators.CeilOperator;
import math.representation.operators.CosOperator;
import math.representation.operators.CotOperator;
import math.representation.operators.ExpOperator;
import math.representation.operators.FloorOperator;
import math.representation.operators.LnOperator;
import math.representation.operators.LogOperator;
import math.representation.operators.SinOperator;
import math.representation.operators.SqrtOperator;
import math.representation.operators.TanOperator;
import utils.Utils;

public abstract class FunctionOperator extends MathObject {
	public static final String[] functions = {"sqrt", "log", "exp", "ln", "cos", "sin", "tan", "cot", "arctan", "arccot", "arccos", "arcsin", "floor", "ceil"};
	
	public FunctionOperator() {}
	
	public static int getFunctionOperatorIndex(String token) {
		return Utils.indexOf(token, functions);
	}
	
	// returns whether the token is an incomplete part of an operator
	public static boolean isPartOfOperator(String token) {
		for (String func : functions)
			if (func.startsWith(token))
				return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "[Function]";
	}
	
	public abstract double apply(double... args);
	
	public static FunctionOperator getFunctionOperatorFromIndex(int idx, MathObject prev) {
		if (idx < 0 || idx >= functions.length)
			return null;
		
		FunctionOperator op = null;
		String operator_name = functions[idx];
		switch (operator_name) {
		case "sqrt":
			op = new SqrtOperator();
			break;
		case "log":
			op = new LogOperator();
			break;
		case "exp":
			op = new ExpOperator();
			break;
		case "ln":
			op = new LnOperator();
			break;
		case "cos":
			op = new CosOperator();
			break;
		case "sin":
			op = new SinOperator();
			break;
		case "tan":
			op = new TanOperator();
			break;
		case "cot":
			op = new CotOperator();
			break;
		case "floor":
			op = new FloorOperator();
			break;
		case "ceil":
			op = new CeilOperator();
			break;
		default:
			op = new LogOperator();
			System.err.println("The index specified in getFunctionOperatorFromIndex does not correspond to an operator");
			break;
		}
		return op;
	}

}
