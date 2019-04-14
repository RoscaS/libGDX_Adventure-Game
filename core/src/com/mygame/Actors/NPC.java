package com.mygame.Actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class NPC extends BaseActor {

    private String text; // text to be displayed
    private boolean viewing; // determine if dialog box text is currently being displayed
    private String id; // used for identifying NPCs with dynamic messages

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public NPC(float x, float y, Stage s) {
        super(x, y, s);
        text = "";
        viewing = false;
    }

    /*------------------------------------------------------------------*\
   	|*							Public Methods 							*|
   	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Getters			*|
	\*------------------------------*/

    public String getText() {
        return text;
    }

    public boolean isViewing() {
        return viewing;
    }

    public String getId() {
        return id;
    }

    /*------------------------------*\
	|*				Setters			*|
	\*------------------------------*/

    public void setText(String text) {
        this.text = text;
    }

    public void setViewing(boolean viewing) {
        this.viewing = viewing;
    }

    public void setId(String id) {
        this.id = id;
        if (id.equals("Gatekeeper")) loadTexture("npc-1.png");
        else if (id.equals("Shopkeeper")) loadTexture("npc-2.png");
        else loadTexture("npc-3.png"); // default image
    }
}
