package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
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
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin

        // Play some background music
        // Background sound
        goToMenu();
    }

    /**
     * Switches to the menu screen.
     */
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

    /**
     * Switches to the game screen.
     */
    public void goToGame(String mapPath) {
        if (screen != null) {
            screen.dispose();
        }
        this.setScreen(new GameScreen(this, mapPath)); // Set the current screen to GameScreen
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        menuScreen.dispose();
        mapSelectionScreen.dispose();
        gameScreen.dispose();
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    public void goToVictory() {
        this.setScreen(new VictoryScreen(this));
    }

    // Getter methods
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
