package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.fop.maze.entities.Enemy;
import de.tum.cit.fop.maze.entities.Player;
import de.tum.cit.fop.maze.objects.*;
import de.tum.cit.fop.maze.pathfinding.Algorithm;

import java.util.*;

/**
 * Represents a game maze loaded from a map file. Handles maze loading, rendering,
 * and collision detection. Manages game objects including walls, traps, enemies,
 * and collectibles.
 */
public class MazeMap {
    private final TextureRegion[][] carpetTiles, wallTiles, furnitureTiles, thingTiles, objectTiles, grassTiles;
    private final TextureRegion tree1Texture, tree2Texture, entryTexture, exitTexture, trapTexture, enemyTexture, keyTexture, pathTexture, grassTexture, wallHorTexture, wallVerTexture, cornerRUTexture, cornerRDTexture, cornerLUTexture, cornerLDTexture;
    private final Map<String, List<GameObject>> gameObjects = new HashMap<>();
    private final List<ExitPoint> exitPoints = new ArrayList<>();
    private final List<LaserTrap> laserTraps = new ArrayList<>();
    private int mazeWidth, mazeHeight;
    private String mapPath;
    private final World world;
    public int entryX, entryY;
    public static final int TILE_SIZE = 16;

    private Algorithm pathfinder;

    public enum WallType {
        HORIZONTAL, VERTICAL, CORNER_LU, CORNER_RU, CORNER_LD, CORNER_RD
    }

    /**
     * Constructs a new MazeMap and loads it from the specified file
     *
     * @param mapPath      Path to the map properties file
     * @param windowWidth  Window width for calculating decorative elements
     * @param windowHeight Window height for calculating decorative elements
     * @param world        Box2D physics world for collision bodies
     */
    public MazeMap(String mapPath, int windowWidth, int windowHeight, World world) {
        TextureAtlas atlas = new TextureAtlas("world.atlas");
        carpetTiles = splitRegion(atlas.findRegion("carpets"), TILE_SIZE, TILE_SIZE);
        wallTiles = splitRegion(atlas.findRegion("walls_floor_doors"), TILE_SIZE, TILE_SIZE);
        furnitureTiles = splitRegion(atlas.findRegion("furniture"), TILE_SIZE, TILE_SIZE);
        thingTiles = splitRegion(atlas.findRegion("things"), TILE_SIZE, TILE_SIZE);
        objectTiles = splitRegion(atlas.findRegion("objects"), TILE_SIZE, TILE_SIZE);
        grassTiles = splitRegion(atlas.findRegion("basictiles"), TILE_SIZE, TILE_SIZE);
        this.world = world;

        entryTexture = carpetTiles[6][2];
        exitTexture = carpetTiles[12][2];
        trapTexture = thingTiles[6][0];
        enemyTexture = thingTiles[3][11];
        keyTexture = thingTiles[0][7];
        pathTexture = carpetTiles[2][2];
        grassTexture = grassTiles[8][0];
        tree1Texture = grassTiles[3][6];
        tree2Texture = grassTiles[4][6];
        wallHorTexture = wallTiles[0][1];
        wallVerTexture = wallTiles[1][0];
        cornerRUTexture = wallTiles[0][2];
        cornerRDTexture = wallTiles[2][2];
        cornerLUTexture = wallTiles[0][0];
        cornerLDTexture = wallTiles[2][0];

        // Load the maze from the properties file
        loadMaze(mapPath, windowWidth, windowHeight);
    }

