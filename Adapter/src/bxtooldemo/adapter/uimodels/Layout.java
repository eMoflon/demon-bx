package bxtooldemo.adapter.uimodels;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arjya Shankar Mishra
 *
 */
public class Layout extends Canvas {
	
	public double gridSize;
	public List<Rectangle> blocks ;
	public List<UIGroup> groups;
	
	public Layout(){
		super(500, 500);
		this.blocks = new ArrayList<Rectangle>();
		this.groups = new ArrayList<UIGroup>();
	}

	/**
	 * @return the gridSize
	 */
	public double getGridSize() {
		return gridSize;
	}

	/**
	 * @param gridSize the gridSize to set
	 */
	public void setGridSize(double gridSize) {
		this.gridSize = gridSize;
	}

	/**
	 * @return the blocks
	 */
	public List<Rectangle> getBlocks() {
		return this.blocks;
	}

	/**
	 * @param blocks the blocks to set
	 */
	public void setBlocks(List<Rectangle> blocks) {
		this.blocks = blocks;
	}

	/**
	 * @return the groups
	 */
	public List<UIGroup> getGroups() {
		return this.groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<UIGroup> groups) {
		this.groups = groups;
	}

}
