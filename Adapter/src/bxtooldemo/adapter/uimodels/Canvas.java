package bxtooldemo.adapter.uimodels;

/**
 * @author Arjya Shankar Mishra
 *
 */
public class Canvas {
	
	private int height;
	private int width;
	
	/**
	 * @param height
	 * @param width
	 */
	public Canvas(int height, int width){
		this.height = height;
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	
}
