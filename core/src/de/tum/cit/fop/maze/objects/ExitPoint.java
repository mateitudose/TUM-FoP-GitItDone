package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.fop.maze.MazeMap;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.entities.Player;

/**
 * Represents the exit point in the maze game.
 */
public class ExitPoint extends GameObject {
    private Body body;

    /**
     * Constructs a new ExitPoint.
     *
     * @param x        the x-coordinate of the exit point
     * @param y        the y-coordinate of the exit point
     * @param tileSize the size of the tile
     * @param texture  the texture of the exit point
     * @param world    the Box2D world the exit point belongs to
     */
    public ExitPoint(int x, int y, int tileSize, TextureRegion texture, World world) {
        super(x, y, tileSize, tileSize, texture);
        createBody(world, x, y, tileSize);
    }

    /**
     * Creates the Box2D body for the exit point.
     *
     * @param world    the Box2D world the exit point belongs to
     * @param x        the x-coordinate of the exit point
     * @param y        the y-coordinate of the exit point
     * @param tileSize the size of the tile
     */
    private void createBody(World world, int x, int y, int tileSize) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        float posX = getX() + 0.5f;
        float posY = getY() + 0.5f;
        bodyDef.position.set(posX, posY);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        float halfWidth = (tileSize / 2f) / MazeMap.TILE_SIZE;
        float halfHeight = (tileSize / 2f) / MazeMap.TILE_SIZE;
        shape.setAsBox(halfWidth, halfHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.filter.categoryBits = 0x0004; // Exit category
        fixtureDef.filter.maskBits = 0x0001;     // Collide with player
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    /**
     * Disables collision for the exit point.
     */
    public void disableCollision() {
        for (Fixture fixture : body.getFixtureList()) {
            body.destroyFixture(fixture);
        }
    }

    /**
     * Checks if the player has reached the exit point.
     *
     * @param player the player entity
     * @param game   the game instance
     * @return true if the player has reached the exit point, false otherwise
     */
    public boolean checkIfPlayerReachedExit(Player player, MazeRunnerGame game) {
        if (player.getCollectedFish() < 1) {
            return false; // Player needs at least one fish to exit
        }

        Vector2 playerPosition = player.getBody().getPosition();
        if (Math.abs(playerPosition.x - (this.getX() + 0.5)) < 0.5f &&
                Math.abs(playerPosition.y - (this.getY() + 0.5)) < 0.5f) {
            return true;
        }
        return false;
    }
}
