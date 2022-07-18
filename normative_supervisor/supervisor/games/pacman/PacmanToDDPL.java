package supervisor.games.pacman;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.logging.Logger;

import spindle.core.dom.Literal;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleException;
import spindle.core.dom.RuleType;
import spindle.core.dom.Superiority;
import supervisor.games.Environment;
import supervisor.games.Game;
import supervisor.normsys.ConstitutiveNorm;
import supervisor.normsys.DDPLTranslator;
import supervisor.normsys.ExceptionNorm;
import supervisor.normsys.NormBase;
import supervisor.normsys.PriorityNorm;
import supervisor.normsys.RegulativeNorm;
import supervisor.normsys.Term;
import util.ProjectUtils;

/**
 * DDPLTranslator modified in order to accommodate parsing of the Pac-Man game.
 * 
 * @author emery
 *
 */

public class PacmanToDDPL extends DDPLTranslator {
	
	ProjectUtils util = new ProjectUtils();
	ArrayList<ArrayList<Literal>> foodLit = new ArrayList<ArrayList<Literal>>();
	ArrayList<ArrayList<Literal>> pacmanLit = new ArrayList<ArrayList<Literal>>();
	ArrayList<ArrayList<Literal>> bGhostLit = new ArrayList<ArrayList<Literal>>();
	ArrayList<ArrayList<Literal>> gGhostLit = new ArrayList<ArrayList<Literal>>();
	ArrayList<ArrayList<Literal>> oGhostLit = new ArrayList<ArrayList<Literal>>();
	//HashMap<Rule, ArrayList<Rule>> strategy = new HashMap<Rule, ArrayList<Rule>>();	
	ArrayList<String> dirs = new ArrayList<String>(Arrays.asList("North", "South", "East", "West"));
	ArrayList<Superiority> hierarchy = new ArrayList<Superiority>();
	

	public PacmanToDDPL(NormBase nb) {
		super(nb);
	}
	
	@Override
	public void init(Environment env, ArrayList<String> actions) {
		generateBaseVars("pacman", (PacmanEnvironment) env);
		generateBaseVars("food", (PacmanEnvironment) env);
		generateBaseVars("bGhost", (PacmanEnvironment) env);
		generateBaseVars("gGhost", (PacmanEnvironment) env);
		generateBaseVars("oGhost", (PacmanEnvironment) env);
		generateActions(actions);
	}
	
	@Override
	public void update(Environment env, ArrayList<String> actions, Game game) {
		generateActionNorms(actions);
		translateBoard((PacmanEnvironment) env);
		generateGameFacts(((PacmanGame) game).hasBlueViolated(), ((PacmanGame) game).hasOrangeViolated());
		generateRegulativeRules((PacmanEnvironment) env);
		generateConstitutiveRules((PacmanEnvironment) env);
		generateDefeaters((PacmanEnvironment) env);
		generateHierarchies();
	}
	
	
	//helpers
	public Rule findRule(String name, ArrayList<Rule> list) { 
		for (Rule rule : list) {
			if(rule.getLabel().equals(name)) {
				return rule;
			}
		}
		return null;
	}
	
	
	
