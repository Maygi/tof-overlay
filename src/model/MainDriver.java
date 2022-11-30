package model;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.sikuli.script.*;

import gui.OverlayFrame;
import gui.GraphFrame;
import sound.Sound;
import util.VersionCheck;

/**
 * The main driver of the class that uses Sikuli to add data to various collections.
 * @author May
 */
public class MainDriver {
	
	public static final String VERSION = "1.1";
	
	private static final int DEFAULT_WIDTH = 1920;
	private static final int DEFAULT_HEIGHT = 1080;
	
	/**
	 * The delay, in milliseconds, before the tracker will automatically restart (when entering combat)
	 * after a pause.
	 */
	private static final long RESET_THRESHOLD = 10000;

	public static long lastActivity = 0;
	public static boolean started = false;
	public static boolean active = false;
	public static boolean mute = false;
	private static final long DEFAULT_TIME = -5000000;
	private static long clickAttempt = 0;
	private static long pauseTime = DEFAULT_TIME;
	private static long startTime = DEFAULT_TIME;
	private static Dimension screenSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	private static Resolution resolution;

	/**
	 * The TrackPoint enum, which describes several constants for image or text recognition.
	 * Constructors without images instead look for text within a certain region.
	 * All x and y coordinates are based on a full screen, 1920x1080 monitor.
	 * It is scaled down or up appropriately in the getRegion local method.
	 * The coordinates are the values of the upper left / bottom right corners around the region to be viewed for a certain TrackPoint.
	 */
	public enum TrackPoint {
		CLAUDIA("Claudia", "Buff duration", "claudia.png", 15, Resolution.ACTIVE_SKILL, 0.99),
		SHIRO("Shiro", "Full Bloom timer and cooldown reset timer", "shiro.png", 45, Resolution.ACTIVE_SKILL, 0.99),
		NEMESIS("Nemesis", "Electrode gauge", "nemesis.png", 25, Resolution.ACTIVE_SKILL, 0.99),
		SAKI("Saki", "Estimated cooldown of Surge (starts on weapon swap), and skill reset counter", "saki.png", 30, Resolution.ACTIVE_SKILL, 0.99),
		TSUBASA("Tsubasa", "Buff timer (refreshes on dodge)", "tsubasa.png", 12, Resolution.ACTIVE_SKILL, 0.99),
		SAMIR("Samir", "", "samir.png", 45, Resolution.ACTIVE_SKILL, 0.99),
		ZERO("Zero", "Shield duration", "zero.png", 30, Resolution.ACTIVE_SKILL, 0.99),
		RUBY("Ruby", "Estimated detonation timer", "ruby.png", 30, Resolution.ACTIVE_SKILL, 0.991),
		MERYL("Meryl", "Shield timer and cooldown", "meryl.png", 45, Resolution.ACTIVE_SKILL, 0.99),

		//wip need new sprite
		COBALT("Cobalt", "Ionic scorch timer", "cobalt.png", 60, Resolution.ACTIVE_SKILL, 0.99),
		HUMA("Huma", "Sharp Axe timer", "huma.png", 25, Resolution.ACTIVE_SKILL, 0.99),
		COCO("Coco", "Healing Bee timer and A3 buff timer", "coco.png", 60, Resolution.ACTIVE_SKILL, 0.99),
		KING("King", "Flaming Scythe timer", "king.png", 45, Resolution.ACTIVE_SKILL, 0.99),
		CROW("Crow", "Discharge timer", "crow.png", 45, Resolution.ACTIVE_SKILL, 0.99),
		FRIGG("Frigg", "Ice Domain", "frigg.png", 45, Resolution.ACTIVE_SKILL, 0.992),
		LIN("Lin", "Moonlight Realm time and discharge count for A6", "lin.png", 30, Resolution.ACTIVE_SKILL, 0.99),


		WEAPON1("Weapon 1", "", Resolution.WEAPON_1),
		WEAPON2("Weapon 2", "", Resolution.WEAPON_2),
		WEAPON_CD("Current CD", "", Resolution.SKILL_COOLDOWN),

		DODGE1("Dodge 1", "dodge.png", Resolution.DODGE1, 0.99),
		DODGE2("Dodge 2", "dodge.png", Resolution.DODGE2, 0.99),
		DODGE3("Dodge 3", "dodge.png", Resolution.DODGE3, 0.99),
		DISCHARGE("Discharge", "discharge.png", Resolution.DISCHARGE, 0.99),
		SHIELD("Shield", "shield.png", Resolution.HP_BAR, 0.9),
		QUEUE_POP("Queue Pop", "", Resolution.QUEUE_POP);
		
