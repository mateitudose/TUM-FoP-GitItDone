package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.entities.Enemy;
import de.tum.cit.fop.maze.objects.*;

import java.util.*;

public class MazeMap {
    private final TextureRegion[][] carpetTiles;
    private final TextureRegion[][] wallTiles;
    private final TextureRegion[][] furnitureTiles;
    private final TextureRegion[][] thingTiles;
    private final TextureRegion[][] objectTiles;
    private final TextureRegion[][] grassTiles;
    private final TextureRegion tree1Texture;
    private final TextureRegion tree2Texture;
    private final Map<String, List<GameObject>> gameObjects = new HashMap<>();
    private int mazeWidth;
    private int mazeHeight;
    private String mapPath;
    private World world;
    public int entryX, entryY;
    public static final int TILE_SIZE = 16;
    private final TextureRegion entryTexture;
    private final TextureRegion exitTexture;
    private final TextureRegion trapTexture;
    private final TextureRegion enemyTexture;
    private final TextureRegion keyTexture;
    private final TextureRegion pathTexture;
    private final TextureRegion grassTexture;
    private final TextureRegion wallHorTexture;
    private final TextureRegion wallVerTexture;
    private final TextureRegion cornerRUTexture;
    private final TextureRegion cornerRDTexture;
    private final TextureRegion cornerLUTexture;
    private final TextureRegion cornerLDTexture;

    public enum WallType {
        HORIZONTAL,
        VERTICAL,
        CORNER_LU, //corner left up
        CORNER_RU, // corner right up
        CORNER_LD, // corner left down
        CORNER_RD //corner right down
    }

