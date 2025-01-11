package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents the exit point of the maze.
 */
public class ExitPoint extends GameObject {
    public ExitPoint(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, texture);
    }
}