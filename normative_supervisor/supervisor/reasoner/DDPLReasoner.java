package supervisor.reasoner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Mode;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleException;
import spindle.core.dom.RuleType;
import spindle.core.dom.Theory;
import spindle.tools.explanation.InferenceLogItem;
import spindle.tools.explanation.InferenceLogger;
import spindle.tools.explanation.RuleInferenceStatus;
import supervisor.normsys.DDPLTranslator;
import supervisor.reasoner.DDPLReasonerCore;
import util.ObjectNotFoundException;
/**
 * First reasoner class for defeasible deontic logic. Can only be used with norm bases with a 
 * full set of strategy rules. Compliant action set computable within linear time, and 
 * computation of best non-compliant action can be done in polynomial time.
 * 
 * The following methods need minor work before they can be used more generally:
 * - findCompliantActions(): make contains checks before declaring concl_pos and concl_neg
 * - findNCActions(): metric is hardcoded - change this!!!!
 * 
 * @author emery
 *
 */


public class DDPLReasoner extends Reasoner{
	DDPLReasonerCore gameState; 
	Mode obl = new Mode("O", false);
	Map<Literal, Map<ConclusionType, Conclusion>> conclusions = new HashMap<Literal, Map<ConclusionType, Conclusion>>();
	InferenceLogger logger;
	Theory copy = new Theory();
	ArrayList<String> labels = new ArrayList<String>();
	DDPLTranslator translator;

	
	public DDPLReasoner(supervisor.games.Game g) {
		super(g);
		gameState = new DDPLReasonerCore();
		translator = (DDPLTranslator) g.getTranslator();
	}
	
	@Override
	public void update() {
		gameState.getTheory().clear();
		ArrayList<Rule> facts = translator.getFacts();
		ArrayList<Rule> rules = translator.getRules();
		ArrayList<Rule> nc = translator.getActionRules();
		ArrayList<Rule> norms = translator.getNorms();
		ArrayList<Rule> all = new ArrayList<Rule>();
		all.addAll(rules);
		all.addAll(nc);
		all.addAll(norms);
		gameState.addFacts(facts);
		gameState.addRules(all);
		gameState.addHierarchy(translator.getHierarchy());
		labels.clear();
		for(Rule r : all) {
			labels.add(r.getLabel());
		}
		copy = gameState.getTheory().clone();
		conclusions.clear();
	}
	
	@Override
	public void reason() {
		conclusions = gameState.drawConclusions().entrySet().stream()
				.filter(p -> p.getKey().getMode().equals(obl))
				.filter(p -> game.getActions().contains(p.getKey().getName()))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		logger = gameState.getInference();
		//for debugging:
		//System.out.println(copy.toString());
		//System.out.println(conclusions.toString());
	}
	
	@Override
	public ArrayList<String> findCompliantActions() {
		ArrayList<String> legalActions = new ArrayList<String>();
		ArrayList<String> possible = game.getPossibleActions();
    	legalActions.addAll(possible);
    	
    	for(Literal a_lit : translator.getActionLits()) {
    		if(possible.contains(a_lit.getName())) {
    			Map<ConclusionType, Conclusion> concl_pos;
    			Map<ConclusionType, Conclusion> concl_neg;
    			if(conclusions.containsKey(a_lit.cloneWithMode(obl))) {
    				concl_pos = conclusions.get(a_lit.cloneWithMode(obl));
    			} else {
    				concl_pos = new HashMap<ConclusionType, Conclusion>();
    			}
    			if(conclusions.containsKey(a_lit.getComplementClone().cloneWithMode(obl))) {
    				concl_neg = conclusions.get(a_lit.getComplementClone().cloneWithMode(obl));
    			} else {
    				concl_neg = new HashMap<ConclusionType, Conclusion>();
    			}
    			
    			//check that no conclusions are missing; this indicates non-concurrence rules have been triggered
        		if((!concl_pos.containsKey(ConclusionType.DEFEASIBLY_PROVABLE) && 
        				!concl_pos.containsKey(ConclusionType.DEFEASIBLY_NOT_PROVABLE)) ||
        				(!concl_neg.containsKey(ConclusionType.DEFEASIBLY_PROVABLE) && 
                				!concl_neg.containsKey(ConclusionType.DEFEASIBLY_NOT_PROVABLE))) {
        			return new ArrayList<String>();
        		}
        		//if there is an obligation, return that because we only have single obligations
        		else if(conclusions.get(a_lit.cloneWithMode(obl)).keySet()
        					.contains(ConclusionType.DEFEASIBLY_PROVABLE)) {
            		legalActions.clear();
        			if (possible.contains(a_lit.getName())) {
        				legalActions.add(a_lit.getName());
        			}
        			return legalActions;
            	}
        		//if there is a prohibition, remove it
        		else if(conclusions.get(a_lit.getComplementClone().cloneWithMode(obl)).keySet()
        					.contains(ConclusionType.DEFEASIBLY_PROVABLE)) {
        			legalActions.remove(a_lit.getName());
        			
            	}
    		}
    		
    	}
    	return legalActions;
    }
	
