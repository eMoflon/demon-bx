/**
 * 
 */

var Layout = new fabric.Canvas('canvasLayout');
var Workspace = new fabric.Canvas('canvasWorkspace');
var Uid = null;
var object_counter;
var noOfBlocks;
var KitItemsCreated = [];
var KitItemsDeleted = [];
var KitItemsMoved = [];
var LayoutBlocksCreated = [];
var LayoutBlocksDeleted = [];
var previousClickedBlock = null;
var lastAssignedColor = null;
var GuiUserChoice = null;
var x = 584;
var y = 92;
$('[data-toggle="tooltip"]').tooltip({
    trigger : 'hover'
}) 
var scenario0= new Array();
var conclusion0 = "This example is a simplified kitchen planner that offers to the right a symbolic view (the ktichen) of all objects currently positioned in the kitchen, and to the left a layout view (the grid) showing how much space the objects occupy as coloured groups of blocks organised in a grid.<br> You can create or delete any number of sinks, tables, and fridges in the kitchen, and press Sync to propagate your changes to the grid.<br><br>" +
        "You will find out that the western blue wall has all the water outlets, so you can only create sinks near enough to it, while the northern red wall has all the electrical outlets, meaning you can only create fridges near enough to it.<br><br>" +
		"Tables can be created anywhere but you'll have to decide if you want the table to take up two horizontal or two vertical blocks in the grid. The synchroniser will prompt you for your preference if necessary.<br><br>" +
		"You can of course move objects around in the kitchen if their final position is still legal.<br><br>" +
		"The kitchen planner can also be used to find out what would fit in a certain group of blocks. To do this click a group of blocks in the grid so they have the same colour (Click on any white block. You will get a new color. Now, same color can be applied on any other white block with a click. For a new color, click thrice on any white block.) and then press Sync to get suggestions for which corresponding object can be placed in the kitchen.<br><br>" +
        "Note that extra blocks are ignored if the object will not occupy them all, but are retained for the rest of the session. You can think of this as reserving empty or extra space (for chairs, or a flower pot, etc). It is also possible to vary the granularity of the grid by changing its height/width in number of blocks (this triggers a refresh).<br><br>" +
		"You can delete whole groups of blocks in the grid by clicking on them (the colour of the group will be greyed out).<br><br>" +
		"You cannot, however, move groups of blocks around in the grid, or perform changes in both the grid and the kitchen before pressing the Sync button.";

var scenario1= new Array("Create a sink in the kitchen (recall that this is only possible directly on the blue wall)", "Synchronise", "Now move the sink away from the blue wall and synchronise","Your change will be rejected as there exists no grid that would be consistent with this change and new kitchen");
var conclusion1 = "This demonstrates that not all possible changes can be synchronised (according to our current rules). Other example for changes in the kitchen that can be made but will be rejected include creating or moving fridges too far away from the red wall, or moving any objects on top of each other.<br><br>" + "Reset and Go to the next scenario.";
var scenario2= new Array("Click on two vertical blocks in the layout so that they have the same colour and the top-most block is directly on the northern(top) wall", "Synchronise", "You will be asked to choose to create either a fridge, or a vertical table", "Enter a number representing your preference", "Your preferred object will be created in the kitchen");
var conclusion2 = "This demonstrates how user interaction (or some other, possibly automated means) can be used to decide between multiple equally consistent results.<br><br>" + "Reset and Go to the next scenario.";
var scenario3= new Array("Create a fridge on the red wall of the kitchen", "Synchronise (and note the colour of the group of blocks created in the grid)", "Now delete the fridge and synchronise", "Assuming the deletion was a mistake, undo it by re-creating the fridge and synchronising", 
"Although the kitchen is now (for all we care) in the same state it was in after Step (2), the grid is, however, in a different state as the re-created group has a different colour than before");
var conclusion3 = "This demonstrates that undoing changes in the kitchen to revert to a previous state does not necessarily imply that this can be reflected analogously in the grid.<br><br>" + "Reset and Go to the next scenario.";
var scenario4= new Array("Create two sinks, sink_1 and sink_2 apart from each other, somewhere along the blue wall of the kitchen", "Synchronise and note the colours of the two groups created in the grid for the sinks", "Now move sink_1 to a new valid location (along the blue wall) via drag and drop", 
		"Also move sink_2 to a new valid location, but this time by deleting it and then recreating a sink at the desired new location (along the blue wall)", 
		"Synchronise and note how both groups are moved correspondingly in the grid, but that the colour of the group for sink_1 is retained, while the group corresponding to the second sink now has a new colour");
