package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import box2dLight.RayHandler;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.MazeMap;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.entities.Player;
import de.tum.cit.fop.maze.objects.EntryPoint;
import de.tum.cit.fop.maze.objects.ExitPoint;
import de.tum.cit.fop.maze.objects.GameObject;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.util.List;


public class GameScreen implements Screen {
    private static final float CAMERA_ZOOM_SPEED = 0.01f;
    private static final float MIN_ZOOM = 0.15f;
    private static final float MAX_ZOOM = 0.3f;

    private final MazeRunnerGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private World gameWorld;
    private SpriteBatch batch;
    private MazeMap mazeMap;
    private Player player;
    private String mapPath;

    private RayHandler rayHandler;

    private long resizeEndTime = 0;

    private Box2DDebugRenderer debugRenderer;

    public GameScreen(MazeRunnerGame game, String mapPath) {
        this.game = game;

        // Initialize RayHandler
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(gameWorld);
        // Set ambient light to brighten the world
        rayHandler.setAmbientLight(1f, 1f, 1f, 1.0f); // Soft white ambient light

        this.mapPath = mapPath;

        // Debug the collision boxes (comment when not testing)
        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void show() {
        Box2D.init();
        gameWorld = new World(new Vector2(0, 0), true);

        gameWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
        // Load the maze
        int windowWidth = Gdx.graphics.getWidth();
        int windowHeight = Gdx.graphics.getHeight();
        mazeMap = new MazeMap(mapPath, windowWidth, windowHeight, gameWorld);

        // Initialize the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        // Calculate maze dimensions in pixels
        int mazePixelWidth = mazeMap.getMazeWidth() * MazeMap.TILE_SIZE;
        int mazePixelHeight = mazeMap.getMazeHeight() * MazeMap.TILE_SIZE;

        viewport = new ScreenViewport(camera);
        viewport.update(windowWidth, windowHeight, true);

        batch = new SpriteBatch();

        // Initialize the player
        Vector2 entryPosition = findEntryPoint();
        float entryX = (entryPosition.x + 0.5f) * MazeMap.TILE_SIZE / 16f; // Add 0.5f to center
        float entryY = (entryPosition.y + 0.5f) * MazeMap.TILE_SIZE / 16f; // Add 0.5f to center
        player = new Player(gameWorld, mazeMap, new Vector2(entryX, entryY));

        Vector2 playerPosition = player.getBody().getPosition();
        camera.position.set(playerPosition.x * MazeMap.TILE_SIZE, playerPosition.y * MazeMap.TILE_SIZE, 0);

        camera.update();
    }

    private Vector2 findEntryPoint() {
        for (List<GameObject> objects : mazeMap.getGameObjects().values()) {
            for (GameObject object : objects) {
                if (object instanceof EntryPoint) {
                    int entryX = object.getX();
                    int entryY = object.getY();

                    // Check if the entry position overlaps a wall
                    if (mazeMap.isWall(entryX, entryY)) {
                        throw new IllegalStateException("Entry point overlaps a wall at: " + entryX + ", " + entryY);
                    }

                    return new Vector2(entryX, entryY);
                }
            }
        }
        throw new IllegalStateException("No EntryPoint found in the maze!");
    }



    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Update player
        player.update(delta);

        checkGameStatus();

        // Check if resizing is complete
        if (resizeEndTime > 0 && System.currentTimeMillis() > resizeEndTime) {
            recenterCameraOnPlayer();
            resizeEndTime = 0;
        }

        // Update the camera position
        updateCamera();

        // Render the maze
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for (List<GameObject> objectList : mazeMap.getGameObjects().values()) {
            for (GameObject object : objectList) {
                object.render(batch);
            }
        }

        player.render(batch);
        batch.end();

        // Render lights
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        Matrix4 scaledMatrix = new Matrix4(camera.combined);
        scaledMatrix.scale(16f, 16f, 16f);

        debugRenderer.render(gameWorld, scaledMatrix);

        // Update Box2D world
        gameWorld.step(1 / 60f, 6, 2);
    }

    private void updateCamera() {
        Vector2 playerPosition = player.getBody().getPosition();
        // Smooth interpolation
        float lerpFactor = 0.3f;
        camera.position.lerp(new Vector3(playerPosition.x * MazeMap.TILE_SIZE, playerPosition.y * MazeMap.TILE_SIZE, 0), lerpFactor);

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += CAMERA_ZOOM_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= CAMERA_ZOOM_SPEED;
        }

        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, camera.zoom));
        camera.update();
    }

    private void recenterCameraOnPlayer() {
        Vector2 playerPosition = player.getBody().getPosition();
        camera.position.set(playerPosition.x, playerPosition.y, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        gameWorld.dispose();
        rayHandler.dispose();
        debugRenderer.dispose();
    }

    private void checkGameStatus() {
        for (ExitPoint exitPoint : mazeMap.getExitPoints()) {
            if (exitPoint.checkIfPlayerReachedExit(player, game)) {
                return;
            }
        }
    }

}
