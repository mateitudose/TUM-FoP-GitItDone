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
    private static final float MOVE_SPEED = 2.0f;
    private float speed;
    private World world;
    private final Sprite sprite;
    private Animation<TextureRegion> downAnim, upAnim, rightAnim, leftAnim;
    private final Body body;
    private MazeMap mazeMap;

    private float stateTime = 0f;
    private String currentDirection = "down";
    private boolean isMoving = false;

    public Player(World world, MazeMap mazeMap, Vector2 startPosition) {
        this.world = world;
        this.mazeMap = mazeMap;
        this.sprite = loadCharacter();
        loadAnimations();
        this.body = createBody(startPosition);
        this.speed = MOVE_SPEED;
    }

    private Sprite loadCharacter() {
        Texture characterSheet = new Texture(Gdx.files.internal("character.png"));
        Sprite sprite = new Sprite(new TextureRegion(characterSheet, 0, 0, 16, 32));
        sprite.setSize(12, 24);
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

    private Body createBody(Vector2 startPosition) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Vector2 safeStartPosition = new Vector2(startPosition.x, startPosition.y);
        bodyDef.position.set(safeStartPosition);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        float collisionWidth = (sprite.getWidth() / 2f) / MazeMap.TILE_SIZE * 0.9f;
        float collisionHeight = (sprite.getHeight() / 2f) / MazeMap.TILE_SIZE * 0.4f;

        Vector2 center = new Vector2(0, -collisionHeight * 0.7f); // Offset down from center
        shape.setAsBox(collisionWidth, collisionHeight, center, 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 1.0f;

        fixtureDef.filter.categoryBits = 0x0001; // Player category
        fixtureDef.filter.maskBits = 0x0002;    // Collision with walls

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true); // Temporarily disable collision

        body.setUserData("Player");
        body.setFixedRotation(true);
        shape.dispose();
        Gdx.app.postRunnable(() -> fixture.setSensor(false));

        return body;
    }

    public void update(float delta) {
        stateTime += delta;

        Vector2 position = body.getPosition();
        float clampedX = Math.max(0.5f, Math.min(position.x, mazeMap.getMazeWidth() - 0.5f));
        float clampedY = Math.max(0.5f, Math.min(position.y, mazeMap.getMazeHeight() - 0.5f));

        if (position.x != clampedX || position.y != clampedY) {
            body.setTransform(clampedX, clampedY, body.getAngle());
        }

        Vector2 velocity = body.getLinearVelocity();
        isMoving = velocity.x != 0 || velocity.y != 0;

        // Check for Shift key to boost speed
        float effectiveSpeed = speed;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            effectiveSpeed *= 2;
        }

        float velX = 0, velY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -effectiveSpeed;
            currentDirection = "left";
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = effectiveSpeed;
            currentDirection = "right";
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velY = effectiveSpeed;
            currentDirection = "up";
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velY = -effectiveSpeed;
            currentDirection = "down";
        }

        // Set velocity to zero if no movement keys are pressed
        if (velX == 0 && velY == 0) {
            body.setLinearVelocity(0, 0);
        } else {
            body.setLinearVelocity(velX, velY);
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
            stateTime = 0;
        }
    }

    public void render(SpriteBatch batch) {
        // Set sprite position to center around the Box2D body
        float spriteX = body.getPosition().x * MazeMap.TILE_SIZE - sprite.getWidth() / 2f;
        float spriteY = body.getPosition().y * MazeMap.TILE_SIZE - sprite.getHeight() / 2f;

        sprite.setPosition(spriteX, spriteY);
        sprite.draw(batch);
    }

    public Body getBody() {
        return body;
    }

}
