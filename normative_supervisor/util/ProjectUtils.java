package util;

import util.ObjectNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pacman.translator.Modality;
import pacman.translator.NormBase;
import pacman.translator.RegulativeNorm;
import pacman.translator.Term;
import spindle.core.dom.Literal;
import spindle.core.dom.Rule;
import spindle.core.dom.Theory;
import spindle.core.dom.TheoryException;



public class ProjectUtils {
	
	public ProjectUtils() { }
	
	public boolean litCalled(Literal lit, String name) {
		return lit.getName().equals(name);
	}
	
	
	public Literal getLit(List<Literal> lits, String name) throws ObjectNotFoundException {
		for(Literal lit : lits) {
			if(litCalled(lit, name)) {
				return lit;
			}
		}
		throw new ObjectNotFoundException("No matching literal!");
	}
	
	
	public Rule getMatchedRule(List<Rule> rules, String label) throws ObjectNotFoundException {
		for(Rule rule : rules) {
			if(rule.getLabel().equals(label)) {
				return rule;
			}
		}
		throw new ObjectNotFoundException("No matching literal!");
	}
	
	
	public void addRulesToTheory(List<Rule> rules, Theory theory) throws TheoryException {
		Set<String> remove = Collections.emptySet();
		Map<String, List<Rule>> map = Collections.emptyMap();
		theory.updateTheory(rules, remove, map);
	}
	
	
	public ArrayList<String> getRuleLabels(Collection<Rule> list) {
		ArrayList<String> labels = new ArrayList<String>();
		for(Rule r : list) {
			labels.add(r.getLabel());
		}
		return labels;
	}
	
	
	public Set<Literal> getFactLits(List<Rule> list) {
		Set<Literal> lits = new HashSet<Literal>();
		for(Rule r : list) {
			lits.addAll(r.getHeadLiterals());
		}
		return lits;
	}
	
	
	public NormBase defaultNormBase(String type) {
		ArrayList<String> actions = new ArrayList<String>(Arrays.asList("North", "South", "East", "West", "Stop"));
		NormBase nb = new NormBase(type);
		nb.generateNonConcurrence(actions);
		try {
		if(type.equals("broken")) {
			Term east = new Term("East", false, false, true);
			east.setMode(Modality.PROHIBITION);
			ArrayList<Term> context = new ArrayList<Term>();
			RegulativeNorm broke;
		    broke = new RegulativeNorm("-moveEast", context, east);
			nb.addRegulativeNorm(broke);
		}
		else if(type.equals("busy")) {
			nb.generateMoveAction();
		}
        else if(type.equals("hungry")) {
			nb.generateEating(true, false, actions, "pacman", "food");
		}
        else if(type.equals("vegan-cautious")) {
        	nb.generateEating(false, true, actions, "pacman", "bGhost");
        	nb.generateEating(false, true, actions, "pacman", "gGhost");
        	nb.generateEating(false, true, actions, "pacman", "oGhost");
        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return nb;
	}


}
