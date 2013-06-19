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

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.hajnar.GravityShip.GameObjects.GameObject;

public class CollisionProcessor implements ContactListener{

	GameWorld world;
			
	public CollisionProcessor(GameWorld world) {
		this.world = world;
	}

	@Override
	public void beginContact(Contact contact) {
		world.handleStartedCollisions((GameObject) contact.getFixtureA().getBody().getUserData(),
		(GameObject) contact.getFixtureB().getBody().getUserData());	
	}

	@Override
	public void endContact(Contact contact) {
		world.handleEndedCollisions((GameObject) contact.getFixtureA().getBody().getUserData(),
		(GameObject) contact.getFixtureB().getBody().getUserData());	
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
