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
    protected int objectSize; // Size of the tile
    protected TextureRegion texture;

    public GameObject(int x, int y, int objectSize, TextureRegion texture) {
        this.x = x;
        this.y = y;
        this.objectSize = objectSize;
        this.texture = texture;
    }

    public void render(SpriteBatch batch) {
        Sprite objectSprite = new Sprite(texture);
        objectSprite.setSize(objectSize, objectSize);
        objectSprite.setPosition(x * TILE_SIZE, y * TILE_SIZE);
        if (this instanceof Fish) {
            // Fish is 10x10 pixels, so we need to adjust the position by (16-10)/2 = 3
            objectSprite.setPosition(x * TILE_SIZE + 3, y * TILE_SIZE + 3);
        }
        objectSprite.draw(batch);
    }

    public int getObjectSize() {
        return objectSize;
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
