package com.hajnar.GravityShip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.hajnar.GravityShip.GameObjects.BlackHole;
import com.hajnar.GravityShip.GameObjects.Bullet;
import com.hajnar.GravityShip.GameObjects.Canon;
import com.hajnar.GravityShip.GameObjects.GameCamera;
import com.hajnar.GravityShip.GameObjects.LandingZone;
import com.hajnar.GravityShip.GameObjects.Player;
import com.hajnar.GravityShip.GameObjects.Star;
import com.hajnar.GravityShip.GameObjects.Terrain;

import java.io.PrintStream;
import java.util.ArrayList;

public class GameWorldRenderGL2
{
  public static final int FBO_SIZE = 256;
  private GameWorld world;
  private GameCamera camera;
  private OrthographicCamera parallaxCamera;
  private OrthographicCamera parallaxCamera2;
  private OrthographicCamera staticCamera;
  private SpriteBatch batch;
  private Sprite starsParticle;
  protected Sprite thrustParticle;
  protected Sprite explosionParticle;
  protected Sprite smokeParticle;
  protected Sprite sparkParticle;
  protected ScrollingBackground gasTexture;
  protected ScrollingBackground gasTexture2;
  protected Box2DDebugRenderer debugRenderer;	
  protected Matrix4 debugMatrix;
  protected FPSLogger fpsLogger;
  protected Vector3 tmpVector;
  protected boolean debugEnabled;
  private FrameBuffer fbo1;
  private FrameBuffer fbo2;
  private FrameBuffer fbo3;
  private TextureRegion fboRegion;
  private ShaderProgram terrainShader;
  private ShaderProgram hiPassShader;
  private ShaderProgram blurShader;

