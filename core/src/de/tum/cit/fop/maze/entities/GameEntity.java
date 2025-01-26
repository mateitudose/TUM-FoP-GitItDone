package de.tum.cit.fop.maze.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import de.tum.cit.fop.maze.objects.GameObject;

/**
 * Abstract class representing a game entity in the maze game.
 * Extends the GameObject class and adds properties for position, texture, and body.
 */
public abstract class GameEntity extends GameObject {
    protected Body body;
    protected Vector2 position;
    protected TextureRegion texture;

    /**
     * Constructs a GameEntity object.
     *
     * @param x the x-coordinate of the entity
     * @param y the y-coordinate of the entity
     * @param entitySizeWidth the width of the entity
     * @param entitySizeHeight the height of the entity
     * @param texture the texture region of the entity
     */
    public GameEntity(int x, int y, int entitySizeWidth, int entitySizeHeight, TextureRegion texture) {
        super(x, y, entitySizeWidth, entitySizeHeight, texture);
        this.position = new Vector2(x, y);
        this.texture = texture;
    }

    /**
     * Gets the Box2D body of the entity.
     *
     * @return the body of the entity
     */
    public Body getBody() {
        return body;
    }

    /**
     * Gets the position of the entity.
     *
     * @return the position of the entity
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Sets the position of the entity.
     *
     * @param position the position to set
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    /**
     * Gets the texture region of the entity.
     *
     * @return the texture region of the entity
     */
    public TextureRegion getTexture() {
        return texture;
    }

    /**
     * Sets the texture region of the entity.
     *
     * @param texture the texture region to set
     */
    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    /**
     * Updates the state of the entity.
     *
     * @param delta the time elapsed since the last update
     */
    public abstract void update(float delta);
}