	//basic translation methods
	public void binaryPredicateToConstitutive(Term term, PacmanGameObject base, PacmanGameObject satelite) {
		
		if(term.evaluateBinary(base, satelite)) {
			try {
				Literal ante1 = getObjectLits(base.getLabel()).get((int) (2.0*base.getCoordX())).get((int) (2.0*base.getCoordY()));
				Literal ante2 = getObjectLits(satelite.getLabel()).get((int) (2.0*satelite.getCoordX())).get((int) (2.0*satelite.getCoordY()));
				Literal consq = new Literal(term.getLabel());
				//name rule with coordinates for which it is generated
				Rule rule = new Rule(term.getLabel()+"("+String.valueOf(base.getCoordX())+
							","+String.valueOf(base.getCoordY()+")")+"("+String.valueOf(satelite.getCoordX())+","+String.valueOf(satelite.getCoordY()+")"), 
							RuleType.STRICT);
				rule.addBodyLiteral(ante1);
				rule.addBodyLiteral(ante2);
				rule.addHeadLiteral(consq);
				rules.add(rule);
			} catch (RuleException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void unaryPredicateToFact(Term term, PacmanGameObject base) {
		//HACKY SOLUTION (FIX)
		boolean cheat = false;
		if(base.getLabel().contains("scared")) {
			cheat = base.isScared();
		} 
		if(cheat || term.evaluateUnary(base)) {
			try {
				Literal lit = new Literal(term.getLabel());
				gameLits.add(lit);
				Rule rule = new Rule(term.getLabel(), RuleType.FACT);
				rule.addHeadLiteral(lit);
				facts.add(rule);
			} catch (RuleException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public ArrayList<ArrayList<Literal>> getObjectLits(String objType) {
		switch (objType) {
		case "pacman": {
			return pacmanLit;
		}
		case "bGhost": {
			return bGhostLit;
		}
		case "gGhost": {
			return gGhostLit;
		}
		case "oGhost": {
			return oGhostLit;
		}
		case "food": {
			return foodLit;
		}
		}
		return new ArrayList<ArrayList<Literal>>();
	}
	
	
	//set var arrays
	public void generateBaseVars(String objType, PacmanEnvironment board) {
		for(int i=0; i < 2*board.getBoardDimensions()[0]; i++) {
			ArrayList<Literal> temp = new ArrayList<Literal>();
			 for(int j=0; j < 2*board.getBoardDimensions()[1]; j++) {
				 Literal l = new Literal(objType+String.valueOf(i/2.0)+","+String.valueOf(j/2.0));
				 temp.add(l);
			 }
			 getObjectLits(objType).add(temp);
		}
	}
	
	
    //translate objects
	public void translateBoard(PacmanEnvironment board) {
		facts.clear();
		try {
		    Rule pac = new Rule("pacman", RuleType.FACT);
		    pac.addHeadLiteral(pacmanLit.get((int) ((2.0*board.getPacman().getCoordX()))).get((int) (2.0*board.getPacman().getCoordY())));
		    facts.add(pac);
		    for (PacmanGameObject g : board.getGhosts()) {
			    Rule ghost = new Rule(g.getLabel(), RuleType.FACT);
			    ghost.addHeadLiteral(getObjectLits(g.getLabel()).get((int) (2.0*g.getCoordX())).get((int) (2.0*g.getCoordY())));
			    facts.add(ghost);
		    }
		} catch (RuleException e) {
			e.printStackTrace();
		}
	}
	
	
	public void generateGameFacts(boolean blue, boolean orange) {
		//check if there have been violations
		if(blue) {
			try {
				Literal v = new Literal("violated(blue)");
				Rule r = new Rule("violated(blue)", RuleType.FACT);
				r.addHeadLiteral(v);
				facts.add(r);
			} catch (RuleException e) {
				e.printStackTrace();
			}
		}
		if(orange) {
			try {
				Literal v = new Literal("violated(orange)");
				Rule r = new Rule("violated(orange)", RuleType.FACT);
				r.addHeadLiteral(v);
				facts.add(r);
			} catch (RuleException e) {
				e.printStackTrace();
			}
		}
		if(blue || orange) {
			try {
				Literal v = new Literal("violated");
				Rule r = new Rule("violated", RuleType.FACT);
				r.addHeadLiteral(v);
				facts.add(r);
			} catch (RuleException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void generateStateConstitutiveRules(PacmanEnvironment board) {
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
					if(term.isPredicate()) {
						for(PacmanGameObject ghost : board.getGhosts()) {
							if(ghost.getLabel().contains(term.getSateliteObject())) {
								try {
									unaryPredicateToFact(term, ghost);
								} catch(NullPointerException e) {}
								try {
									binaryPredicateToConstitutive(term, board.getPacman(), ghost);
								} catch(NullPointerException e) {}
							}
					    }
					}
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
	

	public void generateActionConstitutiveRules(PacmanEnvironment board) {
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
					if(term.isPredicate()) {
						try {
							unaryPredicateToFact(term, board.getObject(term.getBaseObject()));
						} catch(NullPointerException e) {}
						try {
							binaryPredicateToConstitutive(term, board.getObject(term.getBaseObject()), board.getObject(term.getSateliteObject()));
						} catch(NullPointerException e) {}
						
					}
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
				Literal head1 = termToLit(n.getHigherTerm(), false);	
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
	
	

    public void generateConstitutiveRules(PacmanEnvironment board) {
    	rules.clear();
    	generateStateConstitutiveRules(board);
    	generateActionConstitutiveRules(board);
    }
    
		
	

	public void generateRegulativeRules(PacmanEnvironment board) {
		ArrayList<RegulativeNorm> regNorms = normBase.getRegulativeNorms();
        for(RegulativeNorm n : regNorms) {
			Rule rule = new Rule(n.getName(), RuleType.DEFEASIBLE);
			try {
				for(Term term : n.getContext()) {
					if(term.isPredicate()) {
						try {
							unaryPredicateToFact(term, board.getObject(term.getBaseObject()));
						} catch(NullPointerException e) {}
						try {
							binaryPredicateToConstitutive(term, board.getObject(term.getBaseObject()), board.getObject(term.getSateliteObject()));
						} catch(NullPointerException e) {}
					}
					Literal lit = termToLit(term, false);
					rule.addBodyLiteral(lit);
				}
				Literal lit = termToLit(n.getPrescription(), true);
				rule.addHeadLiteral(lit);
				rule.setMode(obl);
				
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
	
	public void generateDefeaters(PacmanEnvironment board) {
		ArrayList<ExceptionNorm> permNorms = normBase.getExceptionNorms();
        for(ExceptionNorm n : permNorms) {
			Rule rule = new Rule(n.getName(), RuleType.DEFEATER);
			try {
				for(Term term : n.getContext()) {
					if(term.isPredicate()) {
						try {
							unaryPredicateToFact(term, board.getObject(term.getBaseObject()));
						} catch(NullPointerException e) {}
						try {
							binaryPredicateToConstitutive(term, board.getObject(term.getBaseObject()), board.getObject(term.getSateliteObject()));
						} catch(NullPointerException e) {}
					}
					Literal lit = termToLit(term, false);
					rule.addBodyLiteral(lit);
				}
				Literal lit = termToLit(n.getException(), true);
				rule.addHeadLiteral(lit);
				rule.setMode(obl);
				norms.add(rule);
			}
			catch (RuleException e) {
				e.printStackTrace();
			}
		}
	}


}
