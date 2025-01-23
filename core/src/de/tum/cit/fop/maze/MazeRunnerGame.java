
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

    // Background Music
    private Music backgroundMusic;
    private Music mazeMusic; // Music for the maze screen

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch, Skin, and music.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));

        // Initialize the background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("menuMusic.mp3"));
        backgroundMusic.setLooping(true);

        // Initialize the maze music
        mazeMusic = Gdx.audio.newMusic(Gdx.files.internal("mazeMusic.mp3"));
        mazeMusic.setLooping(true);

        goToMenu();
    }

    public void goToMenu() {
        if (screen != null) {
            screen.dispose();
        }
        // Play menu music when entering the menu screen
        playBackgroundMusic();
        this.setScreen(new MenuScreen(this));
    }

    public void goToMapSelection() {
        if (screen != null) {
            screen.dispose();
        }
        // Ensure background music continues in the map selection screen
        playBackgroundMusic();
        this.setScreen(new MapSelectionScreen(this));
    }

    public void goToGame(String mapPath) {
        if (screen != null) {
            screen.dispose();
        }
        // Stop background music and play maze music when entering the game screen
        stopBackgroundMusic();
        playMazeMusic();
        this.setScreen(new GameScreen(this, mapPath));
    }

    public void goToGameOver(String mapPath) {
        if (screen != null) {
            screen.dispose();
        }
        // Stop all music when entering the game over screen
        stopAllMusic();
        this.setScreen(new GameOverScreen(this, mapPath));
    }

    public void goToPauseMenu(GameScreen gameScreen) {
        // Stop all music when entering the pause menu screen
        stopAllMusic();
        this.setScreen(new PauseMenuScreen(this, gameScreen));
    }

    public void goToVictory() {
        if (screen != null) {
            screen.dispose();
        }
        // Stop all music when entering the victory screen
        stopAllMusic();
        this.setScreen(new VictoryScreen(this));
    }

    private void playBackgroundMusic() {
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    public void playMazeMusic() {
        if (!mazeMusic.isPlaying()) {
            mazeMusic.play();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    private void stopMazeMusic() {
        if (mazeMusic.isPlaying()) {
            mazeMusic.stop();
        }
    }

    private void stopAllMusic() {
        stopBackgroundMusic();
        stopMazeMusic();
    }

    @Override
    public void dispose() {
        // Dispose of global resources
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        if (mazeMusic != null) {
            mazeMusic.dispose();
        }
        if (spriteBatch != null) spriteBatch.dispose();
        if (skin != null) skin.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }

    public Skin getSkin() {
        return skin;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
}
