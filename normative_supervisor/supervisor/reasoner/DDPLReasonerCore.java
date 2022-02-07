package supervisor.reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Mode;
import spindle.core.dom.Rule;
import spindle.core.dom.Superiority;
import spindle.core.dom.Theory;
import spindle.core.dom.TheoryException;
import spindle.engine.ReasoningEngineException;
import spindle.engine.TheoryNormalizer;
import spindle.engine.TheoryNormalizerException;
import spindle.engine.mdl.MdlReasoningEngine2;
import spindle.engine.mdl.MdlTheoryNormalizer2;
import spindle.tools.explanation.InferenceLogger;

/**
 * Class to interface with SPINdle; contains all direct calls to the theorem prover.
 * Do not modify.
 * 
 * @author emery
 *
 */

public class DDPLReasonerCore extends MdlReasoningEngine2 {
	Theory theory;
	TheoryNormalizer normalizer;
	Mode obl = new Mode("O", false);
	InferenceLogger inf;
	

	public DDPLReasonerCore() {
		spindle.sys.Conf.setLogInferenceProcess(true);
		theory = new Theory();
		normalizer = new MdlTheoryNormalizer2();
		inf = getInferenceLogger();
	}	
	
	
	public void setTheory(Theory th) {
		theory.clear();
		theory = th;
	}
	
	
	public Map<Literal, Map<ConclusionType, Conclusion>> drawConclusions() {
		Map<Literal, Map<ConclusionType, Conclusion>> conclusions = new HashMap<Literal, Map<ConclusionType, Conclusion>>();
		try {
			normalizer = new MdlTheoryNormalizer2();
			normalizer.setTheory(theory);
			normalizer.removeDefeater();
			normalizer.transformTheoryToRegularForm();
			theory = normalizer.getTheory();
			conclusions = getConclusions(theory);
		}
		catch (TheoryNormalizerException | ReasoningEngineException e) {
			System.out.print(e);
		}
		return conclusions;
	}
	
	
	public Theory getTheory() {
		return theory;
	}
	
	
	public void addFacts(ArrayList<Rule> facts) {
		for(Rule fact : facts) {
			try {
				theory.addFact(fact);
			} catch (TheoryException e) {
				//e.printStackTrace();
			}
		}
	}
	
	
	public void addRules(ArrayList<Rule> rules) {
		for(Rule rule : rules) {
			try {
				theory.addRule(rule);
			} catch (TheoryException e) {
				//e.printStackTrace();
			}
		}
	}
	
	
	public void addHierarchy(ArrayList<Superiority> rules) {
		for(Superiority rule : rules) {
			theory.add(rule);
		}
	}
	
	
	public InferenceLogger getInference() {
		return inf;
	}

}
