package com.mygame.Actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class Flyer extends BaseActor {

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public Flyer(float x, float y, Stage s) {
        super(x, y, s);
        loadAnimationFromSheet("enemy-flyer.png", 1, 4, .05f, true);
        setSize(48, 48);
        setBoundaryPolygon(6);

        setSpeed(MathUtils.random(50, 80));
        setMotionAngle(MathUtils.random(0, 360));
    }

    /*------------------------------------------------------------------*\
   	|*							Public Methods 							*|
   	\*------------------------------------------------------------------*/

    public void act(float dt) {
        super.act(dt);
        if (MathUtils.random(1, 120) == 1) setMotionAngle(MathUtils.random(0, 360));
        applyPhysics(dt);
        boundToWorld();
    }
}