var conclusion4 = "This demonstrates that the actual change performed can have an effect on synchronisation results, even if the final result (the kitchen here) might appear to be exactly the same in both cases (the sinks are both moved to their new positions as far as we can see and thus care).<br><br>" + "Reset and Go to the next scenario.";
var scenario5= new Array("Create a fridge on the red wall of the kitchen", "Synchronise to update the grid", "Fill a few isolated and single blocks with unique colours in the grid", "Synchronise (these blocks will be ignored and the kitchen remains unchanged)", "Now move the fridge along the red wall in the kitchen and synchronise",
		"As you might expect, the isolated blocks that have no relevance for the kitchen are nonetheless preserved in the grid");
var conclusion5 = "This demonstrates that it is possible to avoid unnecessary information loss (the single blocks), if the old state of the grid is taken into account. It would be impossible to do this if the grid were created solely from the kitchen (and vice-versa).";


window.onload = init;
window.onunload = close;

function init() {
	Uid = generateUUID();
	object_counter = 0;
	KitItemsCreated.length = 0;
	KitItemsDeleted.length = 0;
	KitItemsMoved.length = 0;
	LayoutBlocksCreated.length = 0;
	LayoutBlocksDeleted.length = 0;
	if (isNaN($("#arrayNumber").val()) || $("#arrayNumber").val() < 5 || $("#arrayNumber").val() > 10 ){
		$("#arrayNumber").val("");
	}
	noOfBlocks = ($("#arrayNumber").val() === "") ? 5 : $("#arrayNumber").val();
	previousClickedBlock = null;
	lastAssignedColor = null;
	GuiUserChoice = null;
	$('#messageDialog').text("");
	$('#divItemList').hide();
	$.ajax({
		url : 'InitController',
		type : 'POST',
		dataType : 'json',
		data : {
			initCanvas : 1,
			userID: Uid,
			blockArrayNo : noOfBlocks
		},
		success : function(data) {
			initVisualize(data);
		}
	});
}

function reInit() {
	init();
	Layout.clear();
	Workspace.clear();
	drawGrid();
}

function close() {
	$.ajax({
		url : 'InitController',
		type : 'POST',
		dataType : 'json',
		data : {
			exitDemo : 1,
			userID: Uid,
		},
		success : function(data) {
			Uid = null;
		}
	});
}

