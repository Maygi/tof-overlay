
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

import particles.Particle;

/**
 * A parent class of most UI elements that supports dynamic scaling.
 * @author May
 */
public abstract class AbstractLabel extends JLabel {

    /**
     * The font size.
     */
    protected static final int FONT_SIZE = 24;

    /**
     * The offset coefficient for the text.
     * 0.1 = 10% of the panel's height.
     */
    protected static final double TEXT_OFFSET = 0.2;

    /**
     * The font color.
     */
    protected static final Color SHADOW_COLOR = new Color(201, 102, 135);
    
    /**
     * The base color of the side panels.
     */
    protected static final Color BASE_COLOR = new Color(255, 181, 205);
    
    /**
     * The amount of fade ticks on the image pop-ups.
     */
    protected static final int IMAGE_FADE_TICKS = 25;
    
    /**
     * The amount of fade ticks on the particles.
     */
    protected static final int PARTICLE_FADE_TICKS = 30;
    
    /**
     * The delay between animation frames of particle movement.
     */
    protected static final int PARTICLE_DELAY = 10;
    
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 7700275356523174681L;
    
    /**
     * The thickness of the top shade.
     * A thickness of X means it is as thick as the 1/Xth of the block.
     */
    private static final double TOP_SHADE_THICKNESS = 12.0;
    
    /**
     * The thickness of the bottom shade.
     * A thickness of X means it is as thick as the 1/Xth of the block.
     */
    private static final double BOTTOM_SHADE_THICKNESS = 8.0;
    
    /**
     * The opacity of the film.
     */
    private static final float FILM_OPACITY = 0.5f;
    
    /**
     * The X percent scaling.
     */
    protected double myXPerc;
    
    /**
     * The Y percent scaling.
     */
    protected double myYPerc;

    /**
     * A reference to the main JFrame.
     */
    protected final JFrame myFrame;
    
    /**
     * The particle collection.
     * Particles will be drawn in the local class, not in the super class, as to allow
     * flexibility in choosing where they appear in terms of order (e.g. behind or in
     * front of other elements).
     */
    protected final List<Particle> myParticles;
    
    /**
     * The queue of particles to add, as to avoid ConcurrentModificationExceptions.
     */
    protected final Queue<Particle> myParticleQueue;
    
    /**
     * The initial X width of the frame, used for scaling.
     */
    private final int myInitialWidth;
    
    /**
     * The initial Y height of the frame, used for scaling.
     */
    private final int myInitialHeight;

    /**
     * Initializes the X and Y percent scaling.
     * @param theFrame The main frame reference.
     * @param theXPerc The X percent scaling.
     * @param theYPerc The Y percent scaling.
     */
    protected AbstractLabel(final JFrame theFrame, 
                            final double theXPerc, final double theYPerc) {
        super();
        myFrame = theFrame;
        myXPerc = theXPerc;
        myYPerc = theYPerc;
        myInitialWidth = (int) (theXPerc * theFrame.getWidth());
        myInitialHeight = (int) (theYPerc * theFrame.getHeight());
        updateSize(theFrame.getSize());
        
        myParticles = new ArrayList<Particle>();
        myParticleQueue = new LinkedList<Particle>();
    }
    
