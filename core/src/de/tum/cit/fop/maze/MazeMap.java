package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public class MazeMap {
    private final TextureRegion[][] carpetTiles;
    private final TextureRegion[][] wallTiles;
    private final TextureRegion[][] furnitureTiles;
    private final int[][] mazeGrid; // 2D grid representing the maze
    private static final int TILE_SIZE = 16; // Size of each tile in pixels (16x16)
    private final int rows;
    private final int cols;

    public MazeMap(int rows, int cols) {
        // Use an atlas to improve performance
        TextureAtlas atlas = new TextureAtlas("world.atlas");

        // Extract tiles from the atlas
        carpetTiles = splitRegion(atlas.findRegion("carpets"), TILE_SIZE, TILE_SIZE);
        wallTiles = splitRegion(atlas.findRegion("walls_floor_doors"), TILE_SIZE, TILE_SIZE);
        furnitureTiles = splitRegion(atlas.findRegion("furniture"), TILE_SIZE, TILE_SIZE);

        this.rows = rows;
        this.cols = cols;

        // Initialize the maze grid
        mazeGrid = new int[rows][cols];
        generateRandomMaze();
    }

    /**
     * Splits a TextureRegion into a 2D array of tiles.
     *
     * @param region The TextureRegion to split.
     * @param tileWidth The width of each tile.
     * @param tileHeight The height of each tile.
     * @return A 2D array of TextureRegion objects.
     */
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

    private void generateRandomMaze() {
        Random random = new Random();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (random.nextFloat() < 0.3f) {
                    mazeGrid[y][x] = 1; // Wall
                } else {
                    mazeGrid[y][x] = 0; // Floor
                }
            }
        }

        // Ensure starting point is always a floor
        mazeGrid[0][0] = 0;
    }

    public void render(SpriteBatch batch) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                TextureRegion tile;
                if (mazeGrid[y][x] == 0) {
                    tile = carpetTiles[1][1]; // Example: Floor tile
                } else {
                    tile = wallTiles[0][3]; // Example: Wall tile
                }
                batch.draw(tile, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public int[][] getMazeGrid() {
        return mazeGrid;
    }
}
