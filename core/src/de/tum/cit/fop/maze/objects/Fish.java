package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a fish in the maze that the player must collect to unlock the exit.
 */
public class Fish extends GameObject {
    public Fish(int x, int y) {
        super(x, y, 10, new TextureRegion(new Texture("fish.png"), 0, 0, 16, 16));
    }
}
