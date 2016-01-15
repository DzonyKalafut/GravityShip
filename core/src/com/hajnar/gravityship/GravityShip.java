package com.hajnar.gravityship;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.hajnar.gravityship.Screens.GameScreen;
import com.hajnar.gravityship.Screens.MenuScreen;
import com.hajnar.gravityship.Screens.SettingsScreen;
import com.hajnar.gravityship.Screens.SplashScreen;

public class GravityShip extends Game {
    public static MenuScreen menuScreen;
    public static GameScreen gameScreen;
    public static SettingsScreen settingsScreen;
    public static SplashScreen splashScreen;
    public static GameSettings prefs;

    public void create() {
        Assets.loadAssets();

        Gdx.input.setCatchBackKey(true);

        if (Gdx.files.external(".GravityShipSettings.json").exists()) {
            prefs = loadPreferences();
            prefs.setNumOfWorlds(getNumOfWorlds());
        } else {
            prefs = new GameSettings();
            prefs.setRecentWorld(1);
            prefs.setMusicOn(true);
            prefs.setSoundOn(true);
            prefs.setVibrationOn(true);
            prefs.setNumOfWorlds(getNumOfWorlds());
            savePreferences();
        }

        if (prefs.isMusicOn()) {
            Assets.music.play();
        }
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        settingsScreen = new SettingsScreen(this);
        splashScreen = new SplashScreen(this);

        setScreen(splashScreen);
    }

    public void dispose() {
        menuScreen.dispose();
        settingsScreen.dispose();
        gameScreen.dispose();
        Assets.disposeAssets();
    }

    public void render() {
        getScreen().render(Gdx.graphics.getDeltaTime());
//    try {
//		Thread.sleep(20);
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
    }

    public void resize(int width, int height) {
    }

    public void pause() {
    }

    public void resume() {
    }

    public GameSettings loadPreferences() {
        Json json = new Json();
        FileHandle file = Gdx.files.external(".GravityShipSettings.json");
        String text = file.readString();
        GameSettings prefs = (GameSettings) json.fromJson(GameSettings.class, text);
        return prefs;
    }

    public static void savePreferences() {
        Json json = new Json();
        FileHandle file = Gdx.files.external(".GravityShipSettings.json");
        file.writeString(json.prettyPrint(prefs), false);
    }

    public static int getNumOfWorlds() {
        int numOfWorlds = 1;
        while (Gdx.files.internal("data/world" + numOfWorlds + ".xml").exists())
            numOfWorlds++;
        return numOfWorlds - 1;
    }
}