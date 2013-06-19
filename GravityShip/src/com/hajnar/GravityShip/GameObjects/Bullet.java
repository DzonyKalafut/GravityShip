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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.hajnar.GravityShip.Assets;
import com.hajnar.GravityShip.Helper;

public class Bullet extends GameObject{

	private Sprite sprite; 
	
	public Bullet(World world, float x, float y, Vector2 velocity)
	{
		super(world, BodyType.DynamicBody, OBJECT_TYPE_BULLET, x, y, 0);
	
		sprite = new Sprite(Assets.bulletRegion);
		sprite.setPosition(objectBody.getPosition().x * Helper.BOX_TO_WORLD - sprite.getWidth()/2,
				   objectBody.getPosition().y * Helper.BOX_TO_WORLD - sprite.getHeight()/2);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(0.08f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 40f; 
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		objectBody.createFixture(fixtureDef);
		circle.dispose();
		
		objectBody.setGravityScale(0);
		objectBody.setFixedRotation(true);
		objectBody.setLinearVelocity(velocity);
		objectBody.setUserData(this);
	}
	
	public void update()
	{
		sprite.setPosition(objectBody.getPosition().x * Helper.BOX_TO_WORLD - sprite.getWidth()/2,
				   objectBody.getPosition().y * Helper.BOX_TO_WORLD - sprite.getHeight()/2);	
	}
	
	public Sprite getSprite() {
		return sprite;
	}

}
