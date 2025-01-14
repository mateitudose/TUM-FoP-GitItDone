package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.objects.*;

import java.util.*;

public class MazeMap {
    private final TextureRegion[][] carpetTiles;
    private final TextureRegion[][] wallTiles;
    private final TextureRegion[][] furnitureTiles;
    private final TextureRegion[][] thingTiles;
    private final TextureRegion[][] objectTiles;
    private final TextureRegion[][] grasTiles;
    private final TextureRegion tree1Texture;
    private final TextureRegion tree2Texture;
    private final Map<String, List<GameObject>> gameObjects = new HashMap<>();
    private int mazeWidth;
    private int mazeHeight;
    private String mapPath;
    private World world;
    public int entryX, entryY;
    public static final int TILE_SIZE = 16;
    private final TextureRegion wallTexture;
    private final TextureRegion entryTexture;
    private final TextureRegion exitTexture;
    private final TextureRegion trapTexture;
    private final TextureRegion enemyTexture;
    private final TextureRegion keyTexture;
    private final TextureRegion pathTexture;
    private final TextureRegion grasTexture;
    private final TextureRegion wallDownTexture;

    public MazeMap(String mapPath, int windowWidth, int windowHeight, World world) {
        // Load textures
        TextureAtlas atlas = new TextureAtlas("world.atlas");
        carpetTiles = splitRegion(atlas.findRegion("carpets"), TILE_SIZE, TILE_SIZE);
        wallTiles = splitRegion(atlas.findRegion("walls_floor_doors"), TILE_SIZE, TILE_SIZE);
        furnitureTiles = splitRegion(atlas.findRegion("furniture"), TILE_SIZE, TILE_SIZE);
        thingTiles = splitRegion(atlas.findRegion("things"), TILE_SIZE, TILE_SIZE);
        objectTiles = splitRegion(atlas.findRegion("objects"), TILE_SIZE, TILE_SIZE);
        grasTiles = splitRegion(atlas.findRegion("basictiles"), TILE_SIZE, TILE_SIZE);
        this.world = world;

        // Assign specific textures
        wallTexture = wallTiles[3][1]; // Example: Wall texture
        entryTexture = carpetTiles[6][2]; // Example: Entry point texture
        exitTexture = carpetTiles[12][2]; // Example: Exit texture
        trapTexture = thingTiles[6][0]; // Example: Trap texture
        enemyTexture = thingTiles[3][11]; // Example: Enemy texture
        keyTexture = thingTiles[0][7]; // Example: Key texture
        pathTexture = carpetTiles[2][2]; // CarpetTiles specifically for paths
        grasTexture = grasTiles[8][0]; // Example: Gras texture
        tree1Texture = grasTiles[3][6]; // Example: Tree texture
        tree2Texture = grasTiles[4][6]; // Example: Tree texture
        wallDownTexture = wallTiles[0][3];
        // Load the maze from the properties file
        loadMaze(mapPath, windowWidth, windowHeight);
    }