    public MazeMap(String mapPath, int windowWidth, int windowHeight, World world) {
        // Load textures
        TextureAtlas atlas = new TextureAtlas("world.atlas");
        carpetTiles = splitRegion(atlas.findRegion("carpets"), TILE_SIZE, TILE_SIZE);
        wallTiles = splitRegion(atlas.findRegion("walls_floor_doors"), TILE_SIZE, TILE_SIZE);
        furnitureTiles = splitRegion(atlas.findRegion("furniture"), TILE_SIZE, TILE_SIZE);
        thingTiles = splitRegion(atlas.findRegion("things"), TILE_SIZE, TILE_SIZE);
        objectTiles = splitRegion(atlas.findRegion("objects"), TILE_SIZE, TILE_SIZE);
        grassTiles = splitRegion(atlas.findRegion("basictiles"), TILE_SIZE, TILE_SIZE);
        this.world = world;

        //wallTexture = wallTiles[3][1]; // Example: Wall texture
        entryTexture = carpetTiles[6][2]; // Example: Entry point texture
        exitTexture = carpetTiles[12][2]; // Example: Exit texture
        trapTexture = thingTiles[6][0]; // Example: Trap texture
        enemyTexture = thingTiles[3][11]; // Example: Enemy texture
        keyTexture = thingTiles[0][7]; // Example: Key texture
        pathTexture = carpetTiles[2][2]; // CarpetTiles specifically for paths
        grassTexture = grassTiles[8][0]; // Example: Grass texture
        tree1Texture = grassTiles[3][6]; // Example: Tree texture
        tree2Texture = grassTiles[4][6]; // Example: Tree texture
        wallHorTexture = wallTiles[0][1]; // Example: Horizontal wall texture
        wallVerTexture = wallTiles[1][0]; // Example: Vertical wall texture
        cornerRUTexture = wallTiles[0][2]; // Example: Corner right up wall texture
        cornerRDTexture = wallTiles[2][2]; // Example: Corner right down wall texture
        cornerLUTexture = wallTiles[0][0]; // Example: Corner left up wall texture
        cornerLDTexture = wallTiles[2][0]; // Example: Corner left down wall texture

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

            boolean hasEntry = false;
            boolean hasExit = false;
            for (int y = 0; y < mazeHeight; y++) {
                for (int x = 0; x < mazeWidth; x++) {
                    String key = x + "," + y;

                    if (props.containsKey(key)) {
                        int type = Integer.parseInt(props.getProperty(key));
                        // Skip walls for now
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
                                        addGameObject(key, new ExitPoint(x, y, TILE_SIZE, exitTexture));
                                        hasExit = true;
                                    }
                                }
                                case 3 -> {
                                    addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                                    addGameObject(key, new Trap(x, y, TILE_SIZE, trapTexture));
                                }
                                case 4 -> {
                                    addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                                    addGameObject(key, new Enemy(x, y, TILE_SIZE, enemyTexture));
                                }
                                case 5 -> {
                                    addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                                    addGameObject(key, new Key(x, y, TILE_SIZE, keyTexture));
                                }
                            }
                        }
                    } else {
                        addGameObject(key, new Path(x, y, TILE_SIZE, pathTexture));
                    }
                }
            }

            if (!hasEntry) addEntryPoint();
            if (!hasExit) addExitPoint();
            if (!hasEntry) {
                throw new IllegalStateException("No entry point defined in the maze file!");
            }
            if (!hasExit) {
                throw new IllegalStateException("No exit point defined in the maze file!");
            }
            // Fill outside the maze with grass
            fillWithGrassAndTrees(windowWidth, windowHeight);

            // Handle the walls
            for (int y = 0; y < mazeHeight; y++) {
                for (int x = 0; x < mazeWidth; x++) {
                    String key = x + "," + y;
                    if (props.containsKey(key) && Integer.parseInt(props.getProperty(key)) == 0 && !gameObjects.containsKey(key)) {
                        // Start DFS from this wall
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
        int padding = Math.max(50, Math.max(mazeWidth, mazeHeight)); // At least 50 tiles or size of maze

        // Calculate the range to fill grass (from before maze starts to after it ends)
        int startX = -padding;
        int endX = mazeWidth + padding;
        int startY = -padding;
        int endY = mazeHeight + padding;

        // Fill the entire area with grass and occasional trees
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                String key = x + "," + y;

                // Skip if inside the maze bounds
                if (x >= 0 && y >= 0 && x < mazeWidth && y < mazeHeight) {
                    continue;
                }

                // Only add grass if no game object exists at this position
                if (!gameObjects.containsKey(key)) {
                    // Add grass tile
                    addGameObject(key, new Path(x, y, TILE_SIZE, grassTexture));

                    // Add trees with reduced probability (adjusted for better distribution)
                    double random = Math.random();
                    if (random < 0.08) { // 8% chance for first tree type
                        addGameObject(key, new Path(x, y, TILE_SIZE, tree1Texture));
                    } else if (random < 0.12) { // 4% chance for second tree type
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
            if (visited.contains(current))
                continue;
            visited.add(current);

            String[] coords = current.split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            // Determine wall type
            WallType wallType = findWallType(x, y, props);
            // Get texture according to type
            TextureRegion current_texture = getWallTexture(wallType);
            addGameObject(current, new Wall(x, y, TILE_SIZE, current_texture, world));
            // Add suitable neighbors to stack
            if (isWallAt(x + 1, y, props))
                if (!visited.contains((x + 1) + "," + y))
                    stack.push((x + 1) + "," + y);
            if (isWallAt(x - 1, y, props))
                if (!visited.contains((x - 1) + "," + y))
                    stack.push((x - 1) + "," + y);
            if (isWallAt(x, y + 1, props))
                if (!visited.contains(x + "," + (y + 1)))
                    stack.push(x + "," + (y + 1));
            if (isWallAt(x, y - 1, props))
                if (!visited.contains(x + "," + (y - 1)))
                    stack.push(x + "," + (y - 1));
        }
    }

    private WallType findWallType(int x, int y, Properties props) {
        boolean isUp = isWallAt(x, y + 1, props);
        boolean isDown = isWallAt(x, y - 1, props);
        boolean isRight = isWallAt(x + 1, y, props);
        boolean isLeft = isWallAt(x - 1, y, props);
        int neighbors_count = 0;
        if (isUp)
            neighbors_count++;
        if (isDown)
            neighbors_count++;
        if (isLeft)
            neighbors_count++;
        if (isRight)
            neighbors_count++;
        switch (neighbors_count) {
            case 1:
                if (isUp || isDown)
                    return WallType.VERTICAL;
                if (isRight || isLeft)
                    return WallType.HORIZONTAL;
            case 2:
                if (isRight && isLeft) return WallType.HORIZONTAL;
                if (isUp && isDown) return WallType.VERTICAL;
                if (isUp && isRight) return WallType.CORNER_LD;
                if (isUp && isLeft) return WallType.CORNER_RD;
                if (isDown && isRight) return WallType.CORNER_LU;
                if (isDown && isLeft) return WallType.CORNER_RU;
            case 3:
                if (!isUp || !isDown) return WallType.HORIZONTAL;
                if (!isRight || !isLeft) return WallType.VERTICAL;
            case 4:
                return WallType.HORIZONTAL;
            default:
                return WallType.HORIZONTAL;
        }
    }

    private boolean isWallAt(int x, int y, Properties props) {
        if (x < 0 || y < 0 || x >= mazeWidth || y >= mazeHeight)
            return false; // Out of maze
        String key = x + "," + y;
        if (!props.containsKey(key))
            return false; // Not a wall
        int type = Integer.parseInt(props.getProperty(key));
        return type == 0; // Return true if it is a wall
    }

    private TextureRegion getWallTexture(WallType type) {
        return switch (type) {
            case HORIZONTAL -> wallHorTexture;
            case VERTICAL -> wallVerTexture;
            case CORNER_LU -> cornerLUTexture;
            case CORNER_RU -> cornerRUTexture;
            case CORNER_LD -> cornerLDTexture;
            case CORNER_RD -> cornerRDTexture;
            default -> wallHorTexture;
        };
    }

    private void addEntryPoint() {
        for (int x = 1; x < mazeWidth - 1; x++) {
            String key = x + ",0";
            // Check if it's a wall
            if (gameObjects.get(key) instanceof Wall) {
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
        for (int x = 1; x < mazeWidth - 1; x++) {
            String key = x + "," + (mazeHeight - 1);
            // Check if it's a wall
            if (gameObjects.get(key) instanceof Wall) {
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
