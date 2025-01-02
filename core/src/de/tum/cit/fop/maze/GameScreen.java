package de.tum.cit.fop.maze;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private final BitmapFont font;
    private final MainCharacter mainCharacter;

    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        this.mainCharacter = new MainCharacter();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        camera.setToOrtho(false);
        camera.zoom = 1f;
        font = game.getSkin().getFont("font");
    }

    @Override
    public void render(float delta) {
        mainCharacter.update(delta, game);
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        SpriteBatch batch = game.getSpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mainCharacter.render(batch, delta);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
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