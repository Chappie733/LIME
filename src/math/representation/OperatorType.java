package math.representation;

public enum OperatorType {
	SUM("+"),
	SUB("-"),
	MUL("*"),
	DIV("/"),
	POW("^");
	
	public String symbol;
	
	OperatorType(String symbol) {
		this.symbol = symbol;
	}
	
}
