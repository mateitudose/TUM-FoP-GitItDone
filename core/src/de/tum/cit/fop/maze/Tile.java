package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
    private final TextureRegion textureRegion;
    private final boolean isWalkable;

    public Tile(TextureRegion textureRegion, boolean isWalkable) {
        this.textureRegion = textureRegion;
        this.isWalkable = isWalkable;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public boolean isWalkable() {
        return isWalkable;
    }
}