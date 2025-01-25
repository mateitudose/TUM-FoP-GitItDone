package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
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

/**
 * Displays the game over screen when the player loses.
 */
public class GameOverScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final String mapPath;
    private Sound buttonClickSound; // Declare the Sound object

    public GameOverScreen(MazeRunnerGame game, String mapPath) {
        this.mapPath = mapPath;
        this.game = game;
        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        Gdx.input.setInputProcessor(stage); // Set stage as input processor

        try {
            // Load the button click sound
            buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("assets/buttonsound.mp3"));
        } catch (Exception e) {
            System.err.println("Error loading sound file: " + e.getMessage());
        }

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title
        table.add(new Label("Game Over", game.getSkin(), "title")).padBottom(50).row();

        // Buttons
        TextButton retryButton = new TextButton("Retry", game.getSkin());
        table.add(retryButton).width(300).padBottom(20).row();

        TextButton mainMenuButton = new TextButton("Main Menu", game.getSkin());
        table.add(mainMenuButton).width(300).padBottom(20).row();

        TextButton exitButton = new TextButton("Exit", game.getSkin());
        table.add(exitButton).width(300);

        // Listeners with delay and sound
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playButtonClickSound();

                // Delay transition by 0.3 seconds
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        game.goToGame(mapPath); // Restart the game
                    }
                }, 0.3f);
            }
        });

        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playButtonClickSound();

                // Delay transition by 0.3 seconds
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        game.goToMenu(); // Navigate to the main menu
                    }
                }, 0.3f);
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playButtonClickSound();

                // Delay transition by 0.3 seconds
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.exit(); // Exit the game
                    }
                }, 0.3f);
            }
        });
    }

    // Method to play the sound
    private void playButtonClickSound() {
        if (buttonClickSound != null) {
            buttonClickSound.play();
        } else {
            System.err.println("Button click sound not loaded.");
        }
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
        if (buttonClickSound != null) {
            buttonClickSound.dispose();
        }
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
