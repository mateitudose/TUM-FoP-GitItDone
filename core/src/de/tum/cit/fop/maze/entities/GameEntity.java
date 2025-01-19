package de.tum.cit.fop.maze.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import de.tum.cit.fop.maze.objects.GameObject;

public abstract class GameEntity extends GameObject {
    protected Body body;
    protected Vector2 position;
    protected TextureRegion texture;

    public GameEntity(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, tileSize, texture);
        this.position = new Vector2(x, y);
        this.texture = texture;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    public abstract void update(float delta);
}
