package com.hajnar.gravityship;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.hajnar.gravityship.GravityShip;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.stencil = 0;
		config.useAccelerometer = false;
		config.useCompass = false;
		initialize(new GravityShip(), config);
	}
}
