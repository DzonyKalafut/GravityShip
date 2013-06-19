package com.hajnar.GravityShip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.hajnar.GravityShip.GameObjects.GameCamera;
import com.hajnar.GravityShip.GameObjects.Terrain;
import java.io.PrintStream;
import java.util.ArrayList;

public class GameWorldRenderGL2 extends GameWorldRender
{
  public static final int FBO_SIZE = 256;
  private FrameBuffer fbo1;
  private FrameBuffer fbo2;
  private FrameBuffer fbo3;
  private TextureRegion fboRegion;
  private ShaderProgram terrainShader;
  private ShaderProgram hiPassShader;
  private ShaderProgram blurShader;

  public GameWorldRenderGL2(GameWorld world, SpriteBatch batch)
  {
    super(world, batch);

    ShaderProgram.pedantic = false;

    this.fbo1 = new FrameBuffer(Format.RGBA8888, 1200, (int)Helper.WINDOW_HEIGHT, false);
    this.fbo2 = new FrameBuffer(Format.RGBA8888, 256, 256, false);
    this.fbo3 = new FrameBuffer(Format.RGBA8888, 256, 256, false);
    this.fboRegion = new TextureRegion(this.fbo1.getColorBufferTexture());
    this.fboRegion.flip(false, true);

    this.terrainShader = new ShaderProgram(Assets.terrainVertexShader, Assets.terrainFragmentShader);
    this.hiPassShader = new ShaderProgram(Assets.hiPassVertexShader, Assets.hiPassFragmentShader);
    this.blurShader = new ShaderProgram(Assets.hiPassVertexShader, Assets.blurFragmentShader);
    if (this.terrainShader.getLog().length() != 0)
      System.out.println("terrainShader: " + this.terrainShader.getLog());
    if (this.hiPassShader.getLog().length() != 0)
      System.out.println("hiPassShader: " + this.hiPassShader.getLog());
    if (this.blurShader.getLog().length() != 0) {
      System.out.println("blurShader: " + this.terrainShader.getLog());
    }
    this.blurShader.setUniformf("dir", 0.0F, 0.0F);
    this.blurShader.setUniformf("resolution", 256.0F);
    this.blurShader.setUniformf("radius", 1.0F);
  }

  public GameWorldRenderGL2(GameWorld world, SpriteBatch batch, boolean debug)
  {
    super(world, batch, debug);

    ShaderProgram.pedantic = false;

    this.fbo1 = new FrameBuffer(Format.RGBA8888, 1200, (int)Helper.WINDOW_HEIGHT, false);
    this.fbo2 = new FrameBuffer(Format.RGBA8888, 256, 256, false);
    this.fbo3 = new FrameBuffer(Format.RGBA8888, 256, 256, false);
    this.fboRegion = new TextureRegion(this.fbo1.getColorBufferTexture());
    this.fboRegion.flip(false, true);

    this.terrainShader = new ShaderProgram(Assets.terrainVertexShader, Assets.terrainFragmentShader);
    this.hiPassShader = new ShaderProgram(Assets.hiPassVertexShader, Assets.hiPassFragmentShader);
    this.blurShader = new ShaderProgram(Assets.hiPassVertexShader, Assets.blurFragmentShader);
    if (this.terrainShader.getLog().length() != 0)
      System.out.println("terrainShader: " + this.terrainShader.getLog());
    if (this.hiPassShader.getLog().length() != 0)
      System.out.println("hiPassShader: " + this.hiPassShader.getLog());
    if (this.blurShader.getLog().length() != 0) {
      System.out.println("blurShader: " + this.terrainShader.getLog());
    }
    this.blurShader.setUniformf("dir", 0.0F, 0.0F);
    this.blurShader.setUniformf("resolution", 256.0F);
    this.blurShader.setUniformf("radius", 1.0F);
  }

