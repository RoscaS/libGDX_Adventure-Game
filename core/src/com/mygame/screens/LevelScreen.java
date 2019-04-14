package com.mygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.framework.*;
import com.mygame.Actors.*;

public class LevelScreen extends BaseScreen {

    // actors
    Hero hero;
    Sword sword;
    Treasure treasure;

    // values
    int health;
    int coins;
    int arrows;

    // tools
    boolean gameOver;

    // ui
    Label healthLabel;
    Label coinsLabel;
    Label arrowLabel;
    Label messageLabel;

    DialogBox dialogBox;

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
            new Bush((float) props.get("x"), (float) props.get("y"), mainStage);
        }

        // rock
        for (MapObject obj : tma.getTileList("Rock")) {
            MapProperties props = obj.getProperties();
            new Rock((float) props.get("x"), (float) props.get("y"), mainStage);
        }

        // coin
        for (MapObject obj : tma.getTileList("Coin")) {
            MapProperties props = obj.getProperties();
            new Coin((float) props.get("x"), (float) props.get("y"), mainStage);
        }
        // Treasure
        MapObject treasureTile = tma.getTileList("Treasure").get(0);
        MapProperties treasureProps = treasureTile.getProperties();
        treasure = new Treasure((float) treasureProps.get("x"), (float) treasureProps.get("y"), mainStage);


        // UI
        health = 3;
        coins = 5;
        arrows = 3;
        gameOver = false;

        healthLabel = new Label(" x " + health, BaseGame.labelStyle);
        healthLabel.setColor(Color.PINK);

        coinsLabel = new Label(" x " + coins, BaseGame.labelStyle);
        coinsLabel.setColor(Color.GOLD);

        arrowLabel = new Label(" x " + arrows, BaseGame.labelStyle);
        arrowLabel.setColor(Color.TAN);

        messageLabel = new Label("...", BaseGame.labelStyle);
        messageLabel.setVisible(false);


        BaseActor healthIcon = new BaseActor(0, 0, uiStage);
        healthIcon.loadTexture("heart-icon.png");

        BaseActor coinIcon = new BaseActor(0, 0, uiStage);
        coinIcon.loadTexture("coin-icon.png");

        BaseActor arrowIcon = new BaseActor(0, 0, uiStage);
        arrowIcon.loadTexture("arrow-icon.png");

        uiTable.pad(20);
        uiTable.add(healthIcon);
        uiTable.add(healthLabel);
        uiTable.add().expandX();
        uiTable.add(coinIcon);
        uiTable.add(coinsLabel);
        uiTable.add().expandX();
        uiTable.add(arrowIcon);
        uiTable.add(arrowLabel);
        uiTable.row();
        uiTable.add(messageLabel).colspan(8).expandX().expandY();
        uiTable.row();
        uiTable.add(dialogBox).colspan(8);

    }

	/*------------------------------*\
	|*				Getters			*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Public Methods 							*|
	\*------------------------------------------------------------------*/

    @Override
    public void update(float dt) {

        if (gameOver) return;

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

        // gather coin
        for (BaseActor coin : BaseActor.getList(mainStage, Coin.class.getCanonicalName())) {
            if (hero.overlaps(coin)) {
                coin.remove();
                coins++;
            }
        }

        // gather treasure and win
        if (hero.overlaps(treasure)) {
            messageLabel.setText("You win !");
            messageLabel.setColor(Color.LIME);
            messageLabel.setFontScale(2);
            messageLabel.setVisible(true);
            treasure.remove();
            gameOver = true;
        }

        // no more life and die
        if (health <= 0) {
            messageLabel.setText("Game over...");
            messageLabel.setColor(Color.RED);
            messageLabel.setFontScale(2);
            messageLabel.setVisible(true);
            hero.remove();
            gameOver = true;
        }

        // update ui
        healthLabel.setText(" x " + health);
        coinsLabel.setText(" x " + coins);
        arrowLabel.setText(" x " + arrows);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (gameOver) return false;
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
            offset.set(0.50f, 0.20f);
        else if (facingAngle == 90)
            offset.set(0.65f, 0.50f);
        else if (facingAngle == 180)
            offset.set(0.40f, 0.20f);
        else // facingAngle == 270
            offset.set(0.25f, 0.20f);

        sword.setPosition(hero.getX(), hero.getY());
        sword.moveBy(offset.x * hero.getWidth(), offset.y * hero.getHeight());

        float swordArc = 90;
        float swordSpeed = .15f;
        sword.setRotation(facingAngle - swordArc / 2);
        sword.setOriginX(0);

        sword.setVisible(true);
        sword.addAction(Actions.rotateBy(swordArc, swordSpeed));
        sword.addAction(Actions.after(Actions.visible(false)));

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
