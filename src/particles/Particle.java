package particles;

import gui.AbstractLabel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A class that represents a  particle.
 * @author May
 */
public class Particle implements Cloneable {
    
    /**
     * The X position.
     */
    protected int myX;
    
    /**
     * The Y position.
     */
    protected int myY;

    /**
     * The maximum horizontal speed.
     */
    protected double myMaxHSpeed;
    
    /**
     * The minimum horizontal speed.
     */
    protected double myMinHSpeed;

    /**
     * The maximum vertical speed.
     */
    protected double myMaxVSpeed;

    /**
     * The minimum vertical speed.
     */
    protected double myMinVSpeed;
    
    /**
     * The friction coefficient. Each tick, speed will get this much closer to 0, capped at 0.
     */
    protected double myFriction; 
    
    /**
     * The gravity amount. Each time, vertical speed will change by the gravity amount.
     */
    protected double myGravity;
    
    /**
     * The color of the particle. Not relevant for image particles.
     */
    protected Color myColor;
    
    /**
     * The starting life. 
     */
    protected int myMaxLife;
    
    /**
     * The life. After running out of life, the image is removed.
     */
    protected int myLife;
    
    /**
     * The current amount of ticks the particle has lived.
     */
    protected int mySteps;
    
    /**
     * The horizontal speed.
     */
    private double myHSpeed;
    
    /**
     * The vertical speed.
     */
    private double myVSpeed;
    
    /**
     * The amount of ticks it takes for the particle to fade away.
     * A value of 0 denotes that the particle does not fade.
     */
    private int myFadeOutTicks;
    
    /**
     * The amount of ticks it takes for the particle to fade in.
     * A value of 0 denotes that the particle does not fade.
     */
    private int myFadeInTicks;
    
    /**
     * A reference to the label the particle is drawn on, used for scaling.
     */
    private final AbstractLabel myLabel;
    
    /**
     * Constructs a particle.
     * @param theX The X to draw at.
     * @param theY The Y to draw at.
     * @param theHSpeed The horizontal speed.
     * @param theVSpeed The vertical speed.
     * @param theFriction The friction coefficient.
     * @param theLife The life of the particle.
     */
    public Particle(final AbstractLabel theLabel,
                          final int theX, final int theY, final double theHSpeed,
                          final double theVSpeed, 
                          final double theFriction, final int theLife) {
        myLabel = theLabel;
        myX = theX;
        myY = theY;
        myHSpeed = theHSpeed;
        myVSpeed = theVSpeed;
        myFriction = theFriction;
        myLife = theLife;
        myMaxLife = myLife;
        myFadeOutTicks = 0;
        myFadeInTicks = 0;
        mySteps = 0;
    }
    
    /**
     * Sets the amount of fade ticks for the particle to fade away.
     * If fade ticks are greater than the particle life, then the particle will start
     * partially transparent.
     * @param theTicks The amount of ticks it takes for the particle to fade.
     */
    public void setFadeOutTicks(final int theTicks) {
        myFadeOutTicks = theTicks;
    }
    
    /**
     * Sets the amount of fade ticks for the particle to fade in.
     * @param theTicks The amount of ticks it takes for the particle to fade.
     */
    public void setFadeInTicks(final int theTicks) {
        myFadeInTicks = theTicks;
    }
    
    /**
     * Returns the scale factor.
     * @return The scale factor.
     */
    public double getScaleFactor() {
        return myLabel.getScaleFactor();
    }

    /**
     * The process method, called every tick by the particle timer.
     */
    public void process() {
        myX += myHSpeed;
        myY += myVSpeed;
        myVSpeed += myGravity;
        if (myHSpeed > 0) {
            myHSpeed = Math.max(0, myHSpeed - myFriction);
        } else if (myHSpeed < 0) {
            myHSpeed = Math.min(0, myHSpeed + myFriction);
        }
        if (myVSpeed > 0) {
            myVSpeed = Math.max(0, myVSpeed - myFriction);
        } else if (myVSpeed < 0) {
            myVSpeed = Math.min(0, myVSpeed + myFriction);
        }
        myLife--;
        mySteps++;
    }
    
    /**
     * Draws the particle on the given graphics reference.
     * @param theGraphics The graphics reference.
     */
    public void draw(final Graphics2D theGraphics) {
        if (myLife < myFadeOutTicks) {
            theGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                        (float) ((double) myLife / (double) myFadeOutTicks)));
        } else if (mySteps < myFadeInTicks) {
            theGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                        (float) ((double) mySteps / (double) myFadeInTicks)));
        } else {
            theGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }
    }
    
    /**
     * Returns whether or not the particle is ready for deletion.
     * @return Whether or not the particle is ready for deletion.
     */
    public boolean canDelete() {
        return myLife <= 0;
    }
    
    /**
     * Clones the particle.
     * @return A clone of the particle.
     * @throws CloneNotSupportedException If the clone is not supported.
     */
    public Particle clone() throws CloneNotSupportedException {
        return (Particle) super.clone();
    }
    
    /**
     * A helper method to randomize the speed of the particle.
     * Should not be called on image particles.
     */
    protected final void randomizeSpeed() {
        myHSpeed = myMinHSpeed + (int) (Math.random() * ((myMaxHSpeed - myMinHSpeed) + 1));
        myVSpeed = myMinVSpeed + (int) (Math.random() * ((myMaxVSpeed - myMinVSpeed) + 1));
    }
    
    /**
     * Repositions the particle to a new position.
     * Used by emitters, and saves the trouble of having to save all the individual
     * fields as it can just clone the base particle and reposition it relatively.
     * @param theX The X coordinate to move to.
     * @param theY The Y coordinate to move to.
     */
    protected final void reposition(final int theX, final int theY) {
        myX = theX;
        myY = theY;
    }
}
