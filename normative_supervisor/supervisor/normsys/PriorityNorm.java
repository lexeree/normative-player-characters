package supervisor.normsys;

/**
 * Class for object that indicates whether one norm takes priority over the other (referencing the names).
 * 
 * @author emery
 *
 */

public class PriorityNorm {
	String name;
	String low;
	String high;

	public PriorityNorm(String nm, String l, String h) {
		name = nm;
		low = l;
		high = h;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLower() {
		return low;
	}
	
	public String getHigher() {
		return high;
	}

}
