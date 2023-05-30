package math.representation;

import java.util.HashMap;

import utils.Utils;

public class MathElement extends MathObject implements Numeric {
	
	private String value;
	private boolean number;
	
	public MathElement(String value) {
		this.value = value;
		
		number = Utils.isNumeric(value);
	}
	
	public MathElement(MathObject previous, String value) {
		super(previous);
		this.value = value;
	}
	
	@Override
	public String toString() {
		String str = "MathElement [" + value + "]\n";
		return str + ((next!=null)?next.toString():"");
	}
	
	public double getValue(HashMap<String, Double> variables) {
		if (!number)
			return variables.get(value);
	
		return Double.parseDouble(value);
	}
	
	
	public double getValue(char var, double val) {
		if (!number) {
			if (var == value.charAt(0))
				return val;
			
			System.err.println("Variable \"" + value + "\" not passed when calling getValue() in MathElement");
			return -1;
		}
	
		return Double.parseDouble(value);
	}
	
	public boolean isNumber() { return number; } 
	
	
}
