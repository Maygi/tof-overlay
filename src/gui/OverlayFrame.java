package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;

import model.MainDriver;
import sound.Sound;
import util.VersionCheck;

/**
 * The main JFrame that contains the chart and handles several mouse events.
 * @author May
 */
public class OverlayFrame extends AbstractFrame {
	
	private static final boolean STREAM_MODE = false;
	
	public static boolean compact = false;
	
	private final SpringLayout myLayout;
	
	private final Overlay myChart;
    
	private static final long serialVersionUID = -2095870715941752227L;
	
	private int x;
    private int y;
    
    public static final int WIDTH = 200;
    public static final int HEIGHT = 260;
    public static final int COMPACT_HEIGHT = 80;
    
    private boolean hover = false;

    public OverlayFrame() {
        super("");
        //setIconImage((new ImageIcon("images/ui/avi.gif")).getImage());
        setPreferredSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        myLayout = new SpringLayout();
        myChart = new Overlay(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, compact ? COMPACT_HEIGHT : HEIGHT);
    }
    
    /**
     * Loads various properties from the property file.
     */
    private void loadProps() {
        try {
        	int propX = Integer.parseInt(MainDriver.props.getProperty("x"));
        	int propY = Integer.parseInt(MainDriver.props.getProperty("y"));
        	if (propX < 0 || propY < 0) {
            	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            	int newX = (int) (screenSize.getWidth() / 2);
            	int newY = (int) (screenSize.getHeight() / 2);
        		setLocation(newX, newY);
        	} else {
        		setLocation(propX, propY);
        	}
        } catch (Exception e) {
        	System.err.println("Unable to load properties on Overlay.");
        }
    }
    
    public void openReport() {
    	GraphFrame report = new GraphFrame();
    	report.start();
    	Sound.SELECT.play();
    }
    
