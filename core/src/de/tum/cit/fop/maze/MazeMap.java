package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public class MazeMap {
    private final TextureRegion[][] tileRegions; // 2D array of tiles
    private final int[][] mazeGrid; // 2D grid representing the maze
    private static final int TILE_SIZE = 16; // Size of each tile in pixels
    private final int rows;
    private final int cols;

    public MazeMap(String tilesetPath, int rows, int cols) {
        // Load the tileset and split it into regions
        Texture tileset = new Texture(tilesetPath);
        tileRegions = TextureRegion.split(tileset, TILE_SIZE, TILE_SIZE);

        this.rows = rows;
        this.cols = cols;

        // Initialize the maze grid
        mazeGrid = new int[rows][cols];
        generateRandomMaze();
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
                    tile = tileRegions[1][3]; // First tile (floor)
                } else {
                    tile = tileRegions[1][4]; // Second tile (wall)
                }

                batch.draw(tile, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public int[][] getMazeGrid() {
        return mazeGrid;
    }
}
