package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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

    private long activeDuration = 5000; // Active time in milliseconds
    private long inactiveDuration = 3000; // Inactive time in milliseconds

    private long elapsedTimeActive = 0;
    private long elapsedTimeInactive = 0;
    private long lastUpdateTime = 0;

    private boolean isPaused = false;

    public LaserTrap(int x, int y) {
        super(x, y, 8, 16, new TextureRegion(new Texture("editedLaser.png"), 32, 0, 32, 64));
        sprite.setPosition(x * TILE_SIZE + 3, y * TILE_SIZE);
        loadAnimations();
        stateTime = 0f;
        activate();
    }

    private void loadAnimations() {
        TextureRegion[][] frames = TextureRegion.split(new Texture("editedLaser.png"), 32, 64);
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

    public void update(float deltaTime) {
        if (isPaused) {
            lastUpdateTime = System.currentTimeMillis();
            return;
        }

        if (deltaTime > 0) {
            stateTime += deltaTime;
        }

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

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
    }

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

    public void activate() {
        isActivating = true;
        isDeactivating = false;
        isActive = false;
        stateTime = 0;
        elapsedTimeInactive = 0;
        lastUpdateTime = System.currentTimeMillis();
    }

    public void deactivate() {
        isDeactivating = true;
        isActivating = false;
        isActive = false;
        stateTime = 0;
        elapsedTimeActive = 0;
        lastUpdateTime = System.currentTimeMillis();
    }

    public void pauseTimer() {
        isPaused = true;
    }

    public void resumeTimer() {
        if (isPaused) {
            isPaused = false;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    public boolean isActive() {
        return isActive;
    }
}