		private String name, intro, image, secondaryImage = null, req = null;
		private double threshold;
		private int regionIndex;
		private int classIndex = -1;
		private int cd = 0;
		private TrackPoint dependent = null;
		private String text = null;
		private String lastRead = "";
		private int textCount = 0;

		private TrackPoint(String name, String intro, int regionIndex) {
			this.regionIndex = regionIndex;
			this.name = name;
			this.intro = intro;
			this.image = null;
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro, String image, int cd, int resolutionIndex, double threshold) {
			this.regionIndex = resolutionIndex;
			this.name = name;
			this.intro = intro;
			this.cd = cd;
			this.image = image;
			this.threshold = threshold;
		}
		private TrackPoint(String name, String image, int resolutionIndex, double threshold) {
			this.regionIndex = resolutionIndex;
			this.name = name;
			this.image = image;
			this.threshold = threshold;
		}
		public void reset() {
			lastRead = "";
			text = null;
		}
		public String getName() {
			return name;
		}
		public String getIntro() {
			return intro;
		}
		public int getClassIndex() {
			return classIndex;
		}
		public int getCooldown() {
			try {
				int advancement = WeaponConfig.getData().get(name).getAdvancement();
				if (name.equalsIgnoreCase("Ruby")) {
					if (advancement >= 6)
						return 16;
					if (advancement >= 1)
						return 24;
				}
			} catch (Exception e) {

			}
			return cd;
		}
		public TrackPoint getDependent() {
			return dependent;
		}
		public String getLastRead() {
			return lastRead;
		}
		public String getInitialText() {
			return text;
		}
		public boolean isWeapon() {
			return regionIndex == (Resolution.ACTIVE_SKILL);
		}

		/**
		 * Attempts to initialize with the text. Must parse the same text 5 times in a row to set.
		 * @param text
		 */
		public void initialize(String text) {
			if (this.text == null && text.length() == 1) {
				this.text = text;
				log("Initialized " + name + " to " + text);
			}
		}

		public void read(String text) {
			if (text.length() > 0)
				lastRead = text;
		}
		
		/**
		 * Returns the image for Sikuli recognition.
		 * @return An image - usually a very small sliver - for Sikuli's image recognition.
		 */
		public String getImage() {
			if (image == null)
				return null;
			StringBuilder sb = new StringBuilder();
			sb.append("images/sikuli/");
			sb.append(image);
			return sb.toString();
		}
		public String getSecondaryImage() {
			if (secondaryImage == null)
				return null;
			StringBuilder sb = new StringBuilder();
			sb.append("images/sikuli/");
			sb.append(secondaryImage);
			return sb.toString();
		}
		public String getReq() {
			if (req == null)
				return null;
			StringBuilder sb = new StringBuilder();
			sb.append("images/sikuli/");
			sb.append(req);
			return sb.toString();
		}
		
		/**
		 * Returns the image for the table UI.
		 * @return An image for the table UI.
		 */
		public String getIcon() {
			StringBuilder sb = new StringBuilder();
			sb.append("images/tableicons/");
			sb.append(image);
			return sb.toString();
		}
		public boolean usesScreen() {
			return regionIndex == -1;
		}
		public int getRegionIndex() {
			return regionIndex;
		}
		public int[] getRegion() {
			if (regionIndex == -1) {
				return null;
			}
			Integer[] region = resolution.getRegion(regionIndex);
			int[] toReturn = new int[4];
			for (int i = 0; i < region.length; i++) {
				if (resolution.hasMatch())
					toReturn[i] = region[i];
				else
					toReturn[i] = (int)(((double)region[i] / (i % 2 == 0 ? DEFAULT_WIDTH : DEFAULT_HEIGHT)) 
					* (i % 2 == 0 ? screenSize.getWidth() : screenSize.getHeight()));
			}
			return toReturn;
		}
		public double getThreshold() {
			return threshold;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static TrackPoint getOrigin(TrackPoint tp) {
		return tp;
	}

	private static OverlayFrame overlay = new OverlayFrame();
	private static GraphFrame report = new GraphFrame();
    
    public static final Color MAIN_COLOR = new Color(255, 209, 220);
	public static Properties props;
    
    /**
     * Loads various properties from the property file.
     */
    private static void loadProps() {
        try {
        	boolean isMute = Boolean.parseBoolean(MainDriver.props.getProperty("mute"));
        	mute = isMute;
        } catch (Exception e) {
        	System.err.println("Unable to load properties on MainDriver.");
        }
    }
	
    public static void main(String[] args) throws FileNotFoundException {
		WeaponConfig.getData();
		ImagePath.add(System.getProperty("user.dir"));
    	props = new Properties();
    	try {
    		props.load(new FileReader("config.properties"));
    		loadProps();
		} catch (FileNotFoundException e) {
			log("Preferences not found - creating.");
			FileOutputStream file;
			try {
				file = new FileOutputStream("config.properties");
				props.store(file, null);
			} catch (FileNotFoundException e1) {
				log("Error in loading properties.");
				e1.printStackTrace();
			} catch (IOException e1) {
				log("Error in loading properties.");
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	setLookAndFeel();
            	overlay.start();
            }
        });
    	run();
    }
    
