package com.mygame.Actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class Sword extends BaseActor {

    public Sword(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("sword.png");
    }
}
