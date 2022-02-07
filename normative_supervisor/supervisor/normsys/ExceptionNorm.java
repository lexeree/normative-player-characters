package supervisor.normsys;

import java.util.ArrayList;

/**
 * Class for representing strong permissions. May need to be expanded.
 * @author emery
 *
 */

public class ExceptionNorm extends Norm {
	Term exception;

	public ExceptionNorm(String nm, ArrayList<Term> cons, Term pres) throws Exception {
		super(nm, cons);
		if(pres.isAction()) {
			exception = pres;
		}
		else {
			throw new Exception("Cannot create norm,exception is not an action!");
		}
	}
	
	public Term getException() {
		return exception;
	}


}