  public GameWorldRenderGL2(GameWorld world, SpriteBatch batch)
  {
	this.world = world;
	this.batch = batch;
	this.debugEnabled = false;
	this.camera = new GameCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
	this.parallaxCamera = new GameCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
	this.parallaxCamera2 = new GameCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
	this.staticCamera = new OrthographicCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
	this.staticCamera.position.set(Helper.FRUSTUM_WIDTH / 2, Helper.FRUSTUM_HEIGHT / 2, 0);
	this.gasTexture = new ScrollingBackground(parallaxCamera, Assets.gasRegion1, batch);
	this.gasTexture2 = new ScrollingBackground(parallaxCamera2, Assets.gasRegion2, batch);
	this.debugRenderer = new Box2DDebugRenderer();
	this.debugRenderer.setDrawVelocities(true);
	this.fpsLogger = new FPSLogger();
	this.tmpVector = new Vector3();	
	this.starsParticle = new Sprite(Assets.starRegion);
	this.smokeParticle = new Sprite(Assets.smokeParticleRegion);
	this.sparkParticle = new Sprite(Assets.sparkParticleRegion);
	world.getThrustParticleEmitters().get(0).setSprite(smokeParticle);
	world.getExplosionParticleEmitters().get(0).setSprite(sparkParticle);
	world.getExplosionParticleEmitters().get(1).setSprite(smokeParticle);
	world.getStarsParticleEmitters().get(0).setSprite(starsParticle);
	
    ShaderProgram.pedantic = false;

    fbo1 = new FrameBuffer(Format.RGBA8888, 1200, (int)Helper.WINDOW_HEIGHT, false);
    fbo2 = new FrameBuffer(Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
    fbo3 = new FrameBuffer(Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
    fboRegion = new TextureRegion(fbo1.getColorBufferTexture());
    fboRegion.flip(false, true);

    terrainShader = new ShaderProgram(Assets.terrainVertexShader, Assets.terrainFragmentShader);
    hiPassShader = new ShaderProgram(Assets.hiPassVertexShader, Assets.hiPassFragmentShader);
    blurShader = new ShaderProgram(Assets.hiPassVertexShader, Assets.blurFragmentShader);
    if (terrainShader.getLog().length() != 0)
      System.out.println("terrainShader: " + terrainShader.getLog());
    if (hiPassShader.getLog().length() != 0)
      System.out.println("hiPassShader: " + hiPassShader.getLog());
    if (blurShader.getLog().length() != 0) {
      System.out.println("blurShader: " + terrainShader.getLog());
    }
    
    blurShader.setUniformf("dir", 0.0f, 0.0f);
    blurShader.setUniformf("resolution", FBO_SIZE);
    blurShader.setUniformf("radius", 1.0f);
  }

  public GameWorldRenderGL2(GameWorld world, SpriteBatch batch, boolean debug)
  {
		this.world = world;
		this.batch = batch;
		this.debugEnabled = false;
		this.camera = new GameCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
		this.parallaxCamera = new GameCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
		this.parallaxCamera2 = new GameCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
		this.staticCamera = new OrthographicCamera(Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_HEIGHT);
		this.staticCamera.position.set(Helper.FRUSTUM_WIDTH / 2, Helper.FRUSTUM_HEIGHT / 2, 0);
		this.gasTexture = new ScrollingBackground(parallaxCamera, Assets.gasRegion1, batch);
		this.gasTexture2 = new ScrollingBackground(parallaxCamera2, Assets.gasRegion2, batch);
		this.debugRenderer = new Box2DDebugRenderer();
		this.debugRenderer.setDrawVelocities(true);
		this.fpsLogger = new FPSLogger();
		this.tmpVector = new Vector3();	
		this.starsParticle = new Sprite(Assets.starRegion);
		this.smokeParticle = new Sprite(Assets.smokeParticleRegion);
		this.sparkParticle = new Sprite(Assets.sparkParticleRegion);
		world.getThrustParticleEmitters().get(0).setSprite(smokeParticle);
		world.getExplosionParticleEmitters().get(0).setSprite(sparkParticle);
		world.getExplosionParticleEmitters().get(1).setSprite(smokeParticle);
		world.getStarsParticleEmitters().get(0).setSprite(starsParticle);
	
    ShaderProgram.pedantic = false;

    fbo1 = new FrameBuffer(Format.RGBA8888, 1200, (int) Helper.WINDOW_HEIGHT, false);
    fbo2 = new FrameBuffer(Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
    fbo3 = new FrameBuffer(Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
    fboRegion = new TextureRegion(fbo1.getColorBufferTexture());
    fboRegion.flip(false, true);

    terrainShader = new ShaderProgram(Assets.terrainVertexShader, Assets.terrainFragmentShader);
    hiPassShader = new ShaderProgram(Assets.hiPassVertexShader, Assets.hiPassFragmentShader);
    blurShader = new ShaderProgram(Assets.hiPassVertexShader, Assets.blurFragmentShader);
    if (terrainShader.getLog().length() != 0)
      System.out.println("terrainShader: " + terrainShader.getLog());
    if (hiPassShader.getLog().length() != 0)
      System.out.println("hiPassShader: " + hiPassShader.getLog());
    if (blurShader.getLog().length() != 0) {
      System.out.println("blurShader: " + terrainShader.getLog());
    }
    blurShader.setUniformf("dir", 0.0f, 0.0f);
    blurShader.setUniformf("resolution", FBO_SIZE);
    blurShader.setUniformf("radius", 1.0f);
  }
  
  public void renderAndroid(float delta)
  {
    if (world.getState() == GameWorld.WORLD_RUNNING)
    {
      Vector2 cameraTranslation;
      cameraTranslation = camera.follow(world.getPlayer());
      parallaxCamera.translate(cameraTranslation.mul(0.1f));
      parallaxCamera2.translate(cameraTranslation.mul(0.3f));
    }
    camera.update();
    parallaxCamera.update();
    parallaxCamera2.update();
    gasTexture.updateBackground();
    gasTexture2.updateBackground();

    batch.begin();
    Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    Gdx.gl.glClear(16384);
    batch.disableBlending();
    batch.setProjectionMatrix(staticCamera.combined);
    batch.draw(Assets.backgroundTexture, -Helper.FRUSTUM_WIDTH / 2, -Helper.FRUSTUM_HEIGHT / 2, Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_WIDTH);
    batch.enableBlending();
    batch.setProjectionMatrix(parallaxCamera.combined);
    gasTexture.render();
    batch.setProjectionMatrix(parallaxCamera2.combined);
    gasTexture2.render();
    batch.setProjectionMatrix(camera.combined);
    renderBlackHoles();
    renderStars();
    renderParticles(delta);
    renderLandingZones();
    renderPlayer();
    renderBullets();
    batch.end();
    renderTerrain();
    batch.begin();
    renderCanons();
    batch.end();
    
    if (debugEnabled) {
        renderBox2DDebug();
      }
    fpsLogger.log();
  }

  public void renderDesktop(float delta)
  {
    if (world.getState() == GameWorld.WORLD_RUNNING)
    {
      Vector2 cameraTranslation;
      cameraTranslation = camera.followWithZooming(world.getPlayer(), delta);
      parallaxCamera.translate(cameraTranslation.mul(0.1f));
      parallaxCamera2.translate(cameraTranslation.mul(0.3f));
    }
    camera.update();
    parallaxCamera.update();
    parallaxCamera2.update();
    gasTexture.updateBackground();
    gasTexture2.updateBackground();

    batch.begin();
    fbo1.begin();
    Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    Gdx.gl.glClear(16384);
    batch.disableBlending();
    batch.setProjectionMatrix(staticCamera.combined);
    batch.draw(Assets.backgroundTexture, -Helper.FRUSTUM_WIDTH / 2, -Helper.FRUSTUM_HEIGHT / 2, Helper.FRUSTUM_WIDTH, Helper.FRUSTUM_WIDTH);
    batch.enableBlending();
    batch.setProjectionMatrix(parallaxCamera.combined);
    gasTexture.render();
    batch.setProjectionMatrix(parallaxCamera2.combined);
    gasTexture2.render();
    batch.setProjectionMatrix(camera.combined);
    renderBlackHoles();
    renderStars();
    renderParticles(delta);
    renderLandingZones();
    renderPlayer();
    renderBullets();
    batch.end();
    renderTerrain();
    batch.begin();
    renderCanons();
    batch.end();
    fbo1.end();

    if ((world.getState() == GameWorld.WORLD_RUNNING) || ((world.getState() == GameWorld.WORLD_GAME_OVER) && (GravityShip.gameScreen.getGameOverDuration() <= 1.5f)))
    {
      fboRegion.setTexture(fbo1.getColorBufferTexture());

      batch.begin();
      fbo2.begin();
      batch.setShader(hiPassShader);
      hiPassShader.setUniformf("brightPassThreshold", 0.47F);
      Gdx.gl.glClear(16384);
      batch.setProjectionMatrix(staticCamera.combined);
      batch.draw(fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      batch.flush();
      fbo2.end();

      fboRegion.setTexture(fbo2.getColorBufferTexture());

      fbo3.begin();
      batch.setShader(blurShader);
      blurShader.setUniformf("dir", 1.0F, 0.0F);
      blurShader.setUniformf("resolution", 256.0F);
      blurShader.setUniformf("radius", 1.5F);
      Gdx.gl.glClear(16384);
      batch.setProjectionMatrix(staticCamera.combined);
      batch.draw(fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      batch.flush();
      fbo3.end();

      batch.setShader(null);
      Gdx.gl.glClear(16384);
      batch.setBlendFunction(770, 1);

      fboRegion.setTexture(fbo1.getColorBufferTexture());
      batch.setProjectionMatrix(staticCamera.combined);
      batch.draw(fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      batch.setColor(1.0F, 1.0F, 1.0F, 0.7F);
      fboRegion.setTexture(fbo3.getColorBufferTexture());
      batch.setShader(blurShader);
      blurShader.setUniformf("dir", 0.0F, 1.0F);
      blurShader.setUniformf("resolution", 256.0F);
      blurShader.setUniformf("radius", 1.5F);
      batch.draw(fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      batch.setBlendFunction(770, 771);
      batch.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      batch.setShader(null);
      batch.end();
    }
    else
    {
      fboRegion.setTexture(fbo1.getColorBufferTexture());

      batch.begin();
      fbo2.begin();
      Gdx.gl.glClear(16384);
      batch.setProjectionMatrix(staticCamera.combined);
      batch.draw(fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      batch.flush();
      fbo2.end();

      fboRegion.setTexture(fbo2.getColorBufferTexture());
      fbo3.begin();
      batch.setShader(blurShader);
      blurShader.setUniformf("dir", 1.0F, 0.0F);
      blurShader.setUniformf("resolution", 256.0F);
      blurShader.setUniformf("radius", 1.7F);
      Gdx.gl.glClear(16384);
      batch.setProjectionMatrix(staticCamera.combined);
      batch.draw(fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      batch.flush();
      fbo3.end();

      fboRegion.setTexture(fbo3.getColorBufferTexture());
      blurShader.setUniformf("dir", 0.0F, 1.0F);
      batch.setProjectionMatrix(staticCamera.combined);
      batch.draw(fboRegion, -600.0F, -Helper.FRUSTUM_HEIGHT / 2.0F, 1200.0F, Helper.FRUSTUM_HEIGHT);
      batch.setShader(null);
      batch.end();
    }


    if (debugEnabled) {
      renderBox2DDebug();
    }
    fpsLogger.log();
  }

  public void renderTerrain()
  {
    terrainShader.begin();
    terrainShader.setUniformi("u_texture", 0);
    terrainShader.setUniformMatrix("u_projectionViewMatrix", camera.combined);

    Assets.terrainTexture.bind();

    for (int i = 0; i < world.getTerrain().getMeshes().size(); i++) {
      if (camera.frustum.boundsInFrustum((BoundingBox)world.getTerrain().getBoundingBoxes().get(i)))
        ((Mesh)world.getTerrain().meshes.get(i)).render(terrainShader, 5);
    }
    terrainShader.end();
  }
  
  public void renderBox2DDebug()
	{
		debugMatrix.set(camera.combined);
		debugMatrix.scale(Helper.BOX_TO_WORLD, Helper.BOX_TO_WORLD, 1f); 
		debugRenderer.render(world.getBox2dWorld(), debugMatrix);
	}
	
	public void renderPlayer()
	{
		if (world.getPlayer().getState() != Player.SHIP_STATE_DEAD)
			world.getPlayer().getSprite().draw(batch);
	}
	
	public void renderLandingZones()
	{
		int len = world.getLandingZones().size();
		LandingZone lz;
		Vector2 spriteCenter;
		for (int i = 0; i < len; i++) {
			lz = world.getLandingZones().get(i);
			spriteCenter = lz.getBody().getPosition();
			spriteCenter.mul(Helper.BOX_TO_WORLD);
			if (camera.frustum.sphereInFrustumWithoutNearFar(
				tmpVector.set(spriteCenter.x,spriteCenter.y,0),
				lz.getSprite().getWidth()/2))
			{
				lz.getSprite().draw(batch);
			}
		}
	}
	
	public void renderCanons()
	{
		int len = world.getCanons().size();
		Canon canon;
		Vector2 spriteCenter;
		for (int i = 0; i < len; i++) {
			canon = world.getCanons().get(i);
			spriteCenter = canon.getBody().getPosition();
			spriteCenter.mul(Helper.BOX_TO_WORLD);
			if (camera.frustum.sphereInFrustumWithoutNearFar(
				tmpVector.set(spriteCenter.x,spriteCenter.y,0),
				canon.getSprite().getWidth()/2))
			{
				canon.getSprite().draw(batch);
			}
		}
	}
	
	public void renderParticles(float delta)
	{
		world.getThrustParticleEffect().draw(batch, delta);
		world.getExplosionParticleEffect().draw(batch, delta);
		world.getStarsParticleEffect().draw(batch, delta);
	}	
	
	public void renderBullets()
	{
		int len = world.getBullets().size();
		Bullet bullet;
		Vector2 spriteCenter;
		for (int i = 0; i < len; i++) {
			bullet = world.getBullets().get(i);
			spriteCenter = bullet.getBody().getPosition();
			spriteCenter.mul(Helper.BOX_TO_WORLD);
			if (camera.frustum.sphereInFrustumWithoutNearFar(
				tmpVector.set(spriteCenter.x,spriteCenter.y,0),
				bullet.getSprite().getWidth()/2))
			{
				bullet.getSprite().draw(batch);
			}
		}
	}	
	
	public void renderStars()
	{
		int len = world.getStars().size();
		Star star;
		Vector2 spriteCenter;
		for (int i = 0; i < len; i++) {
			star = world.getStars().get(i);
			spriteCenter = star.getBody().getPosition();
			spriteCenter.mul(Helper.BOX_TO_WORLD);
			if (camera.frustum.sphereInFrustumWithoutNearFar(
				tmpVector.set(spriteCenter.x,spriteCenter.y,0),
				star.getSprite().getWidth()/2) && !star.isPickedUp())
			{
				star.getSprite().draw(batch);
			}
		}
	}	
	
	public void renderBlackHoles()
	{
		int len = world.getBlackHoles().size();
		BlackHole hole;
		Vector2 spriteCenter;
		for (int i = 0; i < len; i++) {
			hole = world.getBlackHoles().get(i);
			spriteCenter = hole.getBody().getPosition();
			spriteCenter.mul(Helper.BOX_TO_WORLD);
			if (camera.frustum.sphereInFrustumWithoutNearFar(
				tmpVector.set(spriteCenter.x,spriteCenter.y,0),
				hole.getSprite().getWidth()/2))
			{
				hole.getSprite().draw(batch);
			}
		}
	}
	
  public void dispose()
  {
    terrainShader.dispose();
    hiPassShader.dispose();
    blurShader.dispose();
    fbo1.dispose();
    fbo2.dispose();
  }
}