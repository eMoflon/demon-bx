package bxtooldemo.adapter.core.implementations.emoflon;

import java.net.URL;
import java.util.function.Consumer;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.moflon.tgg.algorithm.configuration.Configurator;

import GridLanguage.Block;
import GridLanguage.Grid;
import GridLanguage.GridLanguageFactory;
import KitchenLanguage.Kitchen;
import KitchenToGridLanguage.KitchenToGridLanguagePackage;
import bxtooldemo.adapter.core.BXTool;
import bxtooldemo.adapter.core.uiservice.Analysis;

public class KitchenToGrid implements BXTool<Grid, Kitchen, ContinuationConfigurator> {
	
	private KitchenToGridSynchronizationHelper helper;

	@Override
	public String getName() {
		return "eMoflon";
	}

	@Override
	public void initiateSynchronisationDialogue() {
		
		BasicConfigurator.configure();
		//for accessing local System Path
		//helper = new SynchronizationHelper(KitchenToGridLanguagePackage.eINSTANCE, "C:/Users/Arjya Shankar Mishra/git/eMoflon-Rules/KitchenToGridLanguage");
		
		//for accessing jar
		URL location = KitchenToGridLanguagePackage.class.getProtectionDomain().getCodeSource().getLocation();
		helper = new KitchenToGridSynchronizationHelper(location.getFile());
		
		Resource r = helper.getResourceSet().createResource(URI.createURI("sourceModel"));
		
		Grid gridRoot = initialiseGrid();
		r.getContents().add(gridRoot);
		
		// Fix default preferences (which can be overwritten)
//		setConfigurator(new Configurator<Decisions>()
//				.makeDecision(Decisions.PREFER_CREATING_PARENT_TO_CHILD, true)
//			    .makeDecision(Decisions.PREFER_EXISTING_FAMILY_TO_NEW, true));
		
		// perform batch to establish consistent starting state
		helper.setSrc(gridRoot);
		helper.integrateForward();	
		
//		helper.setMute(true);
		helper.setVerbose(true);
	}

	private Grid initialiseGrid() {
		//initialize grid with block structure
		Grid gridRoot = GridLanguageFactory.eINSTANCE.createGrid();
		int noOfBlocks = Analysis.blockArrayNo;
		gridRoot.setBlockSize((double)500/noOfBlocks);
		
		Block topLeft = createTopLeft(gridRoot);
		createFirstRow(gridRoot, topLeft, noOfBlocks);
		createFirstCol(gridRoot, topLeft, noOfBlocks);
		createInternalBlocks(gridRoot, topLeft, noOfBlocks);
		
		return gridRoot;
	}

	private Block createTopLeft(Grid gridRoot) {
		Block block = GridLanguageFactory.eINSTANCE.createBlock();
		block.setXIndex(0);
		block.setYIndex(0);
		gridRoot.getBlocks().add(block);
		
		return block;
	}

	private void createFirstRow(Grid gridRoot, Block topLeft, int noOfBlocks) {
		Block west = topLeft;
		for (int i = 1; i < noOfBlocks; i++) {
			Block block = GridLanguageFactory.eINSTANCE.createBlock();
			block.setXIndex(i);
			block.setYIndex(0);
			block.setW(west);
			gridRoot.getBlocks().add(block);
			
			west = block;
		}
	}
	
	private void createFirstCol(Grid gridRoot, Block topLeft, int noOfBlocks) {
		Block north = topLeft;
		for (int i = 1; i < noOfBlocks; i++) {
			Block block = GridLanguageFactory.eINSTANCE.createBlock();
			block.setXIndex(0);
			block.setYIndex(i);
			block.setN(north);
			gridRoot.getBlocks().add(block);
			
			north = block;
		}
	}
	
	private void createInternalBlocks(Grid gridRoot, Block topLeft, int noOfBlocks) {
		Block northwest = topLeft;
		Block north = northwest.getE();
		Block west = northwest.getS();
		for (int i = 1; i < noOfBlocks; i++) {
			
			for (int j = 1; j < noOfBlocks; j++) {
				Block block = GridLanguageFactory.eINSTANCE.createBlock();
				block.setXIndex(j);
				block.setYIndex(i);
				block.setNw(northwest);
				block.setN(north);
				block.setW(west);
				gridRoot.getBlocks().add(block);
				
				northwest = north;
				north = northwest.getE();
				west = block;
			}
			
				topLeft = topLeft.getS();
				northwest = topLeft;
				north = northwest.getE();
				west = northwest.getS();
			
		}
	}

	@Override
	public void performAndPropagateTargetEdit(Consumer<Kitchen> edit) {
		helper.updateConsistentState();
		helper.setChangeTrg((EObject root) ->  edit.accept((Kitchen) root));
		helper.integrateBackward();
	}

	@Override
	public void performAndPropagateSourceEdit(Consumer<Grid> edit) {
		helper.updateConsistentState();
		helper.setChangeSrc((EObject root) ->  edit.accept((Grid) root));
		helper.integrateForward();
	}

	@Override
	public void setConfigurator(ContinuationConfigurator configurator) {
		helper.setConfigurator(configurator);
	}

	@Override
	public Grid getSourceModel() {
		return (Grid) helper.getSrc();
	}

	@Override
	public Kitchen getTargetModel() {
		return (Kitchen) helper.getTrg();
	}
}
