package de.tum.cit.fop.maze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.MazeMap;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.pathfinding.Algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an enemy entity in the maze game.
 * The enemy can move randomly or follow a path towards the player.
 */
public class Enemy extends GameEntity {
    private static final float MOVE_SPEED = 1.5f; // A bit slower than the player (2f)
    private static final float PROXIMITY = 5f;
    private static final float RANDOM_MOVE_INTERVAL = 2f;
    private float speed;
    private World world;
    private Animation<TextureRegion> downAnim, upAnim, rightAnim, leftAnim;
    private MazeMap mazeMap;
    private float stateTime = 0f;
    private String currentDirection = "down";
    private boolean isMoving = false;
    private Player player;
    private Algorithm pathfinder;
    private List<Vector2> path;
    private int currentTileIndex = 0;
    private Vector2 lastPosition = new Vector2();
    private float stuckTimer = 0f;
    private static final float STUCK_TIME_THRESHOLD = 0.5f;
    private static final float STUCK_DISTANCE_EPSILON = 0.01f;
    private int mazeWidth;
    private int mazeHeight;

    private Random random = new Random();
    private float randomMoveTimer = 0f;
    private Vector2 randomDirection = new Vector2();

    private boolean dizzy = false;
    private float dizzyTimer = 0f;

    /**
     * Constructs an Enemy object.
     *
     * @param world the Box2D world the enemy belongs to
     * @param mazeMap the maze map the enemy navigates
     * @param startPosition the starting position of the enemy
     * @param pathfinder the pathfinding algorithm used by the enemy
     */
    public Enemy(World world, MazeMap mazeMap, Vector2 startPosition, Algorithm pathfinder) {
        super((int) startPosition.x, (int) startPosition.y, 14, 14, new TextureRegion(new Texture(Gdx.files.internal("dog.png")), 12, 16, 32, 32));
        this.world = world;
        this.mazeMap = mazeMap;
        this.pathfinder = pathfinder;
        loadAnimation();
        this.body = createBody(startPosition);
        this.speed = MOVE_SPEED;
        this.path = new ArrayList<>();
        this.mazeWidth = mazeMap.getMazeWidth();
        this.mazeHeight = mazeMap.getMazeHeight();
    }

    /**
     * Loads the animation frames for the enemy.
     */
    public void loadAnimation() {
        Texture enemySheet = new Texture(Gdx.files.internal("dog.png"));

        int frames = 3;
        Array<TextureRegion> downFrames = new Array<>(frames);
        Array<TextureRegion> leftFrames = new Array<>(frames);
        Array<TextureRegion> rightFrames = new Array<>(frames);
        Array<TextureRegion> upFrames = new Array<>(frames);

        downFrames.add(new TextureRegion(enemySheet, 9, 16, 36, 34));
        downFrames.add(new TextureRegion(enemySheet, 56, 16, 36, 31));
        downFrames.add(new TextureRegion(enemySheet, 103, 16, 36, 34));

        leftFrames.add(new TextureRegion(enemySheet, 9, 65, 33, 30));
        leftFrames.add(new TextureRegion(enemySheet, 58, 66, 34, 29));
        leftFrames.add(new TextureRegion(enemySheet, 103, 65, 36, 31));

        rightFrames.add(new TextureRegion(enemySheet, 9, 111, 35, 31));
        rightFrames.add(new TextureRegion(enemySheet, 56, 113, 34, 29));
        rightFrames.add(new TextureRegion(enemySheet, 103, 111, 36, 31));

        upFrames.add(new TextureRegion(enemySheet, 9, 156, 36, 36));
        upFrames.add(new TextureRegion(enemySheet, 56, 158, 36, 32));
        upFrames.add(new TextureRegion(enemySheet, 103, 156, 36, 36));

        downAnim = new Animation<>(0.1f, downFrames);
        leftAnim = new Animation<>(0.1f, leftFrames);
        rightAnim = new Animation<>(0.1f, rightFrames);
        upAnim = new Animation<>(0.1f, upFrames);
    }

