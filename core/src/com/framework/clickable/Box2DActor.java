package com.framework.clickable;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class Box2DActor extends BaseActor {

    protected BodyDef bodyDef;
    protected Body body;
    protected FixtureDef fixtureDef;
    protected Float maxSpeed;
    protected Float maxSpeedX;
    protected Float maxSpeedY;

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public Box2DActor(float x, float y, Stage s) {
        super(x, y, s);

        body = null;
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        maxSpeed = null;
        maxSpeedX = null;
        maxSpeedY = null;
    }

    /*------------------------------*\
	|*				Setters			*|
	\*------------------------------*/
    public void setStatic() {
        bodyDef.type = BodyDef.BodyType.StaticBody;
    }

    public void setDynamic() {
        bodyDef.type = BodyDef.BodyType.DynamicBody;
    }

    public void setFixedRotation() {
        bodyDef.fixedRotation = true;
    }

    public void setShapeRectangle() {
        bodyDef.position.set((getX() + getOriginX()) / 100, (getY() + getOriginY()) / 100);
        PolygonShape rect = new PolygonShape();
        rect.setAsBox(getWidth() / 200, getHeight() / 200);
        fixtureDef.shape = rect;
    }

    public void setPyhsicsProperties(float density, float friction, float restitution) {
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
    }

    public void setMaxSpeed(Float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMaxSpeedX(Float maxSpeedX) {
        this.maxSpeedX = maxSpeedX;
    }

    public void setMaxSpeedY(Float maxSpeedY) {
        this.maxSpeedY = maxSpeedY;
    }

    public void initializePhysics(World w) {
        body = w.createBody(bodyDef);
        Fixture f = body.createFixture(fixtureDef);
        f.setUserData("main");
        body.setUserData(this);
    }

    /*------------------------------*\
	|*				Getters			*|
	\*------------------------------*/

    public Body getBody() {
        return body;
    }


    /*------------------------------------------------------------------*\
	|*							Public Methods 							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Private Methods 						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Private Attributs 						*|
	\*------------------------------------------------------------------*/
}
