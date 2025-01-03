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
    private final World world;
    private final Sprite sprite;
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> characterLeftAnimation;
    private final Body body;

    private float stateTime = 0f;
    private String currentDirection = "down";  // Default direction

    /**
     * Constructor for the Player class.
     * @param world
     */
    public Player(World world) {
        this.world = world;

        // Load the character texture and animations
        this.sprite = loadCharacter();
        loadCharacterAnimations();

        // Create Box2D Body for the player
        this.body = createPlayerBody();
    }

    /**
     * Load the character sprite from the character.png file.
     * @return The character sprite.
     */
    private Sprite loadCharacter() {
        Texture characterSheet = new Texture(Gdx.files.internal("character.png"));
        TextureRegion characterTexture = new TextureRegion(characterSheet, 0, 0, 16, 32);
        Sprite sprite = new Sprite(characterTexture);
        sprite.setSize(16, 32);
        return sprite;
    }

    private void loadCharacterAnimations() {
        Texture characterSheet = new Texture(Gdx.files.internal("character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFramesDown = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkFramesRight = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkFramesUp = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkFramesLeft = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        for (int col = 0; col < animationFrames; col++) {
            walkFramesDown.add(new TextureRegion(characterSheet, col * frameWidth, 0, frameWidth, frameHeight));
            walkFramesRight.add(new TextureRegion(characterSheet, col * frameWidth, frameHeight, frameWidth, frameHeight));
            walkFramesUp.add(new TextureRegion(characterSheet, col * frameWidth, 2 * frameHeight, frameWidth, frameHeight));
            walkFramesLeft.add(new TextureRegion(characterSheet, col * frameWidth, 3 * frameHeight, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkFramesDown);
        characterRightAnimation = new Animation<>(0.1f, walkFramesRight);
        characterUpAnimation = new Animation<>(0.1f, walkFramesUp);
        characterLeftAnimation = new Animation<>(0.1f, walkFramesLeft);
    }

    private Body createPlayerBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(2, 2);  // Start at some position (in meters)

        // Create the body in the world
        Body body = world.createBody(bodyDef);

        // Define a box shape for the player
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 1.0f);  // Set the box size (half width and full height)

        // Define the fixture (collisions)
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;

        // Attach the fixture to the body
        body.createFixture(fixtureDef);

        // Dispose of the shape (no longer needed after fixture is created)
        shape.dispose();

        return body;
    }

    public void update(float delta) {
        // Update state time for animations
        stateTime += delta;

        // Handle keyboard input for movement
        Vector2 velocity = body.getLinearVelocity();
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
            body.setLinearVelocity(0, 0);  // No movement if no keys are pressed
        }

        // Update sprite to show the correct animation
        updateAnimation();
    }

    private void updateAnimation() {
        Animation<TextureRegion> currentAnimation = null;

        switch (currentDirection) {
            case "down":
                currentAnimation = characterDownAnimation;
                break;
            case "up":
                currentAnimation = characterUpAnimation;
                break;
            case "right":
                currentAnimation = characterRightAnimation;
                break;
            case "left":
                currentAnimation = characterLeftAnimation;
                break;
        }

        // Set the current frame of the animation
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(currentFrame);
    }

    public void render(SpriteBatch batch) {
        // Render the sprite
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.draw(batch);
    }
}
