package com.mygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.framework.BaseActor;
import com.framework.BaseGame;
import com.framework.BaseScreen;

public class MainMenuScreen extends BaseScreen {

    // audio
    private Music oceanSurf;

	/*------------------------------------------------------------------*\
	|*							Initialization							*|
	\*------------------------------------------------------------------*/

    public void initialize() {
        BaseActor ocean = new BaseActor(0, 0, mainStage);
        ocean.loadTexture("space.png");
        ocean.setSize(800, 600);

        BaseActor title = new BaseActor(0, 0, mainStage);
        title.loadTexture("main-title.png");

        TextButton startButton = new TextButton("Start", BaseGame.textButtonStyle);
        startButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent)) return false;
            if (!((InputEvent) e).getType().equals(InputEvent.Type.touchDown)) return false;
            // AdventureGame.setActiveScreen(new StoryScreen());
            return true;
        });

        TextButton quitButton = new TextButton("Quit", BaseGame.textButtonStyle);
        quitButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent)) return false;
            if (!((InputEvent) e).getType().equals(InputEvent.Type.touchDown)) return false;
            Gdx.app.exit();
            return true;
        });

        oceanSurf = Gdx.audio.newMusic(Gdx.files.internal("audio/opening.ogg"));
        oceanSurf.setLooping(true);
        oceanSurf.setVolume(.5f);
        oceanSurf.play();

        uiTable.row().expandY();
        uiTable.add(title).colspan(2);
        uiTable.row().expandY();
        uiTable.add(startButton);
        uiTable.add(quitButton);
    }

	/*------------------------------------------------------------------*\
	|*							Public Methods 							*|
	\*------------------------------------------------------------------*/

    @Override
    public void update(float dt) { }

    @Override
    public boolean keyDown(int KeyCode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            // AdventureGame.setActiveScreen(new StoryScreen());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        return false;
    }
}


