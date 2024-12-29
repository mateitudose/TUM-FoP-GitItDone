package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;

    private Sprite mainCharacter;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean isMoving;
    private Direction direction;

    private enum Direction {
        DOWN, RIGHT, UP, LEFT
    }

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        this.mainCharacter = game.getMainCharacter();
        this.currentAnimation = game.getCharacterDownAnimation();
        this.stateTime = 0f;
        this.isMoving = false;
        this.direction = Direction.DOWN;

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.75f;

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");
    }

    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        input(delta);
        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        camera.update(); // Update the camera

        // Set up and begin drawing with the sprite batch
        SpriteBatch batch = game.getSpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        batch.begin(); // Important to call this before drawing anything

        if (isMoving) {
            stateTime += delta;
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, mainCharacter.getX(), mainCharacter.getY(), mainCharacter.getWidth(), mainCharacter.getHeight());
        } else {
            mainCharacter.draw(batch);
        }

        batch.end(); // Important to call this after drawing everything
    }

    private void input(float delta) {
        float speed = 100f;
        isMoving = false;

        // TODO: Observe how this changes when replacing ifs with a switch-case structure
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mainCharacter.translateX(speed * delta);
            currentAnimation = game.getCharacterRightAnimation();
            direction = Direction.RIGHT;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mainCharacter.translateY(-speed * delta);
            currentAnimation = game.getCharacterDownAnimation();
            direction = Direction.DOWN;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mainCharacter.translateX(-speed * delta);
            currentAnimation = game.getCharacterLeftAnimation();
            direction = Direction.LEFT;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mainCharacter.translateY(speed * delta);
            currentAnimation = game.getCharacterUpAnimation();
            direction = Direction.UP;
            isMoving = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}