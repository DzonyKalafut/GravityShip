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

package com.hajnar.GravityShip.GameObjects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.hajnar.GravityShip.Assets;
import com.hajnar.GravityShip.Helper;

public class BlackHole extends GameObject{
	
	private int strength;
	private Sprite sprite;
	private Animation timeWarp;
	private float animTime;
	
	public BlackHole(World world, float x, float y, int strength) {
		super(world, BodyType.StaticBody, OBJECT_TYPE_BLACKHOLE, x, y, 0);
			
		sprite = new Sprite(Assets.blackHoleRegions[0]);
		sprite.setPosition(objectBody.getPosition().x * Helper.BOX_TO_WORLD - sprite.getWidth()/2,
				   objectBody.getPosition().y * Helper.BOX_TO_WORLD - sprite.getHeight()/2);
		
		timeWarp = new Animation(0.05f, Assets.blackHoleRegions);
		
		Shape circle = new CircleShape();
		circle.setRadius(0.55f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		
		objectBody.createFixture(fixtureDef);
				
		circle.dispose();
		
		objectBody.setUserData(this);
		
		animTime = 0;		
		this.strength = strength;
	}
	
	public void update(float delta)
	{
		animTime += delta;
		sprite.setRegion(timeWarp.getKeyFrame(animTime, true));
		sprite.setRotation(sprite.getRotation()+2);
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	
}
