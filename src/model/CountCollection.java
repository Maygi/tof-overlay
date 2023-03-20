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
	private int reverseHitProtection = 1;
	private String name, mainName;

	private boolean lastHit = false;

	private static int totalCounts = 0;

	private boolean currentlyActive = false;

	/**
	 * Extra casts for the skill that are independent of the main cooldown.
	 * E.G. Lin A6 charges, gained through every 3 Discharge casts.
	 */
	private int extraCasts = 0;

	/**
	 * Skill charges that are tied to the cooldown.
	 */
	private int skillCharges = 0;
	private int maxSkillCharges = 0;

	private boolean trueHit = false;
	
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
		this.mainName = name;
		lastSawDelay = 0;
	}

	/**
	 * Constructs a CountCollection with a secondary delay.
	 */
	public CountCollection(int delay, int lastSawDelay, String name) {
		super();
		lastActiveList = new LinkedList<>();
		hits = 0;
		countDelay = delay;
		this.lastSawDelay = lastSawDelay;
		this.name = name;
		this.mainName = name;
	}

	/**
	 * Constructs a CountCollection with a secondary delay and a main name.
	 */
	public CountCollection(int delay, int lastSawDelay, String name, String mainName) {
		super();
		lastActiveList = new LinkedList<>();
		hits = 0;
		countDelay = delay;
		this.lastSawDelay = lastSawDelay;
		this.name = name;
		this.mainName = mainName;
	}

	/**
	 * Handles a tick from Cobalt-B's A6 cooldown reduction effect.
	 * @param dodge Whether this is applied by a dodge attack. Dodge attacks do not have the conditional to check
	 *              for whether Ionic Burn is active.
	 */
	public void handleCobaltA6(boolean dodge) {
		final int INTERVAL = 1500;
		System.out.println("SECONDARY: " + getSecondary() + "; burn active: " + (System.currentTimeMillis() - lastActive <= 10000));
		if (System.currentTimeMillis() - lastExtraActive >= INTERVAL && (dodge || System.currentTimeMillis() - lastActive <= 10000)) {
			lastExtraActive = System.currentTimeMillis();
			advanceCooldown(4);
		}
	}

	public double getSecondary() {
		double lastVal = getLastActive() / 1000.0;
		double initialVal = getLastSawDelay();
		return initialVal - lastVal;
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

	/**
	 * Updates the time to the first item in the list.
	 * @param duration
	 */
	public void updateExtraToFirstItem(int duration) {
		final long time = lastActiveList.get(0);
		lastExtraActive = time;
		extraDuration = duration;
	}

	public void updateSecondary() {
		final long time = System.currentTimeMillis();
		lastActive = time;
		if (!name.equalsIgnoreCase("Lyra (Bene)"))
			lastActiveList.add(time);
	}

	public void setSkillCharges(int amount) {
		skillCharges = amount;
	}

	public void setMaxSkillCharges(int amount) {
		maxSkillCharges = amount;
	}

	/**
	 * A flag to count a true hit that doesn't check for consistency.
	 */
	public void setTrueHit(boolean hit) {
		trueHit = hit;
	}

	public void process() {
		final long time = System.currentTimeMillis();
		if (skillCharges < maxSkillCharges && getCooldown() <= 0) {
			skillCharges++;
			if (skillCharges < maxSkillCharges)
				lastCount = time;
		}
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
				case "Lyra (DPS)":
				case "Lyra (Bene)":
				case "Tian":
				case "Alyss":
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
			if (hitProtection > 0 && maxSkillCharges == 0 && !trueHit) {
				hitProtection--;
				return;
			}
			trueHit = false;
			hitProtection = 1;
			reverseHitProtection = 1;
			if (skillCharges > 0 || extraCasts > 0 || Math.abs(time - lastCount) / 1000 > countDelay) {
				//if (lastCount != -1) {
				//}
				MainDriver.log("Skill used: " + name);
				if (skillCharges > 0) {
					skillCharges--;
					MainDriver.log("Charges left: " + skillCharges);
					if (getCooldown() <= 0)
						lastCount = time; //start the cooldown ticking only if it is not active
				} else if (extraCasts > 0) {
					if (getCooldown() <= 0) { //prioritize using cooldown first
						lastCount = time;
					} else {
						extraCasts--;
					}
				} else
					lastCount = time;
				hits++;
				int advancement = WeaponConfig.getData().get(mainName).getAdvancement();
				if (name.equals("Shiro") || name.equals("Zero")
						|| name.equals("Huma") || name.equals("Frigg") || name.equals("Lin")
						|| name.equals("Lyra (DPS)") || name.equals("Tian") || name.equals("Alyss"))
					updateSecondary();
				if (name.equals("Coco") && advancement >= 3) {
					lastExtraActive = time;
					extraDuration = 6;
				}
				if ((name.equals("Lyra (Bene)"))) {
					lastActiveList.add(time);
					if (lastActiveList.size() > 3)
						lastActiveList.remove(0); //remove first
					updateExtraToFirstItem(75);
				}
				if ((name.equals("Nemesis") || name.equals("Claudia")) && advancement >= 1) {
					updateSecondary();
					if (lastActiveList.size() > 2)
						lastActiveList.remove(0); //remove first
				}
				totalCounts++;
				if (totalCounts % 5 == 0) {
					if (MainDriver.sakiCheck()) {
						totalCounts++; //double count it
					}
				}
				MainDriver.log("Total skill count: " + totalCounts);
				if (WeaponConfig.hasClaudia4pc(mainName))
					MainDriver.handleClaudia4pc();
			}
		}
		if (!hit && maxSkillCharges == 0) {
			hitProtection = 1;
			if (active)
				reverseHitProtection--;
			if (getCooldown() <= 44000 && getCooldown() >= 1000 && active && lastHit) { //cooldown was reset!
				//lastCount = 0;
				MainDriver.shiroCheck();
			}
			if (reverseHitProtection <= 0 && active) {
				reverseHitProtection = 1;
				if (getCooldown() > 0) { //it's clearly not on cooldown
					MainDriver.log(name + " was reported to be on cooldown, but was found anyway");
					resetCooldown();
				}
			}
		}
		addData(new BigInteger(Integer.toString(hits)));
		lastHit = hit;
	}

	public int getSecondaryCount(int delay) {
		long time = System.currentTimeMillis();
		int count = 0;
		if (lastActiveList == null)
			return 0;
		for (Long l : lastActiveList) {
			if (time - l <= delay) {
				count++;
			}
		}
		return count;
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

	public String getMainName() {
		return mainName;
	}
	
	public void handleHit(boolean hit) {
		handleHit(hit, false);
	}

	public void increaseExtraCasts(int num) {
		extraCasts += num;
	}
	public void setExtraCasts(int num) {
		extraCasts = num;
	}

	public int getExtraCasts() {
		return extraCasts;
	}
	public int getSkillCharges() {
		return skillCharges;
	}

	/**
	 * If a collection uses a skill charge count, we'll use skill charge icons to determine the CD,
	 * instead of the standard image matching.
	 * @return
	 */
	public int getMaxSkillCharges() {
		return maxSkillCharges;
	}

	/**
	 * Returns the cooldown in milliseconds.
	 */
	public int getCooldown() {
		if (lastCount <= 0)
			return 0;
		final long time = System.currentTimeMillis();
		return (countDelay * 1000 - (int)(Math.abs(time - lastCount)));
	}

	/**
	 * Advances the cooldown by the given amount of seconds.
	 */
	public void advanceCooldown(int seconds) {
		lastCount -= seconds * 1000;
	}
	public void advanceCooldownMs(int ms) {
		lastCount -= ms;
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
		return (lastSawDelay * 1000 - (int)(Math.abs(time - lastActive)));
	}

	@Override
	public void reset() {
		super.reset();
		hits = 0;
		totalCounts = 0;
	}
}
