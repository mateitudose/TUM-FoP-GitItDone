package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * Displays the victory screen when the player wins.
 */
public class VictoryScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Texture starTexture;

    /**
     * Constructs a new VictoryScreen object.
     *
     * @param game the main game class
     * @param fish the number of fish collected
     */
    public VictoryScreen(MazeRunnerGame game, int fish) {
        this.game = game;

        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        Gdx.input.setInputProcessor(stage);

        // Load the star texture
        starTexture = new Texture(Gdx.files.internal("assets/Star.png"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title
        table.add(new Label("You Won!", game.getSkin(), "title")).padBottom(50).row();

        // Display stars
        Table starTable = new Table();
        for (int i = 0; i < fish; i++) {
            starTable.add(new Image(starTexture)).pad(5);
        }
        table.add(starTable).padBottom(50).row();

        // Buttons
        TextButton mainMenuButton = new TextButton("Main Menu", game.getSkin());
        table.add(mainMenuButton).width(300).padBottom(20).row();

        TextButton exitButton = new TextButton("Exit", game.getSkin());
        table.add(exitButton).width(300);

        // Button listeners
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        starTexture.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
