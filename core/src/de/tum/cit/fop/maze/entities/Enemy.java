package de.tum.cit.fop.maze.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents an enemy in the maze.
 */
public class Enemy extends GameEntity {

    public Enemy(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, texture);
        this.position = new Vector2(x, y);
    }

    public void move(int newX, int newY) {
        this.position.set(newX, newY);

        // If the enemy has a physics body, update its position accordingly
        if (body != null) {
            body.setTransform(newX, newY, body.getAngle());
        }
    }

    @Override
    public void update(float delta) {
        // Implement enemy-specific update logic here
        // For example, you can add code to move the enemy or check for collisions
    }
}
