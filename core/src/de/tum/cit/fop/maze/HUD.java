package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.entities.Player;
import de.tum.cit.fop.maze.objects.ExitPoint;

/**
 * Represents the HUD in the maze game.
 * The HUD displays the player's lives, collected fish, and the direction to the nearest exit.
 */
public class HUD {
    private final Player player;
    private final MazeMap mazeMap;
    private final BitmapFont font;
    private final TextureRegion arrowTexture;
    private final TextureRegion fullHeartTexture;
    private final TextureRegion emptyHeartTexture;
    private final Vector2 exitDirection = new Vector2();

    /**
     * Constructs a new HUD object.
     *
     * @param player  the player whose status is displayed
     * @param mazeMap the maze map containing the exits
     */
    public HUD(Player player, MazeMap mazeMap) {
        this.player = player;
        this.mazeMap = mazeMap;
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);
        this.font.setColor(Color.WHITE);
        this.arrowTexture = new TextureRegion(new Texture("arrow.png"));
        TextureAtlas atlas = new TextureAtlas("world.atlas");
        TextureRegion[][] heartRegions = atlas.findRegion("objects").split(MazeMap.TILE_SIZE, MazeMap.TILE_SIZE);
        this.fullHeartTexture = heartRegions[0][4];
        this.emptyHeartTexture = heartRegions[0][8];
    }

    /**
     * Renders the HUD.
     *
     * @param batch          the SpriteBatch used for rendering
     * @param playerPosition the current position of the player
     */
    public void render(SpriteBatch batch, Vector2 playerPosition) {
        // Draw hearts for lives
        int lives = player.getLives();
        float heartX = 20;
        float heartY = Gdx.graphics.getHeight() - 40;
        int maxLives = Player.PLAYER_LIVES;

        for (int i = 0; i < maxLives; i++) {
            TextureRegion heart = (i < lives) ? fullHeartTexture : emptyHeartTexture;
            batch.draw(heart, heartX + (i * 30), heartY, 30, 30);
        }

        // Draw fish counter
        font.draw(batch, "Fish: " + player.getCollectedFish(), 20, heartY - 10);

        // Draw exit arrow
        Vector2 closestExit = findClosestExit(playerPosition);
        if (closestExit != null) {
            exitDirection.set(closestExit).sub(playerPosition);
            float angle = exitDirection.angleDeg();

            batch.draw(arrowTexture,
                    20,
                    heartY - 100,  // Position below fish counter
                    25, 25,  // Origin (center)
                    50, 50,  // Size
                    1, 1,
                    angle - 90);
        }
    }

    /**
     * Finds the closest exit to the player's current position.
     *
     * @param playerPosition the current position of the player
     * @return the position of the closest exit, or null if no exits are found
     */
    private Vector2 findClosestExit(Vector2 playerPosition) {
        Vector2 closest = null;
        float minDistance = Float.MAX_VALUE;

        for (ExitPoint exit : mazeMap.getExitPoints()) {
            Vector2 exitPos = new Vector2(
                    exit.getX() + 0.5f,
                    exit.getY() + 0.5f
            );
            float distance = exitPos.dst2(playerPosition);

            if (distance < minDistance) {
                minDistance = distance;
                closest = exitPos;
            }
        }
        return closest;
    }

    /**
     * Disposes of the resources used by the HUD.
     */
    public void dispose() {
        font.dispose();
        arrowTexture.getTexture().dispose();
    }
}
