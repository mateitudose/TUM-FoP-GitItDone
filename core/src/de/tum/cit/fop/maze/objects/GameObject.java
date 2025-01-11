package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Abstract base class for all game objects in the maze.
 */
public abstract class GameObject {
    protected int x, y; // Position in tile coordinates
    protected int tileSize; // Size of the tile
    protected TextureRegion texture;

    public GameObject(int x, int y, int tileSize, TextureRegion texture) {
        this.x = x;
        this.y = y;
        this.tileSize = tileSize;
        this.texture = texture;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x * tileSize, y * tileSize, tileSize, tileSize);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
