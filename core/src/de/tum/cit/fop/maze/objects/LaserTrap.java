package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Timer;
import java.util.TimerTask;

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

    private Timer timer;

    public LaserTrap(int x, int y) {
        super(x, y, 8, 16, new TextureRegion(new Texture("editedLaser.png"), 32, 0, 32, 64));
        sprite.setPosition(x * TILE_SIZE + 3, y * TILE_SIZE);
        loadAnimations();
        stateTime = 0f;
        timer = new Timer();
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
        stateTime += deltaTime;
    }

    @Override
    public void render(SpriteBatch batch) {
        update(Gdx.graphics.getDeltaTime());

        if (isActivating) {
            sprite.setRegion(activatingAnimation.getKeyFrame(stateTime, false));
            if (activatingAnimation.isAnimationFinished(stateTime)) {
                isActivating = false;
                isActive = true;
                stateTime = 0;
            }
        } else if (isActive) {
            sprite.setRegion(activeAnimation.getKeyFrame(stateTime, true));
        } else if (isDeactivating) {
            sprite.setRegion(deactivatingAnimation.getKeyFrame(stateTime, false));
            if (deactivatingAnimation.isAnimationFinished(stateTime)) {
                isDeactivating = false;
                stateTime = 0;
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

        // Schedule deactivation after 5 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                deactivate();
            }
        }, 5000);
    }

    public void deactivate() {
        isDeactivating = true;
        isActivating = false;
        isActive = false;
        stateTime = 0;
    }

    public boolean isActive() {
        return isActive;
    }
}
