package supervisor.reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Mode;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleException;
import spindle.core.dom.RuleType;
import spindle.core.dom.Theory;
import spindle.tools.explanation.InferenceLogger;
import supervisor.games.Game;
import supervisor.normsys.DDPLTranslator;

/**
 * Second reasoner class for defeasible deontic logic. Can be used with any norm base, 
 * but the set of compliant actions is computed in polynomial time instead of linear time; 
 * the findNCActions() method, however, does not need to call DDPLReasonerCore at all, 
 * and we only need to parse already computed conclusion sets.
 * 
 * The permissibility option is hardcoded for now, that should be changed.
 * 
 * @author emery
 *
 */

public class DDPLReasoner2 extends Reasoner {
	DDPLReasonerCore gameState; 
	Mode obl = new Mode("O", false);
	Map<String, Map<Literal, Map<ConclusionType, Conclusion>>> conclusions = new HashMap<String, Map<Literal, Map<ConclusionType, Conclusion>>>();
	InferenceLogger logger;
	Theory copy = new Theory();
	DDPLTranslator translator;
	Set<Literal> literals;
	boolean permissive;

	public DDPLReasoner2(Game g, boolean perm) {
		super(g);
		gameState = new DDPLReasonerCore();
		translator = (DDPLTranslator) g.getTranslator();
		permissive = perm;
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
		copy = gameState.getTheory().clone();
		literals = copy.getAllLiteralsInRules();
		conclusions.clear();
	}

	@Override
	public void reason() {
		for(String act : game.getPossibleActions()) {
			gameState.setTheory(copy.clone());
			try {
				Literal lit = new Literal(act);
				Rule fct = new Rule(lit.getName(), RuleType.DEFEASIBLE);
				fct.addHeadLiteral(lit);
				ArrayList<Rule> fcts = new ArrayList<Rule>();
				fcts.add(fct);
				gameState.addFacts(fcts);
			} catch (RuleException e) {
				e.printStackTrace();
			}
			Map<Literal, Map<ConclusionType, Conclusion>> concl = gameState.drawConclusions();
			conclusions.put(act, concl);
			//System.out.println(concl.toString());
		}
		//System.out.println(copy.toString());
		//System.out.println(conclusions.toString());
	}

	@Override
	public ArrayList<String> findCompliantActions() {
		ArrayList<String> legalActions = new ArrayList<String>();
		ArrayList<String> possible = game.getPossibleActions();
    	legalActions.addAll(possible);
		for(String act : possible) {
			if(!checkActionCompliance(act)) {
				legalActions.remove(act);
			}
		}
		return legalActions;
	}

	@Override
	public boolean checkActionCompliance(String action) {
		return evaluateAction(action, true) == 0;
	}

	@Override
	public ArrayList<String> findNCActions() {
		ArrayList<String> possible = game.getPossibleActions();
		ArrayList<String> best = new ArrayList<String>();
		Map<String, Integer> scores = new HashMap<String, Integer>();
		int min = Integer.MAX_VALUE;
		for(String act : possible) {
			int score = evaluateAction(act, permissive);
			scores.put(act, score);
			if(score < min) {
				min = score;
			}
		}
		int n = min;
		scores = scores.entrySet().stream()
				.filter(p -> p.getValue().equals(n))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		best.addAll(scores.keySet());
		return best;
		
	}
	
	public int evaluateAction(String action, boolean perm) {
		int score = 0;
		Map<Literal, Map<ConclusionType, Conclusion>> concl = conclusions.get(action);
		Map<Literal, Map<ConclusionType, Conclusion>> obls = concl.entrySet().stream()
				.filter(p -> p.getKey().getMode().equals(obl))
				.filter(p -> util.checkForLit(literals, p.getKey().getName()))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		for(Literal lit : obls.keySet()) {
			if(obls.get(lit).containsKey(ConclusionType.DEFEASIBLY_PROVABLE)) {
				Literal l1 = lit.clone();
				l1.removeMode();
				Literal l2 = l1.getComplementClone();
				if(perm) {
					if(concl.keySet().contains(l2) && concl.get(l2).containsKey(ConclusionType.DEFEASIBLY_PROVABLE)){
						score += 1;
					}
				}
				else {
					if(!concl.containsKey(l1) || !concl.get(l1).containsKey(ConclusionType.DEFEASIBLY_PROVABLE)){
						score += 1;
					}
				}
			}		
		}
		return score;
	}
	
	public void printTheory() {
		System.out.println(copy.toString());
	}

}
