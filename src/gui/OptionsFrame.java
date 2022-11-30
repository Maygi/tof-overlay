package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

import model.MainDriver;
import sound.Sound;

public class OptionsFrame extends AbstractFrame {
	
	private static final long serialVersionUID = 3067281801758591724L;
	
	private final SpringLayout myLayout;
	private int x;
    private int y;
    
    private Options myOptions;
    private boolean hover = false;
    
    public OptionsFrame() {
        super("Options");
        //setIconImage((new ImageIcon("images/ui/avi.gif")).getImage());
        setPreferredSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        myLayout = new SpringLayout();
        myOptions = new Options(this, 1, 1);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
    
    public void start() {
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	x = (int) (screenSize.getWidth() / 2);
    	y = (int) (screenSize.getHeight() / 2);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setAutoRequestFocus(true);
        //setFocusableWindowState(false);
        
        getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
        setLocationRelativeTo(null);
        setVisible(true);

        setupFrame();
        pack();
        

        
        addMouseListener(new MouseAdapter(){
           public void mousePressed(MouseEvent ev) {
	            x = ev.getX();
	            y = ev.getY() - 15; //not sure why we need this offset o-o but it makes the click more accurate
	            for (final GuiButton b : GuiButton.class.getEnumConstants()) {
	            	int[] coords = b.getCoords();
	                if (x >= coords[0] && x <= coords[0] +  b.getWidth()  &&
	                		y >= coords[1] && y <= coords[1] + b.getHeight()) {
	                		
	                	resetTooltips();
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
	                	if (!hover) {
	                    	if (b.getImage().contains("pause") && !MainDriver.active) {
	                    		//discrete logic (^:
	                    	} else {
	                    		Sound.HOVER.play();	                    		
	                    	}
	                	}
	                	myOptions.repaint();
	                	hover = true;
	                	hit = true;
	                }
	            }
	            for (final Tooltip tooltip : tooltipMappings.keySet()) {
	                if (tooltip.checkCollision(x, y)) {
	                	hoveredTooltip = tooltip;
	                	if (!hover) {
	                    	Sound.HOVER.play();
	                	}
	                	myOptions.repaint();
	                	hover = true;
	                	hit = true;
	                	break;
	                }
	            }
	            if (!hit) {
	            	hover = false;
	            	if (hoveredTooltip == null)
	                	myOptions.repaint();
	            }
        	}
            public void mouseDragged(MouseEvent evt) {
                int x1 = evt.getXOnScreen() - x;
                int y1 = evt.getYOnScreen() - y;
                //setLocation(x1, y1);
	            //System.out.println("Move: "+x1+", "+y1);

            }
        });
        
        setLayout(myLayout);
    }
    
    private void setupFrame() {

        final Container container = getContentPane();

        container.setBackground(new Color(255, 248, 250));
        myLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, myOptions, 0, 
                               SpringLayout.HORIZONTAL_CENTER,
                               container);
        myLayout.putConstraint(SpringLayout.VERTICAL_CENTER, myOptions, 0,
                             SpringLayout.VERTICAL_CENTER, container);
        add(myOptions);
    }
}