    /**
     * Updates the enemy's state.
     *
     * @param delta the time elapsed since the last update
     */
    @Override
    public void update(float delta) {
        // Guard clause to prevent null errors until player is set
        if (player == null)
            return;

        if (dizzy) {
            dizzyTimer -= delta;
            if (dizzyTimer <= 0) {
                dizzy = false;
                sprite.setColor(Color.WHITE);
            }
            // Don't move while dizzy
            return;
        }

        stateTime += delta;

        // Use exact positions to calculate walk direction
        Vector2 playerExactPosition = player.getBody().getPosition();
        Vector2 enemyExactPosition = body.getPosition();
        Vector2 currentPosition = body.getPosition();

        Vector2 position = body.getPosition();
        // Don't let the enemy go out of bounds of the maze
        float clampedX = Math.max(0.5f, Math.min(position.x, mazeWidth - 0.5f));
        float clampedY = Math.max(0.5f, Math.min(position.y, mazeHeight - 0.5f));

        if (position.x != clampedX || position.y != clampedY) {
            body.setTransform(clampedX, clampedY, body.getAngle());
        }

        // Check if the enemy is stuck
        if (randomDirection.len() > 0) {
            // Only check when doing random movement
            // Check if position hasn't changed too much
            if (currentPosition.epsilonEquals(lastPosition, STUCK_DISTANCE_EPSILON)) {
                stuckTimer += delta;
                // If stuck for more than threshold time
                if (stuckTimer >= STUCK_TIME_THRESHOLD) {
                    generateRandomDirection(currentPosition);
                    // Reset timer
                    stuckTimer = 0f;
                }
            } else {
                // Reset timer if position changed
                stuckTimer = 0f;
            }
        }

        lastPosition.set(currentPosition);

        // If the player is in proximity, calculate the path
        if (isPlayerInProximity(enemyExactPosition, playerExactPosition)) {
            // Find the most efficient path
            path = pathfinder.A_Star(enemyExactPosition, playerExactPosition);
            // So that when we go through the path we start from the first tile
            currentTileIndex = 0;
            // Reset random move timer
            randomMoveTimer = 0f;
        } else {
            // Random movement logic
            randomMoveTimer += delta;
            // If we have moved in the same direction for a while, change direction
            // Every 2 seconds, the enemy picks a new random direction
            if (randomMoveTimer >= RANDOM_MOVE_INTERVAL) {
                generateRandomDirection(enemyExactPosition);
                randomMoveTimer = 0f;
            }
        }

        // If we find a path, the enemy has to move accordingly
        if (!path.isEmpty() && currentTileIndex < path.size()) {
            while (currentTileIndex < path.size()) {
                Vector2 target = path.get(currentTileIndex);
                int tileX = (int) target.x;
                int tileY = (int) target.y;
                // Skip tiles that are exits or out of bounds, so enemy doesn't accidentally go out of maze
                if (tileX < 0 || tileX >= mazeWidth || tileY < 0 || tileY >= mazeHeight ||
                        mazeMap.isExitOrEntrance(tileX, tileY)) {
                    currentTileIndex++;
                } else {
                    break;
                }
            }
            float distance = enemyExactPosition.dst(playerExactPosition);
            // If the enemy is too close to the player, stop moving
            if (distance <= 0.7f) {
                body.setLinearVelocity(0, 0);
                delta = 0;
                path.clear();
                return;
            }

            if (currentTileIndex >= path.size()) {
                path.clear();
                return;
            }

            Vector2 target = path.get(currentTileIndex);
            Vector2 direction = new Vector2(target.x - enemyExactPosition.x, target.y - enemyExactPosition.y).nor();

            body.setLinearVelocity(direction.x * speed, direction.y * speed);
            updateDirectionAndAnimation(direction);

            if (enemyExactPosition.epsilonEquals(target, 0.1f)) {
                currentTileIndex++;
            }

        } else if (randomDirection.len() > 0) {
            // Random movement
            body.setLinearVelocity(randomDirection.x * speed, randomDirection.y * speed);
            updateDirectionAndAnimation(randomDirection);
        } else {
            body.setLinearVelocity(0, 0);
        }

        updateAnimation();
    }

