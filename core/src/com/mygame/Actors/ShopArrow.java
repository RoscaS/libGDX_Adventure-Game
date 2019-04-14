package com.mygame.Actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class ShopArrow extends BaseActor {

    public ShopArrow(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("arrow-icon.png");
    }
}
