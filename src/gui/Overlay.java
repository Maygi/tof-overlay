package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.BevelBorder;

import model.*;
import model.MainDriver.TrackPoint;
import particles.Particle;
import sound.Sound;
import util.VersionCheck;

/**
 * This is the main GUI for the data chart.
 * Lots of hard coding, I know.
 * @author May
 */
public class Overlay extends AbstractLabel {

	private static final long serialVersionUID = -6204287424593635872L;
    
    /**
     * The margin (in pixels) between an icon and the text.
     */
    private static final int MARGIN = 6;
    
    /**
     * The horizontal distance between table columns.
     */
    private static final int GAP = 180;
    
    /**
     * The percentage of the height of each line of text.
     */
    private static final double TEXT_PERCENT = 0.25;
    
    private static final GuiButton[] BUTTONS = {
		GuiButton.UPDATE, 
		GuiButton.CLOSE, GuiButton.MUTE, GuiButton.PAUSE,
		GuiButton.REPORT, GuiButton.RESET, GuiButton.MINIMIZE
    };
    
    
    private static final TrackPoint[] PERSONAL = {
			TrackPoint.CLAUDIA, TrackPoint.SHIRO, TrackPoint.NEMESIS, TrackPoint.SAKI, TrackPoint.TSUBASA,
			TrackPoint.SAMIR, TrackPoint.ZERO, TrackPoint.RUBY, TrackPoint.MERYL,
			TrackPoint.COBALT, TrackPoint.COCO, TrackPoint.KING,
			TrackPoint.CROW, TrackPoint.FRIGG, TrackPoint.LIN
    };
    
    private static final TrackPoint[] RAID = {
    };
    
    private static final TrackPoint[] BUFFS = {
    };
    
    private static final TrackPoint[] DEBUFFS = {
    };
    
    private static final TrackPoint[] MISC = {
    };
    
    private JFrame myFrame;

	protected Overlay(JFrame theFrame) {
		super(theFrame, 1, 1);
		myFrame = theFrame;
		setup();
	}
    
