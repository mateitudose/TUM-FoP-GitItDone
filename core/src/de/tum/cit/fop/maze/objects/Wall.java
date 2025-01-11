package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.fop.maze.MazeMap;

/**
 * Represents a wall in the maze.
 */
public class Wall extends GameObject {
    private World world;
    public Wall(int x, int y, int size, TextureRegion texture, World world) {
        super(x, y, size, texture);
        this.world = world;

        // Create a Box2D body for collision
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // Center of the tile
        bodyDef.position.set(
                (x + 0.5f) * MazeMap.TILE_SIZE / MazeMap.TILE_SIZE, // Center within the tile
                (y + 0.5f) * MazeMap.TILE_SIZE / MazeMap.TILE_SIZE
        );

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();

        // Wall size in Box2D world units
        // Shrink the collision box size for the wall
        float collisionWidth = (size / 2f) / MazeMap.TILE_SIZE;  // 90% of tile size
        float collisionHeight = (size / 2f) / MazeMap.TILE_SIZE; // 90% of tile size

        shape.setAsBox(collisionWidth, collisionHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.filter.categoryBits = 0x0002; // Wall category
        fixtureDef.filter.maskBits = 0x0001;    // Collides with player
        fixtureDef.friction = 0.5f;
        body.setUserData("Wall " + x + " - " + y);

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}
