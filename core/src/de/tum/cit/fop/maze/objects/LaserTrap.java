package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a laser trap in the maze that reduces player's lives.
 */
public class LaserTrap extends GameObject {
    private static final float ANIMATION_SPEED = 0.1f;
    private static final int ANIMATION_FRAMES = 4;
    private Animation<TextureRegion> activeAnimation;

    public LaserTrap(int x, int y) {
        super(x, y, 16, 32, new TextureRegion(new Texture("laser.png"), 32, 0, 32, 96));
    }
}
