package model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sikuli.script.Match;
import org.sikuli.script.Region;

/**
 * A class for handling methods related to finding screen regions given different resolutions.
 * @author May
 *
 */
public class Resolution {

	public static final int DEFAULT_WIDTH = 2560;
	public static final int DEFAULT_HEIGHT = 1440;
	
	/**
	 * The indexes of certain screen areas.
	 */
	public static final int SKILL_COOLDOWN = 0;
	public static final int ACTIVE_SKILL = 1;
	public static final int ACTIVE_WEAPON = 2;
	public static final int WEAPON_1 = 3;
	public static final int WEAPON_2 = 4;
	public static final int QUEUE_POP = 5;
	public static final int DODGE1 = 6;
	public static final int DODGE2 = 7;
	public static final int DODGE3 = 8;
	public static final int DISCHARGE = 9;
	public static final int HP_BAR = 10;
	public static final int SKILL_CHARGES = 11;
	public static final int DISCHARGE_GLOW = 12;
	
	private Dimension screenSize;

	private List<Integer[]> regions;
	private boolean initialized = false;
	private String extraPath = "";
	
	public Resolution(Dimension screenSize) {
		MainDriver.log("Screen size is: " + screenSize.toString());
		this.screenSize = screenSize;
		initialize();
	}
	
	public boolean initialized() {
		return initialized;
	}
	
	private List<Integer[]> baseRegion;
	
	public void initialize() {
		//configure 2560x1440 region sizes.
		
		initialized = true;
		System.out.println("Region initialized.");
		//x, y, x2, y2
		baseRegion = new ArrayList<>();

		//active cooldown
		baseRegion.add(new Integer[]{2320, 1295,
				(2320 + 60), (1295 + 37)});

		//active weapon skill
		baseRegion.add(new Integer[]{2300, 1269,
				2400, 1373});

		//active weapon
		baseRegion.add(new Integer[]{2350, 1100,
				2502, 1252});

		//weapon 1
		baseRegion.add(new Integer[]{2310, 1217,
				2332, 1234});
		//weapon 2
		baseRegion.add(new Integer[]{2474, 1370,
				2496, 1387});

		//queue pop
		baseRegion.add(new Integer[]{1390, 970,
				1551, 1010});

		//dodge meters 1, 2, and 3, 52x8
		baseRegion.add(new Integer[]{2395, 944,
				(2395 + 52), (944 + 8)});
		baseRegion.add(new Integer[]{2446, 944,
				(2446 + 52), (944 + 8)});
		baseRegion.add(new Integer[]{2497, 944,
				(2497 + 52), (944 + 8)});

		//discharge meter
		baseRegion.add(new Integer[]{2228, 1133,
				(2228 + 120), (1133 + 120)});

		//hp meter
		baseRegion.add(new Integer[]{1072, 1371,
				(1072 + 441), (1371 + 8)});

		//skill extra usages
		baseRegion.add(new Integer[]{2385, 1273,
				(2385 + 30), (1273 + 91)});

		//true discharge meter
		baseRegion.add(new Integer[]{2216, 1126,
				(2216 + 128), (1126 + 133)});

		double defaultProportion = (double)DEFAULT_WIDTH / (double)DEFAULT_HEIGHT;
		double currentProportion = screenSize.getWidth() / screenSize.getHeight();
		boolean proportionMatch = currentProportion == defaultProportion;
		MainDriver.log("Resolution Proportion: " + currentProportion + "; " + proportionMatch);
		if (screenSize.getWidth() != DEFAULT_WIDTH || screenSize.getHeight() != DEFAULT_HEIGHT) {
			double scale = screenSize.getHeight() / (double)DEFAULT_HEIGHT;
			int properWidth = (int)(screenSize.getHeight() * defaultProportion);
			MainDriver.log("Mismatch detected. Scaling by a base of: " + scale + ". Expected Width: " + properWidth);
			int xOffset = 0;
			if (properWidth < (int) screenSize.getWidth()) { //wide-screen. there's no such thing as a tall-screen, right?
				xOffset = (int)(screenSize.getWidth() - properWidth);
				MainDriver.log("Widescreen detected. Offset: " + xOffset);
			}
			for (int i = 0; i < baseRegion.size(); i++) {
				Integer[] vals = baseRegion.get(i);
				for (int j = 0; j < vals.length; j++) {
					vals[j] = (int) ((double) vals[j] * scale);
					if (j % 2 == 0)
						vals[j] += xOffset;
				}
				baseRegion.set(i, vals);
				MainDriver.log("Updated Region #" + i + " to: " + Arrays.toString(vals));
			}
		}
		regions = baseRegion;


		if (screenSize.getHeight() == 1080) {
			extraPath = "1920x1080/";
		}
		if (screenSize.getHeight() == 2160) {
			extraPath = "3840x2160/";
		}
	}

	/**
	 * Returns the extraPath. This is an additional path to a folder for Sikuli-match images with a different scale.
	 * @return A part of a folder path String.
	 */
	public String getExtraPath() {
		return extraPath;
	}

	public Integer[] getRegion(int index) {
		return regions.get(index);
	}

	public List<Integer[]> getRegions() {
		return regions;
	}
}
