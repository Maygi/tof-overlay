package model;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import model.MainDriver.TrackPoint;

/**
 * This is a DataCollection that counts up a portion (multiplier) of data based on the
 * change, per tick, of a parallel collection.
 * @author May
 */
public class DeltaCollection extends DataCollection {
	
	public static final int DAMAGE_AMP = 0;
	public static final int DEFENSE_DEBUFF = 1;
	public static final int ATTACK_BUFF = 2;
	public static final int OTHER = 3;
	
	/**
	 * Readings of the last, and second last HP values.
	 */
	private static BigInteger last, secondLast;
	
	/**
	 * Whether or not data should be updated on this tick.
	 */
	private static boolean addData;
	
	/**
	 * The data collection to mirror
	 */
	private DataCollection data;
	
	private List<BigInteger> dataOverTime;
	
	/**
	 * Data accumulates. Deltas give us the value per tick.
	 */
	private List<BigInteger> deltas;
	
	/**
	 * These delta arrays return the average of the last X deltas, based on the delta constant.
	 */
	private List<BigInteger> softDeltas;
	
	private static final int DELTA_CONSTANT = 5;
	
	private double multiplier;
	
	private BigInteger total;
	private static BigInteger totalDamageCounted = BigInteger.ZERO;
	
	/**
	 * The type of debuff.
	 */
	private int type;
	
	/**
	 * A temporary flag, to be used in post-tick calculations, of the hit.
	 */
	private boolean flag = false;
	
	/**
	 * The amount of "inaccurate" readings that occured.
	 * When an inaccurate reading occurs, the soft delta (used by the graph) is not updated immediately.
	 * Breaks are counted up towards the next accurate reading, and then the difference is
	 * evenly distributed throughout the breakpoints.
	 */
	private int breaks;
	
	public DeltaCollection(DataCollection data, double multiplier, int type) {
		super();
		this.data = data;
		this.multiplier = multiplier;
		this.type = type;
		this.total = BigInteger.ZERO;
		this.breaks = 0;
		this.dataOverTime = new ArrayList<BigInteger>();
		this.deltas = new ArrayList<BigInteger>();
		this.softDeltas = new ArrayList<BigInteger>();
	}
	
	public double getMultiplier() {
		return multiplier;
	}
	
	/**
	 * Returns the last (valid) delta behind the given index.
	 * @return
	 */
	public BigInteger getLastDelta(int index) {
		if (index > data.getData().size())
			return BigInteger.ZERO;
		List<BigInteger> realData = data.getData();
		int indexToRead = index;
		BigInteger reference = BigInteger.ZERO;
		while (indexToRead > 0) { //find the last successful reading
			indexToRead--;
			BigInteger value = realData.get(index);
			if (value.compareTo(BigInteger.ZERO) < 0) //if failed to read
				continue;
			else {
				reference = value;
				break;
			}
		}
		return realData.get(index).subtract(reference);
	}

	
	public List<BigInteger> getDataOverTime() {
		return dataOverTime;
	}
	
	public List<BigInteger> getDeltas() {
		return deltas;
	}
	
	public List<BigInteger> getSoftDeltas() {
		return softDeltas;
	}
	
	/**
	 * Handles pre-processing.
	 * To be called before post-processing, but after raid damage processing.
	 */
	public static void preProcess() {
		/*addData = true;
		List<BigInteger> realData = MainDriver.data.get(TrackPoint.HP).getData();
		last = new BigInteger("-1");
		secondLast = new BigInteger("-1");
		BigInteger lastValid = null;
		int index = realData.size() - 2;
		if (realData.size() < 2)
			addData = false;
		else {
			last = realData.get(realData.size() - 1);
			secondLast = realData.get(index);
			lastValid = last;
		}
		if (secondLast == null || last == null)
			addData = false;
		else {
			for (int i = realData.size() - 1; i > Math.max(realData.size() - 5, 0); i--) {
				if (lastValid == null && last == null && realData.get(i) != null)
					lastValid = realData.get(i);		
				if (lastValid == null)
					continue;					
				BigInteger integrityCheck = realData.get(i);
				if (integrityCheck == null)
					continue;
				if (integrityCheck.subtract(lastValid).abs().compareTo(lastValid) > 0) {
					addData = false;
					break;
				}
				
			}
			if (last.compareTo(BigInteger.ZERO) < 0 || secondLast.compareTo(BigInteger.ZERO) < 0) //if failed to read either
				addData = false;
			if (secondLast.subtract(last).compareTo(BigInteger.ZERO) < 0) //it healed - can't count it
				addData = false;
			if (secondLast.subtract(last).compareTo(last) > 0) //probably counted an extra digit...
				addData = false;
		}
		while (!addData && index > 0 && (last == null || last.compareTo(BigInteger.ZERO) > 0)) { //find the last successful reading
			index--;
			secondLast = realData.get(index);
			if (secondLast == null || last == null)
				addData = false;
			else if (last.compareTo(BigInteger.ZERO) < 0 || secondLast.compareTo(BigInteger.ZERO) < 0) //if failed to read either
				addData = false;
			else if (secondLast.subtract(last).compareTo(BigInteger.ZERO) < 0) //it healed - can't count it
				addData = false;
			else if (secondLast.subtract(last).compareTo(last) > 0) //probably counted an extra digit...
				addData = false;
			else if (secondLast.subtract(last).compareTo(last) > 0) //probably counted an extra digit...
				addData = false;
			else if (realData.size() > 10) {
				boolean flag = false;
				for (int i = realData.size() - 1; i > Math.max(realData.size() - 5, 0); i--) {
					if (lastValid == null && last == null && realData.get(i) != null)
						lastValid = realData.get(i);
					if (lastValid == null)
						continue;
					BigInteger integrityCheck = realData.get(i);
					if (integrityCheck == null)
						continue;
					if (integrityCheck.subtract(last).abs().compareTo(last) > 0) {
						addData = false;
						flag = true;
						break;
					}
				}
				if (!flag)
					addData = true;
			} else
				addData = true;
		}*/
	}
	
