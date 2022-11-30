package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.MainDriver;

public enum GuiButton {
	PARTY_DPS(30, 160 + 24, "images/ui/button.png", "Party DPS"),
	BUFF_BREAKDOWN(30, 195 + 24, "images/ui/button.png", "Buff/Debuff rDPS"),
	PERSONAL(30, 400 + 24, "images/ui/button.png", "Personal"),
	BUFFS(30, 435 + 24, "images/ui/button.png", "Buffs"),
	DEBUFFS(30, 470 + 24, "images/ui/button.png", "Debuffs"),
	UP(30 + 170 - 20 + 800, 435 + 24, "images/ui/up_arrow.png"),
	DOWN(30 + 170 - 20 + 800, 435 + 24 + 19, "images/ui/down_arrow.png"),

	UPDATE(100, 12, "images/ui/update.png"),
	WARNING(100, 12, "images/ui/warning.png"),
	MUTE(125, 12, "images/ui/mute.png"),
	PAUSE(300, 12, "images/ui/pause.png"),
	RESET(150, 12, "images/ui/reset.png"),
	REPORT(475, 37, "images/ui/report.png"),
	MINIMIZE(450, 37, "images/ui/minimize.png"),
	CLOSE(175, 12, "images/ui/close.png");
	
	private static final int FONT_SIZE = 20;
	
	private boolean active = true;
	
	private int x, y;
	private String image, text;
	private GuiButton(int x, int y, String image) {
		this.x = x;
		this.y = y;
		this.image = image;
		this.text = "";
	}
	private GuiButton(int x, int y, String image, String text) {
		this.x = x;
		this.y = y;
		this.image = image;
		this.text = text;
	}
	public int[] getCoords() {
		return new int[]{x, y};
	}
	public String getText() {
		return text;
	}
	public String getImage() {
		if (image.contains("minimize") && OverlayFrame.compact)
			return "images/ui/maximize.png";
		return image;
	}
	public boolean isActive() {
		return active;
	}
	public int getWidth() {
		BufferedImage bimg;
		try {
			bimg = ImageIO.read(new File(image));
		} catch (IOException e) {
			return 0;
		}
		return bimg.getWidth();
	}
	public int getHeight() {
		BufferedImage bimg;
		try {
			bimg = ImageIO.read(new File(image));
		} catch (IOException e) {
			return 0;
		}
		return bimg.getHeight();    		
	}
	public void handleDraw(AbstractLabel label, Graphics theGraphics) {
    	int[] coords = getCoords();
    	if (OverlayFrame.compact) {
    		coords[1] += OverlayFrame.HEIGHT / 2 - OverlayFrame.COMPACT_HEIGHT / 2;
    	}
        label.drawImage(theGraphics, getImage(), coords[0],  coords[1]);
        Point p = MouseInfo.getPointerInfo().getLocation();
        active = true;
        if (getImage().contains("pause") && !MainDriver.active)
        	active = false;
        if (getImage().contains("mute") && MainDriver.mute)
        	active = false;
        if (getImage().contains("up_arrow")) {
        	if (!((Graph)label).hasPreviousPage())
        		active = false;
        }
        if (getImage().contains("down_arrow")) {
        	if (!((Graph)label).hasNextPage())
        		active = false;
        }
        if (getText().length() > 0) {
    		if (getText().contains("Party") || getText().contains("rDPS")) {
            	if (!((Graph)label).currentView.equalsIgnoreCase(getText())) {
            		active = false;
            	}
    		} else if (!((Graph)label).currentTab.equalsIgnoreCase(getText())) {
        		active = false;
        	}
        }
        if (p != null) {
        	Point diff = label.getLocationOnScreen();
        	p.translate((int)(-1 * diff.getX()), (int)(-1 * diff.getY()));
            if (p.getX() >= coords[0] && p.getX() <= coords[0] + getWidth() &&
            		p.getY() >= coords[1] && p.getY() <= coords[1] + getHeight()) {
            	if (getImage().contains("pause") && !MainDriver.active) {
            		//discrete logic where
            	} else {
            		label.drawRect(theGraphics, coords[0], coords[1], getWidth(), getHeight(), Color.WHITE, (float) 0.5);
            	}
            }
        }
        if (text.length() > 0)
        	label.drawNormalTextCentered(theGraphics, text, FONT_SIZE, coords[0] + getWidth() / 2, coords[1] + getHeight() * 2 / 3, 1, Color.WHITE, Color.BLACK);
    	if (!active) {
    		label.drawRect(theGraphics, coords[0], coords[1], getWidth(), getHeight(), Color.BLACK, (float) 0.5);
    	}
	}
}
