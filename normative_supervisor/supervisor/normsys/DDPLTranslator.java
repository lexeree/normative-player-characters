package supervisor.normsys;

import java.util.ArrayList;

import spindle.core.dom.Literal;
import spindle.core.dom.Mode;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleException;
import spindle.core.dom.RuleType;
import spindle.core.dom.Superiority;
import supervisor.games.Environment;
import supervisor.games.Game;

/**
 * Translator from norms to defeasible deontic logic facts/rules. Should only be used with DDPLReasoner class.
 * 
 * @author emery
 *
 */

public class DDPLTranslator extends Translator {
	protected Mode obl = new Mode("O", false);
	protected Mode perm = new Mode("P", false);
	protected ArrayList<Literal> actionLits = new ArrayList<Literal>();
	protected ArrayList<Literal> gameLits = new ArrayList<Literal>();
	protected ArrayList<Rule> facts = new ArrayList<Rule>();
	protected ArrayList<Rule> rules = new ArrayList<Rule>();
	protected ArrayList<Rule> norms = new ArrayList<Rule>();
	protected ArrayList<Rule> actionRules = new ArrayList<Rule>();
	protected ArrayList<Rule> strategies = new ArrayList<Rule>();
	protected ArrayList<Superiority> hierarchy = new ArrayList<Superiority>();

	public DDPLTranslator(NormBase nb) {
		super(nb);
	}

	@Override
	public void init(Environment env, ArrayList<String> actions) {
		generateActions(actions);
	}

	@Override
	public void update(Environment env, ArrayList<String> possible, Game game) {
		labelsToFacts(env);
		generateActionNorms(possible);
		generateConstitutiveRules(env);
		generateRegulativeRules();
		generateDefeaters();
		generateHierarchies();

	}
	
	
	public void synth_update(Environment env) {
		labelsToFacts(env);
		generateConstitutiveRules(env);
		generateRegulativeRules();
		generateDefeaters();
		generateHierarchies();
	}
	
	
	
	public void generateActionNorms(ArrayList<String> actions) {
			normBase.generateNonConcurrence(actions);
			//normBase.generateRequired(actions);
			generateActions(actions);
	}
	
	public Literal termToLit(Term term, boolean head) {
		Literal lit = new Literal(term.getLabel());
		if(term.getModes().contains(Modality.OBLIGATION)) {
			if(!head) {
				lit.setMode(obl);
			}
		} 
		else if (term.getModes().contains(Modality.PROHIBITION)) {
			lit = lit.getComplementClone();
			if(!head) {
				lit.setMode(obl);
			}
		}
		else if (term.getModes().contains(Modality.PERMISSION)) {
			if(!head) {
				lit.setMode(perm);
			}
		}
		if(term.isNegated()) {
			lit = lit.getComplementClone();
		}
		return lit;
	}
	
	public void termToFact(Term term) {
		Literal lit = termToLit(term, false);
		Rule fact = new Rule(term.getLabel(), RuleType.FACT);
		try {
			fact.addHeadLiteral(lit);
		} catch (RuleException e) {
			e.printStackTrace();
		}
		facts.add(fact);
	}
	
	public void labelsToFacts(Environment env) {
		facts.clear();
		for(String lab : env.getPosLabels()) {
			Literal lit = new Literal(lab);
			Rule fact = new Rule(lab, RuleType.FACT);
			try {
				fact.addHeadLiteral(lit);
			} catch (RuleException e) {}
			facts.add(fact);
		}
		for(String lab : env.getNegLabels()) {
			Literal lit = new Literal(lab);
			Literal nlit = lit.getComplementClone();
			Rule fact = new Rule(lab, RuleType.FACT);
			try {
				fact.addHeadLiteral(nlit);
			} catch (RuleException e) {}
			facts.add(fact);
		}
	}

	@Override
	public void generateActions(ArrayList<String> actions) {
		for(Term act : normBase.generateActionTerms(actions)) {
			actionLits.add(termToLit(act, false));
		}
	}
	
	public void generateStateConstitutiveRules() {
		try {
			ArrayList<ConstitutiveNorm> constNorms = normBase.getStateConstitutiveNorms();
			for(ConstitutiveNorm n : constNorms) {
				Rule rule = new Rule(n.getName(), RuleType.STRICT);
				ArrayList<Term> antecedents = new ArrayList<Term>();
				antecedents.addAll(n.getContext());
				antecedents.addAll(n.getLowerTerms());
				for(Term term : antecedents) {
					Literal lit = termToLit(term, false);
					rule.addBodyLiteral(lit);
				}
				Literal lit = termToLit(n.getHigherTerm(), true);
				rule.addHeadLiteral(lit);
				rules.add(rule);
			}
			
		}
		catch (RuleException e) {
			e.printStackTrace();
		}
	}
	