	/**
	 * Must be called before updateSoftDelta
	 * @param addData Whether or not the reading was valid.
	 * @param value The value to add
	 */
	public void updateDelta(boolean addData, BigInteger value) {
		if (!addData) {
			breaks++;
			MainDriver.logOutput.println("Update Delta. Bad reading. Breaks: "+breaks);
			return;
		}
		if (breaks > 0) {
			MainDriver.logOutput.println("Update Delta. There are breaks: "+breaks+"; dividing value, which is "+value.toString());
			value = value.divide(new BigInteger(Integer.toString(breaks)));
			for (int i = 0; i < breaks; i++) {
				deltas.add(value);
				MainDriver.logOutput.println("Update Delta. Adding value: "+value);
			}
		} else {
			deltas.add(value);
			MainDriver.logOutput.println("Update Delta. Adding value: "+value);
		}
	}
	
	/**
	 * Updates the soft delta, calling itself recursively to update multiple times if breaks occurred.
	 * @param toAdd Whether or not the reading was valid
	 */
	public void updateSoftDelta(boolean toAdd) {
		if (!toAdd) {
			return;
		}
		BigInteger total = BigInteger.ZERO;
		int start = deltas.size() - 1;
		int end = Math.max(0,  deltas.size() - 1 - DELTA_CONSTANT);
		int count = 0;
		for (int i = start; i >= end; i--) {
			total = total.add(deltas.get(i));
			count++;
		}
		if (count == 0) {
			MainDriver.logOutput.println("Updating soft delta with count: "+(deltas.get(deltas.size() - 1)));
			softDeltas.add(deltas.get(deltas.size() - 1));
		} else {
			total = total.divide(new BigInteger(Integer.toString(count)));
			MainDriver.logOutput.println("Updating soft delta with: "+total);
			softDeltas.add(total);
		}
		if (breaks > 0) {
			MainDriver.logOutput.println("Breaks: "+breaks);
			breaks--;
			updateSoftDelta(toAdd);
		}
	}
	
