package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

public class VictoryScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Texture backgroundTexture; // Texture for the background image
    private final SpriteBatch batch; // SpriteBatch to draw the background

    public VictoryScreen(MazeRunnerGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        batch = new SpriteBatch(); // Initialize SpriteBatch for drawing the background
        backgroundTexture = new Texture(Gdx.files.internal("assets/pixelart.png")); // Load the background image

        Gdx.input.setInputProcessor(stage); // Set stage as input processor

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title
        table.add(new Label("You Won!", game.getSkin(), "title")).padBottom(50).row();

        // Buttons
        TextButton mainMenuButton = new TextButton("Main Menu", game.getSkin());
        table.add(mainMenuButton).width(300).padBottom(20).row();

        TextButton exitButton = new TextButton("Exit", game.getSkin());
        table.add(exitButton).width(300);

        // Listeners
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Navigate to the main menu
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Exit the game
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background image
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Draw the background
        batch.end();

        // Update and draw the stage (UI elements)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        // Dispose resources
        backgroundTexture.dispose();
        batch.dispose();
        stage.dispose();
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
