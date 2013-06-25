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
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.hajnar.GravityShip.Assets;
import com.hajnar.GravityShip.GameWorld;
import com.hajnar.GravityShip.GameWorldRender;
import com.hajnar.GravityShip.GameWorldRenderGL2;
import com.hajnar.GravityShip.GravityShip;
import com.hajnar.GravityShip.Helper;

public class GameScreen implements Screen, InputProcessor{
		
	public static final boolean DEBUG_RENDER_ENABLED = false;
	
	private Game game;
	private GameWorld gameWorld;
	private GameWorldRender worldRender;
	private SpriteBatch batch;
	private OrthographicCamera staticCamera;
	
	private float levelDuration;
	private float gameoverDuration;
	
	private Sprite restartSprite;
	private Sprite exitSprite;
	private Sprite resumeSprite;
	private Sprite nextLevelSprite;
	
	StringBuilder stringBuffer;
	
	Controller connectedController;
	
	public GameScreen(Game game) 
	{
		Gdx.input.setInputProcessor(this);
//		connectedController = Controllers.getControllers().first();
//		if (connectedController != null)
//		{
//			Gdx.app.log("Connected controller: ", connectedController + "");
//			if (connectedController.getName().toLowerCase().contains("x-box") && connectedController.getName().toLowerCase().contains("360"))
//				connectedController.addListener(this);
//		}
		
		this.game = game;
		gameWorld = new GameWorld();
		gameWorld.loadWorld(GravityShip.prefs.getRecentWorld());
		this.batch = new SpriteBatch(300);
		this.staticCamera = new OrthographicCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
		this.staticCamera.position.set(Helper.FRUSTUM_WIDTH / 2, Helper.FRUSTUM_HEIGHT / 2, 0);
		this.staticCamera.update();
		if (Gdx.graphics.isGL20Available())
			if (DEBUG_RENDER_ENABLED)
				this.worldRender = new GameWorldRenderGL2(gameWorld, batch, true);
			else
				this.worldRender = new GameWorldRenderGL2(gameWorld, batch, false);
		else
			if (DEBUG_RENDER_ENABLED)
				this.worldRender = new GameWorldRender(gameWorld, batch, true);
			else
				this.worldRender = new GameWorldRender(gameWorld, batch, false);
		
		restartSprite = new Sprite(Assets.restartRegion);
		restartSprite.setPosition(Helper.FRUSTUM_WIDTH/2 - restartSprite.getWidth()/2, Helper.FRUSTUM_HEIGHT/2 - restartSprite.getHeight()/2);
		exitSprite = new Sprite(Assets.exitRegion);
		exitSprite.setPosition(Helper.FRUSTUM_WIDTH/2 - exitSprite.getWidth()/2, Helper.FRUSTUM_HEIGHT/2 - exitSprite.getHeight()/2 - 150);
		nextLevelSprite = new Sprite(Assets.nextLevelRegion);
		nextLevelSprite.setPosition(Helper.FRUSTUM_WIDTH/2 - nextLevelSprite.getWidth()/2, Helper.FRUSTUM_HEIGHT/2 - nextLevelSprite.getHeight()/2 + 150);
		resumeSprite = new Sprite(Assets.resumeRegion);
		resumeSprite.setPosition(Helper.FRUSTUM_WIDTH/2 - resumeSprite.getWidth()/2, Helper.FRUSTUM_HEIGHT/2 - resumeSprite.getHeight()/2 + 150);
		
		Assets.leftSprite.setPosition(Helper.FRUSTUM_WIDTH - 333,20);
		Assets.rightSprite.setPosition(Helper.FRUSTUM_WIDTH - 190,20);
		Assets.thrustSprite.setPosition(60,20);
		
		Gdx.input.setCursorCatched(true);
		
		stringBuffer = new StringBuilder();
		
		levelDuration = 0;
		gameoverDuration = 0;
	}

	@Override
	public void render(float delta) {
		
		if (gameWorld.getState() != GameWorld.WORLD_PAUSED)
		{
			gameWorld.update(delta);
		}
		
		if (gameWorld.getState()  == GameWorld.WORLD_RUNNING)
			levelDuration += delta;
		else if(gameWorld.getState()  == GameWorld.WORLD_GAME_OVER)
			gameoverDuration += delta;
		
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);	
		