	/**
	 * Final process that calculates debuff contributions.
	 */
	public static void finalProcess() {
		if (!addData)
			return;
		/*BigInteger rawDamage = secondLast.subtract(last);
		totalDamageCounted = totalDamageCounted.add(rawDamage);
		List<BigInteger> realData = MainDriver.data.get(TrackPoint.HP).getData();
		
		int index = realData.size() - 1;
		
		//damage amplification effects
		TrackPoint[] damageAmpColl = {
		};
		DeltaCollection[] damageAmp = new DeltaCollection[damageAmpColl.length];
		for (int i = 0; i < damageAmpColl.length; i++) {
			damageAmp[i] = (DeltaCollection)(MainDriver.data.get(damageAmpColl[i]));
		}
		TrackPoint[] damageAmpTp = {
			TrackPoint.SMITE, TrackPoint.MOD, TrackPoint.PURIFYING_LIGHT, TrackPoint.MADRIA
		};
		
		//attack buffs effects
		TrackPoint[] attackBuffColl = {
			TrackPoint.BLESSINGS_AMP, TrackPoint.VITALITY_AMP, TrackPoint.WARHORN_AMP, TrackPoint.FOCUSSEAL_AMP, TrackPoint.EAGLESQUAD_AMP
		};
		DeltaCollection[] attackBuff = new DeltaCollection[attackBuffColl.length];
		for (int i = 0; i < attackBuffColl.length; i++) {
			attackBuff[i] = (DeltaCollection)(MainDriver.data.get(attackBuffColl[i]));
		}
		TrackPoint[] attackBuffTp = {
			TrackPoint.BLESSINGS, TrackPoint.VITALITY, TrackPoint.WARHORN, TrackPoint.FOCUSSEAL, TrackPoint.EAGLE_SQUAD
		};

		//defense debuff effects
		TrackPoint[] defDebuffColl = {
			TrackPoint.STATIC_FLASH_AMP, TrackPoint.SHIELDTOSS_AMP, TrackPoint.CYCLONE_SHIELD_AMP,
			TrackPoint.SOUL_FLOCK_AMP,TrackPoint.ARIELS_WINGS_AMP
		};
		DeltaCollection[] defDebuff = new DeltaCollection[defDebuffColl.length];
		for (int i = 0; i < defDebuffColl.length; i++) {
			defDebuff[i] = (DeltaCollection)(MainDriver.data.get(defDebuffColl[i]));
		}
		TrackPoint[] defDebuffTp = {
			TrackPoint.STATIC_FLASH, TrackPoint.SHIELDTOSS, TrackPoint.CYCLONE_SHIELD,
			TrackPoint.SOUL_FLOCK, TrackPoint.ARIELS_WINGS
		};

		index--; //collections have 1 less entry than hp
		//calculate the total dmg amp
		double totalAmp = 1;
		double totalDamageAmp = 1;
		double totalMultipliers = 0; //to calculate portions
		for (int i = 0; i < damageAmp.length; i++) {
			List<Boolean> list = ((HitMissCollection)(MainDriver.data.get(damageAmpTp[i]))).getRawData();
			index = list.size() - 1;
			boolean hit = list.get(index);
			totalDamageAmp += hit ? damageAmp[i].getMultiplier() : 0;
		}
		totalMultipliers += totalDamageAmp - 1;
		totalAmp = totalDamageAmp;
		//unused - old calculation assumed one applied before the other
		BigDecimal preAmp = new BigDecimal(rawDamage.doubleValue()).divide(new BigDecimal(totalAmp), 2, RoundingMode.HALF_UP);

		//calculate the damage amp from defense shred
		double defFactor = 0;
		for (int i = 0; i < defDebuff.length; i++) {
			List<Boolean> list = ((HitMissCollection)(MainDriver.data.get(defDebuffTp[i]))).getRawData();
			index = list.size() - 1;
			boolean hit = list.get(index);
			defFactor += hit ? defDebuff[i].getMultiplier() : 0;
		}
		double totalDefAmp = 1 / (1 - defFactor);
		totalAmp *= totalDefAmp;
		totalMultipliers += totalDefAmp - 1;
		
		BigDecimal preDefAmp = preAmp.divide(new BigDecimal(totalDefAmp), 2, RoundingMode.HALF_UP);

		//calculate the total attack buffs
		double attackMultiplier = 1;
		for (int i = 0; i < attackBuff.length; i++) {
			List<Boolean> list = ((HitMissCollection)(MainDriver.data.get(attackBuffTp[i]))).getRawData();
			index = list.size() - 1;
			boolean hit = list.get(index);
			attackMultiplier += hit ? attackBuff[i].getMultiplier() : 0;
		}
		totalAmp *= attackMultiplier;
		totalMultipliers += attackMultiplier - 1;
		if (totalMultipliers == 0)
			return;

		BigDecimal preBuffDamage = new BigDecimal(rawDamage.doubleValue()).divide(new BigDecimal(totalAmp), 2, RoundingMode.HALF_UP);
		BigDecimal damageGained = new BigDecimal(rawDamage.doubleValue()).subtract(preBuffDamage);
		BigDecimal damageGainedFromDefDebuff = damageGained.multiply(new BigDecimal((totalDefAmp - 1) / totalMultipliers));
		BigDecimal damageGainedFromDmgAmp = damageGained.multiply(new BigDecimal((totalDamageAmp - 1) / totalMultipliers));
		BigDecimal damageGainedFromAttackBuff = damageGained.multiply(new BigDecimal((attackMultiplier - 1) / totalMultipliers));
		MainDriver.logOutput.println("Total: "+totalDamageCounted.toString()+"; Raw: "+rawDamage.toString()+"; total amp: "+totalAmp+
				"; preBuff: "+preBuffDamage.toString()+"; from def: "+damageGainedFromDefDebuff.toString()+
				"; from dmg amp: "+damageGainedFromDmgAmp.toString()+"; from attack buff: "+
				damageGainedFromAttackBuff.toString());
		//MainDriver.logOutput.println("Total: "+totalDamageCounted.toString()+"; Raw: "+rawDamage.toString()+"; preAmp: "+preAmp+"; preDefAmp: "+preDefAmp);
		
		for (int i = 0; i < defDebuffTp.length; i++) {
			//the total portal of the defense debuff. e.g. if there's a 5% and 10% active (total 15%, 5% is 33%)
			boolean hit = ((HitMissCollection)(MainDriver.data.get(defDebuffTp[i]))).getRawData().get(index);
			if (!hit)
				continue;
			double totalDefPortion = defFactor == 0 ? 0 : defDebuff[i].getMultiplier() / defFactor;
			BigDecimal contribution = damageGainedFromDefDebuff.multiply(new BigDecimal(totalDefPortion));
			MainDriver.logOutput.println(defDebuffTp[i].getName()+" - Total def debuff: "+defFactor+": portion: "+totalDefPortion+"; contribution: "+contribution.toString());
			defDebuff[i].addDataFinal(contribution);
		}
		for (int i = 0; i < damageAmpTp.length; i++) {
			//the total portal of the defense debuff. e.g. if there's a 5% and 10% active (total 15%, 5% is 33%))
			boolean hit = ((HitMissCollection)(MainDriver.data.get(damageAmpTp[i]))).getRawData().get(index);
			if (!hit)
				continue;
			double damageAmpPortion = damageAmp[i].getMultiplier() / (totalDamageAmp - 1);
			BigDecimal contribution = damageGainedFromDmgAmp.multiply(new BigDecimal(damageAmpPortion));
			MainDriver.logOutput.println(damageAmpTp[i].getName()+" - contribution: "+contribution.toString());
			damageAmp[i].addDataFinal(contribution);
		}
		for (int i = 0; i < attackBuffTp.length; i++) {
			//the total portal of the defense debuff. e.g. if there's a 5% and 10% active (total 15%, 5% is 33%))
			boolean hit = ((HitMissCollection)(MainDriver.data.get(attackBuffTp[i]))).getRawData().get(index);
			if (!hit)
				continue;
			double portion = attackBuff[i].getMultiplier() / (attackMultiplier - 1);
			BigDecimal contribution = damageGainedFromAttackBuff.multiply(new BigDecimal(portion));
			MainDriver.logOutput.println(attackBuffTp[i].getName()+" - contribution: "+contribution.toString());
			attackBuff[i].addDataFinal(contribution);
		}
		*/
		//BigInteger synergy = rawDamage.subtract(preDefAmp.toBigInteger()).subtract(totalDebuff.toBigInteger());
		//MainDriver.logOutput.println("SYNERGY: "+synergy.toString());
	}
	