    /**
     * Handles various setup methods.
     */
    private void setup() {
        setBackground(BASE_COLOR);
        //setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, BASE_COLOR.brighter(),
                                                  BASE_COLOR.darker()));

        setVisible(true);
    }

    public void replaceTooltip(TrackPoint tp, int x, int y) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(tp.getIntro());
    	Tooltip toAdd = new Tooltip(x, y, tp.getName(), sb.toString());
    	OverlayFrame o = ((OverlayFrame)myFrame);
    	for (final Tooltip t : o.tooltipMappings.keySet()) {
    		if (t.getCoords()[0] == x && t.getCoords()[1] == y) {
    			o.tooltipMappings.remove(t);
    			break;
    		}
    	}
    	((OverlayFrame)myFrame).tooltipMappings.put(toAdd, false);
    }

    public void addTooltip(TrackPoint tp, int x, int y) {
    	Tooltip toAdd = new Tooltip(x, y, tp.getName(), tp.getIntro());
    	((OverlayFrame)myFrame).tooltipMappings.put(toAdd, false);
    }
    
    /**
     * Returns the amount of decimal places in a BigDecimal.
     * @param bigDecimal The number to check.
     * @return The number of decimal places.
     */
	public static int getDecimalPlaces(BigDecimal bigDecimal) {
		String string = bigDecimal.stripTrailingZeros().toPlainString();
		int index = string.indexOf(".");
		return index < 0 ? 0 : string.length() - index - 1;
	}
	
	public static String format(BigInteger number) {
		return format(new BigDecimal(number));
	}
	
	/**
	 * Formats a number to take less space.
	 * @param number The number to format.
	 * @return A formatted value of the number, using K, M, or B to shorten it.
	 */
	public static String format(BigDecimal number) {
		if (number.compareTo(BigDecimal.ZERO) == 0)
			return "0";
		String[] marks = { "1000000000", "1000000", "1000", "1" };
		String[] labels = { "B", "M", "K", "" };
		BigDecimal value;
		StringBuilder sb = new StringBuilder();
		DecimalFormat df = new DecimalFormat("0.##");
		int decimalPlaces, numberPlaces, totalPlaces;
		for (int i = 0; i < marks.length; i++) {
			if (number.compareTo(new BigDecimal(marks[i])) == 1 || number.compareTo(new BigDecimal(marks[i])) == 0) {
				value = number.divide(new BigDecimal(marks[i]));
				value = value.multiply(new BigDecimal("1000"));
				value = value.setScale(0, RoundingMode.FLOOR);
				value = value.divide(new BigDecimal("1000"));
				numberPlaces = value.toBigInteger().toString().length();
				decimalPlaces = getDecimalPlaces(value);
				if (decimalPlaces <= 1) {
					sb.append(df.format(value));
					sb.append(labels[i]);
				} else {
					totalPlaces = numberPlaces + decimalPlaces;
					if (totalPlaces <= 4) {
						sb.append(df.format(value.multiply(new BigDecimal("1000"))));
						sb.append(labels[i + 1]);
					} else { // don't bring it down - just round
						sb.append(df.format(value.setScale(6 - totalPlaces, BigDecimal.ROUND_HALF_UP)));
						sb.append(labels[i]);
					}
				}
				break;
			}
		}
		return sb.toString();
	}

	private String[] lastCd = new String[3];
	private int tick = 0;

    /**
     * Paints the chart.
     * @param theGraphics The graphics context to use for painting.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        drawFilm(theGraphics, MainDriver.MAIN_COLOR);
        if (!MainDriver.active || (System.currentTimeMillis() - MainDriver.lastActivity <= 15000)) {
            drawFilm(theGraphics, Color.GRAY);
        }
        if (MainDriver.started && !OverlayFrame.compact) {
			tick++;
	        /*drawText(g2d, "Cooldowns", FONT_SIZE, MARGIN,
	                (int) (getSize().getHeight() * TEXT_PERCENT) - 30, 1,
	                Color.WHITE, SHADOW_COLOR.darker());*/
	        int line = 1;
			String text = "";
			String text2 = "";
	        String image = "";
	        
	        //here comes the hardcoding, do-do-do-do..
	        //line++;
	        drawText(g2d, "", FONT_SIZE, MARGIN,
	                (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
	                Color.WHITE, SHADOW_COLOR.darker());
	    	int xOffset = 0;
			double cd = 0;
			double initialCd = 0;
			double lastVal = -1;
			double initialVal = 0;
			boolean buffTimer = true;
			String buffTimerIcon = "";
			double chargeDebuffTime = (System.currentTimeMillis() - MainDriver.getFullChargeTime()) / 1000.0;
			//System.out.println("Charge debuff time / total: " + chargeDebuffTime + "; " + MainDriver.getFullChargeTotal());
			if (chargeDebuffTime < MainDriver.getFullChargeTotal()) {
				drawImage(theGraphics, WeaponData.getFullChargeIcon(MainDriver.getFullChargeType()), 8, 8);
				drawArcReverse(theGraphics, 8 + 2, 8 + 2, 32, 32,
						(int)(chargeDebuffTime / MainDriver.getFullChargeTotal() * 360),
						WeaponData.getColor(MainDriver.getFullChargeType()).darker(),
						4, 1);
				drawArcReverse(theGraphics, 8, 8, 32, 32,
						(int)(chargeDebuffTime / MainDriver.getFullChargeTotal() * 360),
						WeaponData.getColor(MainDriver.getFullChargeType()),
						4, 1);
				drawNormalTextCentered(g2d, Integer.toString(MainDriver.getFullChargeTotal() - (int) chargeDebuffTime), FONT_SIZE, 32 + 24,
						32, 1,
						Color.WHITE, SHADOW_COLOR.darker());
			}
	        for (int i = 0; i < PERSONAL.length; i++) {
		        DataCollection dc = MainDriver.data.get(PERSONAL[i]);
		        try {
					if (dc instanceof CountCollection) {
						if (!dc.isActive())
							continue;
						String name = ((CountCollection)dc).getName();
						cd = ((CountCollection)dc).getCooldown() / 1000.0;
						initialCd = ((CountCollection)dc).getCountDelay();
						initialVal = ((CountCollection)dc).getLastSawDelay();
						if (cd > 0) {
							text = Integer.toString((int)cd); //String.format("%.2f", cd);
						} else {
							text = "Ready";
						}
						if (lastCd[line - 1] != null && text.equals("Ready") &&
								!lastCd[line - 1].equals("Ready")) {
							Sound.HOVER.play(); //why is the sound alert in the gui loop? good question.
						}
						lastCd[line - 1] = text;

						String prefix = "";
						//oh no more hardcoding in a different class what is life
						//pls dont judge
						buffTimerIcon = "";
						switch(name) {
							case "Saki":
								buffTimerIcon = "images/tableicons/surge.png";
								break;
							case "Nemesis":
								buffTimerIcon = "images/tableicons/electrode.png";
								break;
							case "Claudia":
							case "Tsubasa":
							case "Huma":
								buffTimerIcon = "images/tableicons/atkbuff.png";
								break;
							case "Shiro":
								buffTimerIcon = "images/tableicons/shiroskill.png";
								break;
							case "Ruby":
								buffTimerIcon = "images/tableicons/deto.png";
								break;
							case "Zero":
							case "Meryl":
								buffTimerIcon = "images/tableicons/shield.png";
								break;
							case "Cobalt":
								buffTimerIcon = "images/tableicons/ionic.png";
								break;
							case "Coco":
								buffTimerIcon = "images/tableicons/bee.png";
								break;
							case "Crow":
								buffTimerIcon = "images/tableicons/crowdischarge.png";
								break;
							case "King":
								buffTimerIcon = "images/tableicons/kingdischarge.png";
								break;
							case "Frigg":
								buffTimerIcon = "images/tableicons/friggdomain.png";
								break;
							case "Lin":
								buffTimerIcon = "images/tableicons/moonlightrealm.png";
								break;
							/*case "Saki":
								prefix = "Surge: ";
							break;
							case "Claudia":
							case "Nemesis":
							case "Tsubasa":
								prefix = "Buff: ";
								break;*/
							default:
								break;
						}


						if (((CountCollection) dc).isCurrentlyActive()) {
							text2 = "Active";
						} else {
							double offTime = ((CountCollection) dc).getLastActive() / 1000.0;
							lastVal = offTime;
							text2 = Integer.toString((int) offTime);//prefix + String.format("%.2f", offTime);
							if (offTime == 0)
								text2 = "";
							if (offTime <= 0)
								text2 = "Inactive";
						}

					}
		        } catch (Exception e) {
					e.printStackTrace();
		        	continue;
		        }
				if (PERSONAL[i].getName().contains("SP Ef")) {
					image = "images/tableicons/spirit.png";
				} else { //personal buff or debuff
					image = PERSONAL[i].getIcon();
					//if (dc.getLastInt() <= 0) //don't show other class properties
					//	continue;
				}
				Color textColor = Color.WHITE;
				Color textColor2 = Color.WHITE;
				if (text.equalsIgnoreCase("Ready"))
					textColor = Color.GREEN;
				if (text2.equalsIgnoreCase("Active"))
					textColor2 = Color.GREEN;
				if (text2.contains("-") || text2.contains("Inactive"))
					textColor2 = Color.RED;
				int yOffset = 0;
				if (text2.length() == 0)
					yOffset = 10;
		        /*drawNormalText(g2d, text, (int)(FONT_SIZE * 1.5), 24 + MARGIN * 2 + xOffset + 40,
		                (int) (getSize().getHeight() * TEXT_PERCENT) * line + 20 + yOffset, 1,
						textColor, SHADOW_COLOR.darker());
				drawNormalText(g2d, text2, (int)(FONT_SIZE * 1), 24 + MARGIN * 2 + xOffset + 40,
						(int) (getSize().getHeight() * TEXT_PERCENT) * line + 40, 1,
						textColor2, SHADOW_COLOR.darker());*/
		        int x = MARGIN + xOffset;
		        int y = (int) (getSize().getHeight() * TEXT_PERCENT ) * line - 15;
				if (((CountCollection)dc).getName().equals("Crow")) {
					System.out.println("Buff timer: " + buffTimer + "; initialVal: " + initialVal);
				}
				if (buffTimerIcon.length() > 0 && initialVal != 0) {
					double cdPerc = (initialVal - lastVal)/initialVal;
					Color arcColor;
					double drawPerc = (lastVal)/initialVal;
					if (cdPerc <= 0.25 || text2.equals("Active"))
						arcColor = Color.GREEN;
					else if (cdPerc <= 0.50)
						arcColor = Color.YELLOW;
					else if (cdPerc <= 0.75)
						arcColor = Color.ORANGE;
					else
						arcColor = Color.RED;
					if (buffTimerIcon.contains("electrocute"))
						arcColor = new Color(204, 153, 255);
					if (text2.equals("Active"))
						drawPerc = 1;
					if (drawPerc > 1) {
						drawPerc = 0; //nande?
						lastVal = 0;
						text2 = "Inactive";
					}
					int xOffAdditional = 0;
					if (lastVal <= 0) {
						xOffAdditional = -25;
					}
					int fontSize = FONT_SIZE;
					if (text2.equals("Inactive")) {
						fontSize *= 0.8;
					}
					if (((CountCollection)dc).getName().equals("Nemesis") &&
							WeaponConfig.getData().get("Nemesis").getAdvancement() >= 6) {
						int electrodes = ((CountCollection)dc).getElectrodeCount();
						if (electrodes > 0) {
							drawNormalTextCentered(g2d, "x" + Integer.toString(electrodes), fontSize, x + 64 + MARGIN * 2 + 32,
									y + 32, 1,
									textColor2, SHADOW_COLOR.darker());
						}
					}
					drawImage(theGraphics, buffTimerIcon, x + 64 + MARGIN, y + 8);
					drawNormalTextCentered(g2d, text2, fontSize, (int) getSize().getWidth() - 30 + xOffAdditional,
							y + 32, 1,
							textColor2, SHADOW_COLOR.darker());
					drawRect(theGraphics, x + 64 + MARGIN + 2, y + 40 + 2, 110, 12, arcColor.darker().darker(), 1);
					drawRect(theGraphics, x + 64 + MARGIN, y + 40, (int)(110.0 * drawPerc), 12, arcColor, 1);
				}
				double cdPerc = (initialCd - cd)/initialCd;
				if (initialCd != 0) {
					Color arcColor = Color.RED;
					if (cdPerc >= 0.90)
						arcColor = new Color(214, 232, 101);
					if (cdPerc >= 0.75)
						arcColor = Color.YELLOW;
					else if (cdPerc >= 0.50)
						arcColor = Color.ORANGE;
					if (cd <= 0)
						arcColor = Color.GREEN;
					drawArc(theGraphics, x + 6, y + 6, 56, 56, (int)(cdPerc * 360), arcColor.darker(),
							6, 1);
					drawArc(theGraphics, x + 4, y + 4, 56, 56, (int)(cdPerc * 360), arcColor,
							6, 1);
				}
	            drawImage(theGraphics, image, x, y, 0.5);
				if (((CountCollection)dc).getAdditionalCasts() > 0) {
					drawArc(theGraphics, x + 10, y + 10, 44, 44, 360, Color.GREEN.brighter(),
							4, 0.8f);
				}
				if (((CountCollection)dc).getAdditionalCasts() == 0 && cd > 0) {
					//drawCircle(theGraphics, x + 4, y + 4, 56, 56, Color.black, 0.2f);
					fillArc(theGraphics, x + 4, y + 4, 56, 56, 360 - (int)(cdPerc * 360), Color.BLACK,
							6, 0.3f);
					drawNormalTextCentered(g2d, text, (int) (FONT_SIZE * 1.5), x + 32, y + 44, 1,
							textColor, SHADOW_COLOR.darker());
				}

				//weapon-specific ui
				switch(((CountCollection)dc).getName()) {
					/*case "Crow":
					case "Samir":
						double electrocuteTime = (System.currentTimeMillis() - MainDriver.getFullChargeTime()) / 1000.0;
						if (electrocuteTime <= 11) {
							Color barColor = new Color(204, 153, 255);
							if (Math.random() >= 0.67)
								barColor = new Color(198, 152, 245);
							else if (Math.random() >= 0.50)
								barColor = new Color(194, 135, 255);
							drawRect(theGraphics, x + 64 + MARGIN, y + 57, (int)((11.0 - electrocuteTime)/11.0 * 110),
									5, barColor, 1);
						}
						break;*/
					case "Coco":
						double timeDelta = (System.currentTimeMillis() - ((CountCollection)dc).getLastExtraActive()) / 1000.0;
						if (timeDelta < ((CountCollection)dc).getExtraDuration()) {
							double ratio = 1 - timeDelta / ((CountCollection)dc).getExtraDuration();
							Color barColor = new Color(158, 240, 255);
							drawRect(theGraphics, x + 64 + MARGIN, y + 57, (int) (ratio * 110),
									5, barColor, 1);
						}
						break;
					case "Meryl":
						if (WeaponConfig.getData().get("Meryl").getAdvancement() >= 3) {
							double shieldCd = Math.min(20, 20 - Math.min(20, lastVal + 10));
							Color barColor = shieldCd == 20 ? new Color(181, 255, 184) : new Color(158, 240, 255);
							drawRect(theGraphics, x + 64 + MARGIN, y + 57, (int) (shieldCd / 20.0 * 110),
									5, barColor, 1);
						}
						break;
					case "Lin":
						if (WeaponConfig.getData().get("Lin").getAdvancement() >= 6) {
							int resetCounter = MainDriver.getDischargeCount() % 3;
							for (int j = 0; j < resetCounter; j++) {
								Color boxColor = new Color(201, 255, 186);
								drawRect(theGraphics, x + 64 + MARGIN + (int) (56.66 * j) + 2, y + 57 + 2, 50,
										5, boxColor.darker().darker(), 1);
								drawRect(theGraphics, x + 64 + MARGIN + (int) (56.66 * j), y + 57, 50,
										5, boxColor, 1);
							}
						}
						break;
					case "Saki":
						if (WeaponConfig.getData().get("Saki").getAdvancement() >= 1) {
							int resetCounter = ((CountCollection) dc).getCount() % 5;
							for (int j = 0; j < resetCounter; j++) {
								Color boxColor = ((tick % 4) == j && resetCounter == 4) ? new Color(219, 250, 255) : new Color(158, 240, 255);
								drawRect(theGraphics, x + 64 + MARGIN + (int) (28.33 * j) + 2, y + 57 + 2, 25,
										5, boxColor.darker().darker(), 1);
								drawRect(theGraphics, x + 64 + MARGIN + (int) (28.33 * j), y + 57, 25,
										5, boxColor, 1);
							}
						}
						break;
				}

	            replaceTooltip(PERSONAL[i], x, y);
				line++;
	        }
        } else {
        	if (MainDriver.started) {
        		if (MainDriver.active) {
    		        drawNormalTextCentered(g2d, "Collecting data...", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
    		                (int) (getSize().getHeight() * 0.55), 1,
    		                Color.WHITE, SHADOW_COLOR.darker());
        		} else {
    		        drawNormalTextCentered(g2d, "Analysis complete. Maximize to view data!", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
    		                (int) (getSize().getHeight() * 0.55), 1,
    		                Color.WHITE, SHADOW_COLOR.darker());
        		}
        		
        	} else try {
        		if (OverlayFrame.compact) {
			        drawNormalTextCentered(g2d, "Enter combat with a boss to begin!", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
			                (int) (getSize().getHeight() * 0.55), 1,
			                Color.WHITE, SHADOW_COLOR.darker());        			
        		} else if (MainDriver.liveVersion.equals("???")) {
			        drawText(g2d, "Unable to check versions.", FONT_SIZE,  (int) (getSize().getWidth() * 0.3),
			                (int) (getSize().getHeight() * 0.3), 1,
			                Color.WHITE, SHADOW_COLOR.darker());
			        drawNormalTextCentered(g2d, "Enter combat with a boss to begin!", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
			                (int) (getSize().getHeight() * 0.55), 1,
			                Color.WHITE, SHADOW_COLOR.darker());
			        drawNormalTextCentered(g2d, "(make sure UI size is set to 50%)", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
			                (int) (getSize().getHeight() * 0.7), 1,
			                Color.WHITE, SHADOW_COLOR.darker());
	        		
	        	} else if (VersionCheck.needsUpdate()) {
			        drawText(g2d, "New build available!", FONT_SIZE,  (int) (getSize().getWidth() * 0.3),
			                (int) (getSize().getHeight() * 0.3), 1,
			                Color.WHITE, SHADOW_COLOR.darker());
			        drawNormalTextCentered(g2d, "Click the red \"!\" button to download it!", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
			                (int) (getSize().getHeight() * 0.55), 1,
			                Color.WHITE, SHADOW_COLOR.darker());
	        		
	        	} else {
			       /* drawText(g2d, "Waiting for data...", FONT_SIZE,  (int) (getSize().getWidth() * 0.3),
			                (int) (getSize().getHeight() * 0.3), 1,
			                Color.WHITE, SHADOW_COLOR.darker());
			        drawNormalTextCentered(g2d, "Enter combat with a boss to begin!", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
			                (int) (getSize().getHeight() * 0.55), 1,
			                Color.WHITE, SHADOW_COLOR.darker());
			        drawNormalTextCentered(g2d, "(make sure UI size is set to 50%)", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
			                (int) (getSize().getHeight() * 0.7), 1,
			                Color.WHITE, SHADOW_COLOR.darker());*/
	        	}
        	} catch (Exception e) {
		        drawText(g2d, "Unable to check versions.", FONT_SIZE,  (int) (getSize().getWidth() * 0.3),
		                (int) (getSize().getHeight() * 0.3), 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        /*drawNormalTextCentered(g2d, "Enter combat with a boss to begin!", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
		                (int) (getSize().getHeight() * 0.55), 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        drawNormalTextCentered(g2d, "(make sure UI size is set to 50%)", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
		                (int) (getSize().getHeight() * 0.7), 1,
		                Color.WHITE, SHADOW_COLOR.darker());*/
        	}
        }

        String mscaText = "TOF Overlay v" + MainDriver.VERSION;
        /*drawNormalText(g2d, mscaText, Tooltip.TEXT_SIZE * 3 / 4, (int)getSize().getWidth() - 120,
        		(int)getSize().getHeight() - 20, 1,
                Color.WHITE, SHADOW_COLOR.darker());*/
        drawNormalText(g2d, mscaText + " <3 Maygi", Tooltip.TEXT_SIZE * 3 / 4, 20,
        		(int)getSize().getHeight() - 5, 1,
                Color.WHITE, SHADOW_COLOR.darker());
        drawImage(theGraphics, "images/ui/avi.gif", (int)getSize().getWidth() - 40, (int)getSize().getHeight() - 40);
        
        
        
    	/*for (TrackPoint tp : MainDriver.data.keySet()) {
    		DataCollection dc = MainDriver.data.get(tp);
    		String text = "";
    		if (tp.getName().equalsIgnoreCase("HP") || tp.getName().contains("Infernog") || tp.getName().contains("Wings"))
    			continue;
    		if (dc instanceof HitMissCollection) {
    			if (!MainDriver.active)
    				text = tp.getIntro();
    			else
    				text = tp.getName() + " "+dc.getLast()+"%";
    		} else {
    			if (!MainDriver.active)
    				text = tp.getIntro();
    			else
    				text = tp.getName() + " "+dc.getLast();    			
    		}
            line++;
            drawImage(theGraphics, "images/tableicons/partydps.png", MARGIN,  (int) (getSize().getHeight() * TEXT_PERCENT) * line - 20);
            drawStrongText(g2d, text, FONT_SIZE, 24 + MARGIN * 2,
                    (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
                    Color.WHITE, SHADOW_COLOR.darker());
    	}*/
        for (final Particle p : myParticles) {
            p.draw(g2d);
        }
        for (final GuiButton b : BUTTONS) {
        	if (b.getImage().contains("update")) {
        		if (MainDriver.started || !VersionCheck.needsUpdate())
        			continue;
        	}
        	b.handleDraw(this, theGraphics);
        }
        ((OverlayFrame)myFrame).drawTooltips(theGraphics, this);
    }
}