function generateUUID() { // Public Domain/MIT
    var d = new Date().getTime();
    if (typeof performance !== 'undefined' && typeof performance.now === 'function'){
        d += performance.now(); //use high-precision timer if available
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
}

function drawBorder() {

	Workspace.add(new fabric.Rect({
		width : 5,
		height : 500,
		left : 0,
		top : 0,
		hasControls : false,
		stroke : '#000000',
		fill : 'blue',
		lockMovementX : true,
		lockMovementY : true,
		borderColor : 'transparent',
		subType : 'fitting'
	}));    

	Workspace.add(new fabric.Rect({
		width : 500,
		height : 5,
		left : 0,
		top : 0,
		hasControls : false,
		stroke : '#000000',
		fill : 'red',
		lockMovementX : true,
		lockMovementY : true,
		borderColor : 'transparent',
		subType : 'fitting'
	}));
}

function undo() {
	if (LayoutBlocksCreated.length <= 0 && LayoutBlocksDeleted.length <= 0){
		$('#messageDialog').text("There is currently no change to be undone in Layout.");
	}
	else {
		KitItemsCreated.length = 0;
		KitItemsDeleted.length = 0;
		KitItemsMoved.length = 0;
		LayoutBlocksCreated.length = 0;
		LayoutBlocksDeleted.length = 0;
		previousClickedBlock = null;
		lastAssignedColor = null;
		GuiUserChoice = null;
		propagateChanges();
		$('#messageDialog').text("Changes undone in Layout.");
	}
}

function sychro() {
	if((KitItemsCreated.length > 0 || KitItemsDeleted.length > 0 || KitItemsMoved.length > 0) && (LayoutBlocksCreated.length > 0 || LayoutBlocksDeleted.length > 0)){
		KitItemsCreated.length = 0;
		KitItemsDeleted.length = 0;
		KitItemsMoved.length = 0;
		LayoutBlocksCreated.length = 0;
		LayoutBlocksDeleted.length = 0;
		previousClickedBlock = null;
		lastAssignedColor = null;
		GuiUserChoice = null;
		propagateChanges();
		$('#messageDialog').text("You cannot make changes on both side.");
		
	}
	else if(KitItemsCreated.length <= 0 && KitItemsDeleted.length <= 0 && KitItemsMoved.length <= 0 && LayoutBlocksCreated.length <= 0 && LayoutBlocksDeleted.length <= 0 ){
		$('#messageDialog').text("There is currently no change to propagate.");
	}
	else{
		$('#messageDialog').text("");
		propagateChanges();
	}
	
}

function propagateChanges(){
	$.ajax({
		url : 'InitController',
		type : 'POST',
		dataType : 'json',
		data : {
			loadChanges : 1,
			userID: Uid,
			ItemsCreated : JSON.stringify(KitItemsCreated),
			ItemsDeleted : JSON.stringify(KitItemsDeleted),
			ItemsMoved : JSON.stringify(KitItemsMoved),
			BlocksCreated: JSON.stringify(LayoutBlocksCreated),
			BlocksDeleted: JSON.stringify(LayoutBlocksDeleted)
		},
		success : function(data) {
			if(data.userChoices.length > 0){
				userChoiceVisualize(data);
			}
			else{
				changeVisualize(data);
			}
			
		}
	});

	KitItemsCreated.length = 0;
	KitItemsDeleted.length = 0;
	KitItemsMoved.length = 0;
	LayoutBlocksCreated.length = 0;
	LayoutBlocksDeleted.length = 0;
	previousClickedBlock = null;
	lastAssignedColor = null;
	GuiUserChoice = null;
}

function propagateUserChoices(){
	$.ajax({
		url : 'InitController',
		type : 'POST',
		dataType : 'json',
		data : {
			userChoice : 1,
			userID: Uid,
			UserChoice : JSON.stringify(GuiUserChoice),
		},
		success : function(data) {
			if(data.userChoices.length > 0){
				userChoiceVisualize(data);
			}
			else{
				changeVisualize(data);
			}
		}
	});
	
	GuiUserChoice = null;
}

function userChoiceVisualize(uiModels) {
	var options = "";
	var selection = 0;
	for (var i = 0; i < uiModels.userChoices.length; i++) {
		if(i < uiModels.userChoices.length - 1)
		options = options + " " + i + " to " + uiModels.userChoices[i] + ", or";
		else 
			options = options + " " + i + " to " + uiModels.userChoices[i] + ".";
	}
	selection = prompt("Please enter" + options);
	
	if(selection === null || selection > (uiModels.userChoices.length - 1) || isNaN(selection) || selection === "")
		GuiUserChoice = uiModels.userChoices[0];
	else
	    GuiUserChoice = uiModels.userChoices[selection];
	
	propagateUserChoices();
}

function initVisualize(uiModels) {

	Layout.setDimensions({
		width : uiModels.layout.width,
		height : uiModels.layout.height
	});
	Workspace.setDimensions({
		width : uiModels.workspace.width,
		height : uiModels.workspace.width
	});
	drawGrid();
	drawBorder();
	console.log("Visualization done after initialization");
}

function changeVisualize(uiModels) {

	//Visualize Kitchen
	Workspace.clear();
	drawBorder();
	if (uiModels!= null && uiModels.workspace.objects.length > 0) {
		 //create items 
		for (var i = 0; i < uiModels.workspace.objects.length; i++) {
			if(uiModels.workspace.objects[i].type == 'Sink'){
			    addSinkVisualize(uiModels, i);}
			else if(uiModels.workspace.objects[i].type == 'Table') {
				addTableVisualize(uiModels, i);}
			else {
				addFridgeVisualize(uiModels, i);}
		}
	}
	
	//Visualize Grid
	Layout.clear();
	drawGrid();

	if (uiModels!= null && uiModels.layout.groups.length > 0) {
		// fillColor into blocks for groups
		for (var i = 0; i < uiModels.layout.groups.length; i++) {
			for (var j = 0; j < uiModels.layout.groups[i].blocks.length; j++) {
				for (var k = 0; k < Layout.getObjects().length; k++) {
					if (Layout.getObjects()[k].get('id') === uiModels.layout.groups[i].blocks[j].id) {
						Layout.getObjects()[k].setFill(uiModels.layout.groups[i].fillColor);
						Layout.renderAll();
					}

				}
			}
		}
	}
	
	//Visualize Failed Deltas
	if (uiModels!= null && uiModels.failedDeltas!= null && (uiModels.failedDeltas.created.length > 0 
			|| uiModels.failedDeltas.deleted.length > 0 
			|| uiModels.failedDeltas.moved.length > 0
			|| uiModels.failedDeltas.groupCreated.length > 0
			|| uiModels.failedDeltas.groupDeleted.length > 0) ) {
		var failedDeltaMsg = "The following changes were rejected: ";
		if(uiModels.failedDeltas.created.length > 0){
			for(var i = 0; i < uiModels.failedDeltas.created.length; i++) {
				failedDeltaMsg = failedDeltaMsg + " Creation of " + uiModels.failedDeltas.created[i].type;
			}	
		}
		
		if(uiModels.failedDeltas.deleted.length > 0){
			for(var i = 0; i < uiModels.failedDeltas.deleted.length; i++) {
				failedDeltaMsg = failedDeltaMsg + " Deletion of " + uiModels.failedDeltas.deleted[i].type;
			}	
		}
		
		if(uiModels.failedDeltas.moved.length > 0){
			for(var i = 0; i < uiModels.failedDeltas.moved.length; i++) {
				failedDeltaMsg = failedDeltaMsg + " Movement of " + uiModels.failedDeltas.moved[i].type;
			}	
		}
		
		if(uiModels.failedDeltas.groupCreated.length > 0){
			for(var i = 0; i < uiModels.failedDeltas.groupCreated.length; i++) {
				failedDeltaMsg = failedDeltaMsg + " Creation of Group";
			}	
		}
		
		if(uiModels.failedDeltas.groupDeleted.length > 0){
			for(var i = 0; i < uiModels.failedDeltas.groupDeleted.length; i++) {
				failedDeltaMsg = failedDeltaMsg + " Deletion of Group";
			}	
		}
		$('#messageDialog').text(failedDeltaMsg);
	}
	
	console.log("Visualization done after change propagation");

}

function addSink(objectType, object_counter){
	fabric.Image.fromURL('assets/sink.png', function(img) {
		var oImg = img.set({ left: x - 584, top: y - 92, subType: objectType, id: objectType + "_" + object_counter}).scale(0.1);
        Workspace.add(oImg);
        });
}

function addSinkVisualize(uiModels, val){
	fabric.Image.fromURL('assets/sink.png', function(img) {
		var oImg = img.set({ left: uiModels.workspace.objects[val].posX, top: uiModels.workspace.objects[val].posY, subType: uiModels.workspace.objects[val].type, id: uiModels.workspace.objects[val].id}).scale(0.1);
        Workspace.add(oImg);
        });
}

function addTable(objectType, object_counter){
	fabric.Image.fromURL('assets/table.png', function(img) {
		var oImg = img.set({ left: x - 584, top: y - 92, subType: objectType, id: objectType + "_" + object_counter}).scale(0.15);
        Workspace.add(oImg);
        });
}

function addTableVisualize(uiModels, val){
	fabric.Image.fromURL('assets/table.png', function(img) {
		var oImg = img.set({ left: uiModels.workspace.objects[val].posX, top: uiModels.workspace.objects[val].posY, subType: uiModels.workspace.objects[val].type, id: uiModels.workspace.objects[val].id}).scale(0.15);
        Workspace.add(oImg);
        });
}

function addFridge(objectType, object_counter){
	fabric.Image.fromURL('assets/fridge.png', function(img) {
		var oImg = img.set({ left: x - 584, top: y - 92, subType: objectType, id: objectType + "_" + object_counter}).scale(0.1);
        Workspace.add(oImg);
        });
}

function addFridgeVisualize(uiModels, val){
	fabric.Image.fromURL('assets/fridge.png', function(img) {
		var oImg = img.set({ left: uiModels.workspace.objects[val].posX, top: uiModels.workspace.objects[val].posY, subType: uiModels.workspace.objects[val].type, id: uiModels.workspace.objects[val].id}).scale(0.1);
        Workspace.add(oImg);
        });
}

function drawGrid() {

	var noOfBlocks = ($("#arrayNumber").val() === "") ? 5 : $("#arrayNumber")
			.val();
	var blockHeight = Layout.height / noOfBlocks;
	var blockWidth = Layout.width / noOfBlocks;
	for (var i = 0; i < noOfBlocks; i++) {
		for (var j = 0; j < noOfBlocks; j++) {
			var gOptions = {
				width : blockWidth,
				height : blockHeight,
				top : blockHeight * j,
				left : blockWidth * i,
				hasControls : false,
				stroke : '#000000',
				fill : 'transparent',
				lockMovementX : true,
				lockMovementY : true,
				borderColor : 'transparent',
			};
			var rOptions = {
				width : blockWidth,
				height : blockHeight,
				top : blockHeight * j,
				left : blockWidth * i,
				//rx: 10,
				//ry: 10,
				hasControls : false,
				stroke : '#000000',
				fill : 'transparent',
				lockMovementX : true,
				lockMovementY : true,
				borderColor : 'transparent',
				subType : 'block',
				xPos: i,
				yPos: j,
				id : 'block_' + i + '_' + j
			};
			//var c = new fabric.Rect(gOptions);
			var r = new fabric.Rect(rOptions);

			//canvas.add(c);
			Layout.add(r);
		}
	}
	//	$("#grid_btn").disabled = false;
	//	$("#arrayNumber").attr("disabled", "disabled");
}

function addObject() {
	var objectType = $("#objectSelect").val();
	
	if(objectType == "Sink"){
	    addSink(objectType, object_counter);}
	else if(objectType == "Table") {
		addTable(objectType, object_counter);}
	else {
		addFridge(objectType, object_counter);}
	
	KitItemsCreated.push({
		id : objectType + "_" + object_counter,
		type : objectType,
		posX : x - 584,
		posY : y - 92
	});
	object_counter++;
}

function deleteObject() {
	if(Workspace.getActiveObject() == null)
		$('#messageDialog').text("Nothing to delete. Please select an item to delete.");
	else{
		handleDelete();
		Workspace.getActiveObject().remove();
	}
	
}

function handleDelete(){
	var itemCreated = false;
	var itemMoved = false;
	
	//Check the item in Created array
	if(KitItemsCreated.length > 0){
		for(var i = 0; i < KitItemsCreated.length; i++) {
		    if(KitItemsCreated[i].id == Workspace.getActiveObject().id) {
		    	KitItemsCreated.splice(i, 1);
		    	itemCreated = true;
		        break;
		    }
		}
	}
	
	//Check the item in Moved array
	if(KitItemsMoved.length > 0){
		for(var i = 0; i < KitItemsMoved.length; i++) {
		    if(KitItemsMoved[i].id == Workspace.getActiveObject().id) {
		    	KitItemsMoved.splice(i, 1);
		    	itemMoved = true;
		        break;
		    }
		}
	}
	
	if(!itemCreated && !itemMoved){
		KitItemsDeleted.push({
			id : Workspace.getActiveObject().id,
			type : Workspace.getActiveObject().subType
		});
	}
}

function handleMove(){
	var itemCreated = false;
	var itemMoved = false;
	
	//Check the item in Created array
	if(KitItemsCreated.length > 0){
		for(var i = 0; i < KitItemsCreated.length; i++) {
		    if(KitItemsCreated[i].id == Workspace.getActiveObject().id) {
		    	KitItemsCreated[i].posX = Math.ceil(Workspace.getActiveObject().left);
		    	KitItemsCreated[i].posY = Math.ceil(Workspace.getActiveObject().top);
		    	itemCreated = true;
		        break;
		    }
		}
	}
	
	//Check the item in Moved array
	if(KitItemsMoved.length > 0){
		for(var i = 0; i < KitItemsMoved.length; i++) {
		    if(KitItemsMoved[i].id == Workspace.getActiveObject().id) {
		    	KitItemsMoved[i].posX = Math.ceil(Workspace.getActiveObject().left);
		    	KitItemsMoved[i].posY = Math.ceil(Workspace.getActiveObject().top);
		    	itemMoved = true;
		        break;
		    }
		}
	}
	
	if(!itemCreated && !itemMoved){
		KitItemsMoved.push({
			id : Workspace.getActiveObject().id,
			type : Workspace.getActiveObject().subType,
			posX : Math.ceil(Workspace.getActiveObject().left),
			posY : Math.ceil(Workspace.getActiveObject().top)
		});
	}
}

function handleCreateGroup(e) {

	// check if block already exists
	for (var i = 0; i < LayoutBlocksCreated.length; i++) {
		if (LayoutBlocksCreated[i].xIndex == e.target.xPos && LayoutBlocksCreated[i].yIndex == e.target.yPos) {
			LayoutBlocksCreated.splice(i, 1);
			break;
		}
	}
	
    //add new entry
	LayoutBlocksCreated.push({
		// id : objectType + "_" + object_counter,
		xIndex : e.target.xPos,
		yIndex : e.target.yPos,
		fillColor : e.target.fill
	});
}

function showInfo(val) {
	$('#messageHover').text(val);
}

Layout.hoverCursor = 'pointer';
Workspace.hoverCursor = 'pointer';

Workspace.on('mouse:down', function(options) {
//	$("#cursorx").val(options.e.clientX);
//	$("#cursory").val(options.e.clientY);
	x = options.e.clientX;
	y = options.e.clientY;
});

Workspace.on('mouse:move', function(options) {
	var pt = {
		x : options.e.clientX,
		y : options.e.clientY
	};

	if (options.target != null) {
		if (options.target.get('subType') == 'fitting' && options.target.get('fill') == 'blue') {
			showInfo('western wall with water outlet');
		}
		else if (options.target.get('subType') == 'fitting' && options.target.get('fill') == 'red') {
			showInfo('northern wall with electrical outlet');
		}
		else
		showInfo(options.target.subType);
	}
});

Workspace.on('mouse:out', function(e) {
	$('#messageHover').text("");
});

Workspace.on('object:added', function(e) {
	if (e.target != null) {
		console.log(e.target.subType + " created");
	}
});

Workspace.on('object:removed', function(e) {
	if (e.target != null) {
		console.log(e.target.subType + " deleted");
	}
});

Workspace.on('object:moving', function(e) {
	handleMove();
	
});

Layout.on('mouse:down', function(e) {
	var noOfClick = 1;
	var newGeneratedColor = null;
	if (e.target.get('subType') == 'block') {
		var currentClickedBlock = e.target.id;
		var colorBeforeChange = e.target.fill;
		console.log('block ' + e.target.id + ' was clicked');
		
		if(previousClickedBlock != null && currentClickedBlock == previousClickedBlock )
			noOfClick = 2;
		
		if (noOfClick == 1){
			if(colorBeforeChange == 'transparent'){
				newGeneratedColor = lastAssignedColor != null ? lastAssignedColor : genColor();
				e.target.setFill(newGeneratedColor);
				addToCreateGroup(e);
			}
			else if (colorBeforeChange != 'transparent' && blockExistinCreateGroup(e)){
				e.target.setFill("transparent");
				removeFromCreateGroup(e);
			}
			else if (colorBeforeChange != 'transparent' && e.target.opacity < 1){
				removeGroupFromDeleteGroup(colorBeforeChange);
			}
			else if (colorBeforeChange != 'transparent' && !blockExistinCreateGroup(e)){
				addGroupToDeleteGroup(colorBeforeChange);
			}
		}
		else if (noOfClick == 2){
			if(colorBeforeChange == 'transparent'){
				newGeneratedColor =  genColor();
				e.target.setFill(newGeneratedColor);
		       handleCreateGroup(e);   
			}
			else if (colorBeforeChange != 'transparent' && blockExistinCreateGroup(e)){
				e.target.setFill("transparent");
				removeFromCreateGroup(e);
			}
			else if (colorBeforeChange != 'transparent' && e.target.opacity < 1){
				removeGroupFromDeleteGroup(colorBeforeChange);
			}
			else if (colorBeforeChange != 'transparent' && !blockExistinCreateGroup(e)) {
				addGroupToDeleteGroup(colorBeforeChange);
			}
			noOfClick = 1;
		}
			
		Layout.renderAll();
		
		previousClickedBlock = e.target.id;
		lastAssignedColor = newGeneratedColor;
	}
});

function blockExistinCreateGroup(e){
	
	//check if block already exists
	for(var i = 0; i < LayoutBlocksCreated.length; i++) {
	    if(LayoutBlocksCreated[i].xIndex == e.target.xPos && LayoutBlocksCreated[i].yIndex == e.target.yPos) {
	    	return true;
	    }
	}
	return false;
}

function addToCreateGroup(e){
	
	// add block
	LayoutBlocksCreated.push({
		//id : objectType + "_" + object_counter,
		xIndex : e.target.xPos,
		yIndex : e.target.yPos,
		fillColor : e.target.fill
	});
}

function removeFromCreateGroup(e){
	
	//remove block
	if(LayoutBlocksCreated.length > 0){
		for(var i = 0; i < LayoutBlocksCreated.length; i++) {
		    if(LayoutBlocksCreated[i].xIndex == e.target.xPos && LayoutBlocksCreated[i].yIndex == e.target.yPos) {
		    	LayoutBlocksCreated.splice(i, 1);
		        break;
		    }
		}
	}
}

function genColor(){
	return '#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
}

function loadScenario(header, scenario, conclusion){
	var scenarioElement;
	$('#divItemList').show();
	$('#itemHeader').text("");
	$('#itemList').text("");
	$('#itemConclusion').text("");
	$('#itemHeader').append(header);
    for (i = 0; i < scenario.length; i++ ) {
        // Create the <LI> element
    	scenarioElement = document.createElement("LI");
        // Add the array values between the <LI> tags
    	scenarioElement.innerHTML = scenario[i];
        // Append the <LI> to the bottom of the <UL> element
        $('#itemList').append(scenarioElement);
    }
    $('#itemConclusion').append(conclusion);
}

function clearInstruction(){
	$('#divItemList').hide();
}

function addGroupToDeleteGroup(colorBeforeChange){
	
	for(var i = 0; i < Layout._objects.length; i++) {
		if (Layout._objects[i].fill == colorBeforeChange){
			Layout._objects[i].set({
		        opacity: 0.5
		    });
			
			LayoutBlocksDeleted.push({
				//id : objectType + "_" + object_counter,
				xIndex : Layout._objects[i].xPos,
				yIndex : Layout._objects[i].yPos,
				fillColor : colorBeforeChange
			});	
		}
	}
}

function removeGroupFromDeleteGroup(colorBeforeChange){
	
	for(var i = 0; i < Layout._objects.length; i++) {
		if (Layout._objects[i].fill == colorBeforeChange){
			Layout._objects[i].set({
	        opacity: 1
	    });
			
			//remove block
			if(LayoutBlocksDeleted.length > 0){
				for(var j = 0; j < LayoutBlocksDeleted.length; j++) {
				    if(LayoutBlocksDeleted[j].xIndex == Layout._objects[i].xPos && LayoutBlocksDeleted[j].yIndex == Layout._objects[i].yPos) {
				    	LayoutBlocksDeleted.splice(j, 1);
				        break;
				    }
				}
			}	
		}
	}
}

Workspace.renderAll();