package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    private static final float CAMERA_ZOOM_SPEED = 0.02f;
    private static final float MIN_ZOOM = 0.75f;
    private static final float MAX_ZOOM = 1.25f;

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private final World gameWorld;
    private final SpriteBatch batch;
    private final MazeMap mazeMap;
    private final Player player;

    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        camera.setToOrtho(false);
        Box2D.init();
        gameWorld = new World(new Vector2(0, 0), true);
        batch = new SpriteBatch();

        // Load the maze map (100x100 grid)
        mazeMap = new MazeMap("basictiles.png", 490, 840);

        // Create the player
        player = new Player(gameWorld);
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "Game started");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Update player
        player.update(delta);

        // Update the camera position
        updateCamera();

        // Render the maze
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mazeMap.render(batch);

        // Render the player
        player.render(batch);
        batch.end();

        // Update Box2D world
        gameWorld.step(1 / 60f, 6, 2);
    }

    private void updateCamera() {
        // Get the player's position
        Vector2 playerPosition = player.getBody().getPosition();

        // Update camera position to follow the player
        camera.position.lerp(new Vector3(playerPosition.x, playerPosition.y, 0), 0.1f);

        // Ensure the player remains within the middle 80% of the screen
        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();

        float leftBound = camera.position.x - screenWidth * 0.4f;
        float rightBound = camera.position.x + screenWidth * 0.4f;
        float bottomBound = camera.position.y - screenHeight * 0.4f;
        float topBound = camera.position.y + screenHeight * 0.4f;

        if (playerPosition.x < leftBound) {
            camera.position.x = playerPosition.x + screenWidth * 0.4f;
        } else if (playerPosition.x > rightBound) {
            camera.position.x = playerPosition.x - screenWidth * 0.4f;
        }

        if (playerPosition.y < bottomBound) {
            camera.position.y = playerPosition.y + screenHeight * 0.4f;
        } else if (playerPosition.y > topBound) {
            camera.position.y = playerPosition.y - screenHeight * 0.4f;
        }

        // Handle zoom functionality
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += CAMERA_ZOOM_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= CAMERA_ZOOM_SPEED;
        }

        // Clamp zoom level
        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, camera.zoom));

        // Update camera
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        gameWorld.dispose();
    }
}
