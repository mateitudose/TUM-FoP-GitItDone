package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainCharacter {
    private final Sprite mainCharacter;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean isMoving;
    private Direction direction;

    private enum Direction {
        DOWN, RIGHT, UP, LEFT
    }

    public MainCharacter(Animation<TextureRegion> downAnimation, Animation<TextureRegion> rightAnimation, Animation<TextureRegion> upAnimation, Animation<TextureRegion> leftAnimation) {
        this.currentAnimation = downAnimation;
        this.stateTime = 0f;
        this.isMoving = false;
        this.direction = Direction.DOWN;
        this.mainCharacter = loadCharacter();
    }

    private Sprite loadCharacter() {
        Texture characterSheet = new Texture(Gdx.files.internal("character.png"));
        TextureRegion characterTexture = new TextureRegion(characterSheet, 0, 0, 16, 32);
        Sprite sprite = new Sprite(characterTexture);
        sprite.setSize(80, 160);
        return sprite;
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
                    currentAnimation = game.getCharacterRightAnimation();
                    direction = Direction.RIGHT;
                    isMoving = true;
                    break;
                case LEFT:
                    mainCharacter.translateX(-speed * delta);
                    currentAnimation = game.getCharacterLeftAnimation();
                    direction = Direction.LEFT;
                    isMoving = true;
                    break;
                case UP:
                    mainCharacter.translateY(speed * delta);
                    currentAnimation = game.getCharacterUpAnimation();
                    direction = Direction.UP;
                    isMoving = true;
                    break;
                case DOWN:
                    mainCharacter.translateY(-speed * delta);
                    currentAnimation = game.getCharacterDownAnimation();
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
}