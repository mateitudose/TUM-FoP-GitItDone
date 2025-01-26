package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Represents a LaserTrap object in the maze game.
 * The LaserTrap object can activate and deactivate, becoming dangerous when active.
 */
public class LaserTrap extends GameObject {
    private static final float ANIMATION_SPEED = 0.1f;
    private static final int ACTIVATING_FRAMES = 3;
    private static final int ACTIVE_FRAMES = 8;
    private static final int DEACTIVATING_FRAMES = 1;

    private Animation<TextureRegion> activatingAnimation;
    private Animation<TextureRegion> activeAnimation;
    private Animation<TextureRegion> deactivatingAnimation;

    private float stateTime;
    private boolean isActive = false;
    private boolean isActivating = false;
    private boolean isDeactivating = false;
    private boolean wasDangerous = false;
    private boolean previousDangerous = false;

    private long activeDuration = 5000;
    private long inactiveDuration = 3000;

    private long elapsedTimeActive = 0;
    private long elapsedTimeInactive = 0;
    private long lastUpdateTime = 0;

    private boolean isPaused = false;
    private Body body;

    /**
     * Constructs a new LaserTrap object.
     *
     * @param x     the x-coordinate in tile coordinates
     * @param y     the y-coordinate in tile coordinates
     * @param world the Box2D world in which the laser trap exists
     */
    public LaserTrap(int x, int y, World world) {
        super(x, y, 8, 16, new TextureRegion(new Texture("laser.png"), 32, 0, 32, 64));
        sprite.setPosition(x * TILE_SIZE + 3, y * TILE_SIZE);
        createBody(world);
        loadAnimations();
        stateTime = 0f;
        activate();
    }

    /**
     * Loads the animations for the laser trap.
     */
    private void loadAnimations() {
        TextureRegion[][] frames = TextureRegion.split(new Texture("laser.png"), 32, 64);
        TextureRegion[] activatingFrames = new TextureRegion[ACTIVATING_FRAMES];
        TextureRegion[] activeFrames = new TextureRegion[ACTIVE_FRAMES];
        TextureRegion[] deactivatingFrames = new TextureRegion[DEACTIVATING_FRAMES];
        TextureRegion[] allFrames = new TextureRegion[ACTIVATING_FRAMES + ACTIVE_FRAMES + DEACTIVATING_FRAMES];

        for (int i = 0; i < ACTIVATING_FRAMES + ACTIVE_FRAMES + DEACTIVATING_FRAMES; i++) {
            allFrames[i] = frames[0][i * 3 + 1];
        }
        for (int i = 0; i < ACTIVATING_FRAMES; i++) {
            activatingFrames[i] = allFrames[i];
        }
        for (int i = 0; i < ACTIVE_FRAMES; i++) {
            activeFrames[i] = allFrames[i + ACTIVATING_FRAMES];
        }
        for (int i = 0; i < DEACTIVATING_FRAMES; i++) {
            deactivatingFrames[i] = allFrames[i + ACTIVATING_FRAMES + ACTIVE_FRAMES];
        }

        activatingAnimation = new Animation<>(ANIMATION_SPEED, activatingFrames);
        activeAnimation = new Animation<>(ANIMATION_SPEED, activeFrames);
        deactivatingAnimation = new Animation<>(ANIMATION_SPEED, deactivatingFrames);
    }

    /**
     * Creates the Box2D body for the laser trap.
     *
     * @param world the Box2D world in which the body is created
     */
    private void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(getX() + 0.5f, getY() + 0.5f);

        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.3f, 0.3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = 0x0002;
        fixtureDef.filter.maskBits = 0x0001;

        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    /**
     * Updates the state of the laser trap.
     *
     * @param deltaTime the time elapsed since the last update
     */
    public void update(float deltaTime) {
        if (isPaused) {
            lastUpdateTime = System.currentTimeMillis();
            return;
        }

        // Save the previous danger state before processing updates
        previousDangerous = isDangerous();

        stateTime += deltaTime;
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        // State machine logic
        if (isActive) {
            elapsedTimeActive += elapsed;
            if (elapsedTimeActive >= activeDuration) {
                deactivate();
            }
        } else {
            elapsedTimeInactive += elapsed;
            if (elapsedTimeInactive >= inactiveDuration) {
                activate();
            }
        }

        // Update danger state tracking for the next frame
        wasDangerous = isDangerous();
    }

    /**
     * Renders the laser trap using the provided SpriteBatch.
     *
     * @param batch the SpriteBatch used for rendering
     */
    @Override
    public void render(SpriteBatch batch) {
        if (isActivating) {
            sprite.setRegion(activatingAnimation.getKeyFrame(stateTime, false));
            if (activatingAnimation.isAnimationFinished(stateTime)) {
                isActivating = false;
                isActive = true;
                stateTime = 0;
                elapsedTimeActive = 0;
            }
        } else if (isActive) {
            sprite.setRegion(activeAnimation.getKeyFrame(stateTime, true));
        } else if (isDeactivating) {
            sprite.setRegion(deactivatingAnimation.getKeyFrame(stateTime, false));
            if (deactivatingAnimation.isAnimationFinished(stateTime)) {
                isDeactivating = false;
                stateTime = 0;
                elapsedTimeInactive = 0;
            }
        } else {
            sprite.setRegion(activatingAnimation.getKeyFrame(0, true));
        }

        sprite.draw(batch);
    }

    /**
     * Activates the laser trap.
     */
    public void activate() {
        isActivating = true;
        isDeactivating = false;
        isActive = false;
        stateTime = 0;
        elapsedTimeInactive = 0;
        lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Deactivates the laser trap.
     */
    public void deactivate() {
        isDeactivating = true;
        isActivating = false;
        isActive = false;
        stateTime = 0;
        elapsedTimeActive = 0;
        lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Pauses the timer for the laser trap.
     */
    public void pauseTimer() {
        isPaused = true;
    }

    /**
     * Resumes the timer for the laser trap.
     */
    public void resumeTimer() {
        if (isPaused) {
            isPaused = false;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    /**
     * Checks if the laser trap is dangerous.
     *
     * @return true if the laser trap is dangerous, false otherwise
     */
    public boolean isDangerous() {
        return isActive || isActivating || isDeactivating;
    }

    /**
     * Checks if the laser trap became dangerous in the current frame.
     *
     * @return true if the laser trap became dangerous, false otherwise
     */
    public boolean becameDangerous() {
        return isDangerous() && !previousDangerous;
    }
}
