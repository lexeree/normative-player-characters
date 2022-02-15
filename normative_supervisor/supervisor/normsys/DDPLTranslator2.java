package supervisor.normsys;

import java.util.ArrayList;

import spindle.core.dom.Literal;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleException;
import spindle.core.dom.RuleType;

/**
 * Translator from norms to defeasible deontic logic facts/rules. Should only be used with DDPLReasoner2 class.
 * 
 * @author emery
 *
 */

public class DDPLTranslator2 extends DDPLTranslator {

	public DDPLTranslator2(NormBase nb) {
		super(nb);
	}
	
	@Override
	public void generateActionConstitutiveRules() {
		try {
			ArrayList<ConstitutiveNorm> actNorms = normBase.getActionConstitutiveNorms();
			for(ConstitutiveNorm n : actNorms) {
				Rule rule = new Rule(n.getName(), RuleType.STRICT);
				for(Term term : n.getContext()) {
					Literal lit = termToLit(term, false);
					rule.addBodyLiteral(lit);
				}
				for(Term term : n.getLowerTerms()) {
					Literal lit1 = termToLit(term, false);
					rule.addBodyLiteral(lit1);
				}
				Literal head1 = termToLit(n.getHigherTerm(), true);	
				rule.addHeadLiteral(head1);
				rules.add(rule);
				strategies.add(rule);
			}
		}
		catch (RuleException e) {
			e.printStackTrace();
		}
	}

}
