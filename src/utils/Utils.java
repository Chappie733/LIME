package utils;

import java.awt.Graphics2D;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import math.primitives.Vector2i;

public class Utils {
	
	public static boolean isNumeric(String str) {
		// se la stringa finisce con un punto, tipo "3.", voglio rimuovere quel punto in modo che anche "3." sia numerico
		if (str.charAt(str.length()-1) == '.')
			str = str.substring(0, str.length()-1);
		
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	// ritorna l'indice dell'elemento element in arr, se l'elemento non è presente ritorna -1
	public static <T> int indexOf(T element, T[] arr) {
		for (int idx = 0; idx < arr.length; idx++)
			if (arr[idx].equals(element))
				return idx;
		return -1;
	}
	
	// ritorna l'indice dell'elemento element in arr, se l'elemento non è presente ritorna -1
	public static int indexOf(int element, int[] arr) {
		for (int idx = 0; idx < arr.length; idx++)
			if (arr[idx] == element)
				return idx;
		return -1;
	}
	
	// ritorna se il dato vettore di char contiene l'elemento specificato
	public static boolean contains(char[] arr, char element) {
		for (int idx = 0; idx < arr.length; idx++)
			if (arr[idx] == element)
				return true;
		return false;
	}
	
	
	/* Data una stringa e l'indice di una parentesi trova dove si chiude e ritorna
	 * il suo contenuto come una stringa, se la parentesi non viene chiusa ritorna la
	 * stringa così come è stata passata
	 */
	public static String getParenthesisContent(String str, int parenthesis_idx) {
		str = str.substring(parenthesis_idx);
		int curr_open = 0; // conto di quante altre parentesi sono state chiuse
		for (int c_idx = 0; c_idx < str.length(); c_idx++) {
			char c = str.charAt(c_idx);
			if (c == '(')
				curr_open++;
			else if (c == ')') {
				curr_open--;
				if (curr_open == 0)
					return str.substring(1, c_idx);
			}
		}
		return str;
	}
	
	public static double round(double val, int num_digits) {
		return Math.round(Math.pow(10, num_digits) * val)/Math.pow(10, num_digits);
	}
	
	public static int randint(int min, int max) {
		return (int) Math.random()*(max-min) + min;		
	}
	
	public static int randint(int max) {
		return (int) Math.random()*max;
	}
	
	// ritorna un valore all'interno dell'intervallo [min, max], usando val se possibile, e altrimenti usando l'estremo più vicino
	public static double clamp(double val, double min, double max) {
		return Math.min(Math.max(val, min), max);
	}
	
	public static double log(double val, double base) {
		return Math.log10(val)/Math.log10(base);
	}
	
	public static String removeUnnecessaryZeros(String decimal_num) {
		int point_index = decimal_num.indexOf('.');
		if (point_index == -1) {
			System.err.println("Error in function \"removeUnnecessaryZeros\": the argument is invalid, as it isn't a decimal number");
			return decimal_num;
		}
		
		for (int idx = decimal_num.length()-1; idx > point_index; idx--) {
			if (decimal_num.charAt(idx) != '0')
				return decimal_num.substring(0, idx+1);
		}
		return decimal_num.substring(0, point_index);
	}
	
	public static int getNumDecimals(double val) {
		String val_str = removeUnnecessaryZeros(String.valueOf(val));
		if (val_str.indexOf('.') == -1)
			return 0;
			
		return val_str.length() - (val_str.indexOf('.') + 1);
	}

	public static char[] toCharArray(String[] arr) {
		char[] new_arr = new char[arr.length];
		for (int idx = 0; idx < arr.length; idx++)
			new_arr[idx] = arr[idx].charAt(0);
		return new_arr;
	}
	
	// concatena il vettore b alla fine di a e ritorna un unico vettore che comprende gli elementi di entrambi
	public static <T> T[] concatenate(T[] a, T[] b) {
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length);
		for (int idx = 0; idx < a.length; idx++)
			arr[idx] = a[idx];
		
		for (int idx = 0; idx < b.length; idx++)
			arr[idx+a.length] = b[idx];
		
		return arr;	
	}
	
	/*
	 * Trasforma una lista di un tipo generico in un vettore, se la lista è vuota prova a creare una lista vuota di oggetti e fare
	 * un casting esplicito all'oggetto generico passato, nonostante ciò potrebbe chiaramente fallire
	 * */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(List<T> list) {
		if (list.size() == 0)
			return (T[]) new Object[0];
		
		int list_size = list.size();
		T[] arr = (T[]) Array.newInstance(list.get(0).getClass().getComponentType(), list_size);
		for (int idx = 0; idx < list_size; idx++)
			arr[idx] = list.get(idx);
		return arr;
	}
	
	// disegna una freccia con l'oggetto Graphics2D passato dalle coordinate specificate dal vettore "start" a quelle in "end", entrambe specificate sullo schermo
	public static void drawArrow(Graphics2D g, Vector2i start, Vector2i end) {
		Vector2i diff_vec = Vector2i.sub(end, start);
		double screen_vector_mag = diff_vec.getMagnitude();
		double vec_angle = diff_vec.getAngle();
		double tick_length = 2*Math.log(screen_vector_mag+1);
		
		Vector2i arrow_tick_end0 = Vector2i.add(end, Vector2i.fromPolarCoordinates(tick_length, -Math.PI*3/4 + vec_angle));
		Vector2i arrow_tick_end1 = Vector2i.add(end, Vector2i.fromPolarCoordinates(tick_length, -Math.PI*5/4 + vec_angle));
		
		g.drawLine(end.x, end.y, arrow_tick_end0.x, arrow_tick_end0.y);
		g.drawLine(end.x, end.y, arrow_tick_end1.x, arrow_tick_end1.y);
	}
	
	public static String getClassName(String class_path) {
		int idx = class_path.lastIndexOf(".");
		return class_path.substring(idx+1);
	}
	
	// ritorna un vettore sottoinsieme di un altro dati gli indici di inizio e di fine che lo specificano
	@SuppressWarnings("unchecked")
	public static <T> T[] getSubArray(T[] arr, int begin, int end) {
		if (begin < 0 || begin >= arr.length || end > arr.length) {
			System.err.println("Error occurred in function \"getSubArray\": the indices specified are invalid!");
			return null;
		}
		if (begin == end) {
			return (T[]) Array.newInstance(arr[0].getClass().getComponentType(), 0);
		}
		if (end < 0) {
			end %= arr.length;
			end = arr.length+end;
		}
		
		return Arrays.copyOfRange(arr, begin, end);
	}
	
}
