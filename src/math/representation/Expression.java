package math.representation;

import java.util.HashMap;
import java.util.LinkedList;

import math.representation.operators.DivOperator;
import math.representation.operators.MulOperator;
import math.representation.operators.PowOperator;
import utils.Utils;

public class Expression extends MathObject implements Numeric {

	private MathObject head; // elemento "testa" dell'albero, è collegato a ogni cosa che viene dopo
	private FunctionOperator func_operator; // operators only applied to this element
	private char variables[];
	private String expression;
	
	public Expression() {
		variables = new char[0];
	}
	
	public Expression(MathElement head) {
		this.head = head;
		variables = new char[0];
	}
	
	public Operator getPreviousOperator() { return (Operator) previous; }
	public Operator getNextOperator() { return (Operator) next; }
	public FunctionOperator getFunctionOperator() { return func_operator; }
	public void setFunctionOperator(FunctionOperator op) { func_operator = op; }
	public boolean hasOperator() { return func_operator != null; }
	
	// ritorna se un determinato carattere è segnato come una variabile
	public boolean isVariable(char x) { 
		for (int idx = 0; idx < variables.length; idx++)
			if (variables[idx] == x)
				return true;
		return false;
	}
	// setta un determinato carattere come una variabile
	public void addVariable(char var) {
		char[] vars = new char[variables.length+1];
		for (int idx = 0; idx < variables.length; idx++)
			vars[idx] = variables[idx];
		
		vars[variables.length] = var;
		variables = vars;
	}
	public void setVariables(char[] variables) { this.variables = variables; }
	public void addAllVariables(char[] vars) {
		char[] new_variables = new char[variables.length+vars.length];
		
		for (int idx = 0; idx < variables.length; idx++)
			new_variables[idx] = variables[idx];		
		for (int idx = 0; idx < vars.length; idx++)
			new_variables[variables.length+idx] = vars[idx];
		
		variables = new_variables;
	}

	// TODO: sistemarla
	@Override
	public String toString() {
		return expression;
	}
	
