package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public abstract class AbstractFrame extends JFrame {
	
	protected Map<Tooltip, Boolean> tooltipMappings;
	protected Tooltip hoveredTooltip = null;

    /**
     * The font URL.
     */
    private static final String FONT_URL = "fonts/AYearWithoutRain.ttf";
    
    /**
     * The font size.
     */
    private static final int FONT_SIZE = 40;
    
    /**
     * The margin (in pixels) between an icon and the text.
     */
    private static final int MARGIN = 6;
    
    /**
     * The font.
     */
    private Font myFont;

	public AbstractFrame(String string) {
		super(string);
        initializeFont();
        tooltipMappings = new HashMap<Tooltip, Boolean>();
	}
    
    public void resetTooltips() {
        tooltipMappings = new HashMap<Tooltip, Boolean>();
    }
    
    public Tooltip getHoveredTooltip() {
    	return hoveredTooltip;
    }

	private static final long serialVersionUID = 4794611330448287305L;
	
    private void initializeFont() {
        try {
            myFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_URL));
            myFont = myFont.deriveFont(Font.BOLD, FONT_SIZE);
            final GraphicsEnvironment graphics =
                            GraphicsEnvironment.getLocalGraphicsEnvironment();
            graphics.registerFont(myFont);
            setFont(myFont);	
        } catch (final FontFormatException | IOException e) {
            myFont = new Font("TimesRoman", Font.PLAIN, FONT_SIZE);
        }
    }

	public void drawTooltips(final Graphics theGraphics, final AbstractLabel label) {
		final Graphics2D g2d = (Graphics2D) theGraphics;

		Tooltip hover = hoveredTooltip;
		if (hover != null) {
			String[] realLines = getLines(hover.getText(), g2d);
			int boxY = 64;
			int boxWidth = (int)getBounds().getWidth();
			label.drawRect(theGraphics, 0, hover.getCoords()[1], boxWidth,
					boxY, Color.BLACK, (float) 0.6);
			boolean titleSkip = false;
			if (hover.getTitle().length() > 0)
				label.drawNormalText(g2d, hover.getTitle(), Tooltip.TEXT_SIZE, 10, hover.getCoords()[1] + Tooltip.TEXT_SIZE, 1,
						Color.WHITE, AbstractLabel.SHADOW_COLOR.darker());
			else
				titleSkip = true;
			for (int i = 0; i < realLines.length; i++) {
				if (realLines[i] != null)
					label.drawNormalText(g2d, realLines[i], (int)(Tooltip.TEXT_SIZE * 0.9), 10,
							hover.getCoords()[1] + Tooltip.TEXT_SIZE * (i + 2) - (titleSkip ? (int)(Tooltip.TEXT_SIZE * 1) : 0), 1, Color.WHITE, AbstractLabel.SHADOW_COLOR.darker());
			}
		}
	}

	/**
	 * Floating tooltips that follow the mouse.
	 */
    public void drawTooltipsLegacy(final Graphics theGraphics, final AbstractLabel label) {
        final Graphics2D g2d = (Graphics2D) theGraphics;

        Tooltip hover = hoveredTooltip;
        if (hover != null) {
            Point p = MouseInfo.getPointerInfo().getLocation();
        	Point diff = getLocationOnScreen();
        	p.translate((int)(-1 * diff.getX()), (int)(-1 * diff.getY()));
        	//p.translate(20, 20);
        	//g2d.getFontMetrics().stringWidth(hover.getTitle())
            String[] realLines = getLines(hover.getText(), g2d);
            int boxY = Tooltip.TEXT_SIZE * (realLines.length + 1) + MARGIN * 3;
            int boxWidth = (int)Math.min((getMaxLineWidth(g2d, hover.getTitle(), realLines) * 1.4) + MARGIN * 2, getBounds().getWidth());
            int yOffset = 0;
            int xOffset = 0;
            if ((int)p.getY() + boxY > getBounds().getHeight()) {
            	yOffset = -1 * boxY;
            }
            if (p.getY() + yOffset + boxY > getBounds().getHeight()) {
            	int total = (int)(p.getY() + yOffset + boxY);
            	yOffset -= total - getBounds().getHeight();
            }
			System.out.println("X + Width: " + (p.getX() + boxWidth) + "; BoundWidth: " + getBounds().getWidth());
            if (p.getX() + boxWidth > getBounds().getWidth()) {
				xOffset += getBounds().getWidth() - (p.getX() + boxWidth);
            	//xOffset -= boxWidth;
            }
    		label.drawRect(theGraphics, (int)p.getX() + xOffset, (int)p.getY() + yOffset, (int)(getMaxLineWidth(g2d, hover.getTitle(), realLines) * 1.2) + MARGIN * 2,
    				boxY, Color.BLACK, (float) 0.7);
			boolean titleSkip = false;
			if (hover.getTitle().length() > 0)
				label.drawNormalText(g2d, hover.getTitle(), Tooltip.TEXT_SIZE, (int)p.getX() + MARGIN + xOffset, (int)p.getY() + MARGIN * 3 + yOffset, 1,
						Color.WHITE, AbstractLabel.SHADOW_COLOR.darker());
			else
				titleSkip = true;
            for (int i = 0; i < realLines.length; i++) {
            	if (realLines[i] != null)
	            	label.drawNormalText(g2d, realLines[i], (int)(Tooltip.TEXT_SIZE * 0.9), (int)p.getX() + MARGIN + xOffset,
	    	        		(int)p.getY() + MARGIN * 5 + Tooltip.TEXT_SIZE * (i + 1) + yOffset - (titleSkip ? Tooltip.TEXT_SIZE : 0), 1, Color.WHITE, AbstractLabel.SHADOW_COLOR.darker());
            }
        }
    }

    
    private int getMaxLineWidth(Graphics2D g2d, String title, String[] lines) {
    	int maxWidth = 0;
    	for (String s : lines) {
    		if (s == null)
    			continue;
    		int width = g2d.getFontMetrics().stringWidth(s);
    		if (width > maxWidth)
    			maxWidth = width;
    	}
    	return Math.max(maxWidth, g2d.getFontMetrics().stringWidth(title));
    }
    
    private String[] getLines(String text, Graphics2D g2d) {
    	final int WIDTH = 160;
    	int lines = (int)(Math.ceil((double)g2d.getFontMetrics().stringWidth(text) / (double)WIDTH));
    	String[] toReturn = new String[lines];
    	int word = 0;
    	String[] words = text.split(" ");
    	int index = 0;
    	while (word < words.length && text.length() > 0) {
    		StringBuilder newLine = new StringBuilder();
    		while (word < words.length && g2d.getFontMetrics().stringWidth(newLine.toString()) < WIDTH) {
    			if (words[word].contains("$")) {
    				word++;
    				break;
    			}
    			newLine.append(words[word]);
    			newLine.append(" ");
    			if (g2d.getFontMetrics().stringWidth(newLine.toString().substring(0, newLine.toString().length() - 1)) > WIDTH) { //carry-over
    				String oversize = newLine.toString().substring(0, newLine.toString().length() - 1);
    				newLine = new StringBuilder();
    				newLine.append(oversize.substring(0, oversize.lastIndexOf(" ")));
    				break;
    			} else {
    				word++;
    			}
    		}
    		//System.out.println(Arrays.toString(toReturn));
    		try {
    			toReturn[index] = newLine.toString();
    		} catch (Exception e) {
    			String[] temp = toReturn.clone();
    			toReturn = new String[toReturn.length + 1];
    			for (int i = 0; i < temp.length; i++) {
    				toReturn[i] = temp[i];
    			}
    			toReturn[index] = newLine.toString();
    		}
    		index++;
    	}
    	return toReturn;
    }


}
