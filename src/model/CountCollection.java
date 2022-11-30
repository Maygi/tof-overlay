package model;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * A DataCollection that counts up every time a certain image is found.
 * There is a cooldown between how often it can count.
 * @author May
 */
public class CountCollection extends DataCollection {
	private int hits;
	private int countDelay;
	private long lastCount = 0;

	/**
	 * Not to be confused with the "weapon being active", this is used to track when the last time the secondary
	 * effect happened.
	 */
	private long lastActive = 0;
	private List<Long> lastActiveList;

	private long lastInitialActive = 0;
	private boolean activeLastTick = false;
	private int lastSawDelay = 0;

	/**
	 * A timer for the secondary bar, used by certain weapons.
	 */
	private long lastExtraActive = 0;
	private int extraDuration = 0;

	private int hitProtection = 1;
	private String name;

	private boolean lastHit = false;

	private static int totalCounts = 0;

	private boolean currentlyActive = false;

	private int additionalCasts = 0;
	
	/**
	 * Standard constructor for a CountCollection.
	 */
	public CountCollection(int delay) {
		super();
		lastActiveList = new LinkedList<>();
		hits = 0;
		countDelay = delay;
		name = "";
		lastSawDelay = 0;
	}
	/**
	 * Standard constructor for a CountCollection.
	 */
	public CountCollection(int delay, String name) {
		super();
		lastActiveList = new LinkedList<>();
		hits = 0;
		countDelay = delay;
		this.name = name;
		lastSawDelay = 0;
	}

	/**
	 * Standard constructor for a CountCollection.
	 */
	public CountCollection(int delay, int lastSawDelay, String name) {
		super();
		lastActiveList = new LinkedList<>();
		hits = 0;
		countDelay = delay;
		this.lastSawDelay = lastSawDelay;
		this.name = name;
	}

	public void updateExtra() {
		final long time = System.currentTimeMillis();
		lastExtraActive = time;
	}

	public void updateExtra(int duration) {
		final long time = System.currentTimeMillis();
		lastExtraActive = time;
		extraDuration = duration;
	}

	public void updateSecondary() {
		final long time = System.currentTimeMillis();
		lastActive = time;
		lastActiveList.add(time);
	}

	public void handleHit(boolean hit, boolean active) {
		if (!MainDriver.active)
			return;
		final long time = System.currentTimeMillis();
		if (active) { //if the weapon is currently found
			if (!activeLastTick) { //if the weapon was seen on the last hit
				MainDriver.log("Swap Diff: "+(System.currentTimeMillis() - lastInitialActive));
				if (name.equals("Meryl")) { //on-swap effects
					if (System.currentTimeMillis() - lastInitialActive > 20000) {
						lastInitialActive = System.currentTimeMillis();
						lastActive = lastInitialActive;
					}
				}
			}
			switch(name) {
				case "Saki":
					if (time - lastActive >= 10000) {
						lastActive = time;
					}
					currentlyActive = false; //always show
					break;
				/*case "Nemesis":
					lastActive = time;
					currentlyActive = true;
					break;*/
				case "Nemesis":
				case "Claudia":
				case "Tsubasa":
				case "Shiro":
				case "Zero":
				case "Meryl":
				case "Samir":
				case "Cobalt":
				case "Huma":
				case "Coco":
				case "King":
				case "Frigg":
				case "Crow":
				case "Lin":
					currentlyActive = false;
					break;
				case "Ruby":
					if (System.currentTimeMillis() - lastCount <= 15000 && time - lastActive >= 6000) {
						lastActive = time;
					}
					currentlyActive = false; //always show
					break;
				default:
					currentlyActive = true;
					break;
			}
			activeLastTick = true;
		} else {
			currentlyActive = false;
			activeLastTick = false;
		}
		if (hit) {
			if (hitProtection > 0) {
				hitProtection--;
				return;
			}
			hitProtection = 1;
			if (additionalCasts > 0 || Math.abs(time - lastCount) / 1000 > countDelay) {
				if (lastCount != -1)
					totalCounts++;
				if (additionalCasts > 0) {
					additionalCasts--;
				} else
					lastCount = time;
				hits++;

				int advancement = WeaponConfig.getData().get(name).getAdvancement();
				if (name.equals("Shiro") || name.equals("Zero")
						|| name.equals("Huma") || name.equals("Frigg") || name.equals("Lin"))
					updateSecondary();
				if (name.equals("Coco") && advancement >= 3) {
					lastExtraActive = time;
					extraDuration = 6;
				}
				if ((name.equals("Nemesis") || name.equals("Claudia")) && advancement >= 1) {
					updateSecondary();
					if (lastActiveList.size() > 2)
						lastActiveList.remove(0); //remove first
				}
				if (totalCounts % 5 == 0)
					MainDriver.sakiCheck();
			}
		}
		if (!hit) {
			hitProtection = 1;
			if (getCooldown() <= 44000 && getCooldown() >= 0 && active && lastHit) { //cooldown was reset!
				//lastCount = 0;
				MainDriver.shiroCheck();
			}
		}
		addData(new BigInteger(Integer.toString(hits)));
		lastHit = hit;
	}

	public int getElectrodeCount() {
		long time = System.currentTimeMillis();
		int count = 0;
		if (lastActiveList == null)
			return 0;
		for (Long l : lastActiveList) {
			if (time - l <= 31000) {
				count++;
			}
		}
		return count;
	}

	public void resetCooldown() {
		lastCount = -1;
		hitProtection = 1;
	}

	public int getCount() {
		return totalCounts;
	}

	public String getName() {
		return name;
	}
	
	public void handleHit(boolean hit) {
		handleHit(hit, false);
	}

	public void setAdditionalCasts(int num) {
		additionalCasts = num;
	}

	public int getAdditionalCasts() {
		return additionalCasts;
	}

	/**
	 * Returns the cooldown in milliseconds.
	 * @return
	 */
	public int getCooldown() {
		if (lastCount <= 0)
			return 0;
		final long time = System.currentTimeMillis();
		return (countDelay * 1000 - (int)(Math.abs(time - lastCount)));
	}

	public int getCountDelay() {
		return countDelay;
	}

	public int getLastSawDelay() {
		return lastSawDelay;
	}

	public boolean isCurrentlyActive() {
		return currentlyActive;
	}

	public int getExtraDuration() {
		return extraDuration;
	}

	public long getLastExtraActive() {
		return lastExtraActive;
	}

	/**
	 * Returns the active delay time in milliseconds.
	 * @return
	 */
	public int getLastActive() {
		if (lastSawDelay == 0)
			return 0;
		final long time = System.currentTimeMillis();
		return (int)(lastSawDelay * 1000 - (int)(Math.abs(time - lastActive)));
	}

	@Override
	public void reset() {
		super.reset();
		hits = 0;
	}
}
