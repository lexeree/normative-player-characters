package filter;

import java.util.ArrayList;

public class State {
	ArrayList<String> labels;
	String action;
	int violations;
	
	public State(ArrayList<String> lab, String act, int viol) {
		labels  = lab;
		action = act;
		violations = viol;
	}
	
	public ArrayList<String> getLabels(){
		return labels;
	}
	
	public String getAction() {
		return action;
	}
	
	public int getViolations() {
		return violations;
	}

}
