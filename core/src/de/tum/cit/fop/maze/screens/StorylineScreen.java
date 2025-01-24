package de.tum.cit.fop.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

public class StorylineScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private Texture backgroundTexture;

    public StorylineScreen(MazeRunnerGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        Gdx.input.setInputProcessor(stage);

        // Load the background image for this screen
        backgroundTexture = new Texture(Gdx.files.internal("assets/pixelart.png")); // Change path as needed

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Add storyline text
        table.add(new Label("Welcome to the thrilling life of CATNAME the cat burglar of the town town!CATNAME mission? To collect as much delicious fish as possible while going through the house unnoticed.\n" +
                        "\n" +
                        "But beware! This house is no ordinary home. High-tech laser traps guard the treasures, ready to catch Simba in the act. Sticky zone surfaces and tricky obstacles will slow you down, while hidden hearts offer extra lives to keep CATNAME going.\n" +
                        "\n" +
                        "Your goal is clear: dodge the dangers like the owners dog and the lasers, gather the fish that you came to steal to continue your quest for the ultimate feast. Are you clever and quick enough to outsmart everyone and complete the heist?\n" +
                        "\n" +
                        "Good luck, thief ! ", game.getSkin(), "default"))
                .padBottom(50).row();

        // TODO: Add a timer to automatically transition after 5 seconds
        // TODO: Add background music for this screen
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background image
        game.getSpriteBatch().begin();
        game.getSpriteBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getSpriteBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose(); // Dispose the background texture when done
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
