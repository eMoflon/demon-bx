package bxtooldemo.adapter.core.uiservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;

import GridLanguage.Block;
import GridLanguage.Grid;
import GridLanguage.GridLanguageFactory;
import GridLanguage.Group;
import KitchenLanguage.Item;
import KitchenLanguage.ItemSocket;
import KitchenLanguage.Kitchen;
import KitchenLanguage.KitchenLanguageFactory;
import KitchenLanguage.KitchenLanguagePackage;
import bxtooldemo.adapter.core.implementations.emoflon.KitchenToGrid;
import bxtooldemo.adapter.core.implementations.emoflon.KitchenToGridConfigurator;
import bxtooldemo.adapter.core.implementations.emoflon.SynchronisationFailedException;
import bxtooldemo.adapter.uimodels.Change;
import bxtooldemo.adapter.uimodels.Element;
import bxtooldemo.adapter.uimodels.Layout;
import bxtooldemo.adapter.uimodels.Rectangle;
import bxtooldemo.adapter.uimodels.UIGroup;
import bxtooldemo.adapter.uimodels.UIModels;
import bxtooldemo.adapter.uimodels.Workspace;

public class Analysis {

	private KitchenToGridConfigurator configurator;
	private KitchenToGrid kitchenToGrid;
	private UIModels uiModelsAdapter;
	public static int blockArrayNo;
	private Change failedSynchroChange;
	private Map<Integer, String> groupColors;
	private Map<String, String> blockColors;
	private Change change;
	private List<Element> createItemstoRemove;
	private List<Element> deleteItemstoRemove;
	private List<Element> movedItemstoRemove;
	private List<UIGroup> grpCreatedtoRemove;
	private List<UIGroup> grpDeletedtoRemove;

	public void initeMoflonTool(int blockArrayNo) {
		kitchenToGrid = new KitchenToGrid();
		Analysis.blockArrayNo = blockArrayNo;
		configurator = new KitchenToGridConfigurator();
		kitchenToGrid.initiateSynchronisationDialogue();
		kitchenToGrid.setConfigurator(configurator);
		groupColors = new HashMap();
		blockColors = new HashMap();
		failedSynchroChange = new Change();
		change = new Change();
		createItemstoRemove = new ArrayList<>();
		deleteItemstoRemove = new ArrayList<>();
		movedItemstoRemove = new ArrayList<>();
		grpCreatedtoRemove = new ArrayList<>();
		grpDeletedtoRemove = new ArrayList<>();
	}

	public UIModels getUIModels() {
		return convertToUIModels();
	}
	
	public UIModels continueSynchronisation(String decision){
		
		configurator.continueSynchronisation(decision);
		
		return getUIModelsAfterAtomicDeltaPropagation(change); 
	}

	private UIModels convertToUIModels() {

		Layout layoutAdapter = new Layout();
		Workspace workspaceAdapter = new Workspace();
		UIModels uiModelAdapter = new UIModels();
		Grid grid = this.kitchenToGrid.getSourceModel();
		Kitchen kitchen = this.kitchenToGrid.getTargetModel();
		UIGroup uiGroup;
		Rectangle rect;
		Element element;
		
		//update the change
		change.getCreated().removeAll(createItemstoRemove);
		createItemstoRemove.clear();
		change.getDeleted().removeAll(deleteItemstoRemove);
		deleteItemstoRemove.clear();
		change.getMoved().removeAll(movedItemstoRemove);
		movedItemstoRemove.clear();
		change.getGroupCreated().removeAll(grpCreatedtoRemove);
		grpCreatedtoRemove.clear();
		change.getGroupDeleted().removeAll(grpDeletedtoRemove);
		grpDeletedtoRemove.clear();
		
		//For user choices
		if (configurator != null && configurator.getChoices().size() > 0) {
			uiModelAdapter.setUserChoices(configurator.getChoices());
			return uiModelAdapter;
		}
		
		
		// layout conversion
		layoutAdapter.setGridSize(grid.getBlockSize());
		if (grid != null && grid.getGroups().size() > 0) {
			for (Group group : grid.getGroups()) {
				uiGroup = new UIGroup();
				for (Block block : group.getOccupies()) {
					rect = new Rectangle();
					rect.setId("block_"+block.getXIndex()+"_"+block.getYIndex());
					uiGroup.setFillColor(getColorForGroup(block, group));
					uiGroup.getBlocks().add(rect);
				}
				layoutAdapter.getGroups().add(uiGroup);
			}
		}
		
		// workspace conversion
		workspaceAdapter.setWidth((int) kitchen.getXSize());
		workspaceAdapter.setHeight((int) kitchen.getYSize());
		
		if (kitchen != null && kitchen.getItemSockets().size() > 0) {
			
			for (ItemSocket itemSocket : kitchen.getItemSockets()) {
			
				if(itemSocket.getItem()!=null){
					element = new Element();
					element.setId(itemSocket.getId());
					element.setPosX(itemSocket.getItem().getXPos());
					element.setPosY(itemSocket.getItem().getYPos());
					element.setType(itemSocket.getItem().eClass().getName());
					workspaceAdapter.getObjects().add(element);
				}
			}
		}
		
		// setting the UIModels
		uiModelAdapter.setLayout(layoutAdapter);
		uiModelAdapter.setWorkspace(workspaceAdapter);
		uiModelAdapter.setFailedDeltas(failedSynchroChange);
		failedSynchroChange = new Change();
		
		return uiModelAdapter;
	}
	
