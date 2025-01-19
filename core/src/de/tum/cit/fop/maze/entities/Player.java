package de.tum.cit.fop.maze.entities;

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
import de.tum.cit.fop.maze.MazeMap;

public class Player extends GameEntity {
    private static final float MOVE_SPEED = 2.0f;
    private float speed;
    private World world;
    private final Sprite sprite;
    private Animation<TextureRegion> downAnim, upAnim, rightAnim, leftAnim;
    private MazeMap mazeMap;

    private float stateTime = 0f;
    private String currentDirection = "down";
    private boolean isMoving = false;

    public Player(World world, MazeMap mazeMap, Vector2 startPosition) {
        super((int) startPosition.x, (int) startPosition.y, MazeMap.TILE_SIZE, null);
        this.world = world;
        this.mazeMap = mazeMap;
        this.sprite = loadCharacter();
        loadAnimations();
        this.body = createBody(startPosition);
        this.speed = MOVE_SPEED;
    }

    private Sprite loadCharacter() {
        Texture characterSheet = new Texture(Gdx.files.internal("cat.png"));
        Sprite sprite = new Sprite(new TextureRegion(characterSheet, 0, 32, 32, 32));
        sprite.setSize(22, 22);
        return sprite;
    }

    private void loadAnimations() {
        Texture characterSheet = new Texture(Gdx.files.internal("cat.png"));
        int frameWidth = 32, frameHeight = 32, frames = 4;

        Array<TextureRegion> downFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> rightFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> upFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> leftFrames = new Array<>(TextureRegion.class);

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
        float radius = (sprite.getWidth() / 2f) / MazeMap.TILE_SIZE * 0.6f;
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;

        fixtureDef.filter.categoryBits = 0x0001;
        fixtureDef.filter.maskBits = 0x0002;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);

        body.setUserData("Player");
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

        boolean touchingWall = false;
        for (Contact contact : body.getWorld().getContactList()) {
            if (contact.isTouching() && (contact.getFixtureA().getBody() == body ||
                    contact.getFixtureB().getBody() == body)) {
                touchingWall = true;
                break;
            }
        }

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

            if (touchingWall) {
                Vector2 movementDir = new Vector2(velX, velY).nor();
                float slipForce = 2.0f;
                body.applyForceToCenter(movementDir.scl(slipForce), true);

                Vector2 perpForce = new Vector2(-movementDir.y, movementDir.x).scl(0.5f);
                body.applyForceToCenter(perpForce, true);
            }
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
        float spriteX = body.getPosition().x * MazeMap.TILE_SIZE - sprite.getWidth() / 2f;
        float spriteY = body.getPosition().y * MazeMap.TILE_SIZE - sprite.getHeight() / 2f;

        sprite.setPosition(spriteX, spriteY);
        sprite.draw(batch);
    }
}
