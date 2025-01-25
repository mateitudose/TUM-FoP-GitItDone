package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.fop.maze.entities.Enemy;
import de.tum.cit.fop.maze.entities.Player;
import de.tum.cit.fop.maze.objects.*;
import de.tum.cit.fop.maze.pathfinding.Algorithm;

import java.util.*;

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
                                    LaserTrap laserTrap = new LaserTrap(x, y);

                                    // Physics setup
                                    BodyDef bodyDef = new BodyDef();
                                    bodyDef.type = BodyDef.BodyType.StaticBody;
                                    bodyDef.position.set(x + 0.5f, y + 0.5f);

                                    Body body = world.createBody(bodyDef);

                                    PolygonShape shape = new PolygonShape();
                                    shape.setAsBox(0.3f, 0.3f);

                                    FixtureDef fixtureDef = new FixtureDef();
                                    fixtureDef.shape = shape;
                                    fixtureDef.isSensor = true;
                                    fixtureDef.filter.categoryBits = 0x0002; // Trap category
                                    fixtureDef.filter.maskBits = 0x0001;     // Collide with player

                                    body.createFixture(fixtureDef);
                                    body.setUserData(laserTrap);
                                    shape.dispose();

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
                                    Fish fish = new Fish(x, y);

                                    // Create physics body for the fish
                                    BodyDef bodyDef = new BodyDef();
                                    bodyDef.type = BodyDef.BodyType.StaticBody;
                                    bodyDef.position.set(x + 0.5f, y + 0.5f); // Center in tile

                                    Body body = world.createBody(bodyDef);
                                    PolygonShape shape = new PolygonShape();
                                    shape.setAsBox(0.25f, 0.25f); // Smaller hitbox

                                    FixtureDef fixtureDef = new FixtureDef();
                                    fixtureDef.shape = shape;
                                    fixtureDef.isSensor = true; // No collision response
                                    fixtureDef.filter.categoryBits = 0x0004; // Fish category
                                    fixtureDef.filter.maskBits = 0x0001; // Collide with player

                                    body.createFixture(fixtureDef);
                                    body.setUserData(fish); // Link fish to body
                                    fish.setBody(body); // Link body to fish

                                    shape.dispose();
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

    private void addGameObject(String key, GameObject object) {
        gameObjects.computeIfAbsent(key, k -> new ArrayList<>()).add(object);
    }

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

    private boolean isWallAt(int x, int y, Properties props) {
        if (x < 0 || y < 0 || x >= mazeWidth || y >= mazeHeight) return false;
        String key = x + "," + y;
        return props.containsKey(key) && Integer.parseInt(props.getProperty(key)) == 0;
    }

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

    private boolean isCorner(int x, int y) {
        return (x == 0 && y == 0) || (x == 0 && y == mazeHeight - 1) || (x == mazeWidth - 1 && y == 0) || (x == mazeWidth - 1 && y == mazeHeight - 1);
    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }

    public String getMapPath() {
        return mapPath;
    }

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

    public boolean isExitorEntrance(int x, int y) {
        String key = x + "," + y;
        List<GameObject> objects = gameObjects.get(key);
        if (objects != null) {
            return objects.stream()
                    .anyMatch(o -> o instanceof ExitPoint || o instanceof EntryPoint);
        }
        return false;
    }

    public boolean isWalkable(int x, int y) {
        return !isWall(x, y) && !isExitorEntrance(x, y);
    }

    private GameObject getGameObjectAt(int x, int y) {
        String key = x + "," + y;
        List<GameObject> objects = gameObjects.get(key);
        return objects != null ? objects.stream().findFirst().orElse(null) : null;
    }

    public void removeGameObject(GameObject object) {
        String key = object.getX() + "," + object.getY();
        List<GameObject> objects = gameObjects.get(key);
        if (objects != null) {
            objects.remove(object);
            if (objects.isEmpty()) gameObjects.remove(key);
        }
    }

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