	/* crea la rappresentazione dell'equazione a partire dall'espressione matematica
	 * La stringa che prende può essere qualcosa come "x^2 + 2" e da questo deve generare
	 * i MathElements di conseguenza
	 */
	public void build(String expression) throws Exception {
		this.expression = expression;
		func_operator = null;
		expression = expression.replaceAll(" ", ""); // rimuovi gli spazi
		// serve per riconoscere se l'ultimo carattere è una variabile non definita o qualcosa di irregolare
		expression += " "; 
		int idx = -1;
		String curr_token = "";
		MathObject curr_element = null;
		while (idx < expression.length()-1) {
			idx++;
			curr_token += expression.charAt(idx);
			
			// CONTROLLARE SE E' UNA FUNZIONE
			if (curr_token.length() > 1) {
				int func_operator_idx = FunctionOperator.getFunctionOperatorIndex(curr_token);
				// se non è un operatore vai al prossimo carattere
				if (func_operator_idx == -1) {
					if (!FunctionOperator.isPartOfOperator(curr_token))
						throw new Exception("Invalid text: \"" + curr_token + "\"");
					
					continue;
				}
				
				FunctionOperator func_op = FunctionOperator.getFunctionOperatorFromIndex(func_operator_idx, curr_element);
				String function_arg = Utils.getParenthesisContent(expression, idx+1); // aspettandosi che expression[idx+1] sia '('
				
				Expression expr = buildSubExpression(function_arg);
				expr.setFunctionOperator(func_op);
				
				// aggiorna l'elemento corrente/segna testa
				if (head == null) {
					head = expr;
					curr_element = expr;
				}
				else {
					MathObject.buildLink(curr_element, expr);
					curr_element = expr;
				}
				expr.prune();
				idx += function_arg.length()+2;
				curr_token = "";
				continue;
			}
			
			// CONTROLLARE SE C'E' UNA PARENTESI
			if (curr_token.equals("(")) {		
				String parenthesis_content = Utils.getParenthesisContent(expression, idx);
				Expression expr = buildSubExpression(parenthesis_content);
				
				// aggiorna l'elemento corrente/segna testa
				if (head == null) {
					head = expr;
					curr_element = expr;
				}
				else {
					MathObject.buildLink(curr_element, expr);
					curr_element = expr;
				}
				expr.prune();
				idx += parenthesis_content.length()+1;
				curr_token = "";
				continue;
			}
			
			
			// se il token corrente è un numero
			if (Utils.isNumeric(curr_token)) {
				while (idx+1 < expression.length() && Utils.isNumeric(curr_token + expression.charAt(idx+1))) {
					idx++;
					curr_token += expression.charAt(idx);
				}
				
				MathElement ME = new MathElement(curr_token);
				if (head == null) {
					head = ME;
					curr_element = ME;
				}
				else {
					MathObject.buildLink(curr_element, ME);
					curr_element = ME;
				}
				curr_token = "";
				continue;
			}
			
			int std_operator_idx = Operator.getStandardOperatorIndex(curr_token);
			// se è un'operatore comune, come +, -, * ecc.
			if (std_operator_idx != -1) {
				Operator op = Operator.getOperatorFromIndex(std_operator_idx, curr_element);
				if (head == null) {
					OperatorType op_type = op.getType();
					if (op_type != OperatorType.SUM && op_type != OperatorType.SUB) {
						System.err.println("Error whilst trying to load expression \"" + expression + "\": cannot start the expression with an operator different from plus or minus");
						return;
					}
					
					head = op;
					curr_element = op;
				}
				else {
					MathObject.buildLink(curr_element, op);
					curr_element = op;
				}
				curr_token = "";
				continue;
			}
			
			char ch = expression.charAt(idx);
			if (Utils.contains(variables, ch)) {
				MathElement ME = new MathElement(curr_token);
				if (head == null) {
					head = ME;
					curr_element = ME;
				}
				else {
					MathObject.buildLink(curr_element, ME);
					curr_element = ME;
				}
				curr_token = "";
				continue;
			}
			
		}
		
		lookForImplicitOperators();
		solveOperatorOrder();
	}
	
	
	// aggiunge un Operator (MUL) dove, nel link tra testa e successivi, ci sono due MathObject consecutivi entrambi diversi dal tipo Operator
	// per esempio in "x(x-4)" dopo il ME di x c'è direttamente un'espressione di x-4, in mezzo questa funzione mette un operator (MUL)
	private void lookForImplicitOperators() {
		MathObject curr_obj = head;
		while (curr_obj.next != null) {
			boolean curr_is_op = (curr_obj instanceof Operator);
			boolean next_is_op = (curr_obj.next instanceof Operator);
			
			// due non operatori di fila --> inserire l'operatore in mezzo
			if (!curr_is_op && !next_is_op) {
				Operator mul_op = new MulOperator();
				mul_op.setNext(curr_obj.next);
				mul_op.setPrevious(curr_obj);
				
				MathObject curr_obj_next = curr_obj.getNext();
				curr_obj.setNext(mul_op);
				curr_obj_next.setPrevious(mul_op);
			}
			curr_obj = curr_obj.next;
		}
	} 
	
	/* raggruppa certe parti dell'espressione in sottoespressioni in modo da rispettare l'ordine delle operazioni
	   per quanto riguarda le potenze, per esempio, utilizzando un ordine sequenziale nel modo in cui sono processati
	   gli operatori, in log(3x^2) con x = 4 verrebbe fatto log(3x^2) = log(3*4^2) = log(12^2) = log(144).
	   Questa funzione crea un'espressione con x^2 e la sostituisce dopo 3
	*/
	private void solveOperatorOrder() {
		MathObject curr = head;
		while (curr.next != null) {
			curr = curr.next;
			if (!(curr instanceof PowOperator || curr instanceof MulOperator || curr instanceof DivOperator))
				continue;
			
			// COSTRUZIONE SOTTO ESPRESSIONE
			MathObject exponent = curr.getNext();
			MathObject base = curr.getPrevious(); 
			Expression sub_expr = new Expression();
			
			MathObject expr_prev = base.getPrevious(); // salva i link necessari per la nuova espressione
			MathObject expr_next = exponent.getNext();
			exponent.next = null; // cancella i link dagli elementi interni all'espressione a quelli interni
			base.previous = null;
			
			sub_expr.setHead(base);
			
			// RIMPIAZZO SOTTO ESPRESSIONE IN QUESTA ISTANZA
			MathObject.buildLink(expr_prev, sub_expr);
			MathObject.buildLink(sub_expr, expr_next);
			
			curr = sub_expr;
			if (expr_prev == null)
				head = sub_expr;
		}
	}
	