	private String getColorForGroup(Block block, Group group){
		String color = null;
		Boolean groupColorExist = false;
		
		//Group-Color mapping already exists
		for (Map.Entry<Integer, String> entry : this.groupColors.entrySet()){
			if(group.hashCode() == entry.getKey()){
				groupColorExist = true;
				return entry.getValue();
			}
		}
		
		//Group-color mapping for Group created by Forward Transformation(Create) 
		if(!groupColorExist && this.blockColors.size() > 0){
				for (Map.Entry<String, String> blockColor : this.blockColors.entrySet()){
					if(blockColor.getKey().equals(block.getXIndex()+"_"+block.getYIndex())){
						this.groupColors.put(group.hashCode(), blockColor.getValue());
						return blockColor.getValue();
					}
				}	
		}
		
		Random random = new Random();
        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(256*256*256);
        // format it as hexadecimal string (with hashtag and leading zeros)
        color = String.format("#%06x", nextInt);
        
        //Push Group-Color mapping for newly created Group()
		this.groupColors.put(group.hashCode(), color );
		
		return color;
	}
	
	private void refreshOldMapping(Copier objectMapping){
		String color = null;
		Map<Integer, String> groupColors_new = new HashMap();
		
		for (Map.Entry<Integer, String> currentMapping : this.groupColors.entrySet()){
			for (Map.Entry<EObject, EObject> entry : objectMapping.entrySet()){
				if(currentMapping.getKey() == entry.getKey().hashCode()){
					groupColors_new.put(entry.getValue().hashCode(),currentMapping.getValue());
				}
			}
		}
		
		this.groupColors.clear();
		this.groupColors = groupColors_new;
	}
	
