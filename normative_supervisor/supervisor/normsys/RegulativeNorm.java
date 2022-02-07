package supervisor.normsys;

import java.util.ArrayList;

/**
 * Class defining a Regulative Norm object.
 * 
 * @author emery
 *
 */

public class RegulativeNorm extends Norm {
	Term prescription;

	public RegulativeNorm(String nm, ArrayList<Term> cons, Term pres) {
		super(nm, cons);
		prescription = pres;
	}
	
	
	public Term getPrescription() {
		return prescription;
	}
	
	
	
	//a debugging method
	/*
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
		System.out.print(prescription.getModes().toString() + " ");
		System.out.print(prescription.getLabel()+ ", ");
		System.out.println("("+prescription.getBaseObject()+", "+prescription.getSateliteObject()+")");
	} */

}
