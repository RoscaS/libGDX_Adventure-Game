package com.mygame.Actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class ShopHeart extends BaseActor {

    public ShopHeart(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("heart-icon.png");
    }
}
