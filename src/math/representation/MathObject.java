
package math.representation;

/*
 * Written in 19/03/2023 by Nicola Trombini
 * */
public abstract class MathObject {
	
	protected MathObject next, previous;
	
	public MathObject(MathObject previous, MathObject next) {
		this.next = next;
		this.previous = previous;
	}

	public MathObject(MathObject previous) {
		this.previous = previous;
	}
	
	public MathObject() {}
	
	public MathObject getNext() { return next; }
	public MathObject getPrevious() { return previous; }
	public void setNext(MathObject next) { this.next = next; }
	public void setPrevious(MathObject previous) { this.previous = previous; }
	public boolean isUnlinked() { return previous == null && next == null; }
	
	public abstract String toString();
	
	public static void buildLink(MathObject from, MathObject to) {
		if (from == null || to == null)
			return;
		
		from.setNext(to);
		to.setPrevious(from);
	}
	
}