    public void openUpdate() {
    	try {
			java.awt.Desktop.getDesktop().browse(new URI("https://github.com/Maygi/tof-overlay/releases"));
		} catch (IOException | URISyntaxException e) {
			JOptionPane.showMessageDialog(this,
				    "Unable to open website. Please manually navigate to https://github.com/Maygi/tof-overlay/releases to download it!",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
    }
    
    public void start() {
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	x = (int) (screenSize.getWidth() / 2);
    	y = (int) (screenSize.getHeight() / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);
        setFocusableWindowState(false);
        
        if (!STREAM_MODE) {
	        setUndecorated(true);
	        setBackground(new Color(0, 0, 0, 0));
        }
        
        setAlwaysOnTop(true);
        
        
        getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
        setLocationRelativeTo(null);
        setVisible(true);
        addKeyListener(new KeyboardListener());
    	loadProps();
        JFrame that = this;
        addMouseListener(new MouseAdapter(){
           public void mousePressed(MouseEvent ev) {
	            int x = ev.getX();
	            int y = ev.getY();
            	int offX = (that.getWidth() - WIDTH) / 2;
            	int offY = (that.getHeight() - HEIGHT) / 2;
            	x -= offX;
            	y -= offY;
            	if (STREAM_MODE)
            		y -= 15; //for undecorated
	            for (final GuiButton b : GuiButton.class.getEnumConstants()) {
	            	int[] coords = b.getCoords();
	            	if (compact) //we need this for click for some reason
	            		coords[1] += OverlayFrame.HEIGHT / 2 - OverlayFrame.COMPACT_HEIGHT / 2;
					if (System.currentTimeMillis() - MainDriver.lastActivity <= 15000 && !ev.isShiftDown()) {
						MainDriver.setClickAttempt();
						break;
					}
	                if (x >= coords[0] && x <= coords[0] +  b.getWidth()  &&
	                		y >= coords[1] && y <= coords[1] + b.getHeight()) {
	                	if (b.getImage().contains("report")) {
	                		openReport();
	                	}
	                    if (b.getImage().contains("update") && VersionCheck.needsUpdate()) {
	                    	openUpdate();
	                    }
	                	if (b.getImage().contains("mute")) {
	                		MainDriver.toggleMute();
	                	}
	                	if (b.getImage().contains("pause")) {
	                		MainDriver.pause();
	                	}
	                	if (b.getImage().contains("reset")) {
	                		MainDriver.reset();
	                	}
	                	if (b.getImage().contains("minimize")) {
	                		compact = true;
	                		Sound.SELECT.play();
	                        setMinimumSize(getPreferredSize());
	                        setSize(getPreferredSize());
	                		//setLocation(that.getX(), that.getY() + (HEIGHT - COMPACT_HEIGHT));
	                        refresh();
	                	} else if (b.getImage().contains("maximize")) {
	                		compact = false;
	                		Sound.SELECT.play();
	                        setMinimumSize(getPreferredSize());
	                        setSize(getPreferredSize());
	                		//setLocation(that.getX(), that.getY() - (HEIGHT - COMPACT_HEIGHT));
	                        refresh();
	                	}
	                	if (b.getImage().contains("close")) {
	                		MainDriver.saveProps();
	                		Sound.SELECT.play();
	                		System.exit(0);
	                	}
	                }
	            }
           }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
        	public void mouseMoved(MouseEvent ev) {
        		hoveredTooltip = null;
	            x = ev.getX();
	            y = ev.getY();
	            boolean hit = false;
	            for (final GuiButton b : GuiButton.class.getEnumConstants()) {
	            	int[] coords = b.getCoords();
	                if (x >= coords[0] && x <= coords[0] + b.getWidth() &&
	                		y >= coords[1] && y <= coords[1] + b.getHeight()) {
	                	myChart.repaint();
	                	hover = true;
	                	hit = true;
	                }
	            }
	            for (final Tooltip tooltip : tooltipMappings.keySet()) {
	                if (tooltip.checkCollision(x, y)) {
	                	hoveredTooltip = tooltip;
	                	if (!hover) {
	                    	//Sound.HOVER.play();
	                	}
	                	myChart.repaint();
	                	hover = true;
	                	hit = true;
	                	break;
	                }
	            }
	            if (!hit) {
	            	hover = false;
	            	if (hoveredTooltip == null)
	            		myChart.repaint();
	            }
        	}
            public void mouseDragged(MouseEvent evt) {
                int x1 = evt.getXOnScreen() - x;
                int y1 = evt.getYOnScreen() - y;
				if (System.currentTimeMillis() - MainDriver.lastActivity >= 15000) { //move if no activity for 15s
					setLocation(x1, y1);
					MainDriver.saveWindowPosition(x1, y1);
				}
	            //System.out.println("Move: "+x1+", "+y1);

            }
        });

        setupFrame();
        pack();
        
        setLayout(myLayout);
    }
    private void setupFrame() {

        final Container container = getContentPane();

        myLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, myChart, 0, 
                               SpringLayout.HORIZONTAL_CENTER,
                               container);
        myLayout.putConstraint(SpringLayout.VERTICAL_CENTER, myChart, 0,
                             SpringLayout.VERTICAL_CENTER, container);
        add(myChart);
    }
    public void refresh() {
    	/*getContentPane().removeAll();
    	for (TrackPoint tp : MainDriver.data.keySet()) {
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
    		getContentPane().add(new JTextArea(text));
    	}*/
        setVisible(true);
    	revalidate();
    	repaint();
    }
    
    /**
     * An inner class to handle keyboard events.
     */
    private class KeyboardListener implements KeyListener {

        /**
         * Handles the pressing of a key.
         */
        @Override
        public void keyPressed(final KeyEvent theEvent) {
            final int key = theEvent.getKeyCode();
            if (key == KeyEvent.VK_PAGE_DOWN) {
            	MainDriver.reset();
            }
            if (key == KeyEvent.VK_P) {
            	MainDriver.pause();
            }
        }

        @Override
        public void keyReleased(final KeyEvent theEvent) {
        }

        @Override
        public void keyTyped(final KeyEvent theEvent) {
        }
        
    }
}
