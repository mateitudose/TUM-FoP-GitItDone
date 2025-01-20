package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a path in the maze that the player can walk on.
 */
public class Path extends GameObject {
    public Path(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, tileSize, texture);
    }
}