	public void generateActionConstitutiveRules() {
		try {
			ArrayList<ConstitutiveNorm> actNorms = normBase.getActionConstitutiveNorms();
			for(ConstitutiveNorm n : actNorms) {
				Rule rule1 = new Rule("pos:"+n.getName(), RuleType.DEFEASIBLE);
				Rule rule2 = new Rule("neg:-"+n.getName(), RuleType.DEFEASIBLE);
				/*if(n.getName().contains("strategy")) {
					rule1 = new Rule("pos:"+n.getName(), RuleType.STRICT);
					rule2 = new Rule("neg:-"+n.getName(), RuleType.STRICT);
				} else {
					rule1 = new Rule("pos:"+n.getName(), RuleType.DEFEASIBLE);
					rule2 = new Rule("neg:-"+n.getName(), RuleType.DEFEASIBLE);
				}*/
				for(Term term : n.getContext()) {
					Literal lit = termToLit(term, false);
					rule1.addBodyLiteral(lit);
					rule2.addBodyLiteral(lit);
				}
				//positive
				for(Term term : n.getLowerTerms()) {
					Literal lit1 = termToLit(term, false);
					lit1.setMode(obl);
					rule1.addBodyLiteral(lit1);
				}
				Literal head1 = termToLit(n.getHigherTerm(), true);	
				rule1.addHeadLiteral(head1);
				rule1.setMode(obl);
				rules.add(rule1);
				strategies.add(rule1);
				//contrapositive
				Literal lit2 = termToLit(n.getHigherTerm(), false).getComplementClone();
				lit2.setMode(obl);
				rule2.addBodyLiteral(lit2);
				for(Term term : n.getLowerTerms()) {
					Literal head2 = termToLit(term, false).getComplementClone();
					rule2.addHeadLiteral(head2);
					rule2.setMode(obl);
					rules.add(rule2);
					strategies.add(rule2);
				}
			}
		}
		catch (RuleException e) {
			e.printStackTrace();
		}
	}
	

    public void generateConstitutiveRules(Environment board) {
    	rules.clear();
    	generateStateConstitutiveRules();
    	generateActionConstitutiveRules();
    }
		
	
 
	public void generateRegulativeRules() {
		ArrayList<RegulativeNorm> regNorms = normBase.getRegulativeNorms();
        for(RegulativeNorm n : regNorms) {
			Rule rule = new Rule(n.getName(), RuleType.DEFEASIBLE);
			try {
				for(Term term : n.getContext()) {
					Literal lit = termToLit(term, false);
					rule.addBodyLiteral(lit);
				}
				Literal lit = termToLit(n.getPrescription(), true);
				rule.addHeadLiteral(lit);
				if(n.getName().contains("default")) {
					for(Rule r : rules) {
						if(r.isConflictRule(rule)) {
							Superiority sup = new Superiority(r.getLabel(),rule.getLabel());
							hierarchy.add(sup);
						}
					}
				}
				else {
				    rule.setMode(obl);
				}
				if(n.getName().contains("concur") || n.getName().contains("req")) {
					rule.setRuleType(RuleType.DEFEASIBLE);
					actionRules.add(rule.clone());
				}
				else {
					norms.add(rule);
				}
			}
			catch (RuleException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateDefeaters() {
		ArrayList<ExceptionNorm> permNorms = normBase.getExceptionNorms();
        for(ExceptionNorm n : permNorms) {
        	Rule permr = new Rule(n.getName(), RuleType.DEFEASIBLE);
			Rule rule = new Rule("~"+n.getName(), RuleType.DEFEATER);
			try {
				for(Term term : n.getContext()) {
					Literal lit = termToLit(term, false);
					permr.addBodyLiteral(lit);
				}
				Literal lit = termToLit(n.getException(), true);
				permr.addHeadLiteral(lit);
				permr.setMode(perm);
				norms.add(permr);
				rule.addBodyLiteral(lit.cloneWithMode(perm));
				rule.addHeadLiteral(lit.getComplementClone());
				//rule.setMode(obl);
				norms.add(rule);
			}
			catch (RuleException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void generateHierarchies() {
		for(PriorityNorm pr : normBase.getPriorityNorms()) {
			Superiority sup = new Superiority(pr.getHigher(),pr.getLower());
			hierarchy.add(sup);
		}
	}
	

	
	public ArrayList<Literal> getActionLits(){
		return actionLits;
	}
	
	public ArrayList<Rule> getFacts(){
		return facts;
	}
	
	public ArrayList<Rule> getRules(){
		return rules;
	}
	
	public ArrayList<Rule> getActionRules(){
		return actionRules;
	}
	
	public ArrayList<Rule> getNorms(){
		return norms;
	}
	
	public ArrayList<Rule> getStrategies(){
		return strategies;
	}
	
	public ArrayList<Superiority> getHierarchy(){
		return hierarchy;
	}
	
	
	public void clear() {
		facts.clear();
		rules.clear();
		norms.clear();
	}

}
