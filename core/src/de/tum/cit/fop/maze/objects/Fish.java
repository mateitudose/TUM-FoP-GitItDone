package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Represents a Fish object in the maze game.
 * The Fish object can be collected by the player and is used as a key and also to determine level score.
 */
public class Fish extends GameObject {
    private Body body;

    /**
     * Constructs a new Fish object.
     *
     * @param x     the x-coordinate in tile coordinates
     * @param y     the y-coordinate in tile coordinates
     * @param world the Box2D world in which the fish exists
     */
    public Fish(int x, int y, World world) {
        super(x, y, 10, 10, new TextureRegion(new Texture("fish.png"), 0, 0, 16, 16));
        // Fish is 10x10 pixels, so we need to adjust the position by (16-10)/2 = 3
        sprite.setPosition(x * TILE_SIZE + 3, y * TILE_SIZE + 3);
        createBody(world);
    }

    /**
     * Creates the Box2D body for the fish.
     *
     * @param world the Box2D world in which the body is created
     */
    private void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(getX() + 0.5f, getY() + 0.5f);

        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.25f, 0.25f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = 0x0004;
        fixtureDef.filter.maskBits = 0x0001;

        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    /**
     * Destroys the Box2D body of the fish.
     */
    public void destroyBody() {
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
    }
}
