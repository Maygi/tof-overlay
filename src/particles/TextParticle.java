package particles;

import gui.AbstractLabel;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * A class that represents a text particle.
 * @author May
 */
public class TextParticle extends Particle {

    /**
     * The default font size.
     */
    private static final int FONT_SIZE = 40;
    
    /**
     * The initial emphasis.
     */
    private static final double INITIAL_EMPHASIS = 2.0;
    
    /**
     * The emphasis decay.
     */
    private static final double EMPHASIS_DECAY = 0.1;
    
    /**
     * The text to draw.
     */
    private final String myText;
    
    /**
     * A multiplier that increases the size of the text.
     */
    private double myEmphasis;
    
    /**
     * A reference to the panel, to use the draw centered text method.
     */
    private final AbstractLabel myPanel;
    
    /**
     * The font size.
     */
    private final int mySize;
    
    /**
     * Constructs a text particle.
     * @param theLabel A reference to the label the particle is drawn on, used for scaling.
     * @param theX The X to draw at.
     * @param theY The Y to draw at.
     * @param theHSpeed The horizontal speed.
     * @param theVSpeed The vertical speed.
     * @param theFriction The friction coefficient.
     * @param theLife The life of the particle.
     * @param theText The text to draw.
     * @param thePanel The reference to the label.
     */
    public TextParticle(final AbstractLabel theLabel, final int theX, final int theY, 
                          final double theHSpeed, final double theVSpeed, 
                          final double theFriction, final int theLife,
                          final String theText,
                          final AbstractLabel thePanel) {
        this(theLabel, theX, theY, theHSpeed, theVSpeed, theFriction, theLife, theText, 
             thePanel, FONT_SIZE);
    }

    /**
     * Constructs a text particle with the given size.
     * @param theLabel A reference to the label the particle is drawn on, used for scaling.
     * @param theX The X to draw at.
     * @param theY The Y to draw at.
     * @param theHSpeed The horizontal speed.
     * @param theVSpeed The vertical speed.
     * @param theFriction The friction coefficient.
     * @param theLife The life of the particle.
     * @param theText The text to draw.
     * @param thePanel The reference to the label.
     * @param theSize The size of the font.
     */
    public TextParticle(final AbstractLabel theLabel, final int theX, final int theY, 
                          final double theHSpeed, final double theVSpeed, 
                          final double theFriction, final int theLife,
                          final String theText,
                          final AbstractLabel thePanel,
                          final int theSize) {
        super(theLabel, theX, theY, theHSpeed, theVSpeed, theFriction, theLife);
        myText = theText;
        myPanel = thePanel;
        mySize = theSize;
    }
    
    /**
     * Emphasizes the text, making it bigger. It will shrink naturally over time.
     */
    public void emphasize() {
        myEmphasis = INITIAL_EMPHASIS;
    }

    /**
     * Draw the graphics, using the configuration set up in the superclass.
     * @param theGraphics The graphics reference.
     */
    @Override
    public void draw(final Graphics2D theGraphics) {
        super.draw(theGraphics);
        final Rectangle bound = theGraphics.getClipBounds();
        myPanel.drawAlignedText(theGraphics, myText, mySize, (double) myX / bound.width,
                                (double) myY / bound.height, myEmphasis);
    }
    
    /**
     * Updates the emphasis amount.
     */
    @Override
    public void process() {
        super.process();
        myEmphasis = Math.max(1,  myEmphasis - EMPHASIS_DECAY);
    }

}
