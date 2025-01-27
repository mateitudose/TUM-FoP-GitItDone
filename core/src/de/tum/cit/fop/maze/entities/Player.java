package de.tum.cit.fop.maze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.MazeMap;

/**
 * Represents the player entity in the maze game.
 */
public class Player extends GameEntity {
    private static final float MOVE_SPEED = 4.0f;
    public static final int PLAYER_LIVES = 4;
    private float speed;
    private World world;
    private Animation<TextureRegion> downAnim, upAnim, rightAnim, leftAnim, rotatingAnim;
    private MazeMap mazeMap;
    private float stateTime = 0f;
    private String currentDirection = "down";
    private boolean isMoving = false;
    private int lives;
    private int collectedFish = 0;
    private float slowTimer;

    private float damageEffectTimer = 0f;
    private static final float DAMAGE_EFFECT_DURATION = 0.5f;
    private final Color damageColor = new Color(1, 0.3f, 0.3f, 1); // Red tint color
    private float contactTimer = 0f;
    private static final float CONTACT_DAMAGE_INTERVAL = 1.0f;

    private float abilityActiveTimer = 0f;
    private boolean abilityActive = false;

    /**
     * Constructs a new Player.
     *
     * @param world         the Box2D world the player belongs to
     * @param mazeMap       the maze map the player navigates
     * @param startPosition the starting position of the player
     */
    public Player(World world, MazeMap mazeMap, Vector2 startPosition) {
        super((int) startPosition.x, (int) startPosition.y, 20, 20, new TextureRegion(new Texture(Gdx.files.internal("cat.png")), 0, 32, 32, 32));
        this.world = world;
        this.mazeMap = mazeMap;
        loadAnimations();
        this.body = createBody(startPosition);
        this.speed = MOVE_SPEED;
        this.lives = PLAYER_LIVES;
        this.slowTimer = 0;
    }

    /**
     * Loads the animations for the player character.
     */
    private void loadAnimations() {
        Texture characterSheet = new Texture(Gdx.files.internal("cat.png"));
        int frameWidth = 32, frameHeight = 32, frames = 4;

        Array<TextureRegion> downFrames = new Array<>(frames);
        Array<TextureRegion> rightFrames = new Array<>(frames);
        Array<TextureRegion> upFrames = new Array<>(frames);
        Array<TextureRegion> leftFrames = new Array<>(frames);
        Array<TextureRegion> rotatingFrames = new Array<>(frames + 2);

        for (int col = 0; col < frames; col++) {
            downFrames.add(new TextureRegion(characterSheet, (12 + col) * frameWidth, frameHeight, frameWidth, frameHeight));
            rightFrames.add(new TextureRegion(characterSheet, (12 + col) * frameWidth, 13 * frameHeight, frameWidth, frameHeight));
            upFrames.add(new TextureRegion(characterSheet, (12 + col) * frameWidth, 9 * frameHeight, frameWidth, frameHeight));
            leftFrames.add(new TextureRegion(characterSheet, (12 + col) * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
        }

        rotatingFrames.add(new TextureRegion(characterSheet, 11 * frameWidth, 16 * frameHeight, frameWidth, frameHeight));
        rotatingFrames.add(new TextureRegion(characterSheet, 11 * frameWidth, 14 * frameHeight, frameWidth, frameHeight));
        rotatingFrames.add(new TextureRegion(characterSheet, 11 * frameWidth, 12 * frameHeight, frameWidth, frameHeight));
        rotatingFrames.add(new TextureRegion(characterSheet, 11 * frameWidth, 8 * frameHeight, frameWidth, frameHeight));
        rotatingFrames.add(new TextureRegion(characterSheet, 11 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));
        rotatingFrames.add(new TextureRegion(characterSheet, 11 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));

        downAnim = new Animation<>(0.1f, downFrames);
        rightAnim = new Animation<>(0.1f, rightFrames);
        upAnim = new Animation<>(0.1f, upFrames);
        leftAnim = new Animation<>(0.1f, leftFrames);
        rotatingAnim = new Animation<>(0.05f, rotatingFrames);
    }

    /**
     * Creates the Box2D body for the player.
     *
     * @param startPosition the starting position of the player
     * @return the created Box2D body
     */
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
        fixtureDef.filter.maskBits = 0x0002 | 0x0004 | 0x0008 | 0x0010 | 0x0009 | 0x0011; // Collide with walls, fish, slow tiles, hearts, ability and enemies

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);

        body.setUserData(this);
        body.setFixedRotation(true);

        shape.dispose();
        Gdx.app.postRunnable(() -> fixture.setSensor(false));

        return body;
    }

