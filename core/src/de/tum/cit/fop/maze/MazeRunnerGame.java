package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.fop.maze.screens.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private MapSelectionScreen mapSelectionScreen;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private VictoryScreen victoryScreen;
    private PauseMenuScreen pauseMenuScreen;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));

        // Play some background music
        Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("menuMusic.mp3"));
        backgroundMusic.setLooping(true);
//        backgroundMusic.play();
        goToMenu();
    }

    public void goToMenu() {
        if (screen != null) {
            screen.dispose();
        }
        this.setScreen(new MenuScreen(this));
    }

    public void goToMapSelection() {
        if (screen != null) {
            screen.dispose();
        }
        this.setScreen(new MapSelectionScreen(this));
    }

    public void goToGame(String mapPath) {
        if (screen != null) {
            screen.dispose();
        }
        // Set the current screen to GameScreen and selected map path
        this.setScreen(new GameScreen(this, mapPath));
    }

    public void goToGameOver(String mapPath) {
        if (screen != null) {
            screen.dispose();
        }
        this.setScreen(new GameOverScreen(this, mapPath));
    }

    public void goToPauseMenu(GameScreen gameScreen) {
        // Don't dispose the game screen here, as we want to resume the game after the pause menu is closed
        this.setScreen(new PauseMenuScreen(this, gameScreen));
    }

    public void goToVictory() {
        if (screen != null) {
            screen.dispose();
        }
        this.setScreen(new VictoryScreen(this));
    }

    @Override
    public void dispose() {
        menuScreen.dispose();
        mapSelectionScreen.dispose();
        gameScreen.dispose();
        getScreen().hide();
        getScreen().dispose();
        spriteBatch.dispose();
        skin.dispose();
    }

    public Skin getSkin() {
        return skin;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
}
