package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import model.DataCollection;
import model.DeltaCollection;
import model.HitMissCollection;
import model.MainDriver;
import model.TimeCollection;
import model.MainDriver.TrackPoint;

public class Options extends AbstractLabel {
	
	private static final long serialVersionUID = -6526533159294553453L;

	private static final int START_X = 100;
	private static final int START_Y = 100;
	
	private static final int Y_SPACING = 40;
	
	protected Options(JFrame theFrame, double theXPerc, double theYPerc) {
		super(theFrame, theXPerc, theYPerc);
	}
    
    private static final TrackPoint[] ITEMS = {
    };

    
    private static final TrackPoint[] BUFFS = {
    };

    
    private static final TrackPoint[] DEBUFFS = {
    };

    private static final GuiButton[] BUTTONS = {
		GuiButton.PERSONAL, GuiButton.BUFFS, GuiButton.DEBUFFS, GuiButton.UP, GuiButton.DOWN
    };

    public void addTooltip(TrackPoint tp, int x, int y) {
    	Tooltip toAdd = new Tooltip(x, y, tp.getName(), tp.getIntro());
    	((OptionsFrame)myFrame).tooltipMappings.put(toAdd, false);
    }
    

    /**
     * Paints the graph.
     * @param theGraphics The graphics context to use for painting.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        
        int x = START_X;
        int y = START_Y;
        for (int i = 0; i < ITEMS.length; i++) {
            drawImage(theGraphics, ITEMS[i].getIcon(), x, y);
            addTooltip(ITEMS[i], x, y);
            y += Y_SPACING;
        }
    	
        ((OptionsFrame)myFrame).drawTooltips(theGraphics, this);
    }
}
