package gfx.widgets.standard;

//se è ticked_hovered e viene cliccata ripassa a clicked
public enum TickBoxState {
	DEFAULT(0),
	HOVERED(1),
	CLICKED(2),
	TICKED(3),
	TICKED_HOVERED(4);
	
	public int index;
	
	TickBoxState(int index) { this.index = index; }
}