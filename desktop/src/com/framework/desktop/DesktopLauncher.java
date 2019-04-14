package com.framework.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.mygame.AdventureGame;

public class DesktopLauncher {
	public static void main (String[] arg) {

		Game myGame = new AdventureGame();
		LwjglApplication launcher = new LwjglApplication(myGame, "Adventure Game", 800, 600);
	}
}
