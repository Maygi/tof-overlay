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
	
	/**
	 * The indexes of certain screen areas.
	 */
	public static final int SKILL_COOLDOWN = 0;
	public static final int ACTIVE_COOLDOWN = 1;
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
	
	public Resolution(Dimension screenSize) {
		this.screenSize = screenSize;
		initialize();
	}
	
	public boolean initialized() {
		return initialized;
	}
	
	/**
	 * Derives a new set of regions based on a list of points.
	 * @param points
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<Integer[]> derive(List<Integer[]> points) {
		List<Integer[]> toReturn = new ArrayList<Integer[]>();
		for (int i = 0; i < points.size(); i++) {
			Integer[] point = points.get(i);
			Integer[] region = new Integer[4];
			region[0] = point[0];
			region[1] = point[1];
			region[2] = point[0] + sizes.get(i)[0];
			region[3] = point[1] + sizes.get(i)[1];
			toReturn.add(region);
		}
		return toReturn;
	}
	
	private List<Integer[]> baseRegion, sizes;
	
	private int getBossOffset() {
		if (koreanClient)
			return 17;
		return 0;
	}
	
	private boolean koreanClient = false;
	
	public void initialize() {
		//configure 1920x1080 sizes. region sizes should be similar
        Region clock = new Region(0, 0, 30, 30);
		Match m = clock.exists("images/sikuli/clock.png", 0.01);
		/*if (m != null && m.getScore() >= 0.9) {
			System.out.println("Game client is in focus.");
		} else {
			System.out.println("Game client not in focus. Not initializing.");
			return;
		}*/
		
		initialized = true;
		System.out.println("Region initialized.");
		//x, y, x2, y2
		baseRegion = new ArrayList<Integer[]>();
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() * 0.85), (int) (screenSize.getHeight() * 0.85),
				(int) (screenSize.getWidth()), (int) (screenSize.getHeight())});

		//active cooldown
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
		
		sizes = new ArrayList<Integer[]>();
		for (int i = 0; i < baseRegion.size(); i++) {
			Integer[] newSize = new Integer[2];
			newSize[0] = baseRegion.get(i)[2] - baseRegion.get(i)[0];
			newSize[1] = baseRegion.get(i)[3] - baseRegion.get(i)[1];
			sizes.add(newSize);
		}
		
		regions = new ArrayList<Integer[]>(); 
		match = true;
		regions = baseRegion;
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
}
