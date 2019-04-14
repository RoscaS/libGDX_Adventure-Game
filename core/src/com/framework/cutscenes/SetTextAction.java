package com.framework.cutscenes;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.framework.DialogBox;

public class SetTextAction extends Action {

    protected String textToDisplay;

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public SetTextAction(String text) {
        this.textToDisplay = text;
    }

	/*------------------------------------------------------------------*\
	|*							Public Methods 							*|
	\*------------------------------------------------------------------*/

	@Override
	public boolean act(float dt) {
        DialogBox db = (DialogBox) target;
        db.setText(textToDisplay);
        return true;
    }
}
