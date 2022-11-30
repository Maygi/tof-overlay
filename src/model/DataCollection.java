package model;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that collects and stores data.
 * @author May
 */
public class DataCollection {
	
	public static final int USE_BIG = 0;
	public static final int USE_INT = 1;
	public static final int USE_STRING = 2;
	
	private int useMode;

	private boolean active;
	
	
	protected List<BigInteger> data;
	protected List<Integer> intData;
	protected List<String> stringData;
	
	/**
	 * Standard constructor for a DataCollection.
	 * Most DataCollections will use integers; some will use strings.
	 */
	public DataCollection() {
		data = new ArrayList<BigInteger>();
		intData = new ArrayList<Integer>();
		stringData = new ArrayList<String>();
		useMode = 0;
		active = false;
	}
	
	public void equalize(int length) {
		if (data.size() > 0) {
			while (data.size() < length) {
				data.add(0, data.get(0));
			}
		}
		if (stringData.size() > 0) {
			while (stringData.size() < length) {
				stringData.add(0, stringData.get(0));
			}
		}
	}
	
	public void addData(String value) {
		stringData.add(value);
		useMode = USE_STRING;
		if (value.length() > 0)
			active = true;
	}
	
	public void addData(Integer value) {
		intData.add(value);
		useMode = USE_INT;
		if (value != 0)
			active = true;
	}
	
	public void addData(BigInteger value) {
		data.add(value);
		useMode = USE_BIG;
		if (value != null && value.intValue() > 0)
			active = true;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public List<String> getStringData() {
		return stringData;
	}
	
	public List<Integer> getIntData() {
		return intData;
	}	
	
	public List<BigInteger> getData() {
		return data;
	}

	public String getLastString() {
		if (stringData.size() > 0)
			return stringData.get(stringData.size() - 1);
		return null;
	}
	
	public Integer getLastInt() {
		if (intData.size() > 0)
			return intData.get(intData.size() - 1);
		return 0;
	}
	
	public BigInteger getLast() {
		if (data.size() > 0)
			return data.get(data.size() - 1);
		return BigInteger.ZERO;
	}
	
	public String getLastAsString() {
		try {
			switch (useMode) {
				case USE_BIG:
					return getLast().toString();
				case USE_INT:
					return Integer.toString(getLastInt());
				case USE_STRING:
					return getLastString();
			}
			return "";
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isActive() {
		return active;
	}
	
	public void reset() {
		data = new ArrayList<BigInteger>();
		intData = new ArrayList<Integer>();
		stringData = new ArrayList<String>();
		useMode = 0;
		active = false;
	}
}
