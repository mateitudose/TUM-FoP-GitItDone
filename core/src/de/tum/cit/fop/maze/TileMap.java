package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TileMap {
    private final Tile[][] map;
    private final int tileWidth;
    private final int tileHeight;
    private final Texture tileTexture;

    public TileMap(String textureFile, int tileWidth, int tileHeight, String propertiesFile) throws IOException {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileTexture = new Texture(Gdx.files.internal(textureFile));
        this.map = loadMap(propertiesFile);
    }

    private Tile[][] loadMap(String propertiesFile) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Gdx.files.internal(propertiesFile).read()) {
            properties.load(input);
        }
        String[] rows = properties.getProperty("maze").split("\\\\n");
        Tile[][] map = new Tile[rows.length][];
        for (int y = 0; y < rows.length; y++) {
            String[] cols = rows[y].split(",");
            map[y] = new Tile[cols.length];
            for (int x = 0; x < cols.length; x++) {
                int tileIndex = Integer.parseInt(cols[x]);
                int tileX = (tileIndex % (tileTexture.getWidth() / tileWidth)) * tileWidth;
                int tileY = (tileIndex / (tileTexture.getWidth() / tileWidth)) * tileHeight;
                TextureRegion tileRegion = new TextureRegion(tileTexture, tileX, tileY, tileWidth, tileHeight);
                boolean isWalkable = tileIndex == 0; // Example condition for walkable tiles
                map[y][x] = new Tile(tileRegion, isWalkable);
            }
        }
        return map;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        int startX = (int) (camera.position.x - camera.viewportWidth / 2) / tileWidth;
        int startY = (int) (camera.position.y - camera.viewportHeight / 2) / tileHeight;
        int endX = (int) (camera.position.x + camera.viewportWidth / 2) / tileWidth;
        int endY = (int) (camera.position.y + camera.viewportHeight / 2) / tileHeight;

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (x >= 0 && x < map[0].length && y >= 0 && y < map.length) {
                    Tile tile = map[y][x];
                    batch.draw(tile.getTextureRegion(), x * tileWidth, y * tileHeight);
                }
            }
        }
    }

    public void dispose() {
        tileTexture.dispose();
    }
}