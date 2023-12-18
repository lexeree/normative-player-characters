package filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.Sets;

import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Mode;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleException;
import spindle.core.dom.RuleType;
import spindle.core.dom.Theory;
import supervisor.games.Environment;
import supervisor.normsys.DDPLTranslator2;
import supervisor.normsys.NormBase;
import supervisor.reasoner.DDPLReasonerCore;
import util.ProjectUtils;

public class NormativeFilter {
	Mode obl = new Mode("O", false);
	Mode perm = new Mode("P", false);
	ProjectUtils util = new ProjectUtils();
	NormBase NS;
	ArrayList<String> AP;
	ArrayList<String> actions;
	DDPLReasonerCore reasoner = new DDPLReasonerCore();
	Environment env;
	DDPLTranslator2 translator;
	ArrayList<State> filter = new ArrayList<State>();
	Set<Literal> literals;
	
	
	
	public NormativeFilter(NormBase nb, ArrayList<String> labels, ArrayList<String> acts) {
		NS = nb;
		translator = new DDPLTranslator2(NS);
		AP = labels;
		actions = acts;
		env = new Environment();
		env.configureLabels(AP);
	}
	
	
	public ArrayList<State> getFilter(){
		return filter;
	}

    

    public ArrayList<ArrayList<String>> powerset(){
    	Set<String> ap = new HashSet<String>(AP);
    	Set<Set<String>> subsets = Sets.powerSet(ap);
    	ArrayList<ArrayList<String>> powerset = new ArrayList<ArrayList<String>>();
    	for(Set<String> set : subsets) {
    		ArrayList<String> north = (ArrayList<String>) set.stream().filter(str -> str.startsWith("North")).collect(Collectors.toList());
    		ArrayList<String> south = (ArrayList<String>) set.stream().filter(str -> str.startsWith("South")).collect(Collectors.toList());
    		ArrayList<String> east = (ArrayList<String>) set.stream().filter(str -> str.startsWith("East")).collect(Collectors.toList());
    		ArrayList<String> west = (ArrayList<String>) set.stream().filter(str -> str.startsWith("West")).collect(Collectors.toList());
    		ArrayList<String> at = (ArrayList<String>) set.stream().filter(str -> str.startsWith("at_")).collect(Collectors.toList());
    		if(north.size() > 1 || south.size() > 1 || east.size() > 1 || west.size() > 1 || at.size() > 1) {
    	        continue;
    		} else {
    			powerset.add(new ArrayList<String>(set));
    		}
    	}
		return powerset;
    }
	
	public void updateReasoner(ArrayList<String> set) {
		env.clear();
		translator.clear();
		env.setLabels(set);
		translator.synth_update(env);
		reasoner.getTheory().clear();
		ArrayList<Rule> facts = translator.getFacts();
		ArrayList<Rule> rules = translator.getRules();
		ArrayList<Rule> norms = translator.getNorms();
		ArrayList<Rule> all = new ArrayList<Rule>();
		all.addAll(rules);
		all.addAll(norms);
		reasoner.addFacts(facts);
		reasoner.addRules(all);
		reasoner.addHierarchy(translator.getHierarchy());
	}
	
	public int countViolations() {
		Map<Literal, Map<ConclusionType, Conclusion>> conclusions = reasoner.drawConclusions()
				.entrySet().stream()
				.filter(p -> util.checkForLit(literals, p.getKey().getName()))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		int score = 0;
		Map<Literal, Map<ConclusionType, Conclusion>> obls = conclusions.entrySet().stream()
				.filter(p -> p.getKey().getMode().equals(obl))
				.filter(p -> util.checkForLit(literals, p.getKey().getName()))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		for(Literal lit : obls.keySet()) {
			if(obls.get(lit).containsKey(ConclusionType.DEFEASIBLY_PROVABLE)) {
				Literal l1 = lit.clone();
				l1.removeMode();
				Literal l2 = l1.getComplementClone();
				if(l1.isNegation()) {
					if(conclusions.keySet().contains(l2) && conclusions.get(l2).containsKey(ConclusionType.DEFEASIBLY_PROVABLE)){
						score += 1;
					}
				}
				else {
					if(!conclusions.keySet().contains(l1) || !conclusions.get(l1).containsKey(ConclusionType.DEFEASIBLY_PROVABLE)){
						score += 1;
					}
			    }
			}		
		}
		return score;
	}
	
	public void constructFilter() throws RuleException {
		ArrayList<ArrayList<String>> subsets = powerset();
		for (ArrayList<String> gamma : subsets) {
			for(String a : actions) {
				Literal act = new Literal(a);
				Rule fact = new Rule(a, RuleType.DEFEASIBLE);
				fact.addHeadLiteral(act);
				ArrayList<Rule> f = new ArrayList<Rule>();
				f.add(fact);
				updateReasoner(gamma);
				reasoner.addRules(f);
				literals = reasoner.getTheory().clone().getAllLiteralsInRules();
				int viol = countViolations();
				State s = new State(gamma, a, viol);
				filter.add(s);
			}
		}
	}
	
	public JSONArray formatFilter() {
		JSONObject f = new JSONObject();
		JSONArray lits = new JSONArray(util.getLitNames(literals));
		f.put("literals", lits);
		JSONArray filt = new JSONArray();
		for(State s : filter) {
			JSONObject state = new JSONObject();
			JSONArray labs = new JSONArray(s.getLabels());
			state.put("labels", labs);
			state.put("action", s.getAction());
			state.put("violations", s.getViolations());
			filt.put(state);
		}
		f.put("filter", filt);
		return filt;
	}
	
	
	
}
