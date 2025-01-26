package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.fop.maze.MazeMap;

/**
 * Represents a SlowTile object in the maze game.
 * The SlowTile object slows down the player when they step on it.
 */
public class SlowTile extends GameObject {
    private Body body;

    /**
     * Constructs a new SlowTile object.
     *
     * @param x     the x-coordinate in tile coordinates
     * @param y     the y-coordinate in tile coordinates
     * @param world the Box2D world the slow tile belongs to
     */
    public SlowTile(int x, int y, World world) {
        super(x, y, MazeMap.TILE_SIZE, MazeMap.TILE_SIZE, new TextureAtlas("world.atlas").findRegion("basictiles").split(MazeMap.TILE_SIZE, MazeMap.TILE_SIZE)[9][2]);
        createPhysicsBody(world);
    }

    /**
     * Creates the Box2D body for the slow tile.
     *
     * @param world the Box2D world the slow tile belongs to
     */
    private void createPhysicsBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(getX() + 0.5f, getY() + 0.5f);

        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.4f, 0.4f); // Make the collision box a bit smaller than the tile

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = 0x0008; // Category for slow tiles
        fixtureDef.filter.maskBits = 0x0001; // Collide with player, as we don't want to slow down the enemies

        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    /**
     * Gets the Box2D body of the slow tile.
     *
     * @return the Box2D body
     */
    public Body getBody() {
        return body;
    }
}