    /**
     * Paints the text, in addition to the super drawing components.
     * @param theGraphics The graphics context to use for painting.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    /**
     * Draws a 50% transparent film over the window.
     * @param theGraphics The graphics reference.
     * @param theColor The color of the film.
     */
    protected final void drawFilm(final Graphics theGraphics, final Color theColor) {
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, FILM_OPACITY));
        g2d.setColor(theColor);
        g2d.fillRect(0, 0, (int) getSize().getWidth(), (int) getSize().getHeight());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }
    
    /**
     * Change the frame's percentages of the main frame, and updates the size.
     * @param theXPerc The X percent scaling.
     * @param theYPerc The Y percent scaling.
     */
    protected final void changeFramePercents(final double theXPerc, final double theYPerc) {
        myXPerc = theXPerc;
        myYPerc = theYPerc;
        updateSize(getSize());
    }
    
    /**
     * Updates the size based on the main frame's size.
     * @param theDimension The dimension of the main frame.
     */
    protected final void updateSize(final Dimension theDimension) {
        Dimension proposedDim = new Dimension((int) (theDimension.getWidth() * myXPerc),
                                              (int) (theDimension.getHeight() * myYPerc));
        final double ratio = myXPerc / myYPerc; //todo... fix...
        final double newRatio = proposedDim.getWidth() / proposedDim.getHeight();
        /*if (newRatio > ratio) { //new ratio is greater - it is relatively wider
            proposedDim = new Dimension((int) (proposedDim.getWidth() * ratio / newRatio), 
                                        (int) proposedDim.getHeight());
        } else if (newRatio < ratio) { //new ratio is smaller - it is relatively taller
            proposedDim = new Dimension((int) proposedDim.getWidth(), 
                                        (int) (proposedDim.getHeight() * newRatio / ratio));
        }*/
        setPreferredSize(proposedDim);
        updateUI();
    }
    
    /**
     * Draws text aligned within the graphical bounds to the given percentages.
     * @param theGraphics The graphics reference.
     * @param theText The text to draw.
     * @param theSize The size to draw the text at.
     * @param theXPerc The X percent to draw at. 50% = center
     * @param theYPerc The Y percent to draw at. 50% = center
     * @param theEmphasis The bonus to size. (1.5 = 150% size)
     */
    public void drawAlignedText(final Graphics theGraphics, final String theText, 
                                final int theSize, final double theXPerc,
                                final double theYPerc, final double theEmphasis) {
        final Rectangle bound = theGraphics.getClipBounds();
        final Font scaledFont = myFrame.getFont().deriveFont((float) (theSize * theEmphasis
                        * getScaleFactor()));
        theGraphics.setFont(scaledFont);
        final FontMetrics fm = theGraphics.getFontMetrics();
        final int drawX = (int) ((bound.width - fm.stringWidth(theText)) * theXPerc);
        final int drawY = (int) (bound.height * theYPerc);
        drawText(theGraphics, theText, theSize, drawX, drawY, theEmphasis);
    }

    /**
     * Draws text at a certain X and Y position.
     * @param theGraphics The graphics reference.
     * @param theText The text to draw.
     * @param theSize The size to draw the text at.
     * @param theX The X coordinate to draw at.
     * @param theY The Y coordinate to draw at.
     * @param theEmphasis The bonus to size. (1.5 = 150% size)
     */
    public void drawText(final Graphics theGraphics, final String theText, 
                                final int theSize, final int theX,
                                final int theY, final double theEmphasis) {
        drawText(theGraphics, theText, theSize, theX, theY, theEmphasis, Color.WHITE,
                 Color.BLACK);
    }

    /**
     * Draws colored text at the certain X and Y position.
     * @param theGraphics The graphics reference.
     * @param theText The text to draw.
     * @param theSize The size to draw the text at.
     * @param theX The X coordinate to draw at.
     * @param theY The Y coordinate to draw at.
     * @param theEmphasis The bonus to size. (1.5 = 150% size)
     * @param theColor The color to draw at.
     * @param theShadow The shadow color.
     */
    public void drawText(final Graphics theGraphics, final String theText, 
                                final int theSize, final int theX,
                                final int theY, final double theEmphasis,
                                final Color theColor, final Color theShadow) {
        final Font scaledFont = myFrame.getFont().deriveFont((float) (theSize * theEmphasis
                        * getScaleFactor()));
        theGraphics.setFont(scaledFont);
        theGraphics.setColor(theShadow);
        theGraphics.drawString(theText, theX + 2, theY + 2);
        theGraphics.setColor(theColor);
        theGraphics.drawString(theText, theX, theY);
    }

    /**
     * Draws colored text at the certain X and Y position.
     * The shadow for this text is less offset than normal, making it more slim.
     * @param theGraphics The graphics reference.
     * @param theText The text to draw.
     * @param theSize The size to draw the text at.
     * @param theX The X coordinate to draw at.
     * @param theY The Y coordinate to draw at.
     * @param theEmphasis The bonus to size. (1.5 = 150% size)
     * @param theColor The color to draw at.
     * @param theShadow The shadow color.
     */
    public void drawNormalText(final Graphics theGraphics, final String theText, 
                                final int theSize, final int theX,
                                final int theY, final double theEmphasis,
                                final Color theColor, final Color theShadow) {
        final Font scaledFont = myFrame.getFont().deriveFont((float) (theSize * theEmphasis
                        * getScaleFactor()));
        theGraphics.setFont(scaledFont.deriveFont(Font.BOLD));
        theGraphics.setColor(theShadow);
        theGraphics.drawString(theText, theX + 1, theY + 1);
        theGraphics.setColor(theColor);
        theGraphics.drawString(theText, theX, theY);
    }

    /**
     * Draws colored text at the certain X and Y position, center aligned.
     * The shadow for this text is less offset than normal, making it more slim.
     * @param theGraphics The graphics reference.
     * @param theText The text to draw.
     * @param theSize The size to draw the text at.
     * @param theX The X coordinate to draw at.
     * @param theY The Y coordinate to draw at.
     * @param theEmphasis The bonus to size. (1.5 = 150% size)
     * @param theColor The color to draw at.
     * @param theShadow The shadow color.
     */
    public void drawNormalTextCentered(final Graphics theGraphics, final String theText, 
                                final int theSize, final int theX,
                                final int theY, final double theEmphasis,
                                final Color theColor, final Color theShadow) {
        final Font scaledFont = myFrame.getFont().deriveFont((float) (theSize * theEmphasis
                * getScaleFactor()));
        theGraphics.setFont(scaledFont.deriveFont(Font.BOLD));
        FontMetrics metrics = theGraphics.getFontMetrics();
        theGraphics.setColor(theShadow);
        theGraphics.drawString(theText, theX - metrics.stringWidth(theText) / 2 + 1, theY + 1);
        theGraphics.setColor(theColor);
        theGraphics.drawString(theText, theX - metrics.stringWidth(theText) / 2, theY);
    }

    /**
     * Adds the given particle to the queue.
     * @param theParticle The particle to add.
     */
    public void addParticle(final Particle theParticle) {
        myParticleQueue.add(theParticle);
    }
    
    /**
     * Returns the scale factor.
     * @return The scale factor of the label, and its components.
     */
    public double getScaleFactor() {
        return Math.min((double) getWidth() / myInitialWidth,
                        (double) getHeight() / myInitialHeight);
    }
    
    /**
     * Draws a shaded square.
     * @param theGraphics The graphics reference.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     * @param theWidth The width and height of the square.
     * @param theColor The color of the square.
     */
    protected void drawSquare(final Graphics theGraphics, final int theX, final int theY,
                            final int theWidth, final Color theColor) {
        theGraphics.setColor(theColor);
        theGraphics.fillRect(theX, theY, theWidth, theWidth);
        theGraphics.setColor(theColor.brighter());
        theGraphics.fillRect(theX, theY, theWidth, 
                             (int) (theWidth / TOP_SHADE_THICKNESS)); //top shade
        theGraphics.fillRect(theX, theY,
                             (int) (theWidth / TOP_SHADE_THICKNESS), theWidth); //left shade
        theGraphics.setColor(theColor.darker());
        theGraphics.fillRect(theX, theY + theWidth
                             - (int) (theWidth / BOTTOM_SHADE_THICKNESS),
                         theWidth, (int) (theWidth / BOTTOM_SHADE_THICKNESS)); //bottom shade
        theGraphics.fillRect(theX + theWidth - (int) (theWidth / BOTTOM_SHADE_THICKNESS), 
                     theY, (int) (theWidth / BOTTOM_SHADE_THICKNESS), theWidth); //right shade
    }
    
    /**
     * Draws a shaded square at the given transparency.
     * @param theGraphics The graphics reference.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     * @param theWidth The width and height of the square.
     * @param theColor The color of the square.
     */
    protected void drawSquare(final Graphics theGraphics, final int theX, final int theY,
                            final int theWidth, final Color theColor, final float alpha) {
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        theGraphics.setColor(theColor);
        theGraphics.fillRect(theX, theY, theWidth, theWidth);
        theGraphics.setColor(theColor.brighter());
        theGraphics.fillRect(theX, theY, theWidth, 
                             (int) (theWidth / TOP_SHADE_THICKNESS)); //top shade
        theGraphics.fillRect(theX, theY,
                             (int) (theWidth / TOP_SHADE_THICKNESS), theWidth); //left shade
        theGraphics.setColor(theColor.darker());
        theGraphics.fillRect(theX, theY + theWidth
                             - (int) (theWidth / BOTTOM_SHADE_THICKNESS),
                         theWidth, (int) (theWidth / BOTTOM_SHADE_THICKNESS)); //bottom shade
        theGraphics.fillRect(theX + theWidth - (int) (theWidth / BOTTOM_SHADE_THICKNESS), 
                     theY, (int) (theWidth / BOTTOM_SHADE_THICKNESS), theWidth); //right shade
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }
    
    /**
     * Draws a shaded square at the given transparency.
     * @param theGraphics The graphics reference.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     * @param theWidth The width of the rectangle.
     * @param theHeight The height of the rectangle.
     * @param theColor The color of the square.
     */
    protected void drawRect(final Graphics theGraphics, final int theX, final int theY,
                            final int theWidth, final int theHeight, final Color theColor, final float alpha) {
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        theGraphics.setColor(theColor);
        theGraphics.fillRect(theX, theY, theWidth, theHeight);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    /**
     * Draws a shaded circle at the given transparency.
     * @param theGraphics The graphics reference.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     * @param theWidth The width of the circle.
     * @param theHeight The height of the circle.
     * @param theColor The color of the square.
     */
    protected void drawCircle(final Graphics theGraphics, final int theX, final int theY,
                            final int theWidth, final int theHeight, final Color theColor, final float alpha) {
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        theGraphics.setColor(theColor);
        theGraphics.fillOval(theX, theY, theWidth, theHeight);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    /**
     * Draws an arc.
     * @param theGraphics The graphics reference.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     * @param theWidth The width of the arc.
     * @param theHeight The height of the arc.
     * @param theDegrees The degree
     * @param theColor The color of the square.
     */
    protected void drawArc(final Graphics theGraphics, final int theX, final int theY,
                           final int theWidth, final int theHeight, final int theDegrees, final Color theColor, final int theThickness, final float alpha) {
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        theGraphics.setColor(theColor);
        Stroke s = ((Graphics2D) theGraphics).getStroke();
        ((Graphics2D) theGraphics).setStroke(new BasicStroke(theThickness));
        theGraphics.drawArc(theX, theY, theWidth, theHeight, 90, -1 * theDegrees);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        ((Graphics2D) theGraphics).setStroke(s);
    }

    /**
     * Draws an arc.
     * @param theGraphics The graphics reference.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     * @param theWidth The width of the arc.
     * @param theHeight The height of the arc.
     * @param theDegrees The degree
     * @param theColor The color of the square.
     */
    protected void drawArcReverse(final Graphics theGraphics, final int theX, final int theY,
                           final int theWidth, final int theHeight, final int theDegrees, final Color theColor, final int theThickness, final float alpha) {
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        theGraphics.setColor(theColor);
        Stroke s = ((Graphics2D) theGraphics).getStroke();
        ((Graphics2D) theGraphics).setStroke(new BasicStroke(theThickness));
        theGraphics.drawArc(theX, theY, theWidth, theHeight, 90 - theDegrees, -360 + theDegrees);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        ((Graphics2D) theGraphics).setStroke(s);
    }

    /**
     * Fills an arc.
     * @param theGraphics The graphics reference.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     * @param theWidth The width of the arc.
     * @param theHeight The height of the arc.
     * @param theDegrees The degree
     * @param theColor The color of the square.
     */
    protected void fillArc(final Graphics theGraphics, final int theX, final int theY,
                           final int theWidth, final int theHeight, final int theDegrees, final Color theColor, final int theThickness, final float alpha) {
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        theGraphics.setColor(theColor);
        Stroke s = ((Graphics2D) theGraphics).getStroke();
        ((Graphics2D) theGraphics).setStroke(new BasicStroke(theThickness));
        theGraphics.fillArc(theX, theY, theWidth, theHeight, 90, 1 * theDegrees);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        ((Graphics2D) theGraphics).setStroke(s);
    }
    
    /**
     * Draws an image at the given coordinates.
     * @param theGraphics The graphics reference.
     * @param theImageURL The URL of the image to draw.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     */
    protected void drawImage(final Graphics theGraphics, final String theImageURL, final int theX,
            final int theY) {
        final BufferedImage image;
        try {
            image = ImageIO.read(new File(theImageURL));
            final Image scaledImage = image.getScaledInstance((int) (image.getWidth()
                            * getScaleFactor()), (int) (image.getHeight() * getScaleFactor()),
                                                            Image.SCALE_DEFAULT);
            theGraphics.drawImage(scaledImage, theX, theY, null);
        } catch (final IOException e) {
        	System.err.println(theImageURL);
            e.printStackTrace();
        }
    }

    /**
     * Draws an image at the given coordinates.
     * @param theGraphics The graphics reference.
     * @param theImageURL The URL of the image to draw.
     * @param theX The x coordinate.
     * @param theY The y coordinate.
     */
    protected void drawImage(final Graphics theGraphics, final String theImageURL, final int theX,
                             final int theY, final double theScale) {
        final BufferedImage image;
        try {
            image = ImageIO.read(new File(theImageURL));
            final Image scaledImage = image.getScaledInstance((int) (image.getWidth()
                            * getScaleFactor() * theScale), (int) (image.getHeight() * getScaleFactor() * theScale),
                    Image.SCALE_DEFAULT);
            theGraphics.drawImage(scaledImage, theX, theY, null);
        } catch (final IOException e) {
            System.err.println(theImageURL);
            e.printStackTrace();
        }
    }
    
    /**
     * Draws a background image.
     * @param theGraphics The graphics reference.
     * @param theImageURL The URL of the image to draw.
     * @param theScaleFactor The scale factor.
     */
    protected void drawBackground(final Graphics theGraphics, final String theImageURL,
                                  final double theScaleFactor) {
        final BufferedImage image;
        try {
            image = ImageIO.read(new File(theImageURL));
            final Image scaledImage = image.getScaledInstance((int) (image.getWidth()
                            * theScaleFactor), (int) (image.getHeight() * theScaleFactor),
                                                            Image.SCALE_DEFAULT);
            theGraphics.drawImage(scaledImage, 0, 0, null);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * An inner protected class to handle particle updates.
     */
    protected class ParticleListener implements ActionListener {
    
        /**
         * Update the animation.
         * @param theEvent The event reference.
         */
        @Override
        public void actionPerformed(final ActionEvent theEvent) {
            Particle p;
            boolean flag = false;
            while (!myParticleQueue.isEmpty()) {
                myParticles.add(myParticleQueue.remove());
            }
            for (final Iterator<Particle> iterator = myParticles.iterator();
                            iterator.hasNext();) {
                p = iterator.next();
                if (p.canDelete()) {
                    iterator.remove();
                    flag = true;
                    continue;
                }
                p.process();
            }
            if (flag || !myParticles.isEmpty()) { //repaint if a particle was deleted
                repaint();
            }
        }
    }

}
