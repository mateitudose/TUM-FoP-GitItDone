package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class MainCharacter {
    private final Sprite mainCharacter;
    private Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterLeftAnimation;
    private float stateTime;
    private boolean isMoving;
    private Direction direction;

    private enum Direction {
        DOWN, RIGHT, UP, LEFT
    }

    public MainCharacter() {
        this.stateTime = 0f;
        this.isMoving = false;
        this.direction = Direction.DOWN;
        this.mainCharacter = loadCharacter();
        loadCharacterAnimations();
        this.currentAnimation = characterDownAnimation;
    }

    public Sprite loadCharacter() {
        Texture characterSheet = new Texture(Gdx.files.internal("character.png"));
        TextureRegion characterTexture = new TextureRegion(characterSheet, 0, 0, 16, 32);
        Sprite sprite = new Sprite(characterTexture);
        sprite.setSize(80, 160);
        return sprite;
    }

    /**
     * Loads the character animation from the character.png file.
     */
    public void loadCharacterAnimations() {
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
        }
        for (int col = 0; col < animationFrames; col++) {
            walkFramesRight.add(new TextureRegion(characterSheet, col * frameWidth, frameHeight, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkFramesUp.add(new TextureRegion(characterSheet, col * frameWidth, 2 * frameHeight, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkFramesLeft.add(new TextureRegion(characterSheet, col * frameWidth, 3 * frameHeight, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkFramesDown);
        characterRightAnimation = new Animation<>(0.1f, walkFramesRight);
        characterUpAnimation = new Animation<>(0.1f, walkFramesUp);
        characterLeftAnimation = new Animation<>(0.1f, walkFramesLeft);
    }

    public void update(float delta, MazeRunnerGame game) {
        float speed = 100f;
        isMoving = false;

        Direction newDirection = null;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newDirection = Direction.RIGHT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newDirection = Direction.LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            newDirection = Direction.UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            newDirection = Direction.DOWN;
        }

        if (newDirection != null) {
            switch (newDirection) {
                case RIGHT:
                    mainCharacter.translateX(speed * delta);
                    currentAnimation = getCharacterRightAnimation();
                    direction = Direction.RIGHT;
                    isMoving = true;
                    break;
                case LEFT:
                    mainCharacter.translateX(-speed * delta);
                    currentAnimation = getCharacterLeftAnimation();
                    direction = Direction.LEFT;
                    isMoving = true;
                    break;
                case UP:
                    mainCharacter.translateY(speed * delta);
                    currentAnimation = getCharacterUpAnimation();
                    direction = Direction.UP;
                    isMoving = true;
                    break;
                case DOWN:
                    mainCharacter.translateY(-speed * delta);
                    currentAnimation = getCharacterDownAnimation();
                    direction = Direction.DOWN;
                    isMoving = true;
                    break;
            }
        }
    }

    public void render(SpriteBatch batch, float delta) {
        if (isMoving) {
            stateTime += delta;
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, mainCharacter.getX(), mainCharacter.getY(), mainCharacter.getWidth(), mainCharacter.getHeight());
        } else {
            mainCharacter.draw(batch);
        }
    }

    public Sprite getMainCharacter() {
        return mainCharacter;
    }

    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public float getStateTime() {
        return stateTime;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public Direction getDirection() {
        return direction;
    }
}