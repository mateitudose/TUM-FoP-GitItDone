package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.Array;

public class Player {
    private static final float MOVE_SPEED = 30.0f;
    private final World world;
    private final Sprite sprite;
    private Animation<TextureRegion> downAnim, upAnim, rightAnim, leftAnim;
    private final Body body;

    private float stateTime = 0f;
    private String currentDirection = "down";
    private boolean isMoving = false;

    public Player(World world) {
        this.world = world;
        this.sprite = loadCharacter();
        loadAnimations();
        this.body = createBody();
    }

    private Sprite loadCharacter() {
        Texture characterSheet = new Texture(Gdx.files.internal("character.png"));
        Sprite sprite = new Sprite(new TextureRegion(characterSheet, 0, 0, 16, 32));
        sprite.setSize(16, 32);
        return sprite;
    }

    private void loadAnimations() {
        Texture characterSheet = new Texture(Gdx.files.internal("character.png"));
        int frameWidth = 16, frameHeight = 32, frames = 4;

        Array<TextureRegion> downFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> rightFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> upFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> leftFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < frames; col++) {
            downFrames.add(new TextureRegion(characterSheet, col * frameWidth, 0, frameWidth, frameHeight));
            rightFrames.add(new TextureRegion(characterSheet, col * frameWidth, frameHeight, frameWidth, frameHeight));
            upFrames.add(new TextureRegion(characterSheet, col * frameWidth, 2 * frameHeight, frameWidth, frameHeight));
            leftFrames.add(new TextureRegion(characterSheet, col * frameWidth, 3 * frameHeight, frameWidth, frameHeight));
        }

        downAnim = new Animation<>(0.1f, downFrames);
        rightAnim = new Animation<>(0.1f, rightFrames);
        upAnim = new Animation<>(0.1f, upFrames);
        leftAnim = new Animation<>(0.1f, leftFrames);
    }

    private Body createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(1600, 800);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 1.0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    public void update(float delta) {
        stateTime += delta;
        Vector2 velocity = body.getLinearVelocity();
        isMoving = velocity.x != 0 || velocity.y != 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.setLinearVelocity(-MOVE_SPEED, velocity.y);
            currentDirection = "left";
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.setLinearVelocity(MOVE_SPEED, velocity.y);
            currentDirection = "right";
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            body.setLinearVelocity(velocity.x, MOVE_SPEED);
            currentDirection = "up";
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            body.setLinearVelocity(velocity.x, -MOVE_SPEED);
            currentDirection = "down";
        } else {
            body.setLinearVelocity(0, 0);
        }

        updateAnimation();
    }

    private void updateAnimation() {
        Animation<TextureRegion> currentAnimation = null;

        if (isMoving) {
            switch (currentDirection) {
                case "down":
                    currentAnimation = downAnim;
                    break;
                case "up":
                    currentAnimation = upAnim;
                    break;
                case "right":
                    currentAnimation = rightAnim;
                    break;
                case "left":
                    currentAnimation = leftAnim;
                    break;
            }
            sprite.setRegion(currentAnimation.getKeyFrame(stateTime, true));
        } else {
            sprite.setRegion(downAnim.getKeyFrame(0));
        }
    }

    public void render(SpriteBatch batch) {
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.draw(batch);
    }

    public Body getBody() {
        return body;
    }
}