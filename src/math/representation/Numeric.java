package math.representation;

import java.util.HashMap;

public interface Numeric {
	public double getValue(HashMap<String, Double> variables);
	public double getValue(char variable, double value);
}
