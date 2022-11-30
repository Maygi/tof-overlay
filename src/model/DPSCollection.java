package model;

import java.math.BigInteger;
import java.util.List;

import model.MainDriver.TrackPoint;

/**
 * This class handles calculations of average DPS in and out of Holy Symbol, as well as
 * Holy Symbol estimated contribution.
 * This uses the HOLY_SYMBOL_DAMAGE trackpoint, but has helper methods for other calculations.
 * 
 * Holy Symbol estimated damage is taken by:
 * 1) Taking the total damage dealt during Holy Symbol
 * 2) Comparing it to the highest damage period of similar length, in the previous (scope 
 * multiplier * duration) of the Holy Symbol. If there is no previous period, it instead
 * uses the next period. 
 * 3) Subtracting the comparison "normal" damage from the HS damage.
 * @author May
 */
public class DPSCollection extends DataCollection {
	
	/**
	 * The scope multiplier for finding a similar DPS window.
	 * A multiplier of 4 means that, with a 21-second Holy Symbol, the collection will
	 * attempt to search for the highest 21-second DPS window within 4*21 = 84 seconds
	 * of the Holy Symbol itself.
	 * Scope must be at least 2.
	 */
	private static final int SCOPE_MULTIPLIER = 4;
	private static final int BASE_TICKS = 20;
	
	private HitMissCollection hs;
	private DeltaCollection raidDamage;
	
	private int averageDPS, hsTime, lastHS;

	/**
	 * Standard constructor for a DPSCollection.
	 * This is a DataCollection that estimates Holy Symbol's damage contribution.
	 */
	public DPSCollection(HitMissCollection hs, DeltaCollection raidDamage) {
		super();
		this.hs = hs;
		this.raidDamage = raidDamage;
		this.averageDPS = 0;
		this.hsTime = 0;
		this.lastHS = 0;
	}
	
	@Override
	public void reset() {
		super.reset();
		hs.reset();
		raidDamage.reset();
		hsTime = 0;
		averageDPS = 0;
		lastHS = 0;
	}
	
	/**
	 * Returns the index of the last HS cast.
	 * @return The index of the last HS cast.
	 */
	public int getLastHSTime() {
		return lastHS;
	}
	
	public int getAverageHSTime() {
		if (hs.getEndpoints().size() > 0)
			return hsTime / hs.getEndpoints().size();
		return 0;
	}
	
	public BigInteger getHSDPS() {
		/*if (MainDriver.data.get(TrackPoint.HOLY_SYMBOL_DAMAGE_RAW).data.size() > 0)
			return (MainDriver.data.get(TrackPoint.HOLY_SYMBOL_DAMAGE_RAW).getLast().divide(new BigInteger(Integer.toString(hsTime))));*/
		return BigInteger.ZERO;
	}
	
	public int getAverageDPS() {
		return averageDPS;
	}
	
	public void calculateHSDamage() {
		/*List<Integer[]> endPoints = hs.getEndpoints();
		List<BigInteger> data = raidDamage.getData();
		BigInteger total = new BigInteger("0");
		hsTime = 0;
		if (endPoints.size() > 0) {
			for (int i = 0; i < endPoints.size(); i++) {
				try {
					Integer[] endPointSet = endPoints.get(i);
					BigInteger startDamage = data.get(endPointSet[0]);
					BigInteger endDamage = data.get(endPointSet[1]);
					if (i == endPoints.size() - 1)
						lastHS = endPointSet[0];
					BigInteger differential = endDamage.subtract(startDamage);
					BigInteger comparison = findComparison(endPointSet);
					hsTime += ((TimeCollection)MainDriver.data.get(TrackPoint.TIME)).getTime(endPointSet[0], endPointSet[1]);
					System.out.println("Attempting to compare between: "+endPointSet[0]+", "+endPointSet[1]+". HS damage: "+endDamage.toString()+"-"+startDamage.toString());
					if (comparison == null) //not enough data
						return;
					BigInteger result = differential.subtract(comparison);
					BigInteger toAdd = differential.multiply(new BigInteger("19")).divide(new BigInteger("100")); 
					if (result.compareTo(toAdd) > 0) {
						total = total.add(result);
					} else {
						total = total.add(toAdd);
						System.out.println("Adding pity points: "+toAdd.toString());
					}
					System.out.println("HS Damage: "+differential+". Comparison Damage: "+comparison+".");
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		addData(total.toString());*/
	}
	
