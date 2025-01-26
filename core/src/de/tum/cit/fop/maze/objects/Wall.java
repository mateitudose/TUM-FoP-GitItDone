package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.fop.maze.MazeMap;

/**
 * Represents a wall tile in the maze.
 */
public class Wall extends GameObject {
    private World world;

    /**
     * Constructs a new Wall object.
     *
     * @param x        the x-coordinate in tile coordinates
     * @param y        the y-coordinate in tile coordinates
     * @param tileSize the size of the tile
     * @param texture  the texture region for the wall
     * @param world    the Box2D world the wall belongs to
     */
    public Wall(int x, int y, int tileSize, TextureRegion texture, World world) {
        super(x, y, tileSize, tileSize, texture);
        this.world = world;

        // Create a Box2D body for collision
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // Center of the tile
        bodyDef.position.set(getX() + 0.5f, getY() + 0.5f);

        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();

        // Wall size in Box2D world units
        // Shrink the collision box size for the wall
        float collisionWidth = (tileSize / 2f) / MazeMap.TILE_SIZE;
        float collisionHeight = (tileSize / 2f) / MazeMap.TILE_SIZE;

        shape.setAsBox(collisionWidth, collisionHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.filter.categoryBits = 0x0003; // Wall category
        fixtureDef.filter.maskBits = 0x0001;    // Collides with player
        fixtureDef.friction = 0.0f;
        body.setUserData("Wall " + x + "," + y);

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}
