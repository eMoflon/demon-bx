package bxtooldemo.adapter.core.implementations.emoflon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.moflon.tgg.algorithm.configuration.RuleResult;
import org.moflon.tgg.runtime.IsApplicableMatch;

public class KitchenToGridConfigurator implements ContinuationConfigurator {
	
	private Collection<RuleResult> alternatives = new ArrayList<>();
	private Optional<Consumer<IsApplicableMatch>> continuation = Optional.empty();
	
	@Override
	public void chooseOneAndContinue(Collection<RuleResult> alternatives, Consumer<IsApplicableMatch> continuation) {
		this.alternatives = alternatives;
		this.continuation = Optional.of(continuation);
		
	}
	
	@Override
	public boolean hasContinuation(){
		return continuation.isPresent();
	}
	
	@Override
	public List<String> getChoices(){
		return alternatives.stream().map(rr -> rr.getRule()).collect(Collectors.toList());
	}
	
	@Override
	public void continueSynchronisation(String decision){
		RuleResult choice = alternatives.stream()
				.filter(rr -> rr.isRule(decision))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("I could not find " + decision + " as an option."));
		
		continuation.ifPresent(cnt -> cnt.accept(choice.anyMatch()));
		removeContinuation();
	}

	private void removeContinuation() {
		continuation = Optional.empty();
		alternatives.clear();
	}
}