	public BigInteger findComparison(Integer[] endPointSet) {
		int length = endPointSet[1] - endPointSet[0];
		int[] scope = new int[2];
		List<BigInteger> data = raidDamage.getData();
		if (endPointSet[0] - 1 - length * SCOPE_MULTIPLIER < 0) { //use some data from later on
			if (data.size() < endPointSet[1] + 1 + length * SCOPE_MULTIPLIER) { //not enough data
				return null;
			}
			scope[0] = endPointSet[1] + 1;
			scope[1] = endPointSet[1] + 1 + length * SCOPE_MULTIPLIER;
		} else {
			scope[0] = endPointSet[0] - length * SCOPE_MULTIPLIER;
			scope[1] = endPointSet[0] - 1;
		}
		//System.out.println("Scope: "+Arrays.toString(scope)+"; length of HS: "+length);
		BigInteger largest = new BigInteger("0");
		int start = 0;
		int end = 0;
		for (int i = scope[0]; i < scope[1] - length; i++) {
			BigInteger sum = data.get(i + length).subtract(data.get(i));
			if (sum.compareTo(largest) > 0) {
				largest = sum;
				start = i;
				end = i + length;
			}
		}
		System.out.println("Comparison point: "+largest.toString()+", from "+start+" to "+end);
		return largest;
	}
	
	/**
	 * Calculates the average damage per tick with no Holy Symbol.
	 * Attempts to take chunks (similar to the size of 1 -> SCOPE_MULTIPLIER) of no HS windows,
	 * either before or after the most recent HS cast.
	 */
	public void calculateAverageDamage() {
		/*List<Integer[]> endPoints = hs.getEndpoints();
		List<BigInteger> data = raidDamage.getData();
		int start, end;
		if (endPoints.size() == 0) { //no holy symbol
			if (data.size() < BASE_TICKS * (SCOPE_MULTIPLIER + 1)) { //use everything
				start = 0;
				end = data.size() - 1;
			} else { //use the most recent data
				start = data.size() - BASE_TICKS * SCOPE_MULTIPLIER;
				end = data.size() - 1;
			}
		} else {
			Integer[] last = endPoints.get(endPoints.size() - 1);
			int length = last[1] - last[0];
			//find a reasonable estimation chunk before, or after, the most recent HS
			if (last[0] - length < 0) { //hs was cast after a similar window of normal DPS at the start
				if (last[1] + length >= data.size()) {
					return; //inaccurate data
				} else { //use the window after the hs
					start = last[1];
					end = last[1] + length;
				}
			} else {
				if (last[1] + length * (SCOPE_MULTIPLIER + 1) < data.size()) { //we have a lot of data to work with after the last HS
					start = last[1];
					end = last[1] + length * SCOPE_MULTIPLIER;
				} else { //use the data before the HS
					if (last[0] - length * (SCOPE_MULTIPLIER + 1) > 0) { //there's enough data to take a large sample
						start = last[0] - length * SCOPE_MULTIPLIER;
						end = last[0];
					} else { //there is not enough data
						if (last[0] - length < 0) {
							return; //this case shouldn't hit, but...
						}
						start = last[0] - length;
						end = last[0];
					}
				}
			}
		}
		BigInteger val = new BigInteger("0");
		val = val.add((data.get(end).subtract(data.get(start))));
		int compareTime = ((TimeCollection)MainDriver.data.get(TrackPoint.TIME)).getTime(start, end);
		if (compareTime == 0)
			return;
		val = val.divide(new BigInteger(compareTime + "")); //average damage per second 
		averageDPS = Integer.parseInt(val.toString());*/
	}
}
