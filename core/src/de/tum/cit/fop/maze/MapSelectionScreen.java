package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MapSelectionScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;

    public MapSelectionScreen(MazeRunnerGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        Gdx.input.setInputProcessor(stage); // Set stage as input processor

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title
        table.add(new Label("Select a Map", game.getSkin(), "title")).padBottom(50).row();

        // List available maps
        FileHandle[] mapFiles = Gdx.files.internal("maps").list(".properties");
        if (mapFiles.length == 0) {
            table.add(new Label("No maps available!", game.getSkin())).row();
            return;
        }

        for (FileHandle mapFile : mapFiles) {
            TextButton mapButton = new TextButton(mapFile.nameWithoutExtension(), game.getSkin());
            table.add(mapButton).width(300).padBottom(20).row();

            mapButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.goToGame(mapFile.path());
                }
            });
        }

        // Back button
        TextButton backButton = new TextButton("Back", game.getSkin());
        table.add(backButton).width(300).padTop(50);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
