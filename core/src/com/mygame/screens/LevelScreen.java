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

    // Tiled map objects
    TilemapActor tma;

    // actors
    Hero hero;
    Sword sword;
    Treasure treasure;

    // Shop
    ShopHeart shopHeart;
    ShopArrow shopArrow;

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
        tma = new TilemapActor("map.tmx", mainStage);
        initSolids();
        initShops();
        initHero();
        initActors();
        initUi();
    }

    private void initSolids() {
        for (MapObject obj : tma.getRectangleList("Solid")) {
            MapProperties props = obj.getProperties();
            new Solid(
                    (float) props.get("x"), (float) props.get("y"),
                    (float) props.get("width"), (float) props.get("height"), mainStage
            );
        }
    }

    private void initHero() {
        // start point
        MapObject startPoint = tma.getRectangleList("start").get(0);
        MapProperties startProps = startPoint.getProperties();

        // Actors
        hero = new Hero((float) startProps.get("x"), (float) startProps.get("y"), mainStage);
        sword = new Sword(0, 0, mainStage);
        sword.setVisible(false);
    }

    private void initActors() {
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

        // Flyers
        for (MapObject obj : tma.getTileList("Flyer")) {
            MapProperties props = obj.getProperties();
            new Flyer((float) props.get("x"), (float) props.get("y"), mainStage);
        }

        // NPCs
        for (MapObject obj : tma.getTileList("NPC")) {
            MapProperties props = obj.getProperties();
            NPC npc = new NPC((float) props.get("x"), (float) props.get("y"), mainStage);
            npc.setId((String) props.get("id"));
            npc.setText((String) props.get("text"));
        }
    }

    private void initShops() {
        MapObject shopHeartTile = tma.getTileList("ShopHeart").get(0);
        MapProperties shopHeartProps = shopHeartTile.getProperties();
        shopHeart = new ShopHeart((float) shopHeartProps.get("x"), (float) shopHeartProps.get("y"), mainStage);

        MapObject shopArrowTile = tma.getTileList("ShopArrow").get(0);
        MapProperties shopArrowProps = shopArrowTile.getProperties();
        shopArrow = new ShopArrow((float) shopArrowProps.get("x"), (float) shopArrowProps.get("y"), mainStage);
    }

    private void initUi() {
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

        dialogBox = new DialogBox(0, 0, uiStage);
        dialogBox.setBackgroundColor(Color.TAN);
        dialogBox.setFontColor(Color.BROWN);
        dialogBox.setDialogSize(600, 100);
        dialogBox.setFontScale(0.80f);
        dialogBox.alignCenter();
        dialogBox.setVisible(false);

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

	/*------------------------------------------------------------------*\
	|*							Updating     							*|
	\*------------------------------------------------------------------*/

    @Override
    public boolean keyDown(int keycode) {
        if (gameOver) return false;
        if (keycode == Input.Keys.S) swingSword();
        if (keycode == Input.Keys.A) shootArrow();

        // shop actions
        if (keycode == Input.Keys.B) {
            if (hero.overlaps(shopHeart) && coins >= 3) {
                coins -= 3;
                health += 1;
            }
            if (hero.overlaps(shopArrow) && coins >= 4) {
                coins -= 4;
                arrows += 3;
            }
        }
        return false;
    }

    @Override
    public void update(float dt) {
        if (gameOver) return;

        // can't use the sword if moving
        if (!sword.isVisible()) {
            pollMovements();
        }

        checkCollisions();
        checkSwordStrike();
        checkArrowShot();
        knockbackMechanic();
        gatherCoin();
        gatherTreasureAndWin();
        npcDialog();
        checkHealth();
        updateUi();
    }

    private void pollMovements() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) hero.accelerateAtAngle(180);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) hero.accelerateAtAngle(0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) hero.accelerateAtAngle(90);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) hero.accelerateAtAngle(270);
    }

    private void checkCollisions() {
        for (BaseActor solid : BaseActor.getList(mainStage, Solid.class.getCanonicalName())) {
            // hero
            hero.preventOverlap(solid);
            // flyers
            for (BaseActor flyer : BaseActor.getList(mainStage, Flyer.class.getCanonicalName())) {
                if (flyer.overlaps(solid)) {
                    flyer.preventOverlap(solid);
                    flyer.setMotionAngle(flyer.getMotionAngle() + 180);
                }
            }
        }
    }

    private void checkSwordStrike() {
        if (sword.isVisible()) {
            // bushes
            for (BaseActor bush : BaseActor.getList(mainStage, Bush.class.getCanonicalName())) {
                if (sword.overlaps(bush)) bush.remove();
            }
            // flyer
            for (BaseActor flyer : BaseActor.getList(mainStage, Flyer.class.getCanonicalName())) {
                if (sword.overlaps(flyer)) {
                    killFlyer((Flyer) flyer);
                }
            }
        }
    }

    private void checkArrowShot() {
        for (BaseActor arrow : BaseActor.getList(mainStage, Arrow.class.getCanonicalName())) {
            for (BaseActor flyer : BaseActor.getList(mainStage, Flyer.class.getCanonicalName())) {
                if (arrow.overlaps(flyer)) {
                    arrow.remove();
                    killFlyer((Flyer) flyer);
                }
            }
            // arrows are blocked by Solids
            for (BaseActor solid : BaseActor.getList(mainStage, Solid.class.getCanonicalName())) {
                if (arrow.overlaps(solid)) {
                    arrow.preventOverlap(solid);
                    arrow.setSpeed(0);
                    arrow.addAction(Actions.fadeOut(10f));
                    arrow.addAction(Actions.after(Actions.removeActor()));
                }
            }
        }
    }

    private void knockbackMechanic() {
        for (BaseActor flyer : BaseActor.getList(mainStage, Flyer.class.getCanonicalName())) {
            if (hero.overlaps(flyer)) {
                hero.preventOverlap(flyer);
                flyer.setMotionAngle(flyer.getMotionAngle() + 180);
                Vector2 heroPosition = new Vector2(hero.getX(), hero.getY());
                Vector2 flyerPosition = new Vector2(flyer.getX(), flyer.getY());
                Vector2 hitVector = heroPosition.sub(flyerPosition);
                hero.setMotionAngle(hitVector.angle());
                hero.setSpeed(100);
                health--;
            }
        }
    }

    private void checkHealth() {
        if (health <= 0) {
            messageLabel.setText("Game over...");
            messageLabel.setColor(Color.RED);
            messageLabel.setFontScale(2);
            messageLabel.setVisible(true);
            hero.remove();
            gameOver = true;
        }
    }

    private void gatherCoin() {
        for (BaseActor coin : BaseActor.getList(mainStage, Coin.class.getCanonicalName())) {
            if (hero.overlaps(coin)) {
                coin.remove();
                coins++;
            }
        }
    }

    private void gatherTreasureAndWin() {
        if (hero.overlaps(treasure)) {
            messageLabel.setText("You win !");
            messageLabel.setColor(Color.LIME);
            messageLabel.setFontScale(2);
            messageLabel.setVisible(true);
            treasure.remove();
            gameOver = true;
        }
    }

    private void updateUi() {
        healthLabel.setText(" x " + health);
        coinsLabel.setText(" x " + coins);
        arrowLabel.setText(" x " + arrows);
    }

    private void npcDialog() {
        for (BaseActor npcActor : BaseActor.getList(mainStage, NPC.class.getCanonicalName())) {
            NPC npc = (NPC) npcActor;
            hero.preventOverlap(npc);
            boolean nearby = hero.isWithinDistance(4, npc);

            if (nearby && !npc.isViewing()) {
                // check NPC id for dynamic text
                if (npc.getId().equals("Gatekeeper")) {
                    int flyerCount = BaseActor.count(mainStage, Flyer.class.getCanonicalName());
                    String message = "Destroy the Flyers and the treasure is yours.";

                    if (flyerCount > 1) {
                        message += " (" + flyerCount + " Flyers)";
                    } else if (flyerCount == 1) {
                        message += "Only one Flyer left !";
                    } else { // flyerCount == 0
                        message += "The threasure is yours !";
                        npc.addAction(Actions.fadeOut(5.f));
                        npc.addAction(Actions.after(Actions.moveBy(-10000, -10000)));
                    }
                    dialogBox.setText(message);
                } else {
                    dialogBox.setText(npc.getText());
                }
                dialogBox.setVisible(true);
                npc.setViewing(true);
            }
            if (npc.isViewing() && !nearby) {
                dialogBox.setText(" ");
                dialogBox.setVisible(false);
                npc.setViewing(false);
            }
        }
    }

    /*------------------------------*\
   	|*			Complex actions		*|
   	\*------------------------------*/

    private void killFlyer(Flyer flyer) {
        flyer.remove();
        Coin coin = new Coin(0, 0, mainStage); // spawn a coin ...
        coin.centerAtActor(flyer);  // ... over the dead flyer
        Smoke smoke = new Smoke(0, 0, mainStage); // smoke animation ...
        smoke.centerAtActor(flyer); // ... over the dying flyer
    }

    private void swingSword() {
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

    private void shootArrow() {
        if (arrows <= 0) return;
        arrows--;
        Arrow arrow = new Arrow(0, 0, mainStage);
        arrow.centerAtActor(hero);
        arrow.setRotation(hero.getFacingAngle());
        arrow.setMotionAngle(hero.getFacingAngle());
    }
}
