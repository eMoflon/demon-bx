package bxtooldemo.adapter.uimodels;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arjya Shankar Mishra
 *
 */
public class Workspace extends Canvas {

	private List<Element> objects;
	private List<String> objectTypeList;
	
	public Workspace(){
		super(500, 500);
		this.objects = new ArrayList<Element>();
	}

	/**
	 * @return the objects
	 */
	public List<Element> getObjects() {
		return objects;
	}

	/**
	 * @param objects the objects to set
	 */
	public void setObjects(Element object) {
		this.objects.add(object);
	}

	/**
	 * @return the objectTypeList
	 */
	public List<String> getObjectTypeList() {
		return objectTypeList;
	}

	/**
	 * @param objectTypeList the objectTypeList to set
	 */
	public void setObjectTypeList(List<String> objectTypeList) {
		this.objectTypeList = objectTypeList;
	}
}
