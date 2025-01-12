package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents an enemy in the maze.
 */
public class Enemy extends GameObject {
    public Enemy(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, texture);
    }

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
}