package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.fop.maze.screens.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private MapSelectionScreen mapSelectionScreen;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private VictoryScreen victoryScreen;
    private PauseMenuScreen pauseMenuScreen;
    private float mazeMusicPosition;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    // Background Music
    private Music backgroundMusic;
    private Music mazeMusic;
    private Music abilityMusic;
    private Music victoryMusic; // New Music for Victory
    private Music gameOverMusic; // New Music for Game Over

    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

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

        // Initialize the ability music
        abilityMusic = Gdx.audio.newMusic(Gdx.files.internal("superability_audio.mp3"));
        abilityMusic.setLooping(false);

        // Initialize victory music
        victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("gamewon.mp3"));
        victoryMusic.setLooping(false); // Play once

        // Initialize game over music
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("gameover.mp3"));
        gameOverMusic.setLooping(false); // Play once

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
        // Stop all music and play Game Over music
        stopAllMusic();
        playGameOverMusic();
        this.setScreen(new GameOverScreen(this, mapPath));
    }

    public void goToVictory() {
        if (screen != null) {
            screen.dispose();
        }
        // Stop all music and play Victory music
        stopAllMusic();
        playVictoryMusic();
        this.setScreen(new VictoryScreen(this));
    }

    public void goToPauseMenu(GameScreen gameScreen) {
        // Stop all music when entering the pause menu screen
        stopAllMusic();
        this.setScreen(new PauseMenuScreen(this, gameScreen));
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

    public void stopMazeMusic() {
        if (mazeMusic.isPlaying()) {
            mazeMusic.stop();
        }
    }

    public void playVictoryMusic() {
        if (!victoryMusic.isPlaying()) {
            victoryMusic.play();
        }
    }

    public void stopVictoryMusic() {
        if (victoryMusic.isPlaying()) {
            victoryMusic.stop();
        }
    }

    public void playGameOverMusic() {
        if (!gameOverMusic.isPlaying()) {
            gameOverMusic.play();
        }
    }

    public void stopGameOverMusic() {
        if (gameOverMusic.isPlaying()) {
            gameOverMusic.stop();
        }
    }

    private void stopAllMusic() {
        stopBackgroundMusic();
        stopMazeMusic();
        stopVictoryMusic();
        stopGameOverMusic();
    }

    public void pauseMazeMusic() {
        if (mazeMusic.isPlaying()) {
            mazeMusicPosition = mazeMusic.getPosition();
            mazeMusic.pause();
        }
    }

    public void resumeMazeMusic() {
        if (!mazeMusic.isPlaying()) {
            mazeMusic.setPosition(mazeMusicPosition);
            mazeMusic.play();
        }
    }

    public void playAbilityMusic() {
        if (!abilityMusic.isPlaying()) {
            abilityMusic.play();
        }
    }

    public void stopAbilityMusic() {
        if (abilityMusic.isPlaying()) {
            abilityMusic.stop();
        }
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
        if (abilityMusic != null) {
            abilityMusic.dispose();
        }
        if (victoryMusic != null) {
            victoryMusic.dispose();
        }
        if (gameOverMusic != null) {
            gameOverMusic.dispose();
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