    private TextureRegion[][] splitRegion(TextureRegion region, int tileWidth, int tileHeight) {
        int cols = region.getRegionWidth() / tileWidth;
        int rows = region.getRegionHeight() / tileHeight;

        TextureRegion[][] tiles = new TextureRegion[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                tiles[y][x] = new TextureRegion(
                        region,
                        x * tileWidth,
                        y * tileHeight,
                        tileWidth,
                        tileHeight
                );
            }
        }
        return tiles;
    }

    public void loadMaze(String filePath, int windowWidth, int windowHeight) {
        try {
            mapPath = filePath;
            Properties props = new Properties();
            props.load(Gdx.files.internal(filePath).reader());

            // Minimum maze dimensions
            // int minCols = 32;
            // int minRows = 18;

            // Find maximum x and y from the property file
            int maxX = 0;
            int maxY = 0;

            for (String key : props.stringPropertyNames()) {
                String[] coords = key.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                if (x > maxX) maxX = x;
                if (y > maxY) maxY = y;
            }
            // Don't delete the padding again
            // Add padding of 2 to max dimensions
            maxX += 1;
            maxY += 1;

            // Set maze width and height, ensuring the minimum size is used
            mazeWidth = maxX;
            mazeHeight = maxY;

            System.out.println("Maze dimensions: " + mazeWidth + "x" + mazeHeight);

            boolean hasEntry = false;
            boolean hasExit = false;

            for (int y = 0; y < mazeHeight; y++) {
                for (int x = 0; x < mazeWidth; x++) {
                    String key = x + "," + y;

                    if (props.containsKey(key)) {
                        int type = Integer.parseInt(props.getProperty(key));
                        // System.out.println("Creating object at (" + x + ", " + y + ") with type: " + type); // Debugging object creation

                        switch (type) {
                            case 0 -> addGameObject(key, new Wall(x, y, TILE_SIZE, wallTexture, world));
                            case 1 -> {
                                if (!hasEntry && !isCorner(x, y)) {
                                    addGameObject(key, new EntryPoint(x, y, TILE_SIZE, entryTexture));
                                    hasEntry = true;
                                    entryX = x;
                                    entryY = y;
                                } else {
                                    addGameObject(key, new Wall(x, y, TILE_SIZE, wallTexture, world));
                                }
                            }
                            case 2 -> {
                                if (!isCorner(x, y)) {
                                    addGameObject(key, new ExitPoint(x, y, TILE_SIZE, exitTexture));
                                    hasExit = true;
                                }
                            }
                            case 3 -> addGameObject(key, new Trap(x, y, TILE_SIZE, trapTexture));
                            case 4 -> addGameObject(key, new Enemy(x, y, TILE_SIZE, enemyTexture));
                            case 5 -> addGameObject(key, new Key(x, y, TILE_SIZE, keyTexture));
                        }
                    } else {
                        // Default to path if undefined
                        addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                    }
                }
            }

            //addOuterWalls();
            if (!hasEntry) addEntryPoint();
            if (!hasExit) addExitPoint();
            if (!hasEntry) {
                throw new IllegalStateException("No entry point defined in the maze file!");
            }
            if (!hasExit) {
                throw new IllegalStateException("No exit point defined in the maze file!");
            }
            // Fill outside of the maze with grass
            fillWithGrassAndTrees(windowWidth, windowHeight);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addGameObject(String key, GameObject object) {
        gameObjects.computeIfAbsent(key, k -> new ArrayList<>()).add(object);
    }

    private void fillWithGrassAndTrees(int windowWidth, int windowHeight) {
        int gridWidth = windowWidth / TILE_SIZE;
        int gridHeight = windowHeight / TILE_SIZE;

        for (int y = -gridHeight; y < gridHeight; y++) {
            for (int x = -gridWidth; x < gridWidth; x++) {

                String key = x + "," + y;
                if(x >= 0 && y >= 0 && x < mazeWidth && y < mazeHeight) {
                    continue;
                }
                if (!gameObjects.containsKey(key)) {
                    addGameObject(key, new Path(x, y, TILE_SIZE, grasTexture));//add grass
                    if (Math.random() < 0.1) {
                        addGameObject(key, new Path(x, y, TILE_SIZE, tree1Texture));//add first tree
                    } else if (Math.random() < 0.05) {
                        addGameObject(key, new Path(x, y, TILE_SIZE, tree2Texture));//add second tree
                    }
                }
            }
        }
    }


    private void addOuterWalls() {
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                String key = x + "," + y;
                if ((x == 0 || y == 0 || x == mazeWidth - 1 || y == mazeHeight - 1) &&
                        !(gameObjects.get(key) instanceof EntryPoint || gameObjects.get(key) instanceof ExitPoint)) {
                    addGameObject(key, new Wall(x, y, TILE_SIZE, wallTexture, world));
                }
            }
        }
    }

    private void addEntryPoint() {
        for (int x = 1; x < mazeWidth - 1; x++) { // Avoid corners
            String key = x + ",0";
            if (gameObjects.get(key) instanceof Wall) { // Check if it's a wall
                String belowKey = x + ",1";
                // Ensure no wall directly below the entry point
                if (!(gameObjects.get(belowKey) instanceof Wall)) {
                    addGameObject(key, new EntryPoint(x, 0, TILE_SIZE, entryTexture));
                    entryX = x;
                    entryY = 0;
                    System.out.println("Added Entry Point at: " + key);
                    return;
                }
            }
        }
        System.out.println("No suitable position found for Entry Point!");
    }

    private void addExitPoint() {
        for (int x = 1; x < mazeWidth - 1; x++) { // Avoid corners
            String key = x + "," + (mazeHeight - 1);
            if (gameObjects.get(key) instanceof Wall) { // Check if it's a wall
                String aboveKey = x + "," + (mazeHeight - 2);
                // Ensure no wall directly above the exit point
                if (!(gameObjects.get(aboveKey) instanceof Wall)) {
                    addGameObject(key, new ExitPoint(x, mazeHeight - 1, TILE_SIZE, exitTexture));
                    System.out.println("Added Exit Point at: " + key);
                    return;
                }
            }
        }
        System.out.println("No suitable position found for Exit Point!");
    }

    private boolean isCorner(int x, int y) {
        return (x == 0 && y == 0) || (x == 0 && y == mazeHeight - 1) || (x == mazeWidth - 1 && y == 0) || (x == mazeWidth - 1 && y == mazeHeight - 1);
    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public void setMazeWidth(int mazeWidth) {
        this.mazeWidth = mazeWidth;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }

    public void setMazeHeight(int mazeHeight) {
        this.mazeHeight = mazeHeight;
    }

    public String getMapPath() {
        return mapPath;
    }

    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
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
        if (object instanceof Wall) {
            return true;
        }
        return false;
    }

    private GameObject getGameObjectAt(int x, int y) {
        String key = x + "," + y;
        List<GameObject> objects = gameObjects.get(key);
        if (objects != null) {
            return objects.stream().findFirst().orElse(null);
        }
        return null;
    }

    public Array<Vector2> getExitPositions() {
        Array<Vector2> exitPositions = new Array<>();
        gameObjects.values().stream()
                .flatMap(List::stream)
                .filter(obj -> obj instanceof ExitPoint)
                .forEach(obj -> exitPositions.add(new Vector2(obj.getX(), obj.getY())));
        return exitPositions;
    }
}