    /**
     * Generates a random direction for the enemy to move in.
     *
     * @param currentPosition the current position of the enemy
     */
    private void generateRandomDirection(Vector2 currentPosition) {
        int maxAttempts = 10;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int dir = random.nextInt(4);
            Vector2 direction = new Vector2();
            switch (dir) {
                case 0:
                    direction.set(1, 0);
                    break;  // Right
                case 1:
                    direction.set(-1, 0);
                    break; // Left
                case 2:
                    direction.set(0, 1);
                    break;  // Up
                case 3:
                    direction.set(0, -1);
                    break; // Down
            }

            int checkX = (int) (currentPosition.x + direction.x);
            int checkY = (int) (currentPosition.y + direction.y);

            // Check if target is within maze bounds and not an exit
            if (checkX < 0 || checkX >= mazeWidth || checkY < 0 || checkY >= mazeHeight) {
                continue; // Skip directions outside the maze
            }

            // Skip exit/entry tiles
            if (mazeMap.isExitOrEntrance(checkX, checkY)) {
                continue;
            }

            if (mazeMap.isWalkable(checkX, checkY)) {
                randomDirection.set(direction).nor();
                return;
            }
        }
        randomDirection.setZero(); // Stop if no valid direction
    }
    public void dizziness(float duration) {
        this.dizzy = true;
        this.dizzyTimer = duration;
        this.body.setLinearVelocity(0, 0);
        this.sprite.setColor(0.7f, 0.7f, 0.7f, 1); // Gray out
    }

    public boolean isDizzy() {
        return dizzy;
    }

    /**
     * Updates the direction and animation of the enemy based on the movement direction.
     *
     * @param moveDirection the direction the enemy is moving in
     */
    private void updateDirectionAndAnimation(Vector2 moveDirection) {
        if (moveDirection.len() > 0) {
            float velX = moveDirection.x;
            float velY = moveDirection.y;

            if (Math.abs(velX) > Math.abs(velY)) {
                currentDirection = velX > 0 ? "right" : "left";
            } else {
                currentDirection = velY > 0 ? "up" : "down";
            }
        }
    }

    /**
     * Checks if the player is within proximity of the enemy.
     *
     * @param enemyPosition the position of the enemy
     * @param playerPosition the position of the player
     * @return true if the player is within proximity, false otherwise
     */
    private boolean isPlayerInProximity(Vector2 enemyPosition, Vector2 playerPosition) {
        float distance = enemyPosition.dst(playerPosition);
        return distance <= PROXIMITY;
    }

    /**
     * Updates the animation of the enemy based on the current direction.
     */
    public void updateAnimation() {
        Animation<TextureRegion> currentAnimation = switch (currentDirection) {
            case "left" -> leftAnim;
            case "right" -> rightAnim;
            case "up" -> upAnim;
            default -> downAnim;
        };
        // Set the sprite's texture to the current frame of the animation
        sprite.setRegion(currentAnimation.getKeyFrame(stateTime, true));
    }

    /**
     * Renders the enemy on the screen.
     *
     * @param batch the SpriteBatch used for rendering
     */
    public void render(SpriteBatch batch) {
        // Calculate the center of the tile
        float spriteX = body.getPosition().x * MazeMap.TILE_SIZE - sprite.getWidth() / 2f;
        float spriteY = body.getPosition().y * MazeMap.TILE_SIZE - sprite.getHeight() / 2f;

        // Set the position of the enemy to the calculated coordinates
        sprite.setPosition(spriteX, spriteY);
        // Draw the enemy's frame
        sprite.draw(batch);
    }

    /**
     * Creates the Box2D body for the enemy.
     *
     * @param startPosition the starting position of the enemy
     * @return the created Box2D body
     */
    private Body createBody(Vector2 startPosition) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                startPosition.x + 0.5f,
                startPosition.y + 0.5f
        );

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();

        shape.setRadius((sprite.getWidth() / 2f) / MazeMap.TILE_SIZE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.filter.categoryBits = 0x0009;
        fixtureDef.filter.maskBits = 0x0001 | 0x0002; // Collides with player and walls

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);

        body.setUserData(this);
        body.setFixedRotation(true);

        shape.dispose();
        Gdx.app.postRunnable(() -> fixture.setSensor(false));

        return body;
    }

    /**
     * Sets the player that the enemy will target.
     *
     * @param player the player to target
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Sets the pathfinding algorithm for the enemy.
     *
     * @param pathfinder the pathfinding algorithm
     */
    public void setPathfinder(Algorithm pathfinder) {
        this.pathfinder = pathfinder;
    }
}
