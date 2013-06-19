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

package com.hajnar.GravityShip;

import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.hajnar.GravityShip.GameObjects.BlackHole;
import com.hajnar.GravityShip.GameObjects.Bullet;
import com.hajnar.GravityShip.GameObjects.Canon;
import com.hajnar.GravityShip.GameObjects.GameCamera;
import com.hajnar.GravityShip.GameObjects.LandingZone;
import com.hajnar.GravityShip.GameObjects.Player;
import com.hajnar.GravityShip.GameObjects.Star;

public class GameWorldRender {
	
	protected GameWorld world;
	protected GameCamera camera;
	protected OrthographicCamera parallaxCamera;
	protected OrthographicCamera parallaxCamera2;
	protected OrthographicCamera staticCamera;
	protected SpriteBatch batch;
	protected Sprite starsParticle;
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
	
	public GameWorldRender(GameWorld world, SpriteBatch batch)
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
	}
	
	public GameWorldRender(GameWorld world, SpriteBatch batch, boolean debugEnabled)
	{
		this.world = world;
		this.batch = batch;
		this.debugEnabled = debugEnabled;
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
		this.debugMatrix = new Matrix4();
		this.starsParticle = new Sprite(Assets.starRegion);
		this.smokeParticle = new Sprite(Assets.smokeParticleRegion);
		this.sparkParticle = new Sprite(Assets.sparkParticleRegion);
		world.getThrustParticleEmitters().get(0).setSprite(smokeParticle);
		world.getExplosionParticleEmitters().get(0).setSprite(sparkParticle);
		world.getExplosionParticleEmitters().get(1).setSprite(smokeParticle);
		world.getStarsParticleEmitters().get(0).setSprite(starsParticle);
	}
	
	public void render(float delta)
	{
		Vector2 cameraTranslation;
		if (world.getState() == GameWorld.WORLD_RUNNING)
		{
			if (GravityShip.deviceType == GravityShip.DEV_TYPE_DESKTOP)
				cameraTranslation = camera.followWithZooming(world.getPlayer());
			else 
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
		batch.disableBlending();
		batch.setProjectionMatrix(staticCamera.combined);
		batch.draw(Assets.backgroundTexture, -Helper.FRUSTUM_WIDTH/2, -Helper.FRUSTUM_HEIGHT/2, 1200, 1200);
		batch.enableBlending();
		batch.setProjectionMatrix(parallaxCamera.combined);
		gasTexture.render();
		batch.setProjectionMatrix(parallaxCamera2.combined);
		gasTexture2.render();	
		batch.setProjectionMatrix(camera.combined);
		renderBlackHoles();
		renderStars();
		renderParticles(delta);		
		renderPlayer(); 
		renderLandingZones();		
		renderBullets();
		batch.flush();
		renderTerrain();
		renderCanons();
		batch.end();
		
		if (debugEnabled)
			renderBox2DDebug();
		
		fpsLogger.log();
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
	
	public void renderTerrain()
	{		
		Assets.backgroundTexture.bind();
		
		for (int i = 0; i < world.getTerrain().getMeshes().size(); i++) {		
				if (camera.frustum.boundsInFrustum(world.getTerrain().getBoundingBoxes().get(i)))
						world.getTerrain().meshes.get(i).render(GL10.GL_TRIANGLE_STRIP);
		}
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
	
	public void dispose() {}

}