    /**
     * Splits a texture region into a grid of smaller texture regions
     *
     * @param region     Source texture region to split
     * @param tileWidth  Width of each tile
     * @param tileHeight Height of each tile
     * @return 2D array of texture regions
     */
    private TextureRegion[][] splitRegion(TextureRegion region, int tileWidth, int tileHeight) {
        int cols = region.getRegionWidth() / tileWidth;
        int rows = region.getRegionHeight() / tileHeight;
        TextureRegion[][] tiles = new TextureRegion[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                tiles[y][x] = new TextureRegion(region, x * tileWidth, y * tileHeight, tileWidth, tileHeight);
            }
        }
        return tiles;
    }

    /**
     * Loads maze structure from a properties file
     *
     * @param filePath     Path to the map properties file
     * @param windowWidth  Window width for decorative elements
     * @param windowHeight Window height for decorative elements
     */
    public void loadMaze(String filePath, int windowWidth, int windowHeight) {
        try {
            mapPath = filePath;
            Properties props = new Properties();
            props.load(Gdx.files.internal(filePath).reader());

            int maxX = 0, maxY = 0;
            for (String key : props.stringPropertyNames()) {
                String[] coords = key.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                if (x > maxX) maxX = x;
                if (y > maxY) maxY = y;
            }

            mazeWidth = maxX + 1;
            mazeHeight = maxY + 1;

            boolean hasEntry = false, hasExit = false;
            for (int y = 0; y < mazeHeight; y++) {
                for (int x = 0; x < mazeWidth; x++) {
                    String key = x + "," + y;
                    if (props.containsKey(key)) {
                        int type = Integer.parseInt(props.getProperty(key));
                        if (type != 0) {
                            switch (type) {
                                case 1 -> {
                                    if (!hasEntry && !isCorner(x, y)) {
                                        addGameObject(key, new EntryPoint(x, y, TILE_SIZE, entryTexture));
                                        hasEntry = true;
                                        entryX = x;
                                        entryY = y;
                                    }
                                }
                                case 2 -> {
                                    if (!isCorner(x, y)) {
                                        ExitPoint exitPoint = new ExitPoint(x, y, TILE_SIZE, exitTexture, world);
                                        addGameObject(key, exitPoint);
                                        exitPoints.add(exitPoint);
                                        hasExit = true;
                                    }
                                }
                                case 3 -> {
                                    LaserTrap laserTrap = new LaserTrap(x, y, world);
                                    addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                                    addGameObject(key, laserTrap);
                                    laserTraps.add(laserTrap);
                                }
                                case 4 -> {
                                    addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                                    addGameObject(key, new Enemy(world, this, new Vector2(x, y), pathfinder));
                                }
                                case 5 -> {
                                    addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                                    Fish fish = new Fish(x, y, world);
                                    addGameObject(key, fish);
                                }
                                case 6 -> {
                                    SlowTile slowTile = new SlowTile(x, y, world);
                                    addGameObject(key, slowTile);
                                }
                                case 7 -> {
                                    Heart heart = new Heart(x, y, world);
                                    addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                                    addGameObject(key, heart);
                                }
                                case 8 -> {
                                    Ability ability = new Ability(x, y, world);
                                    addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                                    addGameObject(key, ability);
                                }
                            }
                        }
                    } else {
                        addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                    }
                }
            }

            if (!hasEntry) throw new IllegalStateException("No entry point defined in the maze file!");
            if (!hasExit) throw new IllegalStateException("No exit point defined in the maze file!");

            // Fill outside the maze with grass
            fillWithGrassAndTrees(windowWidth, windowHeight);

            // Each island of connected wall blocks should have proper textures that allow connecting them
            for (int y = 0; y < mazeHeight; y++) {
                for (int x = 0; x < mazeWidth; x++) {
                    String key = x + "," + y;
                    if (props.containsKey(key) && Integer.parseInt(props.getProperty(key)) == 0 && !gameObjects.containsKey(key)) {
                        dfsWalls(key, props);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a game object to the map at specified coordinates
     *
     * @param key    Map coordinates in "x,y" format
     * @param object Game object to add
     */
    private void addGameObject(String key, GameObject object) {
        gameObjects.computeIfAbsent(key, k -> new ArrayList<>()).add(object);
    }

    /**
     * Fills areas outside the main maze with grass and trees
     *
     * @param windowWidth  Window width for calculating fill area
     * @param windowHeight Window height for calculating fill area
     */
    private void fillWithGrassAndTrees(int windowWidth, int windowHeight) {
        // Calculate padding size (how far out from the maze to extend the grass)
        int padding = Math.max(50, Math.max(mazeWidth, mazeHeight));
        int startX = -padding, endX = mazeWidth + padding, startY = -padding, endY = mazeHeight + padding;

        // Fill the entire area with grass and occasional trees
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                String key = x + "," + y;
                if (x >= 0 && y >= 0 && x < mazeWidth && y < mazeHeight) continue;
                if (!gameObjects.containsKey(key)) {
                    addGameObject(key, new Path(x, y, TILE_SIZE, grassTexture));
                    double random = Math.random();
                    if (random < 0.08) {
                        addGameObject(key, new Path(x, y, TILE_SIZE, tree1Texture));
                    } else if (random < 0.12) {
                        addGameObject(key, new Path(x, y, TILE_SIZE, tree2Texture));
                    }
                }
            }
        }
    }

    /**
     * Performs depth-first search to generate proper wall textures
     *
     * @param key   Starting coordinates in "x,y" format
     * @param props Map properties containing wall data
     */
    private void dfsWalls(String key, Properties props) {
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        stack.push(key);
        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (visited.contains(current)) continue;
            visited.add(current);

            String[] coords = current.split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            WallType wallType = findWallType(x, y, props);
            TextureRegion current_texture = getWallTexture(wallType);
            addGameObject(current, new Wall(x, y, TILE_SIZE, current_texture, world));

            if (isWallAt(x + 1, y, props) && !visited.contains((x + 1) + "," + y)) stack.push((x + 1) + "," + y);
            if (isWallAt(x - 1, y, props) && !visited.contains((x - 1) + "," + y)) stack.push((x - 1) + "," + y);
            if (isWallAt(x, y + 1, props) && !visited.contains(x + "," + (y + 1))) stack.push(x + "," + (y + 1));
            if (isWallAt(x, y - 1, props) && !visited.contains(x + "," + (y - 1))) stack.push(x + "," + (y - 1));
        }
    }

    /**
     * Determines the appropriate wall type for a given position
     *
     * @param x     X coordinate of wall
     * @param y     Y coordinate of wall
     * @param props Map properties containing wall data
     * @return WallType enum representing the wall configuration
     */
    private WallType findWallType(int x, int y, Properties props) {
        boolean isUp = isWallAt(x, y + 1, props);
        boolean isDown = isWallAt(x, y - 1, props);
        boolean isRight = isWallAt(x + 1, y, props);
        boolean isLeft = isWallAt(x - 1, y, props);
        int neighbors_count = (isUp ? 1 : 0) + (isDown ? 1 : 0) + (isLeft ? 1 : 0) + (isRight ? 1 : 0);

        return switch (neighbors_count) {
            case 1 -> (isUp || isDown) ? WallType.VERTICAL : WallType.HORIZONTAL;
            case 2 -> {
                if (isRight && isLeft) yield WallType.HORIZONTAL;
                if (isUp && isDown) yield WallType.VERTICAL;
                if (isUp && isRight) yield WallType.CORNER_LD;
                if (isUp) yield WallType.CORNER_RD;
                if (isRight) yield WallType.CORNER_LU;
                yield WallType.CORNER_RU;
            }
            default -> WallType.HORIZONTAL;
        };
    }

    /**
     * Checks if there's a wall at specified coordinates
     *
     * @param x     X coordinate to check
     * @param y     Y coordinate to check
     * @param props Map properties containing wall data
     * @return true if a wall exists at the location
     */
    private boolean isWallAt(int x, int y, Properties props) {
        if (x < 0 || y < 0 || x >= mazeWidth || y >= mazeHeight) return false;
        String key = x + "," + y;
        return props.containsKey(key) && Integer.parseInt(props.getProperty(key)) == 0;
    }

    /**
     * Gets the appropriate texture for a wall type
     *
     * @param type WallType enum value
     * @return TextureRegion for the specified wall type
     */
    private TextureRegion getWallTexture(WallType type) {
        return switch (type) {
            case HORIZONTAL -> wallHorTexture;
            case VERTICAL -> wallVerTexture;
            case CORNER_LU -> cornerLUTexture;
            case CORNER_RU -> cornerRUTexture;
            case CORNER_LD -> cornerLDTexture;
            case CORNER_RD -> cornerRDTexture;
        };
    }

    /**
     * Checks if a given position is a corner of the maze
     *
     * @param x X coordinate to check
     * @param y Y coordinate to check
     * @return true if the position is a corner
     */
    private boolean isCorner(int x, int y) {
        return (x == 0 && y == 0) || (x == 0 && y == mazeHeight - 1) || (x == mazeWidth - 1 && y == 0) || (x == mazeWidth - 1 && y == mazeHeight - 1);
    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }

    /**
     * Renders all game objects in the maze
     *
     * @param batch SpriteBatch used for rendering
     */
    public void render(SpriteBatch batch) {
        for (List<GameObject> objects : gameObjects.values()) {
            for (GameObject object : objects) {
                object.render(batch);
            }
        }
    }

    public Map<String, List<GameObject>> getGameObjects() {
        return gameObjects;
    }

    public boolean isWall(int x, int y) {
        GameObject object = getGameObjectAt(x, y);
        return object instanceof Wall;
    }

    /**
     * Checks if a given position is an exit or entrance
     *
     * @param x X coordinate to check
     * @param y Y coordinate to check
     * @return true if the position is an exit or entrance
     */
    public boolean isExitOrEntrance(int x, int y) {
        String key = x + "," + y;
        List<GameObject> objects = gameObjects.get(key);
        if (objects != null) {
            return objects.stream()
                    .anyMatch(o -> o instanceof ExitPoint || o instanceof EntryPoint);
        }
        return false;
    }

    /**
     * Checks if a given position is walkable
     *
     * @param x X coordinate to check
     * @param y Y coordinate to check
     * @return true if the position is walkable
     */
    public boolean isWalkable(int x, int y) {
        return !isWall(x, y) && !isExitOrEntrance(x, y);
    }

    /**
     * Gets the game object at a given position
     *
     * @param x X coordinate to check
     * @param y Y coordinate to check
     * @return GameObject at the specified position
     */
    private GameObject getGameObjectAt(int x, int y) {
        String key = x + "," + y;
        List<GameObject> objects = gameObjects.get(key);
        return objects != null ? objects.stream().findFirst().orElse(null) : null;
    }

    /**
     * Removes a game object from the maze
     *
     * @param object GameObject to remove
     */
    public void removeGameObject(GameObject object) {
        String key = object.getX() + "," + object.getY();
        List<GameObject> objects = gameObjects.get(key);
        if (objects != null) {
            objects.remove(object);
            if (objects.isEmpty()) gameObjects.remove(key);
        }
    }

    /**
     * Gets the enemy objects in the maze
     *
     * @return List of Enemy objects
     */
    public List<Enemy> getEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        for (List<GameObject> objects : gameObjects.values()) {
            for (GameObject object : objects) {
                if (object instanceof Enemy) {
                    enemies.add((Enemy) object);
                }
            }
        }
        return enemies;
    }

    public List<ExitPoint> getExitPoints() {
        return exitPoints;
    }

    public List<LaserTrap> getLaserTraps() {
        return laserTraps;
    }

    public void setPlayer(Player player) {
        for (Enemy enemy : getEnemies()) {
            enemy.setPlayer(player);
        }
    }

    public void setPathfinder(Algorithm pathfinder) {
        for (Enemy enemy : getEnemies()) {
            enemy.setPathfinder(pathfinder);
        }
    }
}
