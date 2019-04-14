package com.mygame.Actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class Coin extends BaseActor {

    public Coin(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("coin.png");
    }
}