	public UIModels getUIModelsAfterAtomicDeltaPropagation(Change changeFromGui) {
		change = changeFromGui;
		Consumer<Kitchen> kitchenEdit = (kitchen) -> {
		};
		
		Consumer<Grid> gridEdit = (grid) -> {
		};

		//order of handling the changes: deletion -> movement -> creation
		if (change.getDeleted() != null && change.getDeleted().size() > 0) {
			for (Element element : change.getDeleted()) {
				Consumer<Kitchen> editDelete = kitchenEdit.andThen((kitchen) -> {
					ItemSocket itemSocket =  kitchen.getItemSockets().stream().filter(x -> x.getId().equals(element.getId())).findFirst().orElse(null);
					EcoreUtil.delete(itemSocket.getItem());
				    EcoreUtil.delete(itemSocket);
				});
				try
			      {
					this.kitchenToGrid.performAndPropagateTargetEdit(editDelete);
			      } catch (SynchronisationFailedException e)
			      {
			    	  refreshOldMapping(e.getObjectMapping());
			    	  this.failedSynchroChange.getDeleted().add(element);
			      }
				
				deleteItemstoRemove.add(element);
				
				if(configurator.hasContinuation()){
					return convertToUIModels();
				}
			}
		}
		
		if (change.getMoved() != null && change.getMoved().size() > 0) {
			for (Element element : change.getMoved()) {
				Consumer<Kitchen> editMoved = kitchenEdit.andThen((kitchen) -> {
					ItemSocket itemSocket = kitchen.getItemSockets().stream().filter(x -> x.getId().equals(element.getId())).findFirst().orElse(null);
					itemSocket.getItem().setXPos(element.getPosX());
					itemSocket.getItem().setYPos(element.getPosY());
				});
				try
			      {
					this.kitchenToGrid.performAndPropagateTargetEdit(editMoved);
			      } catch (SynchronisationFailedException e)
			      {
			    	  refreshOldMapping(e.getObjectMapping());
			    	  this.failedSynchroChange.getMoved().add(element);
			      }
				
				movedItemstoRemove.add(element);
				
				if(configurator.hasContinuation()){
					return convertToUIModels();
				}
			}
		}
		
		if (change.getCreated() != null && change.getCreated().size() > 0) {
			for (Element element : change.getCreated()) {
				Consumer<Kitchen> editCreate = kitchenEdit.andThen((kitchen) -> {
					String type = element.getType();
					EClass eClass = (EClass) KitchenLanguagePackage.eINSTANCE.getEClassifier(type);
					ItemSocket itemSocket = KitchenLanguageFactory.eINSTANCE.createItemSocket();
					Item item = (Item) KitchenLanguageFactory.eINSTANCE.create(eClass);
					itemSocket.setId(element.getId());
					item.setXPos(element.getPosX());
					item.setYPos(element.getPosY());
					itemSocket.setItem(item);
					kitchen.getItemSockets().add(itemSocket);
				});
				try
			      {
					this.kitchenToGrid.performAndPropagateTargetEdit(editCreate);
			      } catch (SynchronisationFailedException e)
			      {
			    	refreshOldMapping(e.getObjectMapping());
			    	this.failedSynchroChange.getCreated().add(element);
			      }
				
				createItemstoRemove.add(element);
				
				if(configurator.hasContinuation()){
					return convertToUIModels();
				}
			}
		}
		
		if (change.getGroupDeleted() != null && change.getGroupDeleted().size() > 0) {
			for (UIGroup uiGroup : change.getGroupDeleted()) {
				Consumer<Grid> editGroupDeleted = gridEdit.andThen((grid) -> {
					Group matchGroup = null;
					out:
					for (Group group : grid.getGroups()){
						for (Block block : group.getOccupies()){
							if (block.getXIndex() == uiGroup.getBlocks().get(0).getxIndex() && block.getYIndex() == uiGroup.getBlocks().get(0).getyIndex()){
								matchGroup = group;
								break out;
							}
						}
					}
				    EcoreUtil.delete(matchGroup);
				});
				try
			      {
					this.kitchenToGrid.performAndPropagateSourceEdit(editGroupDeleted);
			      } catch (SynchronisationFailedException e)
			      {
			    	refreshOldMapping(e.getObjectMapping());
			    	this.failedSynchroChange.getGroupDeleted().add(uiGroup);
			      }
				
				grpDeletedtoRemove.add(uiGroup);
				
				if(configurator.hasContinuation()){
					return convertToUIModels();
				}
			}
		}
		
		if (change.getGroupCreated() != null && change.getGroupCreated().size() > 0) {
			for (UIGroup uiGroup : change.getGroupCreated()) {
				Consumer<Grid> editGroupCreated = gridEdit.andThen((grid) -> {
					Group group = (Group) GridLanguageFactory.eINSTANCE.createGroup();
					for(Rectangle rect : uiGroup.getBlocks()){
						this.blockColors.put(rect.getxIndex()+"_"+rect.getyIndex(), uiGroup.getFillColor());
						Block block = grid.getBlocks().stream().filter(x -> x.getXIndex() == rect.getxIndex() && x.getYIndex() == rect.getyIndex()).findFirst().orElse(null);
						group.getOccupies().add(block);
					}
					grid.getGroups().add(group);
				});
				try
			      {
					this.kitchenToGrid.performAndPropagateSourceEdit(editGroupCreated);
			      } catch (SynchronisationFailedException e)
			      {
			    	refreshOldMapping(e.getObjectMapping());
			    	this.failedSynchroChange.getGroupCreated().add(uiGroup);
			      }
				
				grpCreatedtoRemove.add(uiGroup);
				
				if(configurator.hasContinuation()){
					return convertToUIModels();
				}
			}
		}
		
		this.uiModelsAdapter = convertToUIModels();
		
		return this.uiModelsAdapter;
	}

}
