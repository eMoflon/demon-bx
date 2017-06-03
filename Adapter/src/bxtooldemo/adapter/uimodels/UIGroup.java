package bxtooldemo.adapter.uimodels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/**
 * @author Arjya Shankar Mishra
 *
 */
public class UIGroup {
	
	public List<Rectangle> blocks;
	public String fillColor;
	
	public UIGroup(){
		this.blocks = new ArrayList<Rectangle>();
//	    Random random = new Random();
//        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
//        int nextInt = random.nextInt(256*256*256);
//        // format it as hexadecimal string (with hashtag and leading zeros)
//        this.fillColor = String.format("#%06x", nextInt);
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
	 * @return the fillColor
	 */
	public String getFillColor() {
		return this.fillColor;
	}

	/**
	 * @param fillColor the fillColor to set
	 */
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

}
