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
import com.badlogic.gdx.utils.ScreenUtils;
import box2dLight.RayHandler;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.HUD;
import de.tum.cit.fop.maze.MazeMap;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.entities.Enemy;
import de.tum.cit.fop.maze.entities.Player;
import de.tum.cit.fop.maze.objects.*;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import de.tum.cit.fop.maze.objects.LaserTrap;
import de.tum.cit.fop.maze.pathfinding.Algorithm;

import java.util.HashSet;
import java.util.Set;


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
    private Box2DDebugRenderer debugRenderer;
    private Algorithm pathfinder;

    private int lastWidth = -1;
    private int lastHeight = -1;
    private boolean isPaused = false;

    private Set<LaserTrap> activeContactTraps = new HashSet<>();
    private final Set<Fish> fishToCollect = new HashSet<>();
    private final Set<Heart> heartsToCollect = new HashSet<>();

    private HUD hud;
    private final Vector2 playerPosition = new Vector2();

    public GameScreen(MazeRunnerGame game, String mapPath) {
        this.game = game;
        this.mapPath = mapPath;
        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void show() {
        if (gameWorld != null) {
            return;
        }

        // Initialize RayHandler
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(gameWorld);
        rayHandler.setAmbientLight(1f, 1f, 1f, 1.0f); // Soft white ambient light

        Box2D.init();
        gameWorld = new World(new Vector2(0, 0), true);

        gameWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                handleTrapContact(contact, true);
                handleFishContact(contact, true);
                handleSlowTileContact(contact, true);
                handleHeartContact(contact, true);
                handleEnemyContact(contact, true);
            }

            @Override
            public void endContact(Contact contact) {
                handleTrapContact(contact, false);
            }

            @Override
            public void preSolve(Contact contact, Manifold manifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {

            }

            private void handleTrapContact(Contact contact, boolean isBegin) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                Object userDataA = fixtureA.getBody().getUserData();
                Object userDataB = fixtureB.getBody().getUserData();

                if ((userDataA instanceof Player && userDataB instanceof LaserTrap) ||
                        (userDataB instanceof Player && userDataA instanceof LaserTrap)) {
                    LaserTrap trap = (userDataA instanceof LaserTrap) ? (LaserTrap) userDataA : (LaserTrap) userDataB;
                    if (isBegin) {
                        activeContactTraps.add(trap);
                        if (trap.isDangerous()) {
                            player.loseLives(1);
                        }
                    } else {
                        activeContactTraps.remove(trap);
                    }
                }
            }

            private void handleFishContact(Contact contact, boolean isBegin) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                Object userDataA = fixtureA.getBody().getUserData();
                Object userDataB = fixtureB.getBody().getUserData();

                if ((userDataA instanceof Player && userDataB instanceof Fish) ||
                        (userDataB instanceof Player && userDataA instanceof Fish)) {
                    Fish fish = (userDataA instanceof Fish) ? (Fish) userDataA : (Fish) userDataB;
                    if (isBegin) {
                        fishToCollect.add(fish);
                    }
                }
            }

            private void handleSlowTileContact(Contact contact, boolean isBegin) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                // Check if either fixture is the slow tile and the other is the player
                if ((fixtureA.getBody().getUserData() instanceof SlowTile &&
                        fixtureB.getBody().getUserData() instanceof Player) ||
                        (fixtureB.getBody().getUserData() instanceof SlowTile &&
                                fixtureA.getBody().getUserData() instanceof Player)) {

                    if (isBegin) {
                        player.applySlowEffect(5);
                    }
                }
            }

            private void handleHeartContact(Contact contact, boolean isBegin) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                Object userDataA = fixtureA.getBody().getUserData();
                Object userDataB = fixtureB.getBody().getUserData();

                if ((userDataA instanceof Player && userDataB instanceof Heart) ||
                        (userDataB instanceof Player && userDataA instanceof Heart)) {
                    Heart heart = (userDataA instanceof Heart) ? (Heart) userDataA : (Heart) userDataB;
                    if (isBegin) {
                        heartsToCollect.add(heart);
                    }
                }
            }

            private void handleEnemyContact(Contact contact, boolean isBegin) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                Object userDataA = fixtureA.getBody().getUserData();
                Object userDataB = fixtureB.getBody().getUserData();

                if ((userDataA instanceof Player && userDataB instanceof Enemy) ||
                        (userDataB instanceof Player && userDataA instanceof Enemy)) {

                    if (isBegin && player.canTakeDamage()) {
                        player.loseLives(1);
                    }
                }
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
        float entryX = (entryPosition.x + 0.5f) * MazeMap.TILE_SIZE / 16f;
        float entryY = (entryPosition.y + 0.5f) * MazeMap.TILE_SIZE / 16f;
        player = new Player(gameWorld, mazeMap, new Vector2(entryX, entryY));

        // Initialize the pathfinder and set it to the calculate player paths in the maze map
        pathfinder = new Algorithm(mazeMap);
        mazeMap.setPathfinder(pathfinder);
        mazeMap.setPlayer(player);

        Vector2 playerPosition = player.getBody().getPosition();
        camera.position.set(playerPosition.x * MazeMap.TILE_SIZE, playerPosition.y * MazeMap.TILE_SIZE, 0);
        camera.update();

        hud = new HUD(player, mazeMap);
    }

    private Vector2 findEntryPoint() {
        return new Vector2(mazeMap.entryX, mazeMap.entryY);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Handle game pause when Esc is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            delta = 0; // Prevent updating game logic when paused
            game.goToPauseMenu(this);
            return;
        }

        // Update player
        player.update(delta);

        // Update the animations of the laser traps
        for (LaserTrap laserTrap : mazeMap.getLaserTraps()) {
            laserTrap.update(delta);
        }

        for (Enemy enemy : mazeMap.getEnemies()) {
            enemy.update(delta);
        }

        for (LaserTrap trap : activeContactTraps) {
            if (trap.becameDangerous()) {
                player.loseLives(1);
            }

        }

        // Process collected fish
        for (Fish fish : fishToCollect) {
            mazeMap.removeGameObject(fish);
            if (fish.getBody() != null) {
                mazeMap.removeGameObject(fish);
                gameWorld.destroyBody(fish.getBody());
            }
            player.collectFish();
        }
        fishToCollect.clear();

        // Process collected hearts
        for (Heart heart : heartsToCollect) {
            if (player.canGainLife()) {
                player.addLife();
                mazeMap.removeGameObject(heart);
                heart.collect();
            }
        }
        heartsToCollect.clear();

        // Disable exit points if player has collected 1 fish
        if (player.getCollectedFish() == 1) {
            for (ExitPoint exit : mazeMap.getExitPoints()) {
                exit.disableCollision();
            }
        }

        checkGameStatus();
        updateCamera();

        playerPosition.set(player.getBody().getPosition());

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mazeMap.render(batch);
        player.render(batch);

        for (Enemy enemy : mazeMap.getEnemies()) {
            enemy.render(batch);
        }

        // Switch to screen coordinates for HUD rendering
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        hud.render(batch, playerPosition);

        batch.end();

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        // Debug rendering of all colliders
        Matrix4 scaledMatrix = new Matrix4(camera.combined);
        scaledMatrix.scale(16f, 16f, 16f);

        debugRenderer.render(gameWorld, scaledMatrix);

        // Update Box2D world
        gameWorld.step(1 / 60f, 6, 2);
    }

    private void updateCamera() {
        Vector2 playerPosition = player.getBody().getPosition();
        // Smooth interpolation
        float lerpFactor = 0.2f;
        camera.position.lerp(new Vector3(playerPosition.x * MazeMap.TILE_SIZE, playerPosition.y * MazeMap.TILE_SIZE, 0), lerpFactor);

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += CAMERA_ZOOM_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= CAMERA_ZOOM_SPEED;
        }

        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, camera.zoom));
        camera.update();
    }

    private void checkGameStatus() {
        // Check for game over first
        if (player.getLives() <= 0) {
            game.goToGameOver(mapPath);
            return;
        }

        for (ExitPoint exitPoint : mazeMap.getExitPoints()) {
            if (exitPoint.checkIfPlayerReachedExit(player, game)) {
                game.goToVictory();
                return;
            }
        }
    }

    private void togglePause() {
        isPaused = !isPaused;

        for (LaserTrap laserTrap : mazeMap.getLaserTraps()) {
            if (isPaused) {
                laserTrap.pauseTimer();
            } else {
                laserTrap.resumeTimer();
            }
        }
    }

    // Apparently, after unpausing game, libGDX does call resize() method, so we (actually) need to update the viewport ONLY if the dimensions change
    // Wasted 3 hours on this thing :(
    @Override
    public void resize(int width, int height) {
        if (width != lastWidth || height != lastHeight) {
            lastWidth = width;
            lastHeight = height;
            // Only update if dimensions change
            viewport.update(width, height, true);
            camera.update();
        }
    }

    @Override
    public void pause() {
        togglePause();
    }

    @Override
    public void resume() {
        togglePause();
    }

    @Override
    public void hide() {
    }

    // TODO: Dispose of all resources
    @Override
    public void dispose() {
    }
}
