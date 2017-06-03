package bxtooldemo.adapter.uimodels;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arjya Shankar Mishra
 *
 */
public class UIModels {
	
	private Layout layout;
	private Workspace workspace;
	private Change failedDeltas;
	private List<String> userChoices = new ArrayList<String>();
	/**
	 * @return the failedDeltas
	 */
	public Change getFailedDeltas() {
		return failedDeltas;
	}
	/**
	 * @param failedDeltas the failedDeltas to set
	 */
	public void setFailedDeltas(Change failedDeltas) {
		this.failedDeltas = failedDeltas;
	}
	/**
	 * @return the userChoices
	 */
	public List<String> getUserChoices() {
		return userChoices;
	}
	/**
	 * @param userChoices the userChoices to set
	 */
	public void setUserChoices(List<String> userChoices) {
		this.userChoices = userChoices;
	}
	/**
	 * @return the layout
	 */
	public Layout getLayout() {
		return layout;
	}
	/**
	 * @param layout the layout to set
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
	}
	/**
	 * @return the workspace
	 */
	public Workspace getWorkspace() {
		return workspace;
	}
	/**
	 * @param workspace the workspace to set
	 */
	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

}
