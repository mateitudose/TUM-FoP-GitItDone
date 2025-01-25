package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

public class PauseMenuScreen implements Screen {

    private final Stage stage;
    private final MazeRunnerGame game;
    private final GameScreen previousScreen; // Changed to GameScreen for proper handling

    public PauseMenuScreen(MazeRunnerGame game, GameScreen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;

        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Pause menu UI
        table.add(new Label("Paused", game.getSkin(), "title")).padBottom(50).row();

        // Resume Button
        TextButton resumeButton = new TextButton("Resume", game.getSkin());
        table.add(resumeButton).width(300).padBottom(20).row();

        // Go to Map Selection Button
        TextButton mapSelectionButton = new TextButton("Select New Map", game.getSkin());
        table.add(mapSelectionButton).width(300).padBottom(20).row();

        // Exit Button
        TextButton exitButton = new TextButton("Exit Game", game.getSkin());
        table.add(exitButton).width(300).padBottom(20).row();

        // Listeners
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Stop background music and start maze music
                game.stopBackgroundMusic();
                game.playMazeMusic();
                // Resume the previous game screen
                game.setScreen(previousScreen);
                dispose();
            }
        });

        mapSelectionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMapSelection();
                previousScreen.dispose();
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

        // Unpause the game when the escape key is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.stopBackgroundMusic();
            game.playMazeMusic();
            game.setScreen(previousScreen);
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        previousScreen.pause();
    }

    @Override
    public void hide() {
        previousScreen.resume();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
