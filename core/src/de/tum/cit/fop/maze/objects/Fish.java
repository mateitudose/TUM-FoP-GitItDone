package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Represents a fish in the maze that the player must collect to unlock the exit.
 */
public class Fish extends GameObject {
    private Body body;

    public Fish(int x, int y) {
        super(x, y, 10, 10, new TextureRegion(new Texture("fish.png"), 0, 0, 16, 16));
        // Fish is 10x10 pixels, so we need to adjust the position by (16-10)/2 = 3
        sprite.setPosition(x * TILE_SIZE + 3, y * TILE_SIZE + 3);
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }
}
