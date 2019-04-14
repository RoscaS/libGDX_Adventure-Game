package com.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Extends functionality of the LibGDX Actor class.
 * by adding support for textures/animation,
 * collision polygons, movement, world boundaries, and camera scrolling.
 * Most game objects should extend this class; lists of extensions can be retrieved by stage and class name.
 */
public class BaseActor extends Group {

    // Animation
    private Animation<TextureRegion> animation;
    private boolean animationPaused;
    private float elapsedTime;

    // Physics
    protected Vector2 velocityVec;
    protected Vector2 accelerationVec;
    private float acceleration;
    private float deceleration;
    private float maxSpeed;

    // Collision
    private Polygon boundaryPolygon;

    // Stores size of game world for all actors
    private static Rectangle worldBounds;

	/*------------------------------------------------------------------*\
	|*							Initialization							*|
	\*------------------------------------------------------------------*/

    public BaseActor(float x, float y, Stage s) {
        super();

        // perform additional initialization tasks
        setPosition(x, y);
        s.addActor(this);

        // initialize animation data
        animation = null;
        elapsedTime = 0;
        animationPaused = false;

        // initialize physics data
        velocityVec = new Vector2(0, 0);
        accelerationVec = new Vector2(0, 0);
        acceleration = 0;
        deceleration = 0;
        maxSpeed = 1000;

        boundaryPolygon = null;
    }

    /*------------------------------------------------------------------*\
   	|*							Static methods							*|
   	\*------------------------------------------------------------------*/

    /*------------------------------*\
   	|*				Getters			*|
   	\*------------------------------*/

    /**
     * Get world dimensions
     *
     * @return Rectangle whose width/height represent world bounds
     */
    public static Rectangle getWorldBounds() {
        return worldBounds;
    }
    /*------------------------------*\
   	|*				Setters			*|
   	\*------------------------------*/

