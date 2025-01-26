package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.fop.maze.MazeMap;

/**
 * Represents an ability object in the maze game.
 * This object can be collected by the player and has a rotating animation.
 */
public class Ability extends GameObject {
    private Animation<TextureRegion> rotatingAnimation;
    private float stateTime;
    private boolean collected = false;
    private Body body;

    /**
     * Constructs an Ability object at the specified coordinates in the given world.
     *
     * @param x     the x-coordinate of the ability
     * @param y     the y-coordinate of the ability
     * @param world the Box2D world where the ability exists
     */
    public Ability(int x, int y, World world) {
        super(x, y, 10, 10, new TextureAtlas("world.atlas").findRegion("objects").split(MazeMap.TILE_SIZE, MazeMap.TILE_SIZE)[4][0]);
        sprite.setPosition(x * MazeMap.TILE_SIZE + 3, y * MazeMap.TILE_SIZE + 4);
        rotatingAnimation = loadAnimation();
        rotatingAnimation.setPlayMode(Animation.PlayMode.LOOP);
        stateTime = 0f;
        createBody(world);
    }

    /**
     * Loads the rotating animation for the ability.
     *
     * @return the animation object
     */
    private Animation<TextureRegion> loadAnimation() {
        TextureRegion[][] frames = new TextureAtlas("world.atlas").findRegion("objects").split(MazeMap.TILE_SIZE, MazeMap.TILE_SIZE);
        TextureRegion[] frames1D = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            frames1D[i] = frames[4][i];
        }
        return new Animation<>(0.1f, frames1D);
    }

    /**
     * Creates the Box2D body for the ability in the given world.
     *
     * @param world the Box2D world where the body is created
     */
    private void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((getX() + 0.5f), (getY() + 0.5f));

        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = 0x0011; // New category for ability
        fixtureDef.filter.maskBits = 0x0001;     // Collide with player

        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    /**
     * Marks the ability as collected and destroys its Box2D body.
     */
    public void collect() {
        collected = true;
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
    }

    /**
     * Renders the ability with its rotating animation.
     *
     * @param batch the SpriteBatch used for drawing
     */
    @Override
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        sprite.setRegion(rotatingAnimation.getKeyFrame(stateTime));
        super.render(batch);
    }
}
