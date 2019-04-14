package com.framework.draganddrop;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.framework.BaseActor;

public class DropTargetActor extends BaseActor {

    private boolean targetable;

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public DropTargetActor(float x, float y, Stage s) {
        super(x, y, s);
        targetable = true;
    }

    /*------------------------------------------------------------------*\
   	|*							Public Methods 							*|
   	\*------------------------------------------------------------------*/

    /*------------------------------*\
   	|*				Setters			*|
   	\*------------------------------*/

    /**
     * Set whether this actor can be targeted by a DragAndDrop actor.
     *
     * @param t can this actor be targeted
     */
    public void setTargetable(boolean t) {
        targetable = t;
    }

	/*------------------------------*\
	|*				Getters			*|
	\*------------------------------*/

    /**
     * Check if this actor can be targeted by a DragAndDrop actor.
     *
     * @return can this actor be targeted
     */
    public boolean isTargetable() {
        return targetable;
    }
}
