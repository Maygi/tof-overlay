package particles;

import gui.AbstractLabel;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A class that represents an shape particle.
 * @author May
 */
public abstract class AbstractShapeParticle extends Particle {

    /**
     * The radius of the particle.
     */
    protected final int myRadius;
    
    /**
     * Constructs a  shape particle.
     * @param theLabel A reference to the label the particle is drawn on, used for scaling.
     * @param theX The X to draw at.
     * @param theY The Y to draw at.
     * @param theMaxHSpeed The maximum horizontal speed.
     * @param theMinHSpeed The minimum horizontal speed.
     * @param theMaxVSpeed The maximum vertical speed.
     * @param theMinVSpeed The minimum vertical speed.
     * @param theFriction The friction coefficient.
     * @param theGravity The gravity of the particle.
     * @param theLife The life of the particle.
     * @param theColor The color of the particle.
     * @param theRadius The radius of the particle.
     */
    protected AbstractShapeParticle(final AbstractLabel theLabel,
                          final int theX, final int theY,
                          final double theMaxHSpeed, final double theMinHSpeed,
                          final double theMaxVSpeed, final double theMinVSpeed,
                          final double theFriction, 
                          final double theGravity, final int theLife, final Color theColor,
                          final int theRadius) {
        super(theLabel, theX, theY, theMaxHSpeed, theMaxVSpeed, theFriction, theLife);
        myMinHSpeed = theMinHSpeed;
        myMinVSpeed = theMinVSpeed;
        myMaxHSpeed = theMaxHSpeed;
        myMaxVSpeed = theMaxVSpeed;
        myColor = theColor;
        myRadius = theRadius;
        myGravity = theGravity;
        randomizeSpeed();
    }
    
    /**
     * Draw the graphics, using the configuration set up in the superclass.
     * @param theGraphics The graphics reference.
     */
    @Override
    public void draw(final Graphics2D theGraphics) {
        super.draw(theGraphics);
        theGraphics.setColor(myColor);
    }

}
