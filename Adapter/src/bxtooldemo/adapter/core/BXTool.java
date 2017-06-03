package bxtooldemo.adapter.core;

import java.util.function.Consumer;

import GridLanguage.Grid;
import KitchenLanguage.Kitchen;

/**
 * This interface describes the expected functionality of a "BXTool" from the
 * perspective of the benchmarx.
 * 
 * @author aanjorin
 *
 * @param <S>
 *            The root type of all source models
 * @param <T>
 *            The root type of all target models
 * @param <UP>
 *            Represents runtime decisions that can be requested by the tool at
 *            runtime.
 **/
public interface BXTool<S, T, UP> {

	public void initiateSynchronisationDialogue();

	public void performAndPropagateTargetEdit(Consumer<T> edit);

	public void performAndPropagateSourceEdit(Consumer<S> edit);

	public void setConfigurator(UP updatePolicy);
	
	public Grid getSourceModel();

	public Kitchen getTargetModel();
	
	default public String getName() { return "Please set the name of your bx tool!"; }
}
