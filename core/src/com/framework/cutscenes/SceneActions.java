package com.framework.cutscenes;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.framework.BaseActor;

/**
 * Convenience class for creating custom Action objects
 * for use with SceneSegment and Scene classes.
 */
public class SceneActions extends Actions {

    /*------------------------------------------------------------------*\
   	|*							Public Methods 							*|
   	\*------------------------------------------------------------------*/

    public static Action pause() {
        return Actions.forever(Actions.delay(1));
    }

    public static Action moveToScreenLeft(float duration) {
        return Actions.moveToAligned(0, 0, Align.bottomLeft, duration);
    }

    public static Action moveToScreenRight(float duration) {
        return Actions.moveToAligned(BaseActor.getWorldBounds().width, 0, Align.bottomRight, duration);
    }

    public static Action moveToScreenCenter(float duration) {
        return Actions.moveToAligned(BaseActor.getWorldBounds().width / 2, 0, Align.bottom, duration);
    }

    public static Action moveToOutsideLeft(float duration) {
        return Actions.moveToAligned(0, 0, Align.bottomRight, duration);
    }

    public static Action moveToOutsideRight(float duration) {
        return Actions.moveToAligned(BaseActor.getWorldBounds().width, 0, Align.bottomLeft, duration);
    }

	/*------------------------------*\
	|*				Setters			*|
	\*------------------------------*/

    public static Action setText(String text) {
        return new SetTextAction(text);
    }
}
