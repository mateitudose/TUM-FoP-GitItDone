package de.tum.cit.fop.maze;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {
    private Music mazeMusic;
    private Music menuMusic;
    private boolean isPaused;

    public MusicManager(String mazeMusicPath, String menuMusicPath) {
        try {
            mazeMusic = Gdx.audio.newMusic(Gdx.files.internal(mazeMusicPath));
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal(menuMusicPath));
        } catch (Exception e) {
            Gdx.app.error("MusicManager", "Failed to load music files.", e);
        }

        if (mazeMusic != null) mazeMusic.setLooping(true);
        if (menuMusic != null) menuMusic.setLooping(true);

        isPaused = false;
    }

    public void playMazeMusic() {
        if (isPaused || mazeMusic == null) return;
        stopAllMusic();
        mazeMusic.play();
    }

    public void playMenuMusic() {
        if (isPaused || menuMusic == null) return;
        stopAllMusic();
        menuMusic.play();
    }

    public void pauseAllMusic() {
        isPaused = true;
        if (mazeMusic != null && mazeMusic.isPlaying()) mazeMusic.pause();
        if (menuMusic != null && menuMusic.isPlaying()) menuMusic.pause();
    }

    public void resumeMusic() {
        isPaused = false;
        if (mazeMusic != null && !mazeMusic.isPlaying()) mazeMusic.play();
        if (menuMusic != null && !menuMusic.isPlaying()) menuMusic.play();
    }

    public void stopAllMusic() {
        if (mazeMusic != null) mazeMusic.stop();
        if (menuMusic != null) menuMusic.stop();
    }

    public void setVolume(float volume) {
        if (mazeMusic != null) mazeMusic.setVolume(volume);
        if (menuMusic != null) menuMusic.setVolume(volume);
    }

    public void dispose() {
        if (mazeMusic != null) mazeMusic.dispose();
        if (menuMusic != null) menuMusic.dispose();
    }
}
