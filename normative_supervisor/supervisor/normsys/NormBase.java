package supervisor.normsys;

import java.util.ArrayList;

/**
 * General norm base class containing the basics of what every norm base will need. 
 * Do not modify, but note that this will almost certainly need to be extended for any new norm bases.
 * 
 * @author emery
 *
 */

public class NormBase {
	protected ArrayList<ConstitutiveNorm> actionConstitutive;
	protected ArrayList<ConstitutiveNorm> stateConstitutive;
	protected ArrayList<RegulativeNorm> regulative;
	protected ArrayList<ExceptionNorm> exception;
	protected ArrayList<PriorityNorm> priority;
	protected String name;

	public NormBase(String n) {
		name = n;
		actionConstitutive = new ArrayList<ConstitutiveNorm>();
		stateConstitutive = new ArrayList<ConstitutiveNorm>();
		regulative = new ArrayList<RegulativeNorm>();
		exception = new ArrayList<ExceptionNorm>();
		priority = new ArrayList<PriorityNorm>();
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public Norm getNorm(String nm) {
		ArrayList<Term> al = new ArrayList<Term>();
		Norm norm = new Norm("", al);
		for (Norm n : actionConstitutive) {
			if(n.getName().equals(nm)) {
				norm = n;
			}
		}
		for (Norm n : stateConstitutive) {
			if(n.getName().equals(nm)) {
				norm = n;
			}
		}
		for (Norm n : regulative) {
			if(n.getName().equals(nm)) {
				norm = n;
			}
		}
		return norm;
	}
	
	public void deleteNorm(String nm) {
		actionConstitutive.removeIf(i -> i.getName().equals(nm));
		stateConstitutive.removeIf(i -> i.getName().equals(nm));
		regulative.removeIf(i -> i.getName().equals(nm));
	}
	
	
	public void addStateConstitutiveNorm(ConstitutiveNorm con) {
		stateConstitutive.add(con);
	}
	
	
	public void addActionConstitutiveNorm(ConstitutiveNorm con) {
		actionConstitutive.add(con);
	}
	
	
	public void addRegulativeNorm(RegulativeNorm reg) {
		regulative.add(reg);
	}
	
	public void addExceptionNorm(ExceptionNorm ex) {
		exception.add(ex);
	}
	
	public void addPriorityNorm(PriorityNorm pr) {
		priority.add(pr);
	}
	
	
	public ArrayList<ConstitutiveNorm> getStateConstitutiveNorms() {
		return stateConstitutive;
	}
	
	
	public ArrayList<ConstitutiveNorm> getActionConstitutiveNorms() {
		return actionConstitutive;
	}
	
	
	public ArrayList<RegulativeNorm> getRegulativeNorms() {
		return regulative;
	}
	
	public ArrayList<ExceptionNorm> getExceptionNorms() {
		return exception;
	}
	public ArrayList<PriorityNorm> getPriorityNorms() {
		return priority;
	}
	
	
	
	public ArrayList<Term> generateActionTerms(ArrayList<String> acts){
        ArrayList<Term> actions = new ArrayList<Term>();
        
        for(String act : acts) {
        	Term term = new Term(act, false, false, true);
        	actions.add(term);
        }
		return actions;
	}
	
	
	public void generateNonConcurrence(ArrayList<String> acts) {
        ArrayList<Term> actions = generateActionTerms(acts);
		for (Term action : actions) {
			ArrayList<Term> context = new ArrayList<Term>();
			ArrayList<Term> lower = new ArrayList<Term>();
			Term temp = action.copy();
			lower.add(temp);
			for(Term act : actions) {
				if(!act.equals(action)) {
					Term reg = new Term(act.getLabel(), true, false, true);
					try {
						ConstitutiveNorm noncon = new ConstitutiveNorm("non-concurrence("+action.getLabel()+","+act.getLabel()+")", context, lower, reg);
						addActionConstitutiveNorm(noncon);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public void generateRequired(ArrayList<String> acts) {
        ArrayList<Term> actions = generateActionTerms(acts);
        ArrayList<Term> context = new ArrayList<Term>();
        try {
        Term act = new Term("act", false, false, true);
        act.setMode(Modality.OBLIGATION);
        RegulativeNorm req = new RegulativeNorm("required(act)", context, act);
        addRegulativeNorm(req);
		for (Term action : actions) {
			Term reg = action.copy();
			reg.setMode(Modality.OBLIGATION);
			ArrayList<Term> asreg = new ArrayList<Term>();
			asreg.add(reg);
			ConstitutiveNorm actas = new ConstitutiveNorm("("+action.getLabel()+", act)", context, asreg, act);	
			addActionConstitutiveNorm(actas);
		}	
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	

}
