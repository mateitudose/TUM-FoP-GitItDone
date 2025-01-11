package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a trap in the maze that reduces player's lives.
 */
public class Trap extends GameObject {
    public Trap(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, texture);
    }
}