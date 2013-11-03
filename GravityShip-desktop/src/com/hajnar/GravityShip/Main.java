package com.hajnar.GravityShip;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
 	    Graphics.DisplayMode displayMode = LwjglApplicationConfiguration.getDesktopDisplayMode();
 	    System.out.println("render resolution : " + displayMode.width + "x" + displayMode.height);
		cfg.title = "GravityShip";
		cfg.stencil = 0;
		cfg.useGL20 = true;
		cfg.useCPUSynch = false;
		cfg.vSyncEnabled = true;
		
		if (args.length >= 2)
		{
			cfg.width = Integer.parseInt(args[0]);
			cfg.height = Integer.parseInt(args[1]);
			if (args.length >= 3)
				cfg.fullscreen = Boolean.parseBoolean(args[2]);
		}
		else
		{
			cfg.width = displayMode.width;
			cfg.height = displayMode.height; 
			cfg.fullscreen = true;		
		}
		
		new LwjglApplication(new GravityShip(), cfg);
	}
}