  public void render(float delta)
  {
    if (this.world.getState() == 1)
    {
      Vector2 cameraTranslation;
      if (GravityShip.deviceType == 2)
        cameraTranslation = this.camera.followWithZooming(this.world.getPlayer());
      else
        cameraTranslation = this.camera.follow(this.world.getPlayer());
      this.parallaxCamera.translate(cameraTranslation.mul(0.1F));
      this.parallaxCamera2.translate(cameraTranslation.mul(0.3F));
    }
    this.camera.update();
    this.parallaxCamera.update();
    this.parallaxCamera2.update();
    this.gasTexture.updateBackground();
    this.gasTexture2.updateBackground();

    this.batch.begin();
    this.fbo1.begin();
    Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
    Gdx.gl.glClear(16384);
    this.batch.disableBlending();
    this.batch.setProjectionMatrix(this.staticCamera.combined);
    this.batch.draw(Assets.backgroundTexture, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, 1200.0F);
    this.batch.enableBlending();
    this.batch.setProjectionMatrix(this.parallaxCamera.combined);
    this.gasTexture.render();
    this.batch.setProjectionMatrix(this.parallaxCamera2.combined);
    this.gasTexture2.render();
    this.batch.setProjectionMatrix(this.camera.combined);
    renderBlackHoles();
    renderStars();
    renderParticles(delta);
    renderLandingZones();
    renderPlayer();
    renderBullets();
    this.batch.end();
    renderTerrain();
    this.batch.begin();
    renderCanons();
    this.batch.end();
    this.fbo1.end();

    if ((this.world.getState() == GameWorld.WORLD_RUNNING) || ((this.world.getState() == GameWorld.WORLD_GAME_OVER) && (GravityShip.gameScreen.getGameOverDuration() <= 1.5f)))
    {
      this.fboRegion.setTexture(this.fbo1.getColorBufferTexture());

      this.batch.begin();
      this.fbo2.begin();
      this.batch.setShader(this.hiPassShader);
      this.hiPassShader.setUniformf("brightPassThreshold", 0.47F);
      Gdx.gl.glClear(16384);
      this.batch.setProjectionMatrix(this.staticCamera.combined);
      this.batch.draw(this.fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      this.batch.flush();
      this.fbo2.end();

      this.fboRegion.setTexture(this.fbo2.getColorBufferTexture());

      this.fbo3.begin();
      this.batch.setShader(this.blurShader);
      this.blurShader.setUniformf("dir", 1.0F, 0.0F);
      this.blurShader.setUniformf("resolution", 256.0F);
      this.blurShader.setUniformf("radius", 1.5F);
      Gdx.gl.glClear(16384);
      this.batch.setProjectionMatrix(this.staticCamera.combined);
      this.batch.draw(this.fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      this.batch.flush();
      this.fbo3.end();

      this.batch.setShader(null);
      Gdx.gl.glClear(16384);
      this.batch.setBlendFunction(770, 1);

      this.fboRegion.setTexture(this.fbo1.getColorBufferTexture());
      this.batch.setProjectionMatrix(this.staticCamera.combined);
      this.batch.draw(this.fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      this.batch.setColor(1.0F, 1.0F, 1.0F, 0.7F);
      this.fboRegion.setTexture(this.fbo3.getColorBufferTexture());
      this.batch.setShader(this.blurShader);
      this.blurShader.setUniformf("dir", 0.0F, 1.0F);
      this.blurShader.setUniformf("resolution", 256.0F);
      this.blurShader.setUniformf("radius", 1.5F);
      this.batch.draw(this.fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      this.batch.setBlendFunction(770, 771);
      this.batch.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.batch.setShader(null);
      this.batch.end();
    }
    else
    {
      this.fboRegion.setTexture(this.fbo1.getColorBufferTexture());

      this.batch.begin();
      this.fbo2.begin();
      Gdx.gl.glClear(16384);
      this.batch.setProjectionMatrix(this.staticCamera.combined);
      this.batch.draw(this.fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      this.batch.flush();
      this.fbo2.end();

      this.fboRegion.setTexture(this.fbo2.getColorBufferTexture());
      this.fbo3.begin();
      this.batch.setShader(this.blurShader);
      this.blurShader.setUniformf("dir", 1.0F, 0.0F);
      this.blurShader.setUniformf("resolution", 256.0F);
      this.blurShader.setUniformf("radius", 1.7F);
      Gdx.gl.glClear(16384);
      this.batch.setProjectionMatrix(this.staticCamera.combined);
      this.batch.draw(this.fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      this.batch.flush();
      this.fbo3.end();

      this.fboRegion.setTexture(this.fbo3.getColorBufferTexture());
      this.blurShader.setUniformf("dir", 0.0F, 1.0F);
      this.batch.setProjectionMatrix(this.staticCamera.combined);
      this.batch.draw(this.fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      this.batch.setShader(null);
      this.batch.end();
    }


    if (this.debugEnabled) {
      renderBox2DDebug();
    }
    this.fpsLogger.log();
  }

  public void renderTerrain()
  {
    this.terrainShader.begin();
    this.terrainShader.setUniformi("u_texture", 0);
    this.terrainShader.setUniformMatrix("u_projectionViewMatrix", this.camera.combined);

    Assets.terrainTexture.bind();

    for (int i = 0; i < this.world.getTerrain().getMeshes().size(); i++) {
      if (this.camera.frustum.boundsInFrustum((BoundingBox)this.world.getTerrain().getBoundingBoxes().get(i)))
        ((Mesh)this.world.getTerrain().meshes.get(i)).render(this.terrainShader, 5);
    }
    this.terrainShader.end();
  }

  public void dispose()
  {
    this.terrainShader.dispose();
    this.hiPassShader.dispose();
    this.blurShader.dispose();
    this.fbo1.dispose();
    this.fbo2.dispose();
  }
}