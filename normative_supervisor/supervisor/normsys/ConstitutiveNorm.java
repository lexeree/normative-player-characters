package supervisor.normsys;

import java.util.ArrayList;

/**
 * Class defining a constitutive norm object.
 * 
 * @author emery
 *
 */

public class ConstitutiveNorm extends Norm {
	ArrayList<Term> countsAs;
	Term as;
	String type;

	public ConstitutiveNorm(String nm, ArrayList<Term> cons, ArrayList<Term> cA, Term a) {
		super(nm, cons);
		countsAs = cA;
		as = a;
		if(as.isAction()) {
			type = "action";
		}
		else {
			type = "state";
		}
		
	}
	
		
	public ArrayList<Term> getLowerTerms(){
		return countsAs;
	}
	
	
	public Term getHigherTerm(){
		return as;
	}
	
	
	
	/*
	//debugging method
	public void printNorm() {
		System.out.println("Norm: "+name);
		for(Term t : context) {
			System.out.print(t.getModes().toString() + " ");
			System.out.print(t.getLabel()+ ", ");
			if(t.isPredicate()) {
				System.out.print("("+t.getBaseObject()+", "+t.getSateliteObject()+")");
			}
		}
		System.out.print("  -->  ");
		for(Term t : countsAs) {
			System.out.print(t.getModes().toString() + " ");
			System.out.print(t.getLabel()+ ", ");
			if(t.isPredicate()) {
				System.out.print("("+t.getBaseObject()+", "+t.getSateliteObject()+")");
			}
		}
		System.out.print("  =>  ");
		System.out.println(as.getLabel());
	}*/
	
	

}
