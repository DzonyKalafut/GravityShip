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

package com.hajnar.gravityship.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.hajnar.gravityship.Assets;
import com.hajnar.gravityship.GravityShip;
import com.hajnar.gravityship.Helper;

public class MenuScreen implements Screen, InputProcessor {

    private Game game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float colorValue;
    private boolean colorDirection;
    private Sprite startSprite;
    private Sprite settingsSprite;
    private Sprite resumeSprite;

    public MenuScreen(Game game) {
        Gdx.input.setInputProcessor(this);
        resumeSprite = new Sprite(Assets.resumeRegion);
        resumeSprite.setPosition(Helper.FRUSTUM_WIDTH / 2 - resumeSprite.getWidth() / 2, Helper.FRUSTUM_HEIGHT / 2 - resumeSprite.getHeight() / 2 + 200);
        startSprite = new Sprite(Assets.startRegion);
        startSprite.setPosition(Helper.FRUSTUM_WIDTH / 2 - startSprite.getWidth() / 2, Helper.FRUSTUM_HEIGHT / 2 - startSprite.getHeight() / 2);
        settingsSprite = new Sprite(Assets.settingsRegion);
        settingsSprite.setPosition(Helper.FRUSTUM_WIDTH / 2 - settingsSprite.getWidth() / 2, Helper.FRUSTUM_HEIGHT / 2 - settingsSprite.getHeight() / 2 - 200);
        batch = new SpriteBatch();
        this.game = game;
        camera = new OrthographicCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
        camera.translate(new Vector3(Helper.FRUSTUM_WIDTH / 2, Helper.FRUSTUM_HEIGHT / 2, 0));
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        colorModify();

        Gdx.gl.glClearColor(colorValue, colorValue, colorValue + 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        Assets.gameFont.draw(batch, "Main Menu", Helper.FRUSTUM_WIDTH / 2 - 105, Helper.FRUSTUM_HEIGHT - 5);
        startSprite.draw(batch);
        settingsSprite.draw(batch);
        resumeSprite.draw(batch);
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
        Assets.buttonClickSound.dispose();
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
        if (keycode == Keys.ESCAPE || keycode == Keys.BACK)
            Gdx.app.exit();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

//////////////// touch input ////////////////////

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float x = screenX * (Helper.FRUSTUM_WIDTH / Helper.WINDOW_WIDTH);
        float y = Helper.FRUSTUM_HEIGHT - screenY * (Helper.FRUSTUM_HEIGHT / Helper.WINDOW_HEIGHT);
        if (resumeSprite.getBoundingRectangle().contains(x, y)) {
            Assets.playSound(Assets.buttonClickSound);
            Assets.vibrate(40);
            GravityShip.gameScreen.getGameWorld().loadWorld(GravityShip.prefs.getRecentWorld());
            GravityShip.gameScreen.getGameWorld().reset();
            game.setScreen(GravityShip.gameScreen);
        }
        if (startSprite.getBoundingRectangle().contains(x, y)) {
            Assets.playSound(Assets.buttonClickSound);
            Assets.vibrate(40);
            GravityShip.prefs.setRecentWorld(1);
            GravityShip.savePreferences();
            GravityShip.gameScreen.getGameWorld().loadWorld(GravityShip.prefs.getRecentWorld());
            GravityShip.gameScreen.getGameWorld().reset();
            game.setScreen(GravityShip.gameScreen);
        }
        if (settingsSprite.getBoundingRectangle().contains(x, y)) {
            Assets.playSound(Assets.buttonClickSound);
            Assets.vibrate(40);
            game.setScreen(GravityShip.settingsScreen);
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