		worldRender.render(delta);
			
		switch(gameWorld.getState())
		{
		case GameWorld.WORLD_RUNNING:
				renderOnScreenControlsAndInfo();		
			break;
		case GameWorld.WORLD_GAME_OVER:
			if (gameoverDuration > 1.5f)
				renderGameOverMenu();		
			break;
		case GameWorld.WORLD_NEXT_LEVEL:
			renderNextLevelMenu();
			break;
		case GameWorld.WORLD_PAUSED:
			renderPauseMenu();
			break;
		}
	}

	@Override
	public void resize(int width, int height) {
		Helper.WINDOW_WIDTH = width;		
		Helper.WINDOW_HEIGHT = height;		
	}

	@Override
	public void show() {
		Gdx.input.setCursorCatched(true);
		Gdx.input.setInputProcessor(this);
		gameWorld.reset();
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		gameWorld.pause();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		worldRender.dispose();
	}
	
	public void renderOnScreenControlsAndInfo()
	{
		batch.begin();
		batch.setProjectionMatrix(staticCamera.combined);
		batch.draw(Assets.starHUDRegion, Helper.FRUSTUM_WIDTH/2 - 50, Helper.FRUSTUM_HEIGHT - 33);
		stringBuffer.delete(0, stringBuffer.length());
		stringBuffer.append(gameWorld.getNumOfCollectedStars()).append("/").append(gameWorld.getNumOfStars());
		Assets.gameFont.draw(batch, stringBuffer , Helper.FRUSTUM_WIDTH/2 - 10, Helper.FRUSTUM_HEIGHT - 5);
		stringBuffer.delete(0, stringBuffer.length());
		stringBuffer.append("Life: ").append(gameWorld.getPlayer().getLifeAmmount());
		Assets.gameFont.draw(batch, stringBuffer , 5, Helper.FRUSTUM_HEIGHT - 5);
		stringBuffer.delete(0, stringBuffer.length());
		stringBuffer.append("Fuel: ").append(gameWorld.getPlayer().getFuelAmmount());
		Assets.gameFont.draw(batch, stringBuffer, 5, Helper.FRUSTUM_HEIGHT - 40);
		stringBuffer.delete(0, stringBuffer.length());
		stringBuffer.append("Level ").append(GravityShip.prefs.getRecentWorld());
		Assets.gameFont.draw(batch, stringBuffer, Helper.FRUSTUM_WIDTH - 160, Helper.FRUSTUM_HEIGHT - 5);
		stringBuffer.delete(0, stringBuffer.length());
		stringBuffer.append("Time: ").append(Math.round(levelDuration));
		Assets.gameFont.draw(batch, stringBuffer, Helper.FRUSTUM_WIDTH - 160, Helper.FRUSTUM_HEIGHT - 40);
//		Assets.gameFont.draw(batch, "Loc: " + Math.round(gameWorld.getPlayer().getBody().getPosition().x) + ", " + Math.round(gameWorld.getPlayer().getBody().getPosition().y), Helper.FRUSTUM_WIDTH/2 - 180, 30);
		if (GravityShip.deviceType == GravityShip.DEV_TYPE_ANDROID)
		{
			Assets.leftSprite.draw(batch);
			Assets.rightSprite.draw(batch);
			Assets.thrustSprite.draw(batch);
		}
		batch.end();
	}
	
	public void renderGameOverMenu()
	{
		batch.begin();	
		batch.setProjectionMatrix(staticCamera.combined);
		batch.draw(Assets.gameoverBackground, 0, 0, Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
		Assets.gameFont.draw(batch, "Game Over" , Helper.FRUSTUM_WIDTH/2 - 110, Helper.FRUSTUM_HEIGHT - 5);
		restartSprite.draw(batch);
		exitSprite.draw(batch);
		batch.end();
	}
	
	public void renderNextLevelMenu()
	{
		batch.begin();	
		batch.setProjectionMatrix(staticCamera.combined);
		batch.draw(Assets.succesBackground, 0, 0, Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
		Assets.gameFont.draw(batch, "Level Passed" , Helper.FRUSTUM_WIDTH/2 - 120, Helper.FRUSTUM_HEIGHT - 5);
		stringBuffer.delete(0, stringBuffer.length());
		stringBuffer.append("Time: ").append(Math.round(levelDuration));
		Assets.gameFont.draw(batch, stringBuffer, Helper.FRUSTUM_WIDTH/2 - 73, Helper.FRUSTUM_HEIGHT - 85);
		if (GravityShip.prefs.getRecentWorld() < GravityShip.prefs.getNumOfWorlds())
			nextLevelSprite.draw(batch);
		restartSprite.draw(batch);
		exitSprite.draw(batch);
		batch.end();
	}
	
	public void renderPauseMenu()
	{
		batch.begin();	
		batch.setProjectionMatrix(staticCamera.combined);
		batch.draw(Assets.pauseBackground, 0, 0, Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
		Assets.gameFont.draw(batch, "Pause menu" , Helper.FRUSTUM_WIDTH/2 - 110, Helper.FRUSTUM_HEIGHT - 5);
		resumeSprite.draw(batch);
		restartSprite.draw(batch);
		exitSprite.draw(batch);
		batch.end();
	}
	
	public GameWorld getGameWorld()
	{
		return gameWorld;
	}
	
	public float getGameOverDuration()
	{
		return gameoverDuration;
	}
	
//////////////// keyboard input ////////////////////
	
	@Override
	public boolean keyDown(int keycode) {
		switch(gameWorld.getState())
		{
		case GameWorld.WORLD_RUNNING:
			if(keycode ==  Keys.UP)
			{
				gameWorld.getPlayer().thrustOn();
			}
				
			if(keycode == Keys.LEFT)
				gameWorld.getPlayer().setRotationRatio(-1);
			
			if(keycode == Keys.RIGHT)
				gameWorld.getPlayer().setRotationRatio(1);
			
			if(keycode == Keys.ESCAPE)
			{
				gameWorld.pause();
				Gdx.input.setCursorCatched(false);
			}
			
//			if(keycode == Keys.Q)
//			{
//				gameWorld.getPlayer().getBody().setTransform(98,10, 0);
//			}
			
			if(keycode == Keys.BACK)
			{
				gameWorld.pause();
				Gdx.input.setCursorCatched(false);
			}
			break;
		case GameWorld.WORLD_PAUSED:
			if(keycode == Keys.ENTER)
			{
				gameWorld.resume();
				Gdx.input.setCursorCatched(true);
			}
			if(keycode == Keys.ESCAPE)
			{
				game.setScreen(GravityShip.menuScreen);
			}
			break;
		case GameWorld.WORLD_GAME_OVER:
			if (keycode == Keys.ENTER)
			{
				gameWorld.reset();
				levelDuration = 0;
				gameoverDuration = 0;
				Gdx.input.setCursorCatched(true);
			}
			if(keycode == Keys.ESCAPE)
				game.setScreen(GravityShip.menuScreen);
			break;
		case GameWorld.WORLD_NEXT_LEVEL:
			if (keycode == Keys.ENTER && GravityShip.prefs.getRecentWorld() < GravityShip.prefs.getNumOfWorlds())
			{
				GravityShip.prefs.setRecentWorld(GravityShip.prefs.getRecentWorld()+1);
				gameWorld.loadWorld(GravityShip.prefs.getRecentWorld());
				gameWorld.reset();
				levelDuration = 0;
				gameoverDuration = 0;
				Gdx.input.setCursorCatched(true);
			}
			if(keycode == Keys.ESCAPE)
				game.setScreen(GravityShip.menuScreen);
			break;
		}
		return true;			
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode ==  Keys.UP)
		{
			gameWorld.getPlayer().thrustOff();
		}
		
		if(keycode == Keys.LEFT || keycode == Keys.RIGHT)
			gameWorld.getPlayer().setRotationRatio(0);
		
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

//////////////// touch/mouse input ////////////////////
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float x = screenX*(Helper.FRUSTUM_WIDTH/Helper.WINDOW_WIDTH);
		float y = Helper.FRUSTUM_HEIGHT - screenY*(Helper.FRUSTUM_HEIGHT/Helper.WINDOW_HEIGHT);
		switch(gameWorld.getState())
		{
		case GameWorld.WORLD_RUNNING:
			if (GravityShip.deviceType == GravityShip.DEV_TYPE_ANDROID)
			{
				if (Assets.thrustSprite.getBoundingRectangle().contains(x, y))
				{
					gameWorld.getPlayer().thrustOn();
				}
				if (Assets.leftSprite.getBoundingRectangle().contains(x, y))
					gameWorld.getPlayer().setRotationRatio(-1);
				if (Assets.rightSprite.getBoundingRectangle().contains(x, y))
					gameWorld.getPlayer().setRotationRatio(1);
			}
			break;
		case GameWorld.WORLD_PAUSED:
			if (resumeSprite.getBoundingRectangle().contains(x, y))
			{
				Assets.playSound(Assets.buttonClickSound);
				Assets.vibrate(40);
				gameWorld.resume();
				Gdx.input.setCursorCatched(true);
			}
			if (restartSprite.getBoundingRectangle().contains(x, y))
			{
				Assets.playSound(Assets.buttonClickSound);
				Assets.vibrate(40);
				gameWorld.reset();
				levelDuration = 0;
				gameoverDuration = 0;
				Gdx.input.setCursorCatched(true);
			}
			if (exitSprite.getBoundingRectangle().contains(x, y))
			{
				Assets.playSound(Assets.buttonClickSound);
				Assets.vibrate(40);
				game.setScreen(GravityShip.menuScreen);
			}
			break;
		case GameWorld.WORLD_GAME_OVER:
			if (restartSprite.getBoundingRectangle().contains(x, y))
			{
				Assets.playSound(Assets.buttonClickSound);
				Assets.vibrate(40);
				gameWorld.reset();
				levelDuration = 0;
				gameoverDuration = 0;
				Gdx.input.setCursorCatched(true);
			}
			if (exitSprite.getBoundingRectangle().contains(x, y))
			{
				Assets.playSound(Assets.buttonClickSound);
				Assets.vibrate(40);
				game.setScreen(GravityShip.menuScreen);
			}
			break;
		case GameWorld.WORLD_NEXT_LEVEL:
			if (nextLevelSprite.getBoundingRectangle().contains(x, y) && GravityShip.prefs.getRecentWorld() < GravityShip.prefs.getNumOfWorlds())
			{
				Assets.playSound(Assets.buttonClickSound);
				Assets.vibrate(40);
				GravityShip.prefs.setRecentWorld(GravityShip.prefs.getRecentWorld()+1);
				System.out.println(GravityShip.prefs.getRecentWorld());
				GravityShip.savePreferences();
				gameWorld.loadWorld(GravityShip.prefs.getRecentWorld());
				gameWorld.reset();
				levelDuration = 0;
				gameoverDuration = 0;
				Gdx.input.setCursorCatched(true);
			}
			if (restartSprite.getBoundingRectangle().contains(x, y))
			{
				Assets.playSound(Assets.buttonClickSound);
				Assets.vibrate(40);
				gameWorld.reset();
				levelDuration = 0;
				gameoverDuration = 0;
				Gdx.input.setCursorCatched(true);
			}
			if (exitSprite.getBoundingRectangle().contains(x, y))
			{
				Assets.playSound(Assets.buttonClickSound);
				Assets.vibrate(40);
				game.setScreen(GravityShip.menuScreen);
			}
			break;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (GravityShip.deviceType == GravityShip.DEV_TYPE_ANDROID)
		{
			if (gameWorld.getState() != GameWorld.WORLD_GAME_OVER)
			{
				if(screenX < Helper.WINDOW_WIDTH/2)
				{
					gameWorld.getPlayer().thrustOff();
				}
				else
					gameWorld.getPlayer().setRotationRatio(0);
			}
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (GravityShip.deviceType == GravityShip.DEV_TYPE_ANDROID)
		{
			float x = screenX*(Helper.FRUSTUM_WIDTH/Helper.WINDOW_WIDTH);
			float y = Helper.FRUSTUM_HEIGHT - screenY*(Helper.FRUSTUM_HEIGHT/Helper.WINDOW_HEIGHT);
			if (gameWorld.getState() == GameWorld.WORLD_RUNNING)
			{
				if (Assets.thrustSprite.getBoundingRectangle().contains(x, y))
				{
					gameWorld.getPlayer().thrustOn();
				}
				else if (Assets.leftSprite.getBoundingRectangle().contains(x, y))
					gameWorld.getPlayer().setRotationRatio(-1);
				else if (Assets.rightSprite.getBoundingRectangle().contains(x, y))
					gameWorld.getPlayer().setRotationRatio(1);
				else
				{
					gameWorld.getPlayer().thrustOff();
					gameWorld.getPlayer().setRotationRatio(0);
				}
			}
		}
		return true;
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
	
//////////////// joystick input ////////////////////
//	@Override
//	public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean axisMoved(Controller arg0, int arg1, float arg2) {
//		if (arg1 == 0)
//		{
//			if (arg2 < -0.2)
//				gameWorld.getPlayer().setRotationRatio(arg2);
//			else if (arg2 > 0.2)
//				gameWorld.getPlayer().setRotationRatio(arg2);
//			else 
//				gameWorld.getPlayer().setRotationRatio(0);
//		}
//		if (arg1 == 5)
//		{
//			if (arg2 > -0.6)
//				gameWorld.getPlayer().thrustOn();
//			else
//				gameWorld.getPlayer().thrustOff();
//		}
//		return true;
//	}
//
//	@Override
//	public boolean buttonDown(Controller arg0, int arg1) {
//		switch(gameWorld.getState())
//		{
//		case GameWorld.WORLD_RUNNING:
//			if (arg1 == 0)
//				gameWorld.getPlayer().thrustOn();
//			if (arg1 == 7)
//			{
//				gameWorld.pause();
//				Gdx.input.setCursorCatched(false);
//			}
//			break;
//		case GameWorld.WORLD_PAUSED:
//			if (arg1 == 7)
//			{
//				gameWorld.resume();
//				Gdx.input.setCursorCatched(true);
//			}
//			if (arg1 == 0)
//			{
//				gameWorld.reset();
//				levelDuration = 0;
//				gameoverDuration = 0;
//				Gdx.input.setCursorCatched(true);
//			}
//			if (arg1 == 1)
//			{
//				game.setScreen(GravityShip.menuScreen);
//				Gdx.input.setCursorCatched(false);
//			}
//			break;
//		case GameWorld.WORLD_GAME_OVER:
//			if (arg1 == 0)
//			{
//				gameWorld.reset();
//				levelDuration = 0;
//				gameoverDuration = 0;
//				Gdx.input.setCursorCatched(true);
//			}
//			if (arg1 == 1)
//				game.setScreen(GravityShip.menuScreen);
//			break;
//		case GameWorld.WORLD_NEXT_LEVEL:
//			if (arg1 == 0 && GravityShip.prefs.getRecentWorld() < GravityShip.prefs.getNumOfWorlds())
//			{
//				GravityShip.prefs.setRecentWorld(GravityShip.prefs.getRecentWorld()+1);
//				gameWorld.loadWorld(GravityShip.prefs.getRecentWorld());
//				gameWorld.reset();
//				levelDuration = 0;
//				gameoverDuration = 0;
//				Gdx.input.setCursorCatched(true);
//			}
//			if(arg1 == 1)
//				game.setScreen(GravityShip.menuScreen);
//			break;
//		}		
//		return true;
//	}
//
//	@Override
//	public boolean buttonUp(Controller arg0, int arg1) {
//		if (arg1 == 0)
//			gameWorld.getPlayer().thrustOff();
//		return false;
//	}
//
//	@Override
//	public void connected(Controller arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void disconnected(Controller arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean povMoved(Controller arg0, int arg1, PovDirection arg2) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean xSliderMoved(Controller arg0, int arg1, boolean arg2) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean ySliderMoved(Controller arg0, int arg1, boolean arg2) {
//		// TODO Auto-generated method stub
//		return false;
//	}

}