	@Override
	public boolean checkActionCompliance(String action) {
		Literal a_lit = new Literal(action);
		Map<ConclusionType, Conclusion> concl_neg = conclusions.get(a_lit.getComplementClone().cloneWithMode(obl));
		return !concl_neg.containsKey(ConclusionType.DEFEASIBLY_PROVABLE);
    }


	@Override
	public ArrayList<String> findNCActions() {
		ArrayList<String> bestActions = new ArrayList<String>();
		Map<String, Integer> scores = new HashMap<String, Integer>();
			
		for(String action : game.getPossibleActions()) {
			Map<ArrayList<String>, Integer> eval = evaluateAction(action, "difference");
			for(Integer val : eval.values()) {
				scores.put(action, val);
			}
		}
		int max = Collections.max(scores.values());
		for(String key : scores.keySet()) {
			if(scores.get(key) == max) {
				bestActions.add(key);
			}
		}
		return bestActions;
	}
	
	
	
	protected Map<ArrayList<String>, Integer> evaluateAction(String action, String metric){
		Map<ArrayList<String>, Integer> violated = new HashMap<ArrayList<String>, Integer> ();
		try {
			Literal act = util.getLit(translator.getActionLits(), action);
			Rule ins = new Rule("action", RuleType.FACT);
			ins.addHeadLiteral(act.cloneWithMode(obl));
			ArrayList<Rule> facts = new ArrayList<Rule>();
			facts.add(ins);
			DDPLReasonerCore temp = new DDPLReasonerCore();
			temp.setTheory(copy.clone());
			temp.addFacts(facts);
			ArrayList<Literal> actLits = new ArrayList<Literal>();
			actLits.addAll(translator.getActionLits());
			actLits.remove(act);
			ArrayList<Rule> norms = new ArrayList<Rule>();
			norms.addAll(translator.getNorms());
			norms.addAll(translator.getStrategies());
			norms.addAll(translator.getActionRules());
			labels = util.getRuleLabels(norms);
				
		    //System.out.println(temp.getTheory().toString());
			temp.drawConclusions();
			InferenceLogger templogger = temp.getInference();
			violated = metric(templogger, metric);
		} catch (ObjectNotFoundException | RuleException e) {
			e.printStackTrace();
		}
		return violated;
	}
	
	public Map<ArrayList<String>, Integer> metric(InferenceLogger il, String name) {
		
		Map<ArrayList<String>, Integer> violated = new HashMap<ArrayList<String>, Integer> ();
		ArrayList<String> v = new ArrayList<String>();
		Map<String,InferenceLogItem> templog = il.getInferenceLogItems().entrySet().stream()
				.filter(p -> util.containsSome(p.getKey(),  labels))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		int applicable = 0;
		int defeated = 0;
		for(String rule : templog.keySet()) {
			RuleInferenceStatus status = templog.get(rule).getRuleInferenceStatus(ConclusionType.DEFEASIBLY_PROVABLE);
			try {
				if(status.equals(RuleInferenceStatus.APPICABLE)) {
					applicable++;
				} else if(status.equals(RuleInferenceStatus.DEFEATED)) {
					defeated++;
					v.add(rule);
				}
			} catch(NullPointerException e) {
				
			}
		}
		int score = 0; 
		if (name.equals("difference")) {
			score = applicable - defeated;
		}
		else if (name.equals("defeated")) {
			score = -defeated;
		}
		else if (name.equals("stratefied")) {
			if(defeated == 0) {
				score = applicable;
			}
			else {
				score = -defeated;
			}
		}
		violated.put(v, score);
		return violated;
	}

	public void printTheory() {
		System.out.println(copy.toString());
	}


	
	

}
