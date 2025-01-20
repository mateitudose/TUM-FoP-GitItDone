package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LaserTrap extends GameObject {
    private static final float ANIMATION_SPEED = 0.1f;
    private static final int ANIMATION_FRAMES = 12;
    private Animation<TextureRegion> activeAnimation;
    private float stateTime;
    private boolean isActive = true;

    public LaserTrap(int x, int y) {
        super(x, y, 8, 16, new TextureRegion(new Texture("editedLaser.png"), 32, 0, 32, 64));
        sprite.setPosition(x * TILE_SIZE + 3, y * TILE_SIZE);
        loadAnimation();
        stateTime = 0f;
    }

    private void loadAnimation() {
        TextureRegion[][] frames = TextureRegion.split(new Texture("editedLaser.png"), 32, 64);
        TextureRegion[] animationFrames = new TextureRegion[ANIMATION_FRAMES];
        for (int i = 0; i < ANIMATION_FRAMES; i++) {
            animationFrames[i] = frames[0][i * 3 + 1];
        }
        activeAnimation = new Animation<>(ANIMATION_SPEED, animationFrames);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isActive) {
            update(0);
            sprite.setRegion(activeAnimation.getKeyFrame(0, true));
            sprite.draw(batch);
            return;
        }
        // Self-update game object with delta time so that the animation is in sync with the game
        update(Gdx.graphics.getDeltaTime());
        sprite.setRegion(activeAnimation.getKeyFrame(stateTime, true));
        sprite.draw(batch);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
