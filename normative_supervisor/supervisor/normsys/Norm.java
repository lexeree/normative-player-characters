package supervisor.normsys;

import java.util.ArrayList;

/**
 * General class for norm objects; RegulativeNorm, ConstitutiveNorm, and ExceptionNorm extend it.
 * 
 * @author emery
 *
 */

public class Norm {
	String name;
	ArrayList<Term> context;
	int ord;

	

	public Norm(String nm, ArrayList<Term> cons) {
		name = nm;
		context = cons;
	}

	
	
	public ArrayList<Term> getContext() {
		return context;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public int getRank() {
		try {
			return ord;
		}
		catch(Exception e) {
			System.out.println("No rank!");
			return 0;
		}
	}
	
	
	public void addContext(Term term) {
		context.add(term);
	}
	
	
	public void setRank(int r) {
		ord = r;
	}
	

}
