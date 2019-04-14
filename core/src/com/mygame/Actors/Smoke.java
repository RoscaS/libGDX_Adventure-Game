package com.mygame.Actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.framework.BaseActor;

public class Smoke extends BaseActor {

    public Smoke(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("smoke.png");
        addAction(Actions.fadeOut(.5f));
        addAction(Actions.after(Actions.removeActor()));
    }
}