	// pruning molto semplice, se un'espressione contiene solo un MathElement senza operatori viene trasformata in quel MathElement
	public void prune() {
		if (!(head instanceof Expression))
			return;
		Expression h_expr = (Expression) head;
		
		if (!h_expr.getHead().isUnlinked() || h_expr.hasOperator())
			return;

		MathObject head_next = h_expr.next;
		MathObject head_previous = h_expr.previous;
		head = h_expr.getHead();
		head.setNext(head_next);
		head.setPrevious(head_previous);
	}
	
	// costruisce una sotto-espressione (espressione con le stesse variabili) a partire dal testo
	private Expression buildSubExpression(String expression) throws Exception {
		Expression expr = new Expression();
		expr.setVariables(variables);
		expr.build(expression);
		return expr;
	}
	
	public double getValue(HashMap<String, Double> values) {
		double val = 0;
		MathObject curr = null;
		if (head instanceof Operator)
			curr = head;
		else {
			val = Expression.getValue(head, values);
			if (head.next != null)
				curr = head.next;
			else
				return (func_operator != null)?func_operator.apply(val):val;
		}

		while (true) {
			MathObject curr_next = curr.next;
			Operator op = (Operator) curr;
			double next_val = Expression.getValue(curr_next, values);
			val = op.apply(val, next_val);
			
			if (curr_next.next == null)
				break;
			curr = curr_next.next;
		}
		
		if (func_operator != null)
			val = func_operator.apply(val);
		
		return val;
	}
	
	public double getValue(char var, double value) {
		double val = 0;
		MathObject curr = null;
		if (head instanceof Operator)
			curr = head;
		else {
			val = Expression.getValue(head, var, value);
			if (head.next != null)
				curr = head.next;
			else
				return (func_operator != null)?func_operator.apply(val):val;
		}

		while (true) {
			MathObject curr_next = curr.next;
			Operator op = (Operator) curr;
			double next_val = Expression.getValue(curr_next, var, value);
			val = op.apply(val, next_val);
			
			if (curr_next.next == null)
				break;
			curr = curr_next.next;
		}
		
		if (func_operator != null)
			val = func_operator.apply(val);
		
		return val;
	}
	
	public String[] getUsedVariables(String[] defined_variables) {
		return Expression.getUsedVariables(expression, defined_variables);
	}
	
	public void setHead(MathObject head) { this.head = head; }
	public MathObject getHead() { return head; }
	public String getExpression() { return expression; }
	
	public static double getValue(MathObject obj, HashMap<String, Double> variables) {
		if (obj instanceof MathElement) {
			MathElement obj_ME = (MathElement) obj;
			return obj_ME.getValue(variables);
		}
		else if (obj instanceof Expression) {
			Expression obj_E = (Expression) obj;
			return obj_E.getValue(variables);
		}
		
		throw new IllegalArgumentException("in the public static method getValue() the MathObject passed has to either be a MathElement or an Expression");
	}
	
	public static double getValue(MathObject obj, char var, double val) {
		if (obj instanceof MathElement) {
			MathElement obj_ME = (MathElement) obj;
			return obj_ME.getValue(var, val);
		}
		else if (obj instanceof Expression) {
			Expression obj_E = (Expression) obj;
			return obj_E.getValue(var, val);
		}
		throw new IllegalArgumentException("in the public static method getValue() the MathObject passed has to either be a MathElement or an Expression");
	}
	
	// ritorna una lista che comprende le variabili in "defined_variables" utilizzate nell'espressione, ossia una sottolista di defined_variables
	public static String[] getUsedVariables(String expression, String[] defined_variables) {
		expression = expression.replaceAll("[\\-\\+\\*\\/\\^\\(\\)]", "");
		expression = expression.replaceAll("\\d", "");
		for (String func_op : FunctionOperator.functions)
			expression = expression.replaceAll(func_op, "");
		
		LinkedList<String> used_variables = new LinkedList<String>();
		for (String var : defined_variables)
			if (expression.contains(var))
				used_variables.add(var);
		
		
		return used_variables.toArray(new String[]{});
	}
	
}
