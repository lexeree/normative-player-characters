package synthesis;

public class Transition {
	String action;
	String initial;
	String next;
	
	public Transition(String a, String s1, String s2) {
		action = a;
		initial = s1;
		next = s2;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getTransition() {
		return initial + " && X(" + next + ")";
	}

}
