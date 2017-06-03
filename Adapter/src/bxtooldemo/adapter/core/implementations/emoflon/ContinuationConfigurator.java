package bxtooldemo.adapter.core.implementations.emoflon;

import java.util.Collection;
import java.util.function.Consumer;

import org.moflon.tgg.algorithm.configuration.Configurator;
import org.moflon.tgg.algorithm.configuration.RuleResult;
import org.moflon.tgg.runtime.IsApplicableMatch;

public interface ContinuationConfigurator extends Configurator {

	void chooseOneAndContinue(Collection<RuleResult> alternatives, Consumer<IsApplicableMatch> continuation);

	boolean hasContinuation();

	Collection<String> getChoices();

	void continueSynchronisation(String decision);

}
