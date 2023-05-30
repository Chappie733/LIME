package gfx.widgets;

public enum WidgetState {
	DEFAULT(0),
	HOVERED(1),
	CLICKED(2);
	
	public int index;
	
	WidgetState(int index) {
		this.index = index;
	}
}