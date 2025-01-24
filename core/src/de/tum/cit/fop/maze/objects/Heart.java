package de.tum.cit.fop.maze.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Heart extends GameObject{
    private Animation<TextureRegion> animation;
    private float time;


    public Heart(int x, int y, int tileSize, TextureRegion texture) {
        super(x, y, TILE_SIZE, TILE_SIZE,texture);
        time = 0f;
        sprite.setPosition(x * TILE_SIZE, y * TILE_SIZE);
        loadAnimation();
    }

    private void loadAnimation(){
        TextureRegion[] frames = new TextureRegion[4];
        frames[0] = new TextureRegion(new Texture("world.png"), 67, 262, 16, 16);
        frames[1] = new TextureRegion(new Texture("world.png"), 67 + TILE_SIZE, 262, 16, 16);
        frames[2] = new TextureRegion(new Texture("world.png"), 67 + 2 * TILE_SIZE, 262, 16, 16);
        frames[3] = new TextureRegion(new Texture("world.png"), 67 + 3 * TILE_SIZE, 262, 16, 16);
        animation = new Animation<>(0.1f, frames);
    }

    public void update(float deltaTime) {
        time += deltaTime;
    }

    @Override
    public void render(SpriteBatch batch) {
        time += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animation.getKeyFrame(time, true);
        batch.draw(currentFrame, x * TILE_SIZE, y * TILE_SIZE);
    }
}