	public void addDataFinal(BigDecimal value) {
		System.out.println("ADDING: "+value.toString());
		total = total.add(value.toBigInteger());
		addData(total);
	}
	
	public void postProcess() {
		if (!MainDriver.active)
			return;
		if (flag || type == OTHER) {
			BigDecimal decimal = addData ? new BigDecimal((secondLast.subtract(last).toString())) : BigDecimal.ZERO;
			BigInteger toAdd = addData ? (decimal.multiply(new BigDecimal(multiplier))).toBigInteger() : BigInteger.ZERO;
			if (type == OTHER && addData) {
				total = total.add(toAdd);
			}/* else {
				deltas.add(deltas.size() == 0 ? 0 : deltas.get(deltas.size() - 1));
			}*/
			MainDriver.logOutput.println("Decimal: "+decimal+", toAdd: "+toAdd+", addData: "+addData+", multiplier: "+multiplier);
			updateDelta(addData, toAdd);
			updateSoftDelta(addData);
			if (type == OTHER)
				addData(total);
			if (MainDriver.getEllaspedTime() > 0)
				dataOverTime.add(total.divide(new BigInteger(Integer.toString(MainDriver.getEllaspedTime()))));
		}
		flag = false;
	}

	public void handleHit(boolean hit, double multiplier) {
		if (!MainDriver.active)
			return;
		if (hit) {
			flag = true;
		} else {
			flag = false;
		}
	}
	public void handleHit(boolean hit) {
		handleHit(hit, multiplier);
	}
	@Override
	public void reset() {
		super.reset();
		total = BigInteger.ZERO;
		dataOverTime = new ArrayList<BigInteger>();
		deltas = new ArrayList<BigInteger>();
		softDeltas = new ArrayList<BigInteger>();
		breaks = 0;
	}
}
