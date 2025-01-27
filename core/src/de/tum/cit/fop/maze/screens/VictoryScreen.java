package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * Displays the victory screen when the player wins.
 */
public class VictoryScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private int heart;
    private  int coin;
    private int fish;


    /**
     * Constructs a new VictoryScreen object.
     *
     * @param game the main game class
     */
    public VictoryScreen(MazeRunnerGame game, int heart, int coin, int fish) {
        this.game = game;
        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        Gdx.input.setInputProcessor(stage);

         this.heart= heart;
         this.fish= fish;
            this.coin= coin;


        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title
        table.add(new Label("You Won!", game.getSkin(), "title")).padBottom(50).row();

        Label heartLabel = new Label("You collected: " + heart + " hearts", game.getSkin(), "title");
        heartLabel.setFontScale(0.5f); // Scale the text to 80% of its original size
        table.add(heartLabel).padBottom(50).row();

        Label fishLabel = new Label("You collected: " + fish + " fish", game.getSkin(), "title");
        fishLabel.setFontScale(0.5f);
        table.add(fishLabel).padBottom(50).row();


        Label coinLabel = new Label("You collected: " + coin + " coins", game.getSkin(), "title");
        coinLabel.setFontScale(0.5f);
        table.add(coinLabel).padBottom(50).row();

        // Buttons
        TextButton mainMenuButton = new TextButton("Main Menu", game.getSkin());
        table.add(mainMenuButton).width(300).padBottom(20).row();

        TextButton exitButton = new TextButton("Exit", game.getSkin());
        table.add(exitButton).width(300);

        // Listeners
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Navigate to the main menu
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Exit the game
            }
        });
    }

    /**
     * Renders the victory screen.
     *
     * @param delta the time in seconds since the last render
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    /**
     * Resizes the victory screen.
     *
     * @param width  the new width
     * @param height the new height
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
}
