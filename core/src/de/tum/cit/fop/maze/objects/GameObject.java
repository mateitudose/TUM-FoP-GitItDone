package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Abstract base class for all game objects in the maze.
 */
public abstract class GameObject {
    protected static final int TILE_SIZE = 16; // Tile size constant
    protected int x, y; // Position in tile coordinates
    protected int objectSizeWidth, objectSizeHeight; // Size of the tile
    protected TextureRegion texture;
    protected Sprite sprite;

    public GameObject(int x, int y, int objectSizeWidth, int objectSizeHeight, TextureRegion texture) {
        this.x = x;
        this.y = y;
        this.objectSizeWidth = objectSizeWidth;
        this.objectSizeHeight = objectSizeHeight;
        this.texture = texture;
        this.sprite = new Sprite(texture);
        this.sprite.setSize(objectSizeWidth, objectSizeHeight);
        this.sprite.setPosition(x * TILE_SIZE, y * TILE_SIZE);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public int getObjectSizeWidth() {
        return objectSizeWidth;
    }

    public int getObjectSizeHeight() {
        return objectSizeHeight;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