    public static void saveProps() {
		FileOutputStream file;
		try {
			file = new FileOutputStream("config.properties");
			props.store(file, null);
		} catch (FileNotFoundException e) {
			System.err.println("Error in saving properties.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error in saving properties.");
			e.printStackTrace();
		}
    	
    }
    
    public static void saveWindowPosition(int x, int y) {
    	props.setProperty("x", Integer.toString(x));
    	props.setProperty("y", Integer.toString(y));
    }
    
    /**
     * Set the look and feel for the GUI program.
     */
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (final UnsupportedLookAndFeelException e) {
			log("UnsupportedLookAndFeelException");
        } catch (final ClassNotFoundException e) {
			log("ClassNotFoundException");
        } catch (final InstantiationException e) {
			log("InstantiationException");
        } catch (final IllegalAccessException e) {
			log("IllegalAccessException");
        }
    }
    
    public static Map<TrackPoint, DataCollection> data;
    public static Map<Integer, List<TrackPoint>> trackPointByRegion;
    public static PrintStream logOutput;
    public static String liveVersion = VERSION;
    
    public static void run() throws FileNotFoundException {
		logOutput = new PrintStream(new File("log.txt"));
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 2; //check twice per second
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long lastFpsTime = 0;
        initializeData();
		start();
    	liveVersion = VersionCheck.getVersion();
		log("Live Version: " + liveVersion + "; Current Version: " + VERSION + "; needs update: " +
				VersionCheck.needsUpdate());
		long lastTick = 0;
		long currentTick;
		OCR.reset();

        while(true) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            lastFpsTime += updateLength;
            if(lastFpsTime >= 1000000000){
                lastFpsTime = 0;
            }
			if (System.currentTimeMillis() - lastTick >= 50) {
				lastTick = System.currentTimeMillis();
				tick();
			}
			overlay.refresh();
        }
    }
    
    /**
     * Adds TrackPoints to the data and initializes screen size.
     * Certain data is excluded from the GUI in the Overlay class.
     */
    public static void initializeData() {
    	screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	resolution = new Resolution(screenSize);
    	data = new LinkedHashMap<>();
		data.put(TrackPoint.WEAPON1, new HitMissCollection());
		data.put(TrackPoint.WEAPON2, new HitMissCollection());

		data.put(TrackPoint.SHIELD, new HitMissCollection());

		data.put(TrackPoint.CLAUDIA, new CountCollection(TrackPoint.CLAUDIA.getCooldown(), 25, "Claudia"));
		data.put(TrackPoint.NEMESIS, new CountCollection(TrackPoint.NEMESIS.getCooldown(), 31, "Nemesis"));
		data.put(TrackPoint.SHIRO, new CountCollection(TrackPoint.SHIRO.getCooldown(), 8, "Shiro"));
		data.put(TrackPoint.SAKI, new CountCollection(TrackPoint.SAKI.getCooldown(), 10, "Saki"));
		data.put(TrackPoint.TSUBASA, new CountCollection(TrackPoint.TSUBASA.getCooldown(), 15, "Tsubasa"));
		data.put(TrackPoint.SAMIR, new CountCollection(TrackPoint.SAMIR.getCooldown(), 11, "Samir"));
		data.put(TrackPoint.ZERO, new CountCollection(TrackPoint.ZERO.getCooldown(), 10, "Zero"));
		data.put(TrackPoint.RUBY, new CountCollection(TrackPoint.RUBY.getCooldown(), 6, "Ruby"));
		data.put(TrackPoint.MERYL, new CountCollection(TrackPoint.MERYL.getCooldown(), 10, "Meryl"));

		data.put(TrackPoint.COBALT, new CountCollection(TrackPoint.COBALT.getCooldown(), 10, "Cobalt"));
		//data.put(TrackPoint.HUMA, new CountCollection(TrackPoint.HUMA.getCooldown(), 15, "Huma"));
		data.put(TrackPoint.COCO, new CountCollection(TrackPoint.COCO.getCooldown(), 25, "Coco"));
		data.put(TrackPoint.FRIGG, new CountCollection(TrackPoint.FRIGG.getCooldown(), 25, "Frigg"));
		data.put(TrackPoint.KING, new CountCollection(TrackPoint.KING.getCooldown(), 10, "King"));
		data.put(TrackPoint.CROW, new CountCollection(TrackPoint.CROW.getCooldown(), 5, "Crow"));
		data.put(TrackPoint.LIN, new CountCollection(TrackPoint.LIN.getCooldown(), WeaponConfig.getData().get("Lin").getExtra(), "Lin"));

		data.put(TrackPoint.QUEUE_POP, new HitMissCollection(Sound.QUEUEPOP, 35));
		data.put(TrackPoint.DODGE1, new HitMissCollection());
		data.put(TrackPoint.DODGE2, new HitMissCollection());
		data.put(TrackPoint.DODGE3, new HitMissCollection());
		data.put(TrackPoint.DISCHARGE, new HitMissCollection());
		data.put(TrackPoint.WEAPON_CD, new HitMissCollection());

    	trackPointByRegion = new TreeMap<>();
    	for (TrackPoint tp : data.keySet()) {
    		int region = tp.getRegionIndex();
    		if (trackPointByRegion.get(region) == null) {
    			List<TrackPoint> list = new ArrayList<TrackPoint>();
    			list.add(tp);
    			trackPointByRegion.put(region, list);
    		} else {
    			List<TrackPoint> list = trackPointByRegion.get(region);
    			list.add(tp);
    			trackPointByRegion.put(region, list);
    		}
    	}
    }
    
    /**
     * Toggles the mute state.
     */
    public static void toggleMute() {
    	mute = !mute;
    	Sound.SELECT.play();
    	props.setProperty("mute", Boolean.toString(mute));
    }
    
    /**
     * Resets the combat data.
     */
    public static void reset() {
    	for (TrackPoint tp : data.keySet()) {
    		DataCollection dc = data.get(tp);
    		dc.reset();
    	}
    	startTime = DEFAULT_TIME;
    	pauseTime = DEFAULT_TIME;
    	active = true;
    	started = true;
    	Sound.RESET.play();
        overlay.resetTooltips();
		weaponsFound = 0;
		TrackPoint.WEAPON1.reset();
		TrackPoint.WEAPON2.reset();
		weaponMap = new HashMap<>();
		reverseWeaponMap = new HashMap<>();
		currentWeapon = 0;
		currentWeaponTp = null;
		lastActivity = 0;
		dischargeCount = 0;
		highestScore = 0;
		scoreAdjustment = 1;
    }
    
    /**
     * Pauses the collection of combat data.
     */
    public static void pause() {
    	if (active) {
    		pauseTime = System.currentTimeMillis();
	    	active = false;
	    	Sound.PAUSE.play();
    	}
    }
    
    /**
     * Pauses the collection of combat data fully.
     */
    public static void end() {
		pauseTime = DEFAULT_TIME;
    	active = false;
    	Sound.PAUSE.play();
    }
    /**
     * Starts the collection of combat data.
     */
    public static void start() {
    	if (!active) {
        	if (pauseTime != DEFAULT_TIME) {
        		long differential = pauseTime - startTime;
        		if (differential > RESET_THRESHOLD) {
        			differential = 0;
        			reset();
        		}
        		startTime = System.currentTimeMillis() - differential;
        		pauseTime = DEFAULT_TIME;
        	} else {
        		startTime = System.currentTimeMillis();
        	}
        	active = true;
        	started = true;
			log("Started.");
    	}
    }
    /**
     * Returns the ellasped time in seconds.
     * @return The ellasped time in seconds.
     */
    public static int getEllaspedTime() {
    	return (int)(Math.abs((active ? System.currentTimeMillis() : pauseTime) - startTime) / 1000);
    }
    
    /**
     * Returns the ellasped time in seconds after the given argument.
     * @param time The time to compare to.
     * @return An int representing the ellasped time in seconds after the argument.
     */
    public static int getEllaspedTime(long time) {
    	return (int)(Math.abs(time - startTime) / 1000);    	
    }
    
    /**
     * Returns a time as a formatted string.
     * @param time The time in seconds
     * @return A formatted time string, e.g. 0:46.
     */
    public static String timeToString(int time) {
    	int minutes = time / 60;
    	int seconds = time % 60;
    	StringBuilder sb = new StringBuilder();
    	sb.append(minutes);
    	sb.append(":");
    	sb.append(seconds >= 10 ? seconds : "0" + seconds);
    	return sb.toString();
    }

	private static Map<Integer, TrackPoint> weaponMap = new HashMap<>();
	private static Map<String, Integer> reverseWeaponMap = new HashMap<>();
	private static int currentWeapon = 0;
	private static TrackPoint currentWeaponTp = null;
	private static int weaponsFound = 0;
	private static int lastDodge = 3;
	private static boolean lastDischarge = false;
	private static int fullChargeTotal = 0;
	private static long fullChargeTime = 0;
	private static int fullChargeType = -1;
	private static boolean hasDischarge = false;
	private static boolean shielded = false;

	/**
	 * Sometimes, the parser will just... not be able to parse some images. This will "upscale" the highest score to .995.
	 */
	private static double highestScore = 0;
	private static double scoreAdjustment = 1;

    public static void tick() {
    	if (!resolution.initialized()) {
    		resolution.initialize();
    		return;
    	}
        Screen s = new Screen();
		int dodges = 0;
		shielded = false;

		boolean weaponObscured = false;
        Map<Integer, Region> regionMap = new TreeMap<Integer, Region>();
        for (Integer regionId : trackPointByRegion.keySet()) {
        	List<TrackPoint> list = trackPointByRegion.get(regionId);
			int[] region = list.get(0).getRegion();
			if (region != null) {
		        Region r = new Region(region[0], region[1], region[2] - region[0], region[3] - region[1]);
		        regionMap.put(regionId, r);
			}
        }
		boolean found = false;

    	for (TrackPoint tp : data.keySet()) {
    		DataCollection dc = data.get(tp);
    		String image = tp.getImage();
    		Match m;

    		if (dc instanceof TimeCollection && active) {
    			((TimeCollection) dc).addData(System.currentTimeMillis());
    			continue;
    		}
    		if (image != null && (dc instanceof HitMissCollection || dc instanceof CountCollection) && !weaponObscured) {
				if (found && tp.isWeapon())
					continue;
    			if (!active)
    				continue;
				boolean weaponExists = false;
				if (currentWeaponTp != null && weaponsFound == 3 && tp.isWeapon()) {
					if (!reverseWeaponMap.containsKey(tp.getName())) {
						continue;
					}
				}
	    		boolean hit;
	    		if (tp.usesScreen()) {
	    			m = s.exists(image, 0.01);
	    		} else {
	    			//int[] region = tp.getRegion();
	    	        Region r = regionMap.get(tp.getRegionIndex());
					//System.out.println("Looking in region: " + r.toString());
					//System.out.println("ATTEMPtING TO LOOK FOR: " +image);
	    			m = r.exists(image, 0.01);
					/*if (m != null)
						System.out.println(tp.getName()+": "+m.getScore());*/
		    		if (m == null && tp.getSecondaryImage() != null) {
		    			m = r.exists(tp.getSecondaryImage(), 0.01);
		    		}
	    		}
				hit = m != null && m.getScore() * scoreAdjustment >= tp.getThreshold();
				if (hit && tp.isWeapon()) {
					if (currentWeapon != 0 && !weaponMap.containsKey(currentWeapon) && !reverseWeaponMap.containsKey(tp.getName())) {
						weaponMap.put(currentWeapon, tp);
						reverseWeaponMap.put(tp.getName(), currentWeapon);
						weaponsFound++;
						log("Looks like " + tp.getName() + " is weapon #" + currentWeapon);
						currentWeaponTp = tp;
						found = true;
					}
				}
				if (tp.isWeapon() && weaponsFound < 3) {
					if (scoreAdjustment != 1)
						logOnly(tp.getName() + ": " + (m != null ? m.getScore() : "no match") + "; " + hit + " (Adjusted: " +
								(m != null ? m.getScore() * scoreAdjustment : "no match") + ")");
					else
						logOnly(tp.getName() + ": " + (m != null ? m.getScore() : "no match") + "; " + hit);
					if (weaponsFound == 0 && m.getScore() > highestScore)
						highestScore = m.getScore();
				}
				if (currentWeaponTp != null && currentWeaponTp.getName().equals(tp.getName()) && tp.isWeapon()) {
					if (scoreAdjustment != 1)
						log(tp.getName() + ": " + (m != null ? m.getScore() : "no match") + "; " + hit + " (Adjusted: " +
								(m != null ? m.getScore() * scoreAdjustment : "no match") + ")");
					else
						log(tp.getName() + ": " + (m != null ? m.getScore() : "no match") + "; " + hit);
				}
				if (tp.getName().contains("Dodge") && hit) {
					dodges++;
				}
				if (tp.getName().contains("Shield") && hit) {
					shielded = true;
				}
				if (tp.getName().contains("Discharge")) {
					//System.out.println(tp.getName()+": "+(m != null ? m.getScore() : "no match") +"; "+hit +"; "+lastDischarge);
					if (lastDischarge && !hit && m != null && m.getScore() * scoreAdjustment <= 0.985) {
						handleFullCharge();
					}
					if (!hit && (m == null || m.getScore() * scoreAdjustment <= 0.985))
						lastDischarge = false;
					else if (hit) {
						lastDischarge = true;
						hasDischarge = true;
					}
				}
				if (tp.getReq() != null) {
	    	        Region r = regionMap.get(tp.getRegionIndex());
					m = r.exists(tp.getReq(), 0.01);
					if (m == null || m.getScore() * scoreAdjustment < 0.999)
						hit = false;
				}
				if (dc instanceof HitMissCollection) {
					if (((HitMissCollection)dc).getTrackPoint() != null) {
						((DeltaCollection)data.get(((HitMissCollection)dc).getTrackPoint())).handleHit(true);
					}
					((HitMissCollection) dc).handleHit(hit);
				}
				if (dc instanceof CountCollection) {
					try {
						if (tp.getDependent() == null) { //cooldown collection
							if (weaponMap.get(currentWeapon).getName().equals(tp.getName())) {
								if (!dc.isActive())
									dc.setActive(true);
								//System.out.println("We got a hit on " + currentWeaponTp.getName());
								if (!hit && m.getScore() * scoreAdjustment <= 0.97) {
									((CountCollection) dc).handleHit(true, true);
									lastActivity = System.currentTimeMillis();
								} else
									((CountCollection) dc).handleHit(false, true);
							} else { //if no weapon match, we don't count
								((CountCollection) dc).handleHit(false);
							}
						} else
							((CountCollection) dc).handleHit(hit);
					} catch (Exception e) {
						//waiting...
					}
				}
    		} else if (tp.getRegion() != null) { //look for text
    			int[] region = tp.getRegion();
    	        Region r = new Region(region[0], region[1], region[2] - region[0], region[3] - region[1]);
				if (tp.getName().equalsIgnoreCase("Current CD"))
					OCR.globalOptions().variable("tessedit_char_whitelist", "0123456789");
				else
					OCR.globalOptions().variable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    	        String[] text = r.text().split("\n");
				if (text.length == 0)
					continue;
				if (tp.getName().contains("Weapon")) {
					for (String part : text) {
						tp.initialize(text[0]);
						tp.read(text[0]);
						if (part.length() > 0)
							break;
					}
				}
				if (tp.getName().equalsIgnoreCase("Weapon 2")) {
					if (determineWeapon() == 0) {
						weaponObscured = true;
					} else {
						weaponObscured = false;
					}
					//System.out.println("You are on weapon set #" + determineWeapon());
				}
				if (tp.getName().equalsIgnoreCase("Queue Pop")) {
					/*if (text[0].length() > 0)
					System.out.println(text[0]);*/
					if (text[0].equals("Approve")) {
						((HitMissCollection) dc).handleHit(true);
					}
				}
				if (tp.getName().equalsIgnoreCase("Current CD") && currentWeaponTp != null) {
					int cd = (int)(((CountCollection)data.get(currentWeaponTp)).getCooldown() / 1000.0);

					int advancement = WeaponConfig.getData().get(currentWeaponTp.getName()).getAdvancement();
						if (currentWeaponTp.getName().equals("Samir") && advancement >= 6 &&  cd > 0 && text[0].length() > 0) {
							System.out.println("Parse CD: " + text[0] + "; Real: " + cd);
							try {
								int proposedCd = Integer.parseInt(text[0]);
								if (proposedCd < cd && cd - proposedCd <= 8 && cd - proposedCd > 0) {
									System.out.println("Advancing CD by " + (cd - proposedCd));
									((CountCollection)data.get(currentWeaponTp)).advanceCooldown(cd - proposedCd);
								}
							} catch (Exception e) {
								continue;
							}
						}
				}
    		}
    	}
		if (weaponsFound == 0) { //reset the config
			log("Resetting the weapon config.");
			TrackPoint.WEAPON1.reset();
			TrackPoint.WEAPON2.reset();
			if (!weaponObscured && highestScore > 0) {
				scoreAdjustment = .995 / highestScore;
				log("Highest score was " + highestScore + "; setting adjustment to " + scoreAdjustment);
			}
		}
		if (lastDodge > dodges) {
			handleDodge();
		}
		lastDodge = dodges;
		if (reverseWeaponMap.containsKey("Cobalt") && WeaponConfig.getData().get("Cobalt").getAdvancement() >= 6) {
			((CountCollection)(data.get(weaponMap.get(reverseWeaponMap.get("Cobalt"))))).handleCobaltA6(false);
		}
    }

	public static boolean isScoreAdjusted() {
		return scoreAdjustment != 1;
	}

	public static void log(String s) {
		logOutput.println(s);
		System.out.println(s);
	}

	public static void logOnly(String s) {
		logOutput.println(s);
	}

	public static long getFullChargeTime() {
		return fullChargeTime;
	}

	public static int getFullChargeType() {
		return fullChargeType;
	}
	public static int getFullChargeTotal() {
		return fullChargeTotal;
	}
	public static boolean isShielded() {
		return shielded;
	}

	public static void handleFullCharge() {
		if (currentWeapon == -1 || weaponMap.get(currentWeapon) == null)
			return;
		String name = weaponMap.get(currentWeapon).getName();
		fullChargeTime = System.currentTimeMillis();
		fullChargeType = WeaponConfig.getData().get(name).getElement();
		int advancement = WeaponConfig.getData().get(name).getAdvancement();
		log("You've hit full weapon charge and applied an effect of type: " + fullChargeType + " (t=" + fullChargeTime + ").");
		switch (fullChargeType) {
			case WeaponData.ELEMENT_ABER:
				fullChargeTotal = 5;
				break;
			case WeaponData.ELEMENT_ICE:
				fullChargeTotal = 6;
				break;
			case WeaponData.ELEMENT_VOLT:
				fullChargeTotal = 6;
				if (name.equals("Samir") && advancement >= 3) {
					fullChargeTotal = 11;
				}
				break;
			case WeaponData.ELEMENT_FIRE:
				fullChargeTotal = 8;
				if (reverseWeaponMap.containsKey("Lin")) {
					fullChargeTotal = 12;
				}
				break;
			case WeaponData.ELEMENT_PHYS:
				fullChargeTotal = 7;
				if (reverseWeaponMap.containsKey("Shiro") && WeaponConfig.getData().get("Shiro").getAdvancement() >= 5) {
					fullChargeTotal = 14;
				}
				break;
		}

		/*if (name.equals("Crow") || name.equals("Samir")) {
			((CountCollection)(data.get(weaponMap.get(currentWeapon)))).updateSecondary();
		}*/
	}

	private static int dischargeCount = 0;

	public static void handleDischarge() {
		boolean update = false;
		boolean updateExtra = false;
		int delay = 0;
		int advancement = WeaponConfig.getData().get(weaponMap.get(currentWeapon).getName()).getAdvancement();
		log("You've unleashed a Discharge using A" + advancement + " " + weaponMap.get(currentWeapon).getName());
		dischargeCount++;
		if (reverseWeaponMap.containsKey("Lin")) {
			if (dischargeCount % 3 == 0) {
				log("Adding additional Lin cast");
				((CountCollection)(data.get(weaponMap.get(reverseWeaponMap.get("Lin"))))).setAdditionalCasts(1);
			}
		}
		switch(weaponMap.get(currentWeapon).getName()) {
			case "Ruby": //discharge procs a detonation
			case "King":
				update = true;
				delay = 1000;
				break;
			case "Coco": //discharge activates buff
				if (advancement >= 3) {
					updateExtra = true;
					delay = 1000;
				}
				break;
			case "Claudia": //discharge refreshes buff
				if (advancement >= 1) {
					update = true;
					delay = 500;
				}
				break;
			case "Nemesis":
				update = true;
				delay = 1500;
				break;
			case "Crow":
				update = true;
				delay = 200;
				break;
		}
		if (updateExtra) {
			new java.util.Timer().schedule(
					new java.util.TimerTask() {
						@Override
						public void run() {
							((CountCollection)(data.get(weaponMap.get(currentWeapon)))).updateExtra(7);
							cancel();
						}
					},
					delay
			);
		}
		if (update) {
			new java.util.Timer().schedule(
					new java.util.TimerTask() {
						@Override
						public void run() {
							((CountCollection)(data.get(weaponMap.get(currentWeapon)))).updateSecondary();
							cancel();
						}
					},
					delay
			);
		}
	}

	public static void handleDodge() {
		if (currentWeaponTp == null)
			return;
		String name = null;
		int advancement = 0;
		try {
			name = weaponMap.get(currentWeapon).getName();
			log("You dodged on " + name + ". Now you have: " + lastDodge + " dodges left");
			advancement = WeaponConfig.getData().get(name).getAdvancement();
		} catch (Exception e) { //not initialized
			e.printStackTrace();
			return;
		}
		if (name.equals("Coco") && advancement >= 1 && (System.currentTimeMillis() - ((CountCollection)data.get(currentWeaponTp)).getLastActive()) >= 25000) { //summon bee
			((CountCollection)(data.get(currentWeaponTp))).updateSecondary();
		}
		if (name.equals("Tsubasa") && advancement >= 1) {
			new java.util.Timer().schedule(
					new java.util.TimerTask() {
						@Override
						public void run() {
							((CountCollection)(data.get(weaponMap.get(reverseWeaponMap.get("Tsubasa"))))).updateSecondary();
							cancel();
						}
					},
					1170
			);
		}

		if (reverseWeaponMap.containsKey("Cobalt")) {
			if (WeaponConfig.getData().get("Cobalt").getAdvancement() >= 6) {
				new java.util.Timer().schedule(
						new java.util.TimerTask() {
							@Override
							public void run() {
								((CountCollection)(data.get(weaponMap.get(reverseWeaponMap.get("Cobalt"))))).handleCobaltA6(true);
							}
						},
						500
				);
			}
			double chargeDebuffTime = (System.currentTimeMillis() - fullChargeTime) / 1000.0;
			boolean procCondition = WeaponConfig.getData().get("Cobalt").getAdvancement() >= 3 && name.equals("Cobalt");
			boolean ionicBurning = ((CountCollection)data.get(currentWeaponTp)).getLastActive() <= 10000 && ((CountCollection)data.get(currentWeaponTp)).getLastActive() > 0;
			if ((fullChargeType == WeaponData.ELEMENT_FIRE && chargeDebuffTime < fullChargeTotal
				&& procCondition) || (WeaponConfig.getData().get("Cobalt").getAdvancement() >= 5 && ionicBurning)) { //if enemy is burning
				new java.util.Timer().schedule(
						new java.util.TimerTask() {
							@Override
							public void run() {
								((CountCollection)(data.get(weaponMap.get(reverseWeaponMap.get("Cobalt"))))).updateSecondary();
								cancel();
							}
						},
						500
				);

			}
		}
	}

	public static void setClickAttempt() {
		clickAttempt = System.currentTimeMillis();
	}

	public static boolean recentlyClicked() {
		return System.currentTimeMillis() - clickAttempt < 5000;
	}

	public static int getDischargeCount() {
		return dischargeCount;
	}

	public static void shiroCheck() {
		if (weaponMap.get(currentWeapon).getName().equals("Shiro") &&
				WeaponConfig.getData().get("Shiro").getAdvancement() >= 3 &&
				System.currentTimeMillis() - ((CountCollection)(data.get(weaponMap.get(currentWeapon)))).getLastExtraActive() >= 30000) {
			((CountCollection)(data.get(weaponMap.get(currentWeapon)))).updateExtra(30);
			log("Attempt to shiro reset");
			resetAllSkills();
		}
	}
	public static boolean sakiCheck() {
		if (reverseWeaponMap.containsKey("Saki") &&
				WeaponConfig.getData().get("Saki").getAdvancement() >= 1) {
			log("Attempting to Saki reset");
			resetAllSkills();
			return true;
		}
		return false;
	}

	public static void resetAllSkills() {
		for (TrackPoint tp : data.keySet()) {
			DataCollection dc = data.get(tp);
			if (reverseWeaponMap.containsKey(tp.getName()))
				((CountCollection) dc).resetCooldown();
		}
		overlay.repaint();
	}

	public static int determineWeapon() {
		Map<String, Boolean> found = new HashMap<>();
		String weaponA = TrackPoint.WEAPON1.getInitialText();
		String weaponB = TrackPoint.WEAPON2.getInitialText();
		found.put(weaponA, false);
		found.put(weaponB, false);
		found.put(TrackPoint.WEAPON1.getLastRead(), true);
		found.put(TrackPoint.WEAPON2.getLastRead(), true);
		if (weaponA == null || weaponB == null || TrackPoint.WEAPON1.getLastRead().length() == 0 || TrackPoint.WEAPON2.getLastRead().length() == 0 ||
				(!found.get(weaponA) && !found.get(weaponB)) || weaponA.contains(weaponB)) {
			/*System.out.println("UNKNOWN WEAPON! ");
			System.out.println("weaponA " + weaponA + "; weaponB: " + weaponB + "; foundA: "
				+ TrackPoint.WEAPON1.getLastRead() + "; foundB: " + TrackPoint.WEAPON2.getLastRead());*/
			return 0; //unknown weapon
		}
		int toReturn = 0;
		if (found.get(weaponA) && found.get(weaponB)) //found 1 and 2
			toReturn = 3; //same as initial set
		else if (found.get(weaponA)) //found 1 and ?
			toReturn = 2;
		else
			toReturn = 1;
		if (toReturn != currentWeapon) {
			log("WEAPON: " + toReturn + "; weaponA " + weaponA + "; weaponB: " + weaponB + "; foundA: "
					+ TrackPoint.WEAPON1.getLastRead() + "; foundB: " + TrackPoint.WEAPON2.getLastRead());
			currentWeapon = toReturn;
			try {
				log("You've swapped to " + weaponMap.get(currentWeapon).getName());
				currentWeaponTp = weaponMap.get(currentWeapon);
				if (hasDischarge) {
					handleDischarge();
					hasDischarge = false;
				}
			} catch (Exception e) {
				currentWeaponTp = null;
				e.printStackTrace();
			}

		}
		return toReturn;
	}
}