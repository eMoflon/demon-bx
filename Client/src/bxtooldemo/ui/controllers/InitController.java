package bxtooldemo.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bxtooldemo.adapter.core.uiservice.Analysis;
import bxtooldemo.adapter.uimodels.Change;
import bxtooldemo.adapter.uimodels.Element;
import bxtooldemo.adapter.uimodels.Rectangle;
import bxtooldemo.adapter.uimodels.UIGroup;
import bxtooldemo.adapter.uimodels.UIModels;

/**
 * Servlet implementation class InitController
 */
@WebServlet("/InitController")
public class InitController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Analysis adapterAnalysis;
	private UIModels uimodels;
	private String UUID;
	private Map<String, Object> uidMappings;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InitController() {
		super();
		//this.adapterAnalysis = new Analysis();
		this.uidMappings = new HashMap();
	}

	/**
	 * init() method is typically used to perform servlet initialization and guaranteed 
	 * to be called before the servlet handles its first request.
	 */

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO
	}

	@Override
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();

		if (request.getParameter("initCanvas") != null) {

			initSetup(request, response, gson);
		}

		if (request.getParameter("loadChanges") != null) {

			propagateChanges(request, response, gson);
		}

		if (request.getParameter("userChoice") != null) {

			propagateUserChoice(request, response, gson);
		}
		
		if (request.getParameter("exitDemo") != null) {

			exit(request, response, gson);
		}

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().println( new Gson().toJson(this.uimodels));	
	}

	public void initSetup(HttpServletRequest request, HttpServletResponse response, Gson gson){

		this.adapterAnalysis = new Analysis();
		int blockArrayNo;

		blockArrayNo = gson.fromJson(request.getParameter("blockArrayNo"), int.class);
		this.UUID = gson.fromJson(request.getParameter("userID"), String.class);

		this.uidMappings.put(this.UUID, this.adapterAnalysis);
		this.adapterAnalysis.initeMoflonTool(blockArrayNo);
		this.uimodels = this.adapterAnalysis.getUIModels();

	}

	public void propagateChanges(HttpServletRequest request, HttpServletResponse response, Gson gson){

		String jsonCreated;
		String jsonDeleted;
		String jsonMoved;
		String jsonBlocksCreated;
		String jsonBlocksDeleted;
		List<Element> createdItems = null;
		List<Element> deletedItems = null;
		List<Element> movedItems = null;
		List<Rectangle> createdBlocks = null;
		List<Rectangle> deletedBlocks = null;
		List<UIGroup> createdGroups =  new ArrayList<UIGroup>();
		List<UIGroup> deletedGroups = new ArrayList<UIGroup>();

		jsonCreated = request.getParameter("ItemsCreated");
		jsonDeleted = request.getParameter("ItemsDeleted");
		jsonMoved = request.getParameter("ItemsMoved");
		jsonBlocksCreated = request.getParameter("BlocksCreated");
		jsonBlocksDeleted = request.getParameter("BlocksDeleted");
		createdItems = gson.fromJson(jsonCreated, new TypeToken<List<Element>>(){}.getType());
		deletedItems = gson.fromJson(jsonDeleted, new TypeToken<List<Element>>(){}.getType());
		movedItems = gson.fromJson(jsonMoved, new TypeToken<List<Element>>(){}.getType());
		createdBlocks = gson.fromJson(jsonBlocksCreated, new TypeToken<List<Rectangle>>(){}.getType());
		deletedBlocks = gson.fromJson(jsonBlocksDeleted, new TypeToken<List<Rectangle>>(){}.getType());
		this.UUID = gson.fromJson(request.getParameter("userID"), String.class);

		if(createdBlocks.size()> 0){
			createdGroups = formGroups(createdBlocks);
		}

		if(deletedBlocks.size()> 0){
			deletedGroups = formGroups(deletedBlocks);
		}

		//Create change
		Change change = new Change();
		change.setCreated(createdItems);
		change.setDeleted(deletedItems);
		change.setMoved(movedItems);
		change.setGroupCreated(createdGroups);
		change.setGroupDeleted(deletedGroups);

		this.adapterAnalysis = (Analysis) getObject(this.UUID);
		
		if(this.adapterAnalysis == null) {
			this.adapterAnalysis = new Analysis();
			this.uidMappings.put(this.UUID, this.adapterAnalysis);

			this.adapterAnalysis.initeMoflonTool(5);
			this.uimodels = this.adapterAnalysis.getUIModels();
		}
		else
			this.uimodels = this.adapterAnalysis.getUIModelsAfterAtomicDeltaPropagation(change);	
	}

	private void propagateUserChoice(HttpServletRequest request, HttpServletResponse response, Gson gson) {

		String UserChoice;
		
		UserChoice = gson.fromJson(request.getParameter("UserChoice"), new TypeToken<String>(){}.getType());
		this.UUID = gson.fromJson(request.getParameter("userID"), String.class);
		
		this.adapterAnalysis = (Analysis) getObject(this.UUID);
		
		if(this.adapterAnalysis == null) {
			this.adapterAnalysis = new Analysis();
			this.uidMappings.put(this.UUID, this.adapterAnalysis);

			this.adapterAnalysis.initeMoflonTool(5);
			this.uimodels = this.adapterAnalysis.getUIModels();
		}
		else
			this.uimodels = this.adapterAnalysis.continueSynchronisation(UserChoice);
	}

	public List<UIGroup> formGroups(List<Rectangle> Blocks){
		List<UIGroup> uiGroups = new ArrayList<UIGroup>();
		List<String> colorAll = new ArrayList<String>();
		List<Rectangle> sameColorRect;
		UIGroup uiGroup;


		for (Rectangle rect : Blocks) {
			//list with duplicates(all colors)
			colorAll.add(rect.getFillColor());
		}

		//Unique colors
		HashSet<String> uniqueValues = new HashSet<>(colorAll);

		//collect all same color blocks and create group for them
		for (String value : uniqueValues) {
			uiGroup = new UIGroup();
			sameColorRect = new ArrayList<Rectangle>();

			sameColorRect = Blocks.stream().filter(t -> t.getFillColor().equals(value)).collect(Collectors.toList());
			uiGroup.setBlocks(sameColorRect);
			uiGroup.setFillColor(value);
			uiGroups.add(uiGroup);
		}

		return uiGroups;

	}

	public Object getObject(String UUID) {

		Object object = null;
		
		//UUID-Object mapping already exists
		for (Map.Entry<String, Object> entry : this.uidMappings.entrySet()){
			if(entry.getKey().equals(UUID)){
				object = entry.getValue();
			}
		}
		return object;
	}
	
	public void exit(HttpServletRequest request, HttpServletResponse response, Gson gson) {
		
		this.UUID = gson.fromJson(request.getParameter("userID"), String.class);
		Boolean uidExist = false;;

		//check whether uid exists
		for (Map.Entry<String, Object> entry : this.uidMappings.entrySet()){
			if(entry.getKey().equals(this.UUID)){
				uidExist = true;
			}
		}
		
		if(uidExist)
		this.uidMappings.remove(this.UUID);
	}

}
