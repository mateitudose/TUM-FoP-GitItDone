package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private final World gameWorld;
    private final SpriteBatch batch;
    private final MazeMap mazeMap;

    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        camera.setToOrtho(false);
        Box2D.init();
        gameWorld = new World(new Vector2(0, -10), true);
        batch = new SpriteBatch();

        // Load the maze map (10x10 grid)
        mazeMap = new MazeMap("basictiles.png", 50, 50);
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "Game started");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Render the maze map
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mazeMap.render(batch);
        batch.end();

        // Update world
        gameWorld.step(1 / 60f, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        gameWorld.dispose();
    }
}
