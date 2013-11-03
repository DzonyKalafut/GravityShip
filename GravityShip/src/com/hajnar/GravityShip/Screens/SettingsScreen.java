/*******************************************************************************
 * Copyright 2013 Ján Hajnár.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.hajnar.GravityShip.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.hajnar.GravityShip.Assets;
import com.hajnar.GravityShip.GravityShip;
import com.hajnar.GravityShip.Helper;

public class SettingsScreen implements Screen, InputProcessor {

    private Game game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float colorValue;
    private boolean colorDirection;

    private Sprite backSprite;
    private Sprite musicOnSprite;
    private Sprite musicOffSprite;
    private Sprite effectsOnSprite;
    private Sprite effectsOffSprite;
    private Sprite vibrationOnSprite;
    private Sprite vibrationOffSprite;

    public SettingsScreen(Game game) {
        Gdx.input.setInputProcessor(this);
        batch = new SpriteBatch();
        this.game = game;
        camera = new OrthographicCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
        camera.translate(new Vector3(Helper.FRUSTUM_WIDTH / 2, Helper.FRUSTUM_HEIGHT / 2, 0));
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        effectsOnSprite = new Sprite(Assets.onRegion);
        effectsOnSprite.setPosition(650, Helper.FRUSTUM_HEIGHT / 2 - effectsOnSprite.getWidth() / 2 + 210);
        effectsOffSprite = new Sprite(Assets.offRegion);
        effectsOffSprite.setPosition(770, Helper.FRUSTUM_HEIGHT / 2 - effectsOnSprite.getWidth() / 2 + 210);
        musicOnSprite = new Sprite(Assets.onRegion);
        musicOnSprite.setPosition(650, Helper.FRUSTUM_HEIGHT / 2 - effectsOnSprite.getWidth() / 2 + 110);
        musicOffSprite = new Sprite(Assets.offRegion);
        musicOffSprite.setPosition(770, Helper.FRUSTUM_HEIGHT / 2 - effectsOnSprite.getWidth() / 2 + 110);
        vibrationOnSprite = new Sprite(Assets.onRegion);
        vibrationOnSprite.setPosition(650, Helper.FRUSTUM_HEIGHT / 2 - effectsOnSprite.getWidth() / 2 + 10);
        vibrationOffSprite = new Sprite(Assets.offRegion);
        vibrationOffSprite.setPosition(770, Helper.FRUSTUM_HEIGHT / 2 - effectsOnSprite.getWidth() / 2 + 10);
        backSprite = new Sprite(Assets.backRegion);
        backSprite.setPosition(Helper.FRUSTUM_WIDTH / 2 - backSprite.getWidth() / 2, 50);
    }

    @Override
    public void render(float delta) {
        colorModify();

        Gdx.gl.glClearColor(colorValue + 0.2f, colorValue, colorValue, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        batch.begin();
        Assets.gameFont.draw(batch, "Settings Menu", Helper.FRUSTUM_WIDTH / 2 - 140, Helper.FRUSTUM_HEIGHT - 5);
        batch.draw(Assets.effectsRegion, 150, Helper.FRUSTUM_HEIGHT / 2 - Assets.effectsRegion.getRegionHeight() / 2 + 200);
        batch.draw(Assets.musicRegion, 120, Helper.FRUSTUM_HEIGHT / 2 - Assets.effectsRegion.getRegionHeight() / 2 + 100);
        if (GravityShip.prefs.isSoundOn()) {
            effectsOnSprite.draw(batch);
            effectsOffSprite.draw(batch, 0.4f);
        } else {
            effectsOnSprite.draw(batch, 0.4f);
            effectsOffSprite.draw(batch);
        }
        if (GravityShip.prefs.isMusicOn()) {
            musicOnSprite.draw(batch);
            musicOffSprite.draw(batch, 0.4f);
        } else {
            musicOnSprite.draw(batch, 0.4f);
            musicOffSprite.draw(batch);
        }
        if (GravityShip.deviceType == GravityShip.DEV_TYPE_ANDROID) {
            batch.draw(Assets.vibrationRegion, 180, Helper.FRUSTUM_HEIGHT / 2 - Assets.effectsRegion.getRegionHeight() / 2);
            if (GravityShip.prefs.isVibrationOn()) {
                vibrationOnSprite.draw(batch);
                vibrationOffSprite.draw(batch, 0.4f);
            } else {
                vibrationOnSprite.draw(batch, 0.4f);
                vibrationOffSprite.draw(batch);
            }
        }
        backSprite.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        Helper.WINDOW_WIDTH = width;
        Helper.WINDOW_HEIGHT = height;
    }

    @Override
    public void show() {
        Gdx.input.setCursorCatched(false);
        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        batch.dispose();
    }

    public void colorModify() {
        if (colorValue >= 0.2f)
            colorDirection = false;
        if (colorValue <= 0.0f)
            colorDirection = true;
        if (colorDirection)
            colorValue += 0.005f;
        else
            colorValue -= 0.005;
    }

//////////////// keyboard input ////////////////////	

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.ENTER || keycode == Keys.BACK) {
            GravityShip.savePreferences();
            game.setScreen(GravityShip.menuScreen);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

//////////////// touch input ////////////////////

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float x = screenX * (Helper.FRUSTUM_WIDTH / Helper.WINDOW_WIDTH);
        float y = Helper.FRUSTUM_HEIGHT - screenY * (Helper.FRUSTUM_HEIGHT / Helper.WINDOW_HEIGHT);
        if (backSprite.getBoundingRectangle().contains(x, y)) {
            Assets.playSound(Assets.buttonClickSound);
            Assets.vibrate(40);
            GravityShip.savePreferences();
            game.setScreen(GravityShip.menuScreen);
        }
        if (musicOnSprite.getBoundingRectangle().contains(x, y)) {
            Assets.playSound(Assets.buttonClickSound);
            Assets.vibrate(40);
            Assets.music.play();
            GravityShip.prefs.setMusicOn(true);
        } else if (musicOffSprite.getBoundingRectangle().contains(x, y)) {
            Assets.playSound(Assets.buttonClickSound);
            Assets.vibrate(40);
            Assets.music.pause();
            GravityShip.prefs.setMusicOn(false);
        } else if (effectsOnSprite.getBoundingRectangle().contains(x, y)) {
            Assets.playSound(Assets.buttonClickSound);
            Assets.vibrate(40);
            GravityShip.prefs.setSoundOn(true);
        } else if (effectsOffSprite.getBoundingRectangle().contains(x, y)) {
            Assets.playSound(Assets.buttonClickSound);
            Assets.vibrate(40);
            GravityShip.prefs.setSoundOn(false);
        }
        if (GravityShip.deviceType == GravityShip.DEV_TYPE_ANDROID) {
            if (vibrationOnSprite.getBoundingRectangle().contains(x, y)) {
                Assets.playSound(Assets.buttonClickSound);
                Assets.vibrate(40);
                GravityShip.prefs.setVibrationOn(true);
            } else if (vibrationOffSprite.getBoundingRectangle().contains(x, y)) {
                Assets.playSound(Assets.buttonClickSound);
                Assets.vibrate(40);
                GravityShip.prefs.setVibrationOn(false);
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

}

