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

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.hajnar.GravityShip.Helper;

public class GameCamera extends OrthographicCamera{

	private float playerVelocityBefore; //toto je pre metodu followWithZoom lebo neviem zistit akceleraciu telesa
	private Vector2 cameraTranslation;
	private Vector2 offset;
	
	public GameCamera(float width, float height)
	{
		super(width, height);
		this.playerVelocityBefore = 0;
		this.offset = new Vector2();
		this.cameraTranslation = new Vector2();
	}
	
	public Vector2 follow(Player player)
	{
		Vector2 playerVelocity = player.getBody().getLinearVelocity();
		offset.add(-offset.x/20f,-offset.y/20f);
		offset.add(playerVelocity.mul(1.2f));
		if (offset.x > 200)
			offset.x = 200;
		else if (offset.x < -200)
			offset.x = -200;		
		if (offset.y > 150)
			offset.y = 150;
		else if (offset.y < -150)
			offset.y = -150;
		cameraTranslation.set( player.getBody().getPosition().mul(Helper.BOX_TO_WORLD).sub(
				position.x - offset.x, position.y - offset.y ));
		translate(cameraTranslation);	
		return cameraTranslation;
	}
	
	public Vector2 followWithZooming(Player player)
	{
		Vector2 playerVelocity = player.getBody().getLinearVelocity();
		float playerVelocityLen = playerVelocity.len();
		offset.add(-offset.x/20f,-offset.y/20f);
		offset.add(playerVelocity.mul(1.2f));
		if (offset.x > 200)
			offset.x = 200;
		else if (offset.x < -200)
			offset.x = -200;		
		if (offset.y > 150)
			offset.y = 150;
		else if (offset.y < -150)
			offset.y = -150;
		cameraTranslation.set( player.getBody().getPosition().mul(Helper.BOX_TO_WORLD).sub(
				position.x - offset.x, position.y - offset.y ));
		translate(cameraTranslation);
		if (playerVelocityLen< 20)
			if (playerVelocityBefore < playerVelocityLen && playerVelocityLen > 2)
				changeZoom(0.0045f);
			else
				changeZoom(-0.0045f);
		playerVelocityBefore = playerVelocityLen;
		return cameraTranslation;

	}
	
	public void changeZoom(float value)
	{
		if (zoom + value > 1.5)
			zoom = 1.5f;
		else if (zoom + value < 1)
			zoom = 1;
		else
			zoom += value;
	}
}
