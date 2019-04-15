package com.framework.clickable;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class ClickableActor extends BaseActor {

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public ClickableActor(float x, float y, Stage s) {
        super(x, y, s);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println(getClassName() + "\t" + positionString());
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

	/*------------------------------------------------------------------*\
	|*							Public Methods 							*|
	\*------------------------------------------------------------------*/

    @Override
    public void act(float dt) {
        super.act(dt);
    }

    public String positionString() {
        return "x: " + this.getX() + "\ty: " + this.getY();
    }

}
