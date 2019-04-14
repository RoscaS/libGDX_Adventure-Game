package com.mygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.framework.BaseActor;
import com.framework.BaseScreen;
import com.framework.TilemapActor;
import com.mygame.Actors.Bush;
import com.mygame.Actors.Hero;
import com.mygame.Actors.Solid;
import com.mygame.Actors.Sword;

public class LevelScreen extends BaseScreen {

    Hero hero;
    Sword sword;

	/*------------------------------------------------------------------*\
	|*							Initialization							*|
	\*------------------------------------------------------------------*/

    @Override
    public void initialize() {
        TilemapActor tma = new TilemapActor("map.tmx", mainStage);

        for (MapObject obj : tma.getRectangleList(Solid.class.getCanonicalName())) {
            MapProperties props = obj.getProperties();
            new Solid(
                    (float) props.get("x"), (float) props.get("y"),
                    (float) props.get("width"), (float) props.get("height"), mainStage
            );
        }

        // start point
        MapObject startPoint = tma.getRectangleList("start").get(0);
        MapProperties startProps = startPoint.getProperties();

        // Actors
        hero = new Hero((float) startProps.get("x"), (float) startProps.get("y"), mainStage);
        sword = new Sword(0, 0, mainStage);
        sword.setVisible(false);

        // bushes
        for (MapObject obj : tma.getTileList("Bush")) {
            MapProperties props = obj.getProperties();
            new Bush((float)props.get("x"), (float)props.get("y"), mainStage);
        }
    }

	/*------------------------------*\
	|*				Getters			*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Public Methods 							*|
	\*------------------------------------------------------------------*/

    @Override
    public void update(float dt) {

        if (!sword.isVisible()) { // can't use the sword if moving
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) hero.accelerateAtAngle(180);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) hero.accelerateAtAngle(0);
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) hero.accelerateAtAngle(90);
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) hero.accelerateAtAngle(270);
        }

        for (BaseActor solid : BaseActor.getList(mainStage, Solid.class.getCanonicalName())) {
            hero.preventOverlap(solid);
        }

        // allows sword to destroy bushes
        if (sword.isVisible()) {
            for (BaseActor bush : BaseActor.getList(mainStage, Bush.class.getCanonicalName())) {
                if (sword.overlaps(bush)) bush.remove();
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.S) swingSword();
        return false;
    }

    public void swingSword() {
        // visibility determines if sword is currently swinging
        if (sword.isVisible()) return;
        hero.setSpeed(0);

        float facingAngle = hero.getFacingAngle();

        Vector2 offset = new Vector2();
        if (facingAngle == 0)
            offset.set( 0.50f, 0.20f );
        else if (facingAngle == 90)
            offset.set( 0.65f, 0.50f );
        else if (facingAngle == 180)
            offset.set( 0.40f, 0.20f );
        else // facingAngle == 270
            offset.set( 0.25f, 0.20f );

        sword.setPosition( hero.getX(), hero.getY() );
        sword.moveBy( offset.x * hero.getWidth(), offset.y * hero.getHeight() );

        float swordArc = 90;
        float swordSpeed = .15f;
        sword.setRotation(facingAngle - swordArc/2);
        sword.setOriginX(0);

        sword.setVisible(true);
        sword.addAction( Actions.rotateBy(swordArc, swordSpeed) );
        sword.addAction( Actions.after( Actions.visible(false) ) );

        // hero should appear in front of sword when facing north or west
        if (facingAngle == 90 || facingAngle == 180)
            hero.toFront();
        else
            sword.toFront();
    }

	/*------------------------------------------------------------------*\
	|*							Private Methods 						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Private Attributs 						*|
	\*------------------------------------------------------------------*/
}
