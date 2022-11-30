
package particles;

import gui.AbstractLabel;

import java.awt.Graphics2D;


/**
 * A class that represents a particle emitter, which is a particle that creates other
 * particles each tick.
 * @author May
 */
public class ParticleEmitter extends Particle {

    /**
     * A reference to the panel the emitter was created on, in order to determine where to add
     * the new particles.
     */
    private final AbstractLabel myPanel;
    
    /**
     * The Particle that this emitter produces.
     */
    private final Particle myParticle;

    /**
     * Constructs a  particle emitter.
     * @param theLabel A reference to the label the particle is drawn on, used for scaling.
     * @param theX The X to draw at.
     * @param theY The Y to draw at.
     * @param theHSpeed The horizontal speed.
     * @param theVSpeed The vertical speed.
     * @param theFriction The friction coefficient.
     * @param theLife The life of the particle.
     * @param theParticle The particle that is produced by the emitter.
     */
    public ParticleEmitter(final AbstractLabel theLabel,
                                 final int theX, final int theY, final double theHSpeed,
                                 final double theVSpeed, final double theFriction, 
                                 final int theLife, final Particle theParticle) {
        super(theLabel, theX, theY, theHSpeed, theVSpeed, theFriction, theLife);
        myParticle = theParticle;
        myPanel = theLabel;
    }
    
    /**
     * Creates a new particle.
     */
    @Override
    public void process() {
        final Particle p;
        try {
            p = myParticle.clone();
            p.randomizeSpeed();
            p.reposition(myX, myY);
            myPanel.addParticle(p);
        } catch (final CloneNotSupportedException e) {
            e.printStackTrace();
        }
        super.process();
    }
    
    /**
     * Emitters don't draw anything.
     * @param theGraphics The graphics reference.
     */
    @Override
    public void draw(final Graphics2D theGraphics) {
        //nothing!
    }

}
