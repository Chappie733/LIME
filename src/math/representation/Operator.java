package math.representation;

import math.representation.operators.DivOperator;
import math.representation.operators.MulOperator;
import math.representation.operators.PowOperator;
import math.representation.operators.SubOperator;
import math.representation.operators.SumOperator;

public abstract class Operator extends MathObject {

	private OperatorType type;

	public Operator(OperatorType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "Operator [" + type.symbol + "]\n" + ((next!=null)?next.toString():"");
	}
	
	public OperatorType getType() { return type; }
	
	public abstract double apply(double... vals);
	
	/*
	 * Ritorna l'indice dell'operatore specificato nella stringa secondo l'ordine dell'enumeratore OperatorType, tenendo
	 * conto solo degli operatori standard (+, -, *, /, ^), se la stringa non è un operatore standard ritorna -1
	 */
	public static int getStandardOperatorIndex(String str) {
		OperatorType operator_types[] = OperatorType.values();
		for (int i = 0; i < operator_types.length; i++) {
			String op_symbol = operator_types[i].symbol;
			if (op_symbol.equals(str) && !op_symbol.equals("func"))
				return i;
		}
		return -1;
	}
	
	public static Operator getOperatorFromIndex(int idx, MathObject prev) {
		Operator op;
		switch (idx) {
		case 0:
			op = new SumOperator();
			break;
		case 1:
			op = new SubOperator();
			break;
		case 2:
			op = new MulOperator();
			break;
		case 3:
			op = new DivOperator();
			break;
		case 4:
			op = new PowOperator();
			break;
		default:
			op = new SumOperator();
			System.err.println("The index specified in getOperatorFromIndex does not represent an operator");
			break;
		}
		op.setPrevious(prev);
		return op;
	}

}
