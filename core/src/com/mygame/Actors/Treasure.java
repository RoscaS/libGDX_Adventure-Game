package com.mygame.Actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class Treasure extends BaseActor {

    public Treasure(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("treasure-chest.png");
    }
}
