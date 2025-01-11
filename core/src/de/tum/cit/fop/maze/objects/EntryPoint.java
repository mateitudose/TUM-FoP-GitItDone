package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents the entry point of the maze.
 */
public class EntryPoint extends GameObject {
    public EntryPoint(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, texture);
    }
}