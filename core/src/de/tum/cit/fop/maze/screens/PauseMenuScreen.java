package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

public class PauseMenuScreen implements Screen {

    private final Stage stage;
    private final MazeRunnerGame game;
    private final GameScreen previousScreen;
    private final Sound buttonClickSound;

    public PauseMenuScreen(MazeRunnerGame game, GameScreen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;

        // Load sound effect
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("assets/buttonsound.mp3")); // Ensure file path is correct

        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Pause Menu UI
        table.add(new Label("Paused", game.getSkin(), "title"))
                .padBottom(50)
                .row();

        // Resume Button
        TextButton resumeButton = new TextButton("Resume", game.getSkin());
        table.add(resumeButton)
                .width(300)
                .padBottom(20)
                .row();

        // Map Selection Button
        TextButton mapSelectionButton = new TextButton("Select New Map", game.getSkin());
        table.add(mapSelectionButton)
                .width(300)
                .padBottom(20)
                .row();

        // Exit Button
        TextButton exitButton = new TextButton("Exit Game", game.getSkin());
        table.add(exitButton)
                .width(300)
                .padBottom(20)
                .row();

        // Button Listeners
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleButtonWithSound(() -> handleResume());
            }
        });

        mapSelectionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleButtonWithSound(() -> {
                    game.goToMapSelection();
                    dispose();
                });
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleButtonWithSound(() -> Gdx.app.exit());
            }
        });
    }

    private void handleButtonWithSound(Runnable action) {
        // Play the button click sound
        buttonClickSound.play();

        // Add a delay before executing the action
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                action.run();
            }
        }, 0.3f); // 0.3 seconds delay
    }

    private void handleResume() {
        if (previousScreen != null) {
            game.stopBackgroundMusic();
            game.playMazeMusic();
            game.setScreen(previousScreen);
        } else {
            System.err.println("Error: Previous screen is null.");
        }
        dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handleButtonWithSound(this::handleResume);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        buttonClickSound.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (previousScreen != null) {
            previousScreen.pause();
        }
    }

    @Override
    public void hide() {
        if (previousScreen != null) {
            previousScreen.resume();
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
