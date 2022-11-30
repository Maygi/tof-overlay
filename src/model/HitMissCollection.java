package model;

import java.util.ArrayList;
import java.util.List;

import model.MainDriver.TrackPoint;
import sound.Sound;

/**
 * This is a DataCollection that counts total instances of hits (image is present) vs misses (image is not present).
 * @author May
 */
public class HitMissCollection extends DataCollection {
	private int hits, misses;
	private Sound sound;
	private int soundDelay;
	private long lastSound = 0;
	private long lastSoundTick = 0;
	protected List<Boolean> rawData;
	private boolean canPlaySound;
	
	/**
	 * The index of the last successful hit; -1 if none.
	 */
	private int lastHit = -1;
	 
	/**
	 * A list of integer arrays. These are 2-index arrays that contain start and end points (in terms of indices) of hits.
	 */
	private List<Integer[]> endPoints;
	
	/**
	 * A flag denoting whether this collection is counting values.
	 * Not to be confused with a CountCollection, which counts total occurances of an image with a cooldown.
	 * A counting collection, rather than simply counting hits or misses, counts a value with each iteration.
	 * The percentage returns the average count.
	 * This flag is automatically set if handleHit is called with an Integer instead of a boolean.
	 */
	private boolean countFlag;
	
	private TrackPoint tp = null;
	
	/**
	 * Standard constructor for a HitMissCollection.
	 */
	public HitMissCollection() {
		super();
		rawData = new ArrayList<Boolean>();
		endPoints = new ArrayList<Integer[]>();
		countFlag = false;
		hits = misses = 0;
		sound = null;
		soundDelay = 0;
		canPlaySound = true;
	}
	
	/**
	 * A constructor for a HitMissCollection that is tied to a damage amp TrackPoint.
	 * @param tp The TrackPoint for the associated damage amp collection.
	 */
	public HitMissCollection(TrackPoint tp) {
		super();
		rawData = new ArrayList<Boolean>();
		endPoints = new ArrayList<Integer[]>();
		countFlag = false;
		hits = misses = 0;
		sound = null;
		soundDelay = 0;
		canPlaySound = true;
		this.tp = tp;
	}
	
	/**
	 * Constructs a HitMissCollection with a sound trigger attached.
	 * @param sound The sound to be played upon a successful hit
	 * @param delay The minimum time (in seconds) that must ellapse before the sound is played again
	 */
	public HitMissCollection(Sound sound, int delay) {
		super();
		rawData = new ArrayList<Boolean>();
		endPoints = new ArrayList<Integer[]>();
		hits = misses = 0;
		canPlaySound = true;
		this.sound = sound;
		this.soundDelay = delay;
	}
	
	public TrackPoint getTrackPoint() {
		return tp;
	}
	
	/**
	 * Calculates a value to add to the data.
	 * @return If it is a hit/miss collection, return % as a whole integer.
	 * If it is a count collection, return the average count multiplied by 100, as to be divided later for precision.
	 */
	private int calculatePercentage() {
		if (hits + misses == 0)
			return 0;
		if (countFlag)
			return (int)(100.0 * (double)(hits) / data.size());
		return (int)(100.0 * (double)(hits) / (double)(hits + misses));
	}
	
	public void addRawData(boolean value) {
		if (value && lastHit == -1)
			lastHit = rawData.size();
		if (!value && lastHit != -1) {
			Integer[] endPoint = new Integer[] {lastHit, Math.max(lastHit, rawData.size() - 1)};
			endPoints.add(endPoint);
			lastHit = -1;
		}
		rawData.add(value);
	}
	
	public List<Boolean> getRawData() {
		return rawData;
	}

	public boolean getLastHit() {
		if (rawData.size() > 0)
			return rawData.get(rawData.size() - 1);
		return false;
	}
	
	public List<Integer[]> getEndpoints() {
		return endPoints;
	}
	
	
	public void handleHit(int hit) {
		if (!MainDriver.active)
			return;
		countFlag = true;
		if (hit > 0) {
			hits += hit;
			if (sound != null) {
				final long time = System.currentTimeMillis();
				if (Math.abs(time - lastSound) / 1000 > soundDelay) {
					sound.play();
					lastSound = time; 
				}
			}
		} else
			misses++;
		addData(calculatePercentage());
	}
	
	public void handleHit(boolean hit) {
		if (!MainDriver.active)
			return;
		if (hit) {
			final long time = System.currentTimeMillis();
			//System.out.println("Sound hit! " + (Math.abs(time - lastSound) / 1000) + "; " + soundDelay + "; " + canPlaySound + "; " + (sound != null));
			hits++;
			if (sound != null) {
				if (Math.abs(time - lastSound) / 1000 > soundDelay) {
					sound.play();
					lastSound = time; 
					canPlaySound = true;
					lastSoundTick = time;
					//System.out.println("Sound play!! " + sound.name());
				} else if (sound.name().contains("QUEUE")) {
					//System.out.println("Can tick? " + (Math.abs(time - lastSoundTick)));
					if (Math.abs(time - lastSoundTick) >= 1800) {
						Sound.QUEUETICK.play();
						lastSoundTick = time;
					}
				}
			}
		} else {
			misses++;
			canPlaySound = true;
		}
		addData(calculatePercentage());
		addRawData(hit);
	}

	@Override
	public void reset() {
		super.reset();
		hits = misses = 0;
		rawData = new ArrayList<Boolean>();
		endPoints = new ArrayList<Integer[]>();
	}
}
