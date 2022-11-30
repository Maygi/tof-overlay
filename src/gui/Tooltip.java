package gui;

import java.util.Objects;

import sound.Sound;

/**
 * A class that represents tooltips.
 * @author May
 */
public class Tooltip {
	
	public static final int TEXT_SIZE = 16;
	private static final int BASE_SIZE = 64;
	private static final int BASE_SIZE_X = 180;
	private int x, y, xSize, ySize;
	private String title, text;
	
	public Tooltip(int x, int y, String title, String text) {
		this.x = x;
		this.y = y;
		this.xSize = BASE_SIZE_X;
		this.ySize = BASE_SIZE;
		this.title = title;
		this.text = text;
	}
	
	public Tooltip(int x, int y, String title, String text, int xSize, int ySize) {
		this.x = x;
		this.y = y;
		this.title = title;
		this.text = text;
		this.xSize = xSize;
		this.ySize = ySize;
	}
	
	/**
	 * Returns the coordinates of the upper-left corner of the tooltip.
	 * @return The coordinates of the upper-left corner of the tooltip.
	 */
	public int[] getCoords() {
		return new int[] {x, y};
	}
	
	public boolean checkCollision(int x, int y) {
    	int[] coords = getCoords();
        return (x >= coords[0] && x <= coords[0] + xSize &&
        		y >= coords[1] && y <= coords[1] + ySize);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Tooltip))
			return false;
		return (((Tooltip) other).getTitle().equalsIgnoreCase(title));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(title);
	}
}
