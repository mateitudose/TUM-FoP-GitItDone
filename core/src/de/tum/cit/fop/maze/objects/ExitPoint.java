package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.entities.Player;

public class ExitPoint extends GameObject {
    public ExitPoint(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, tileSize, texture);
    }

    public boolean checkIfPlayerReachedExit(Player player, MazeRunnerGame game) {
        Vector2 playerPosition = player.getBody().getPosition();
        if (Math.abs(playerPosition.x - (this.getX() + 0.5)) < 0.5f &&
                Math.abs(playerPosition.y - (this.getY() + 0.5)) < 0.5f) {
            game.goToVictory();
            return true;
        }
        return false;
    }
}
