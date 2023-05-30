package main;

public enum StateType {
	GRAPH_VIEW(0),
	GRAPH_SCREENSHOTTING(1);
	
	int id;
	
	StateType(int id) {
		this.id = id;
	}
}
