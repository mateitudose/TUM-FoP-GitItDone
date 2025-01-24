package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.fop.maze.MazeMap;

public class SlowTile extends GameObject {
    private Body body;

    public SlowTile(int x, int y, World world) {
        super(x, y, MazeMap.TILE_SIZE, MazeMap.TILE_SIZE, new TextureAtlas("world.atlas").findRegion("basictiles").split(MazeMap.TILE_SIZE, MazeMap.TILE_SIZE)[9][2]);
        createPhysicsBody(world);
    }

    private void createPhysicsBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + 0.5f, y + 0.5f);

        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.4f, 0.4f); // Make the collision box a bit smaller than the tile

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = 0x0008; // Category for slow tiles
        fixtureDef.filter.maskBits = 0x0001; // Collide with player

        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    public Body getBody() {
        return body;
    }
}
