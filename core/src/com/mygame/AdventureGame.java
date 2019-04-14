package com.mygame;

import com.framework.BaseGame;
import com.mygame.screens.LevelScreen;

public class AdventureGame extends BaseGame {

	public void create() {
		super.create();
	    setActiveScreen(new LevelScreen());
    }
}
