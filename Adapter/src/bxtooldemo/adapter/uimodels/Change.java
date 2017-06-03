package bxtooldemo.adapter.uimodels;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arjya Shankar Mishra
 *
 */
public class Change {
	
	private List<Element> created;
	private List<Element> deleted;
	private List<Element> moved;
	private List<UIGroup> groupCreated;
	private List<UIGroup> groupDeleted;
	
	public Change(){
		this.created = new ArrayList<Element>();
		this.deleted = new ArrayList<Element>();
		this.moved = new ArrayList<Element>();
		this.groupCreated = new ArrayList<UIGroup>();
		this.groupDeleted = new ArrayList<UIGroup>();
	}
	/**
	 * @return the created
	 */
	public List<Element> getCreated() {
		return created;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(List<Element> created) {
		this.created = created;
	}
	/**
	 * @return the deleted
	 */
	public List<Element> getDeleted() {
		return deleted;
	}
	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(List<Element> deleted) {
		this.deleted = deleted;
	}
	/**
	 * @return the moved
	 */
	public List<Element> getMoved() {
		return moved;
	}
	/**
	 * @param moved the moved to set
	 */
	public void setMoved(List<Element> moved) {
		this.moved = moved;
	}
	/**
	 * @return the groupCreated
	 */
	public List<UIGroup> getGroupCreated() {
		return groupCreated;
	}
	/**
	 * @param groupCreated the groupCreated to set
	 */
	public void setGroupCreated(List<UIGroup> groupCreated) {
		this.groupCreated = groupCreated;
	}
	/**
	 * @return the groupDeleted
	 */
	public List<UIGroup> getGroupDeleted() {
		return groupDeleted;
	}
	/**
	 * @param groupDeleted the groupDeleted to set
	 */
	public void setGroupDeleted(List<UIGroup> groupDeleted) {
		this.groupDeleted = groupDeleted;
	}

}
