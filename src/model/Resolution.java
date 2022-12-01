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
	
	private Dimension screenSize;

	private List<Integer[]> regions;
	private boolean match = false;
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
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2320.0 / 2560.0)), (int) (screenSize.getHeight() * (1295.0 / 1440.0)),
				(int) (screenSize.getWidth() * ((2320.0 + 60) / 2560.0)), (int) (screenSize.getHeight() * ((1295.0 + 37) / 1440.0))});

		//active weapon skill
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2300.0 / 2560.0)), (int) (screenSize.getHeight() * (1269.0 / 1440.0)),
				(int) (screenSize.getWidth() * (2400.0 / 2560.0)), (int) (screenSize.getHeight() * (1373.0 / 1440.0))});

		//active weapon
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2350.0 / 2560.0)), (int) (screenSize.getHeight() * (1100.0 / 1440.0)),
				(int) (screenSize.getWidth() * (2502.0 / 2560.0)), (int) (screenSize.getHeight() * (1252.0 / 1440.0))});

		//weapon 1
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2310.0 / 2560.0)), (int) (screenSize.getHeight() * (1217.0 / 1440.0)),
				(int) (screenSize.getWidth() * (2325.0 / 2560.0)), (int) (screenSize.getHeight() * (1231.0 / 1440.0))});
		//weapon 2
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2474.0 / 2560.0)), (int) (screenSize.getHeight() * (1370.0 / 1440.0)),
				(int) (screenSize.getWidth() * (2496.0 / 2560.0)), (int) (screenSize.getHeight() * (1387.0 / 1440.0))});

		//queue pop
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (1390.0 / 2560.0)), (int) (screenSize.getHeight() * (970.0 / 1440.0)),
				(int) (screenSize.getWidth() * (1551.0 / 2560.0)), (int) (screenSize.getHeight() * (1010.0 / 1440.0))});

		//dodge meters 1, 2, and 3, 52x8
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2395.0 / 2560.0)), (int) (screenSize.getHeight() * (944.0 / 1440.0)),
				(int) (screenSize.getWidth() * ((2395.0 + 52) / 2560.0)), (int) (screenSize.getHeight() * ((944.0 + 8) / 1440.0))});
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2446.0 / 2560.0)), (int) (screenSize.getHeight() * (944.0 / 1440.0)),
				(int) (screenSize.getWidth() * ((2446.0 + 52) / 2560.0)), (int) (screenSize.getHeight() * ((944.0 + 8) / 1440.0))});
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2497.0 / 2560.0)), (int) (screenSize.getHeight() * (944.0 / 1440.0)),
				(int) (screenSize.getWidth() * ((2497.0 + 52) / 2560.0)), (int) (screenSize.getHeight() * ((944.0 + 8) / 1440.0))});

		//discharge meter
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (2228.0 / 2560.0)), (int) (screenSize.getHeight() * (1133.0 / 1440.0)),
				(int) (screenSize.getWidth() * ((2228.0 + 120) / 2560.0)), (int) (screenSize.getHeight() * ((1133 + 120) / 1440.0))});

		//hp meter
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * (1072.0 / 2560.0)), (int) (screenSize.getHeight() * (1371.0 / 1440.0)),
				(int) (screenSize.getWidth() * ((1072.0 + 441) / 2560.0)), (int) (screenSize.getHeight() * ((1371.0 + 8) / 1440.0))});

		double defaultProportion = (double)DEFAULT_WIDTH / (double)DEFAULT_HEIGHT;
		double currentProportion = screenSize.getWidth() / screenSize.getHeight();
		boolean proportionMatch = currentProportion == defaultProportion;
		MainDriver.log("Resolution Proportion: " + currentProportion + "; " + proportionMatch);
		if (screenSize.getWidth() != DEFAULT_WIDTH || screenSize.getHeight() != DEFAULT_HEIGHT) {
			double scale = screenSize.getHeight() / (double)DEFAULT_HEIGHT;
			int properWidth = (int)(scale * screenSize.getHeight() * defaultProportion);
			MainDriver.log("Mismatch detected. Scaling by a base of: " + scale + ". Expected Width: " + properWidth);
			int xOffset = 0;
			if (properWidth < (int) screenSize.getWidth()) { //wide-screen. there's no such thing as a tall-screen, right?
				xOffset += screenSize.getWidth() - properWidth;
				MainDriver.log("Widescreen detected. Offset: " + xOffset);
			}
			for (int i = 0; i < baseRegion.size(); i++) {
				Integer[] vals = baseRegion.get(i);
				for (int j = 0; j < vals.length; j++) {
					vals[j] = (int) ((double) vals[j] / scale);
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
	}

	/**
	 * Returns the extraPath. This is an additional path to a folder for Sikuli-match images with a different scale.
	 * @return A part of a folder path String.
	 */
	public String getExtraPath() {
		return extraPath;
	}
	
	/**
	 * Returns whether a valid configuration is found for the current resolution.
	 * If no resolution match is found, the regions will be scaled based on the 1920x1080 regions.
	 * @return
	 */
	public boolean hasMatch() {
		return match;
	}

	public Integer[] getRegion(int index) {
		return regions.get(index);
	}

	public List<Integer[]> getRegions() {
		return regions;
	}
}
