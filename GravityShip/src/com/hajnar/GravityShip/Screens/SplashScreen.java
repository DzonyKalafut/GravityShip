package com.hajnar.GravityShip.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hajnar.GravityShip.Assets;
import com.hajnar.GravityShip.GravityShip;

public class SplashScreen  implements Screen, InputProcessor
{
  private Game game;
  private SpriteBatch batch;
  private OrthographicCamera camera;
  private Sprite splashSprite;

  public SplashScreen(Game game)
  {
    Gdx.input.setInputProcessor(this);
    splashSprite = new Sprite(Assets.splashScreenTexture);
    splashSprite.setSize(1, splashSprite.getHeight()/splashSprite.getWidth());
    splashSprite.setOrigin(splashSprite.getWidth()/2, splashSprite.getHeight()/2);
    splashSprite.setPosition(-0.5f, -0.5f);

    batch = new SpriteBatch();
    this.game = game;
    camera = new OrthographicCamera(1, Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
    camera.update();
	batch.setProjectionMatrix(camera.combined);
  }

  public void render(float delta)
  {
    Gdx.gl.glClearColor(1f, 0.0f, 0.0f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
    batch.begin();
    splashSprite.draw(batch);
    batch.end();
  }

  public void resize(int width, int height)
  {
  }

  public void show()
  {
    Gdx.input.setCursorCatched(false);
    Gdx.input.setInputProcessor(this);
  }

  public void hide()
  {
    Gdx.input.setInputProcessor(null);
  }

  public void pause()
  {
  }

  public void resume()
  {
  }

  public void dispose()
  {
  }

  public boolean keyDown(int keycode)
  {
    Assets.playSound(Assets.buttonClickSound);
    Assets.vibrate(40);
    game.setScreen(GravityShip.menuScreen);
    return true;
  }

  public boolean keyUp(int keycode)
  {
    return false;
  }

  public boolean keyTyped(char character)
  {
    return false;
  }

  public boolean touchDown(int screenX, int screenY, int pointer, int button)
  {
    Assets.playSound(Assets.buttonClickSound);
    Assets.vibrate(40);
    game.setScreen(GravityShip.menuScreen);
    return true;
  }

  public boolean touchUp(int screenX, int screenY, int pointer, int button)
  {
    return false;
  }

  public boolean touchDragged(int screenX, int screenY, int pointer)
  {
    return false;
  }

  public boolean mouseMoved(int screenX, int screenY)
  {
    return false;
  }

  public boolean scrolled(int amount)
  {
    return false;
  }
}