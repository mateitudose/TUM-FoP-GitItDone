package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a key in the maze that the player must collect.
 */
public class Key extends GameObject {
    public Key(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, texture);
    }
}