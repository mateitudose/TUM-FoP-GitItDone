package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Abstract base class for all game objects in the maze.
 */
public abstract class GameObject {
    protected static final int TILE_SIZE = 16;
    protected int x, y;
    protected int objectSizeWidth, objectSizeHeight;
    protected TextureRegion texture;
    protected Sprite sprite;

    /**
     * Constructs a new GameObject.
     *
     * @param x                the x-coordinate in tile coordinates
     * @param y                the y-coordinate in tile coordinates
     * @param objectSizeWidth  the width of the object in pixels
     * @param objectSizeHeight the height of the object in pixels
     * @param texture          the texture region for the object
     */
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

    /**
     * Renders the game object using the provided SpriteBatch.
     *
     * @param batch the SpriteBatch used for rendering
     */
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    /**
     * Gets the texture region of the game object.
     *
     * @return the texture region
     */
    public TextureRegion getTexture() {
        return texture;
    }

    /**
     * Gets the x-coordinate of the game object in tile coordinates.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the game object in tile coordinates.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }
}
