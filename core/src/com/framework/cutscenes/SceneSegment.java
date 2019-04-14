package com.framework.cutscenes;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SceneSegment {

    private Actor actor;
    private Action action;

    /*------------------------------------------------------------------*\
   	|*							Public Methods 							*|
   	\*------------------------------------------------------------------*/

    public void start() {
        actor.clearActions();
        actor.addAction(action);
    }

    public void finish() {
        // simulate 100000 seconds elapsed time to complete in-progress action
        if (actor.hasActions()) actor.getActions().first().act(100000);
        // remove any remaining actions
        actor.clearActions();
    }

    public boolean isFinished() {
        return (actor.getActions().size == 0);
    }

    /*------------------------------*\
   	|*				Setters			*|
   	\*------------------------------*/

    public SceneSegment(Actor actor, Action action) {
        this.actor = actor;
        this.action = action;
    }
}
