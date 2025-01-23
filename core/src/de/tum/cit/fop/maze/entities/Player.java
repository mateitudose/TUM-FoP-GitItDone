package de.tum.cit.fop.maze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.MazeMap;

public class Player extends GameEntity {
    private static final float MOVE_SPEED = 2.0f;
    public static final int PLAYER_LIVES = 3;
    private float speed;
    private World world;
    private Animation<TextureRegion> downAnim, upAnim, rightAnim, leftAnim;
    private MazeMap mazeMap;
    private float stateTime = 0f;
    private String currentDirection = "down";
    private boolean isMoving = false;
    private int lives;
    private int collectedFish = 0;

    public Player(World world, MazeMap mazeMap, Vector2 startPosition) {
        super((int) startPosition.x, (int) startPosition.y, 20, 20, new TextureRegion(new Texture(Gdx.files.internal("cat.png")), 0, 32, 32, 32));
        this.world = world;
        this.mazeMap = mazeMap;
        loadAnimations();
        this.body = createBody(startPosition);
        this.speed = MOVE_SPEED;
        this.lives = PLAYER_LIVES;
    }

    private void loadAnimations() {
        Texture characterSheet = new Texture(Gdx.files.internal("cat.png"));
        int frameWidth = 32, frameHeight = 32, frames = 4;

        Array<TextureRegion> downFrames = new Array<>(frames);
        Array<TextureRegion> rightFrames = new Array<>(frames);
        Array<TextureRegion> upFrames = new Array<>(frames);
        Array<TextureRegion> leftFrames = new Array<>(frames);

        for (int col = 0; col < frames; col++) {
            downFrames.add(new TextureRegion(characterSheet, (12 + col) * frameWidth, frameHeight, frameWidth, frameHeight));
            rightFrames.add(new TextureRegion(characterSheet, (12 + col) * frameWidth, 13 * frameHeight, frameWidth, frameHeight));
            upFrames.add(new TextureRegion(characterSheet, (12 + col) * frameWidth, 9 * frameHeight, frameWidth, frameHeight));
            leftFrames.add(new TextureRegion(characterSheet, (12 + col) * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
        }

        downAnim = new Animation<>(0.1f, downFrames);
        rightAnim = new Animation<>(0.1f, rightFrames);
        upAnim = new Animation<>(0.1f, upFrames);
        leftAnim = new Animation<>(0.1f, leftFrames);
    }

    private Body createBody(Vector2 startPosition) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startPosition);

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius((sprite.getWidth() / 2f) / MazeMap.TILE_SIZE * 0.6f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.filter.categoryBits = 0x0001;
        fixtureDef.filter.maskBits = 0x0002 | 0x0004;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);

        body.setUserData(this);
        body.setFixedRotation(true);

        shape.dispose();
        Gdx.app.postRunnable(() -> fixture.setSensor(false));

        return body;
    }

    @Override
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

        if (velX == 0 && velY == 0) {
            body.setLinearVelocity(0, 0);
        } else {
            body.setLinearVelocity(velX, velY);
        }

        updateAnimation();
    }

    private void updateAnimation() {
        Animation<TextureRegion> currentAnimation = switch (currentDirection) {
            case "up" -> upAnim;
            case "right" -> rightAnim;
            case "left" -> leftAnim;
            default -> downAnim;
        };

        if (isMoving) {
            sprite.setRegion(currentAnimation.getKeyFrame(stateTime, true));
        } else {
            // When stopping, use the first frame of the current direction's animation
            sprite.setRegion(currentAnimation.getKeyFrame(0));
            stateTime = 0;
        }
    }

    public void render(SpriteBatch batch) {
        float spriteX = body.getPosition().x * MazeMap.TILE_SIZE - sprite.getWidth() / 2f;
        float spriteY = body.getPosition().y * MazeMap.TILE_SIZE - sprite.getHeight() / 2f;

        sprite.setPosition(spriteX, spriteY);
        sprite.draw(batch);
    }

    public void loseLives(int lives) {
        this.lives -= lives;
    }

    public void addLives(int lives) {
        this.lives += lives;
    }

    public int getLives() {
        return lives;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public void collectFish() {
        collectedFish++;
    }

    public int getCollectedFish() {
        return collectedFish;
    }
}
