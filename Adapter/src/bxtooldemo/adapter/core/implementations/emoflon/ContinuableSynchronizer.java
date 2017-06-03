package bxtooldemo.adapter.core.implementations.emoflon;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.moflon.tgg.algorithm.configuration.Configurator;
import org.moflon.tgg.algorithm.configuration.RuleResult;
import org.moflon.tgg.algorithm.datastructures.CollectionProvider;
import org.moflon.tgg.algorithm.datastructures.SynchronizationProtocol;
import org.moflon.tgg.algorithm.datastructures.TripleMatch;
import org.moflon.tgg.algorithm.delta.Delta;
import org.moflon.tgg.algorithm.exceptions.InputLocalCompletenessException;
import org.moflon.tgg.algorithm.exceptions.TranslationLocalCompletenessException;
import org.moflon.tgg.language.algorithm.TempOutputContainer;
import org.moflon.tgg.language.analysis.StaticAnalysis;
import org.moflon.tgg.runtime.CorrespondenceModel;
import org.moflon.tgg.runtime.IsApplicableMatch;
import org.moflon.tgg.runtime.IsApplicableRuleResult;
import org.moflon.tgg.runtime.Match;
import org.moflon.tgg.runtime.PerformRuleResult;

public abstract class ContinuableSynchronizer extends Synchronizer {

	public ContinuableSynchronizer(CorrespondenceModel graphTriple, Delta delta, SynchronizationProtocol protocol,
			Configurator configurator, StaticAnalysis rules, TempOutputContainer tempOutputContainer) {
		super(graphTriple, delta, protocol, configurator, rules, tempOutputContainer);
	}

	
	@Override
	protected void translate() throws InputLocalCompletenessException, TranslationLocalCompletenessException {
		pg.collectAllPrecedences(inputMatchCollection);

		configurator.inspectPG(pg);
		inputMatches = pg.getMatchIDs();

		extendReady(inputMatches);

		continueTranslate();
	}
	
	private void continueTranslate(){
		if(inputMatches.isEmpty()){
			finalizeGraphTriple(graphTriple);
			return;
		}
		
		try {
			if (readyKernels.isEmpty())
				throw new InputLocalCompletenessException(pg.getAsCollection(inputMatches));

			Stream<Match> maximalSet = chooseOneMaximalSet();
			Collection<IsApplicableRuleResult> extended = extendToFullMatches(maximalSet);

			if (!extended.stream().anyMatch(isApplRR -> isApplRR.isSuccess()))
				throw new TranslationLocalCompletenessException(extended);

			chooseOneAndContinue(extended, (chosen) -> {				
				PerformRuleResult chosenRR = applyAndUpdateTriple(graphTriple, chosen);

				updateProcessedSets(chosen);

				if (amalgamationUtil.isAmalgamatedTGG())
					processAmalgamationComplements(inputMatches, chosen, chosenRR);

				translated = null;
				createdTripleMatchesInLastStep = CollectionProvider.<TripleMatch> getCollection();

				continueTranslate();
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void chooseOneAndContinue(Collection<IsApplicableRuleResult> extended, Consumer<IsApplicableMatch> continuation) {
		List<RuleResult> alternatives = extended.stream()
			      .filter(isApplRR -> isApplRR.isSuccess())
			      .map(RuleResult::new)
					.collect(Collectors.toList());

		// At this point one could extend the choice to individual matches even of the same rule by checking alternatives.get(0).isUnique()
		if (alternatives.size() == 1)
			continuation.accept(alternatives.get(0).anyMatch());
		else
			((ContinuationConfigurator) configurator).chooseOneAndContinue(alternatives, continuation);
	}
}