    /**
     * Set world dimensions for use by methods boundToWorld() and scrollTo().
     *
     * @param width  width of world
     * @param height height of world
     */
    public static void setWorldBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }

    /**
     * Set world dimensions for use by methods boundToWorld() and scrollTo().
     *
     * @param ba whose size determines the world bounds (typically a background image)
     */
    public static void setWorldBounds(BaseActor ba) {
        setWorldBounds(ba.getWidth(), ba.getHeight());
    }

    /*------------------------------*\
   	|*				Tools			*|
   	\*------------------------------*/

    /**
     * Retrieves a list of all instances of the object from the given stage
     * with the given class name or whose class extends the class with the given name.
     * If no instances exist, returns an empty list.
     * Useful when coding interactions between different types of game objects in update method.
     *
     * @param stage     Stage containing BaseActor instances
     * @param className name of a class that extends the BaseActor class
     * @return list of instances of the object in stage which extend with the given class name
     */
    public static Array<BaseActor> getList(Stage stage, String className) {
        Array<BaseActor> actors = new Array<BaseActor>();
        Class theClass = null;
        try {
            theClass = Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Actor a : stage.getActors()) {
            if (theClass.isInstance(a)) {
                actors.add((BaseActor) a);
            }
        }
        return actors;
    }

    /**
     * Returns number of instances of a given class (that extends BaseActor).
     *
     * @param className name of a class that extends the BaseActor class
     * @return number of instances of the class
     */
    public static int count(Stage stage, String className) {
        return getList(stage, className).size;
    }

    /*------------------------------------------------------------------*\
	|*							Public Methods 							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Getters			*|
	\*------------------------------*/

    /**
     * Calculates the speed of movement (in pixels/second).
     *
     * @return speed of movement (pixels/second)
     */
    public float getSpeed() {
        return velocityVec.len();
    }

    /**
     * Get the angle of motion (in degrees), calculated from the velocity vector.
     * To align actor image angle with motion angle,
     * use <code>setRotation( getMotionAngle() )</code>.
     *
     * @return angle of motion (degrees)
     */
    public float getMotionAngle() {
        return velocityVec.angle();
    }

    /**
     * Determines if this object is moving (if speed is greater than zero).
     *
     * @return false when speed is zero, true otherwise
     */
    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    /**
     * Returns bounding polygon for this BaseActor, adjusted by Actor's current position and rotation.
     *
     * @return bounding polygon for this BaseActor
     */
    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }

	/*------------------------------*\
	|*				Setters			*|
	\*------------------------------*/


    /**
     * Sets the angle of motion (in degrees).
     * If current speed is zero, this will have no effect.
     *
     * @param angle of motion (degrees)
     */
    public void setMotionAngle(float angle) {
        velocityVec.setAngle(angle);
    }

    /**
     * Set the speed of movement (in pixels/second) in current direction.
     * If current speed is zero (direction is undefined), direction will be set to 0 degrees.
     *
     * @param speed of movement (pixels/second)
     */
    public void setSpeed(float speed) {
        // if length is 0, assume motion angle is zero deg
        if (velocityVec.len() == 0) {
            velocityVec.set(speed, 0);
        } else {
            velocityVec.setLength(speed);
        }
    }

    /**
     * Set acceleration of this object.
     *
     * @param acc Acceleration in (pixels/second) per second.
     */
    public void setAcceleration(float acc) {
        acceleration = acc;
    }

    /**
     * Set deceleration of this object.
     * Deceleration is only applied when object is not accelerating.
     *
     * @param deceleration Deceleration in (pixels/second) per second.
     */
    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    /**
     * Set maximum speed of this object.
     *
     * @param maxSpeed Maximum speed of this object in (pixels/second).
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * Sets the opacity of this actor.
     *
     * @param opacity value from 0 (transparent) to 1 (opaque)
     */
    public void setOpacity(float opacity) {
        getColor().a = opacity;
    }

    /*------------------------------*\
   	|*				Tools   		*|
   	\*------------------------------*/

    /**
     * Update accelerate vector by angle and value stored in acceleration field.
     * Acceleration is applied by <code>applyPhysics</code> method.
     *
     * @param angle Angle (degrees) in which to accelerate.
     * @see #acceleration
     * @see #applyPhysics
     */
    public void accelerateAtAngle(float angle) {
        accelerationVec.add(new Vector2(acceleration, 0).setAngle(angle));
    }

    /**
     * Update accelerate vector by current rotation angle and value stored in acceleration field.
     * Acceleration is applied by <code>applyPhysics</code> method.
     *
     * @see #acceleration
     * @see #applyPhysics
     */
    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }

    /**
     * Align center of actor at given position coordinates.
     *
     * @param x x-coordinate to center at
     * @param y y-coordinate to center at
     */
    public void centerAtPosition(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    /**
     * Repositions this BaseActor so its center is aligned
     * with center of other BaseActor. Useful when one BaseActor spawns another.
     *
     * @param other BaseActor to align this BaseActor with
     */
    public void centerAtActor(BaseActor other) {
        centerAtPosition(
                other.getX() + other.getWidth() / 2,
                other.getY() + other.getHeight() / 2
        );
    }

    /*------------------------------*\
   	|*				Overriden		*|
   	\*------------------------------*/

    /**
     * Processes all Actions and related code for this object;
     * automatically called by act method in Stage class.
     *
     * @param dt elapsed time (second) since last frame (supplied by Stage act method)
     */
    @Override
    public void act(float dt) {
        super.act(dt);
        if (!animationPaused) {
            elapsedTime += dt;
        }
    }

    /**
     * Draws current frame of animation; automatically called by draw method in Stage class. <br>
     * If color has been set, image will be tinted by that color. <br>
     * If no animation has been set or object is invisible, nothing will be drawn.
     *
     * @param batch       (supplied by Stage draw method)
     * @param parentAlpha (supplied by Stage draw method)
     * @see #setColor
     * @see #setVisible
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if (animation != null && isVisible()) {
            batch.draw(animation.getKeyFrame(elapsedTime),
                    getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(),
                    getScaleY(), getRotation()
            );
        }
        super.draw(batch, parentAlpha);
    }

    /*------------------------------*\
   	|*				Animation		*|
   	\*------------------------------*/

    /**
     * Sets the animation used when rendering this actor; also sets actor size.
     *
     * @param anim animation that will be drawn when actor is rendered
     */
    public void setAnimation(Animation<TextureRegion> anim) {
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize(w, h);
        setOrigin(w / 2, h / 2);

        if (boundaryPolygon == null) {
            setBoundaryRectangle();
        }
    }

    /**
     * Set the pause state of the animation.
     *
     * @param pause true to pause animation, false to resume animation
     */
    public void setAnimationPaused(boolean pause) {
        animationPaused = pause;
    }


    /**
     * Checks if animation is complete: if play mode is normal (not looping)
     * and elapsed time is greater than time corresponding to last frame.
     */
    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(elapsedTime);
    }

    /**
     * Creates an animation from images stored in separate files.
     *
     * @param fileNames     array of names of files containing animation images
     * @param frameDuration how long each frame should be displayed
     * @param loop          should the animation loop
     * @return animation created (useful for storing multiple animations)
     */
    public Animation<TextureRegion> loadAnimationFromFiles(String[] fileNames, float frameDuration,
                                                           boolean loop) {

        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        for (String name : fileNames) {
            Texture texture = new Texture(Gdx.files.internal(name));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);
        anim.setPlayMode(loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);

        if (animation == null) {
            setAnimation(anim);
        }
        return anim;
    }

    /**
     * Creates an animation from a spritesheet: a rectangular grid of images stored in a single file.
     *
     * @param fileName      name of file containing spritesheet
     * @param rows          number of rows of images in spritesheet
     * @param cols          number of columns of images in spritesheet
     * @param frameDuration how long each frame should be displayed
     * @param loop          should the animation loop
     * @return animation created (useful for storing multiple animations)
     */
    public Animation<TextureRegion> loadAnimationFromSheet(String fileName, int rows, int cols,
                                                           float frameDuration, boolean loop) {

        Texture texture = new Texture(Gdx.files.internal(fileName), true);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);
        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                textureArray.add(temp[r][c]);
            }
        }

        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);
        anim.setPlayMode(loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);

        if (animation == null) {
            setAnimation(anim);
        }
        return anim;
    }

    /**
     * Convenience method for creating a 1-frame animation from a single texture.
     *
     * @param fileName names of image file
     * @return animation created (useful for storing multiple animations)
     */
    public Animation<TextureRegion> loadTexture(String fileName) {
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    /*------------------------------*\
   	|*				Physics 		*|
   	\*------------------------------*/

    /**
     * Adjust velocity vector based on acceleration vector,
     * then adjust position based on velocity vector. <br>
     * If not accelerating, deceleration value is applied. <br>
     * Speed is limited by maxSpeed value. <br>
     * Acceleration vector reset to (0,0) at end of method. <br>
     *
     * @param dt Time elapsed since previous frame (delta time); typically obtained from <code>act</code> method.
     * @see #acceleration
     * @see #deceleration
     * @see #maxSpeed
     */
    public void applyPhysics(float dt) {
        // apply acceleration
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt);
        float speed = getSpeed();
        // decelerate when not accelerating
        if (accelerationVec.len() == 0) {
            speed -= deceleration * dt;
        }
        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0, maxSpeed);
        // update velocity
        setSpeed(speed);
        // apply velocity
        moveBy(velocityVec.x * dt, velocityVec.y * dt);
        // reset acceleration
        accelerationVec.set(0, 0);
    }

    /*------------------------------*\
   	|*				Collision  		*|
   	\*------------------------------*/

    /**
     * Set rectangular-shaped collision polygon.
     * This method is automatically called when animation is set,
     * provided that the current boundary polygon is null.
     *
     * @see #setAnimation
     */
    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
    }

    /**
     * Replace default (rectangle) collision polygon with an n-sided polygon. <br>
     * Vertices of polygon lie on the ellipse contained within bounding rectangle.
     * Note: one vertex will be located at point (0,width);
     * a 4-sided polygon will appear in the orientation of a diamond.
     *
     * @param numSides number of sides of the collision polygon
     */
    public void setBoundaryPolygon(int numSides) {
        float w = getWidth();
        float h = getHeight();

        float[] vertices = new float[2 * numSides];
        for (int i = 0; i < numSides; i++) {
            float angle = i * 6.28f / numSides;
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2;     // x
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2; // y
        }
        boundaryPolygon = new Polygon(vertices);
    }

    /**
     * Determine if this BaseActor overlaps other BaseActor (according to collision polygons).
     *
     * @param other BaseActor to check for overlap
     * @return true if collision polygons of this and other BaseActor overlap
     */
    public boolean overlaps(BaseActor other) {
        Polygon p1 = this.getBoundaryPolygon();
        Polygon p2 = other.getBoundaryPolygon();

        // initial test to improve performance. Much lighter overhead to
        // check in first place if the rough rectangle intersect.
        if (!p1.getBoundingRectangle().overlaps(p2.getBoundingRectangle())) {
            return false;
        }

        return Intersector.overlapConvexPolygons(p1, p2);
    }

    /**
     * Implement a "solid"-like behavior:
     * when there is overlap, move this BaseActor away from other BaseActor
     * along minimum translation vector until there is no overlap.
     *
     * @param other BaseActor to check for overlap
     * @return direction vector by which actor was translated, null if no overlap
     */
    public Vector2 preventOverlap(BaseActor other) {
        Polygon p1 = this.getBoundaryPolygon();
        Polygon p2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if (!p1.getBoundingRectangle().overlaps(p2.getBoundingRectangle())) {
            return null;
        }

        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygoneOverlap = Intersector.overlapConvexPolygons(p1, p2, mtv);

        if (!polygoneOverlap) {
            return null;
        }

        moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
        return mtv.normal;
    }

    /**
     * Determine if this BaseActor is near other BaseActor (according to collision polygons).
     *
     * @param distance amount (pixels) by which to enlarge collision polygon width and height
     * @param other    BaseActor to check if nearby
     * @return true if collision polygons of this (enlarged) and other BaseActor overlap
     * @see #setBoundaryRectangle
     * @see #setBoundaryPolygon
     */
    public boolean isWithinDistance(float distance, BaseActor other) {
        Polygon p1 = this.getBoundaryPolygon();
        Polygon p2 = other.getBoundaryPolygon();

        float scaleX = (this.getWidth() + 2 * distance) / this.getWidth();
        float scaleY = (this.getHeight() + 2 * distance) / this.getHeight();

        p1.setScale(scaleX, scaleY);

        // initial test to improve performance
        if (!p1.getBoundingRectangle().overlaps(p2.getBoundingRectangle())) return false;
        return Intersector.overlapConvexPolygons(p1, p2);
    }

    /**
     * If an edge of an object moves past the world bounds,
     * adjust its position to keep it completely on screen.
     */
    public void boundToWorld() {
        if (getX() < 0) setX(0);
        if (getX() + getWidth() > worldBounds.width) setX(worldBounds.width - getWidth());
        if (getY() < 0) setY(0);
        if (getY() + getHeight() > worldBounds.height) setY(worldBounds.height - getHeight());
    }

    /*------------------------------*\
   	|*				Camera  		*|
   	\*------------------------------*/

    /**
     * Center camera on this object, while keeping camera's range of view
     * (determined by screen size) completely within world bounds.
     */
    public void alignCamera() {
        Camera cam = this.getStage().getCamera();
        Viewport v = this.getStage().getViewport();

        // center camera on actor
        cam.position.set(getX() + getOriginX(), getY() + getOriginY(), 0);

        // bound camera to layout
        cam.position.x = MathUtils.clamp(
                cam.position.x, cam.viewportWidth / 2,
                worldBounds.width - cam.viewportWidth / 2
        );
        cam.position.y = MathUtils.clamp(
                cam.position.y, cam.viewportHeight / 2,
                worldBounds.height - cam.viewportHeight / 2
        );
    }

    /*------------------------------*\
   	|*				Behave  		*|
   	\*------------------------------*/

    /**
     * If this object moves completely past the world bounds,
     * adjust its position to the opposite side of the world.
     */
    public void wrapAroundWorld() {
        if (getX() + getWidth() < 0) setX(worldBounds.width);
        if (getX() > worldBounds.width) setX(-getWidth());
        if (getY() + getHeight() < 0) setY(worldBounds.height);
        if (getY() > worldBounds.height) setY(-getHeight());
    }
}