    /**
     * Updates the player's state.
     *
     * @param delta the time elapsed since the last update
     */
    @Override
    public void update(float delta) {
        stateTime += delta;

        // Check if the ability is active and update the timer
        if (abilityActive) {
            abilityActiveTimer -= delta;
            if (abilityActiveTimer <= 0) {
                abilityActive = false;
            }
        }

        if (damageEffectTimer > 0) {
            damageEffectTimer = Math.max(0, damageEffectTimer - delta);
            // Calculate color interpolation, from white to red
            float progress = damageEffectTimer / DAMAGE_EFFECT_DURATION;
            Color currentColor = new Color(Color.WHITE).lerp(damageColor, progress);
            sprite.setColor(currentColor);
        } else {
            sprite.setColor(Color.WHITE);
        }

        Vector2 position = body.getPosition();
        float clampedX = Math.max(0.5f, Math.min(position.x, mazeMap.getMazeWidth() - 0.5f));
        float clampedY = Math.max(0.5f, Math.min(position.y, mazeMap.getMazeHeight() - 0.5f));

        // Clamp the player's position to the maze boundaries
        if (position.x != clampedX || position.y != clampedY) {
            body.setTransform(clampedX, clampedY, body.getAngle());
        }

        Vector2 velocity = body.getLinearVelocity();
        isMoving = velocity.x != 0 || velocity.y != 0;

        // Still affected by slow effect
        if (slowTimer > 0) {
            slowTimer -= delta;
            if (slowTimer <= 0) {
                speed = MOVE_SPEED;
            }
        }

        float effectiveSpeed = speed;
        // Double the speed if the player is holding the shift key and the slow effect is not active
        if ((Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) && slowTimer <= 0) {
            effectiveSpeed *= 2;
        }

        // TODO: Modify the key bindings to use the arrow keys
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

        // Check for contact with enemies
        if (isInContactWithEnemy()) {
            contactTimer += delta;
            // If enemy contact is maintained for a certain time, lose again a life
            if (contactTimer >= CONTACT_DAMAGE_INTERVAL) {
                loseLives(1);
                contactTimer = 0f;
            }
        } else {
            contactTimer = 0f;
        }

        updateAnimation();
    }

    /**
     * Checks if the player is in contact with an enemy.
     *
     * @return true if the player is in contact with an enemy, false otherwise
     */
    private boolean isInContactWithEnemy() {
        for (Contact contact : world.getContactList()) {
            if (contact.isTouching()) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if ((fixtureA.getBody().getUserData() instanceof Enemy && fixtureB.getBody().getUserData() == this) ||
                        (fixtureB.getBody().getUserData() instanceof Enemy && fixtureA.getBody().getUserData() == this)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates the player's animation based on the current direction and movement state.
     */
    private void updateAnimation() {
        if (abilityActive) {
            sprite.setRegion(rotatingAnim.getKeyFrame(stateTime, true));
        } else {
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
    }

    /**
     * Renders the player sprite.
     *
     * @param batch the SpriteBatch used for drawing
     */
    public void render(SpriteBatch batch) {
        float spriteX = body.getPosition().x * MazeMap.TILE_SIZE - sprite.getWidth() / 2f;
        float spriteY = body.getPosition().y * MazeMap.TILE_SIZE - sprite.getHeight() / 2f;

        sprite.setPosition(spriteX, spriteY);
        sprite.draw(batch);
    }

    /**
     * Reduces the player's lives by a specified amount.
     *
     * @param lives the number of lives to lose
     */
    public void loseLives(int lives) {
        this.lives -= lives;
        this.damageEffectTimer = DAMAGE_EFFECT_DURATION;
    }

    /**
     * Adds a specified number of lives to the player.
     *
     * @param lives the number of lives to add
     */
    public void addLives(int lives) {
        this.lives += lives;
    }

    /**
     * Gets the number of lives the player has.
     *
     * @return the number of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Checks if the player is alive.
     *
     * @return true if the player has more than 0 lives, false otherwise
     */
    public boolean isAlive() {
        return lives > 0;
    }

    /**
     * Increments the number of collected fish by one.
     */
    public void collectFish() {
        collectedFish++;
    }

    /**
     * Gets the number of collected fish.
     *
     * @return the number of collected fish
     */
    public int getCollectedFish() {
        return collectedFish;
    }

    /**
     * Applies a slow effect to the player for a specified duration.
     *
     * @param duration the duration of the slow effect
     */
    public void applySlowEffect(float duration) {
        this.speed = MOVE_SPEED / 3f;
        this.slowTimer = duration;
    }

    /**
     * Checks if the player can gain an additional life.
     *
     * @return true if the player can gain an additional life, false otherwise
     */
    public boolean canGainLife() {
        return lives < PLAYER_LIVES;
    }

    /**
     * Adds one life to the player if possible.
     */
    public void addLife() {
        if (canGainLife()) {
            lives++;
        }
    }

    /**
     * Checks if the player can take damage.
     *
     * @return true if the player can take damage, false otherwise
     */
    public boolean canTakeDamage() {
        return damageEffectTimer <= 0;
    }

    public void startAbility() {
        abilityActive = true;
        abilityActiveTimer = 6f;
    }

    public boolean isAbilityActive() {
        return abilityActive;
    }
}
